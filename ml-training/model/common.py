from __future__ import annotations

import json
import random
import re
from collections import Counter
from pathlib import Path
from typing import Iterable

import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split


SEED = 42

MODEL_DIR = Path(__file__).resolve().parent
PROJECT_ROOT = MODEL_DIR.parents[1]
DATA_DIR = MODEL_DIR.parent / "data"
OUTPUT_DIR = MODEL_DIR.parent / "output"

BASE_DATA_PATH = DATA_DIR / "public_unified_multimodal.csv"
AUGMENTED_DATA_PATH = OUTPUT_DIR / "public_unified_multimodal_augmented_30296.csv"
DATASET_REPORT_PATH = OUTPUT_DIR / "dataset_report.json"
MODEL_PATH = OUTPUT_DIR / "scam_model_android.h5"
TOKENIZER_PATH = OUTPUT_DIR / "tokenizer.json"
METADATA_PATH = OUTPUT_DIR / "model_metadata.json"
EVAL_RESULTS_PATH = OUTPUT_DIR / "evaluation_results.json"
TFLITE_OUTPUT_PATH = OUTPUT_DIR / "scam_signal.tflite"
ANDROID_ASSETS_PATH = PROJECT_ROOT / "app" / "src" / "main" / "assets"

MAX_WORDS = 50_000
MAX_LEN = 64
EMBED_DIM = 128
BATCH_SIZE = 128
EPOCHS = 8
TARGET_NEW_ROWS = 30_000

OOV_INDEX = 1
PAD_INDEX = 0

STAGE_LABELS = ["LURE", "ACTION", "THREAT"]
ACTION_LABELS = [
    "CALL_NUMBER",
    "CLICK_LINK",
    "PAY_UPI",
    "SHARE_DETAILS",
    "SEND_OTP",
    "INSTALL_APP",
    "VISIT_OFFICIAL",
    "NO_ACTION",
]

SCAM_STAGE_MAP = {
    "Lure": 0,
    "No action (info only)": 0,
    "Action request": 1,
    "Payment/Transfer": 1,
    "Escalation/Threat": 2,
}

SCAM_ACTION_MAP = {
    "Call number": 0,
    "Click link": 1,
    "Pay via UPI": 2,
    "Scan QR / approve collect request": 2,
    "Share ID docs": 3,
    "Share OTP": 4,
    "Install remote-support app": 5,
    "Visit official app/website": 6,
    "No action (info only)": 7,
}

FLAG_COLUMNS = [
    "has_otp",
    "has_upi",
    "has_url",
    "has_qr",
    "has_phone",
    "has_threat",
    "has_urgency",
]

URL_RE = re.compile(r"(https?://\S+|www\.\S+)", re.IGNORECASE)
UPI_RE = re.compile(r"\b[a-z0-9._-]{2,}@[a-z]{2,}\b", re.IGNORECASE)
PHONE_RE = re.compile(r"(?<!\w)(?:\+91[-\s]?)?\d[\d\-\s]{7,}\d(?!\w)")
AMOUNT_RE = re.compile(r"(₹\s?\d[\d,]*|\b\d[\d,]*(?:\.\d+)?\s?(?:rs|rupees?)\b)", re.IGNORECASE)
CODE_RE = re.compile(r"\b\d{4,8}\b")
WHITESPACE_RE = re.compile(r"\s+")


def seed_everything() -> None:
    random.seed(SEED)
    np.random.seed(SEED)


def normalize_text(text: str) -> str:
    normalized = str(text).lower()
    normalized = URL_RE.sub(" <URL> ", normalized)
    normalized = UPI_RE.sub(" <UPI_ID> ", normalized)
    normalized = PHONE_RE.sub(" <PHONE> ", normalized)
    normalized = AMOUNT_RE.sub(" <AMOUNT> ", normalized)
    normalized = CODE_RE.sub(" <CODE> ", normalized)
    normalized = normalized.replace("₹", " <AMOUNT> ")
    normalized = WHITESPACE_RE.sub(" ", normalized).strip()
    return normalized


def tokenize_text(text: str) -> list[str]:
    return normalize_text(text).split()


def load_base_dataframe() -> pd.DataFrame:
    df = pd.read_csv(BASE_DATA_PATH)
    df["text"] = df["text"].fillna("").astype(str)
    return df


def prepare_dataframe(df: pd.DataFrame) -> pd.DataFrame:
    prepared = df.copy()
    prepared["text"] = prepared["text"].fillna("").astype(str)
    prepared["normalized_text"] = prepared["text"].map(normalize_text)
    prepared["stage_idx"] = prepared["scam_stage"].map(SCAM_STAGE_MAP)
    prepared["action_idx"] = prepared["requested_action"].map(SCAM_ACTION_MAP)
    prepared = prepared.dropna(subset=["stage_idx", "action_idx"]).copy()
    prepared["stage_idx"] = prepared["stage_idx"].astype(int)
    prepared["action_idx"] = prepared["action_idx"].astype(int)
    prepared["severity_idx"] = prepared["severity"].astype(int) - 1
    scam_type_labels = sorted(prepared["scam_type"].astype(str).unique().tolist())
    scam_type_map = {label: idx for idx, label in enumerate(scam_type_labels)}
    prepared["scam_type_idx"] = prepared["scam_type"].map(scam_type_map).astype(int)
    return prepared


def build_word_index(texts: Iterable[str], max_words: int = MAX_WORDS) -> dict[str, int]:
    counter: Counter[str] = Counter()
    for text in texts:
        counter.update(tokenize_text(text))

    vocab_limit = max_words - 2
    most_common = counter.most_common(vocab_limit)
    return {token: idx + 2 for idx, (token, _) in enumerate(most_common)}


def encode_text(text: str, word_index: dict[str, int], max_len: int = MAX_LEN) -> list[int]:
    tokens = tokenize_text(text)
    encoded = [word_index.get(token, OOV_INDEX) for token in tokens[:max_len]]
    if len(encoded) < max_len:
        encoded.extend([PAD_INDEX] * (max_len - len(encoded)))
    return encoded


def encode_dataframe(df: pd.DataFrame, word_index: dict[str, int], max_len: int = MAX_LEN) -> np.ndarray:
    return np.asarray([encode_text(text, word_index, max_len) for text in df["text"]], dtype=np.int32)


def save_tokenizer(word_index: dict[str, int]) -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    payload = {
        "type": "whitespace_vocab_v2",
        "pad_token": "<PAD>",
        "pad_index": PAD_INDEX,
        "oov_token": "<OOV>",
        "oov_index": OOV_INDEX,
        "max_len": MAX_LEN,
        "normalization_version": "v2",
        "word_index": word_index,
    }
    TOKENIZER_PATH.write_text(json.dumps(payload, indent=2))


def load_tokenizer() -> dict[str, int]:
    payload = json.loads(TOKENIZER_PATH.read_text())
    return {key: int(value) for key, value in payload["word_index"].items()}


def build_split_key(df: pd.DataFrame) -> pd.Series:
    return (
        df["is_scam"].astype(str)
        + "|"
        + df["modality"].astype(str)
        + "|"
        + df["language"].astype(str)
    )


def split_dataframe(df: pd.DataFrame):
    stratify_key = build_split_key(df)
    train_df, temp_df = train_test_split(
        df,
        test_size=0.2,
        random_state=SEED,
        stratify=stratify_key,
    )

    temp_key = build_split_key(temp_df)
    try:
        val_df, test_df = train_test_split(
            temp_df,
            test_size=0.5,
            random_state=SEED,
            stratify=temp_key,
        )
    except ValueError:
        fallback_key = temp_df["is_scam"].astype(str)
        val_df, test_df = train_test_split(
            temp_df,
            test_size=0.5,
            random_state=SEED,
            stratify=fallback_key,
        )

    return (
        train_df.reset_index(drop=True),
        val_df.reset_index(drop=True),
        test_df.reset_index(drop=True),
    )


def to_categorical(series: pd.Series | np.ndarray, num_classes: int) -> np.ndarray:
    values = np.asarray(series, dtype=np.int32)
    return np.eye(num_classes, dtype=np.float32)[values]


def build_metadata(
    scam_type_labels: list[str],
    vocab_size: int,
    train_rows: int,
    val_rows: int,
    test_rows: int,
    dataset_path: Path,
) -> dict:
    return {
        "seed": SEED,
        "normalization_version": "v2",
        "dataset_path": str(dataset_path),
        "max_words": MAX_WORDS,
        "max_len": MAX_LEN,
        "vocab_size": vocab_size,
        "embedding_dim": EMBED_DIM,
        "epochs": EPOCHS,
        "batch_size": BATCH_SIZE,
        "train_rows": train_rows,
        "val_rows": val_rows,
        "test_rows": test_rows,
        "output_order": [
            "is_scam",
            "severity",
            "stage",
            "action",
            "scam_type",
            "has_otp",
            "has_upi",
            "has_url",
            "has_qr",
            "has_phone",
            "has_threat",
            "has_urgency",
        ],
        "stage_labels": STAGE_LABELS,
        "action_labels": ACTION_LABELS,
        "scam_type_labels": scam_type_labels,
        "flag_labels": FLAG_COLUMNS,
    }


def save_metadata(metadata: dict) -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    METADATA_PATH.write_text(json.dumps(metadata, indent=2))


def load_metadata() -> dict:
    return json.loads(METADATA_PATH.read_text())
