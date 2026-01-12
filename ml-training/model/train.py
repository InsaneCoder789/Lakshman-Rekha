import pandas as pd
import numpy as np
import tensorflow as tf
from tensorflow.keras.layers import (
    Input, Embedding, Conv1D, GlobalMaxPooling1D,
    Dense, Dropout
)
from tensorflow.keras.models import Model
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from sklearn.model_selection import train_test_split
import json
import os

# -----------------------------
# PATHS
# -----------------------------
DATA_PATH = "../data/public_unified_multimodal.csv"
OUTPUT_DIR = "../output"
MODEL_PATH = os.path.join(OUTPUT_DIR, "scam_model_android.h5")
TOKENIZER_PATH = os.path.join(OUTPUT_DIR, "tokenizer.json")

os.makedirs(OUTPUT_DIR, exist_ok=True)

# -----------------------------
# LOAD DATA
# -----------------------------
df = pd.read_csv(DATA_PATH)
df["text"] = df["text"].fillna("").astype(str)

# -----------------------------
# EXPLICIT LABEL MAPS (CRITICAL)
# -----------------------------
SCAM_STAGE_MAP = {
    "Lure": 0,
    "No action (info only)": 0,
    "Action request": 1,
    "Payment/Transfer": 1,
    "Escalation/Threat": 2
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
    "No action (info only)": 6
}

# -----------------------------
# APPLY MAPPINGS
# -----------------------------
df["stage_idx"] = df["scam_stage"].map(SCAM_STAGE_MAP)
df["action_idx"] = df["requested_action"].map(SCAM_ACTION_MAP)

# Drop invalid rows
before = len(df)
df = df.dropna(subset=["stage_idx", "action_idx"])
print(f"⚠️ Dropped {before - len(df)} rows due to invalid labels")

df["stage_idx"] = df["stage_idx"].astype(int)
df["action_idx"] = df["action_idx"].astype(int)

# -----------------------------
# LABELS
# -----------------------------
y_is_scam = df["is_scam"].astype(int).values
y_severity = tf.keras.utils.to_categorical(df["severity"] - 1, num_classes=5)
y_stage = tf.keras.utils.to_categorical(df["stage_idx"], num_classes=3)
y_action = tf.keras.utils.to_categorical(df["action_idx"], num_classes=7)

y_has_otp = df["has_otp"].astype(int).values
y_has_upi = df["has_upi"].astype(int).values
y_has_url = df["has_url"].astype(int).values
y_has_threat = df["has_threat"].astype(int).values
y_has_urgency = df["has_urgency"].astype(int).values

# -----------------------------
# TOKENIZATION
# -----------------------------
MAX_WORDS = 30000
MAX_LEN = 40

tokenizer = Tokenizer(num_words=MAX_WORDS, oov_token="<OOV>")
tokenizer.fit_on_texts(df["text"])

X = pad_sequences(
    tokenizer.texts_to_sequences(df["text"]),
    maxlen=MAX_LEN,
    padding="post"
)

with open(TOKENIZER_PATH, "w") as f:
    f.write(tokenizer.to_json())

# -----------------------------
# SPLIT
# -----------------------------
(
    X_train, X_val,
    y_is_scam_tr, y_is_scam_val,
    y_sev_tr, y_sev_val,
    y_stage_tr, y_stage_val,
    y_action_tr, y_action_val,
    y_otp_tr, y_otp_val,
    y_upi_tr, y_upi_val,
    y_url_tr, y_url_val,
    y_threat_tr, y_threat_val,
    y_urgency_tr, y_urgency_val
) = train_test_split(
    X,
    y_is_scam,
    y_severity,
    y_stage,
    y_action,
    y_has_otp,
    y_has_upi,
    y_has_url,
    y_has_threat,
    y_has_urgency,
    test_size=0.2,
    random_state=42
)

# -----------------------------
# MODEL (ANDROID SAFE)
# -----------------------------
inputs = Input(shape=(MAX_LEN,), name="input_text")

x = Embedding(MAX_WORDS, 128)(inputs)
x = Conv1D(128, 3, activation="relu")(x)
x = GlobalMaxPooling1D()(x)
x = Dropout(0.3)(x)

outputs = [
    Dense(1, activation="sigmoid", name="is_scam")(x),
    Dense(5, activation="softmax", name="severity")(x),
    Dense(3, activation="softmax", name="stage")(x),
    Dense(7, activation="softmax", name="action")(x),
    Dense(1, activation="sigmoid", name="has_otp")(x),
    Dense(1, activation="sigmoid", name="has_upi")(x),
    Dense(1, activation="sigmoid", name="has_url")(x),
    Dense(1, activation="sigmoid", name="has_threat")(x),
    Dense(1, activation="sigmoid", name="has_urgency")(x),
]

model = Model(inputs, outputs)

model.compile(
    optimizer="adam",
    loss="binary_crossentropy"
)

model.fit(
    X_train,
    [
        y_is_scam_tr, y_sev_tr, y_stage_tr, y_action_tr,
        y_otp_tr, y_upi_tr, y_url_tr, y_threat_tr, y_urgency_tr
    ],
    validation_data=(
        X_val,
        [
            y_is_scam_val, y_sev_val, y_stage_val, y_action_val,
            y_otp_val, y_upi_val, y_url_val, y_threat_val, y_urgency_val
        ]
    ),
    epochs=5,
    batch_size=64
)

model.save(MODEL_PATH)
print("✅ Model saved:", MODEL_PATH)