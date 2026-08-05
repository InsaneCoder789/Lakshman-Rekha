from __future__ import annotations

import json

import numpy as np
import tensorflow as tf
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix, f1_score

from common import (
    ACTION_LABELS,
    EVAL_RESULTS_PATH,
    FLAG_COLUMNS,
    MAX_LEN,
    MODEL_PATH,
    load_tokenizer,
    load_metadata,
    prepare_dataframe,
    split_dataframe,
)
from expand_dataset import ensure_augmented_dataset
from common import encode_dataframe


def report_dict(y_true, y_pred, labels=None) -> dict:
    return classification_report(
        y_true,
        y_pred,
        labels=labels,
        output_dict=True,
        zero_division=0,
    )


def main() -> None:
    raw_df = ensure_augmented_dataset()
    df = prepare_dataframe(raw_df)
    train_df, _val_df, test_df = split_dataframe(df)
    metadata = load_metadata()
    word_index = load_tokenizer()

    x_test = encode_dataframe(test_df, word_index, MAX_LEN)
    model = tf.keras.models.load_model(MODEL_PATH, compile=False)
    preds = model.predict(x_test, verbose=0)

    true_is_scam = test_df["is_scam"].astype(int).to_numpy()
    true_severity = test_df["severity_idx"].to_numpy()
    true_stage = test_df["stage_idx"].to_numpy()
    true_action = test_df["action_idx"].to_numpy()
    true_scam_type = test_df["scam_type_idx"].to_numpy()

    results = {
        "is_scam": report_dict(true_is_scam, (preds[0].reshape(-1) > 0.5).astype(int), labels=[0, 1]),
        "severity": report_dict(true_severity, np.argmax(preds[1], axis=1), labels=[0, 1, 2, 3, 4]),
        "stage": report_dict(true_stage, np.argmax(preds[2], axis=1), labels=[0, 1, 2]),
        "action": report_dict(true_action, np.argmax(preds[3], axis=1), labels=list(range(len(ACTION_LABELS)))),
        "scam_type": report_dict(true_scam_type, np.argmax(preds[4], axis=1)),
        "confusion": {
            "severity": confusion_matrix(true_severity, np.argmax(preds[1], axis=1)).tolist(),
            "stage": confusion_matrix(true_stage, np.argmax(preds[2], axis=1)).tolist(),
            "action": confusion_matrix(true_action, np.argmax(preds[3], axis=1)).tolist(),
        },
        "macro_f1": {
            "severity": f1_score(true_severity, np.argmax(preds[1], axis=1), average="macro", zero_division=0),
            "stage": f1_score(true_stage, np.argmax(preds[2], axis=1), average="macro", zero_division=0),
            "action": f1_score(true_action, np.argmax(preds[3], axis=1), average="macro", zero_division=0),
            "scam_type": f1_score(true_scam_type, np.argmax(preds[4], axis=1), average="macro", zero_division=0),
        },
        "flags": {},
    }

    for idx, flag_name in enumerate(FLAG_COLUMNS, start=5):
        truth = test_df[flag_name].astype(int).to_numpy()
        pred = (preds[idx].reshape(-1) > 0.5).astype(int)
        results["flags"][flag_name] = {
            "accuracy": accuracy_score(truth, pred),
            "report": report_dict(truth, pred, labels=[0, 1]),
        }

    EVAL_RESULTS_PATH.write_text(json.dumps(results, indent=2))
    print(f"Saved evaluation report to {EVAL_RESULTS_PATH}")


if __name__ == "__main__":
    main()
