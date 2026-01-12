import json
import pandas as pd
import numpy as np
import tensorflow as tf

from sklearn.preprocessing import LabelEncoder
from sklearn.metrics import classification_report, accuracy_score

# -----------------------------
# CONFIG
# -----------------------------
DATA_PATH = "../data/public_unified_multimodal.csv"
MODEL_PATH = "../output/scam_model.keras"
TOKENIZER_PATH = "../output/tokenizer.json"

MAX_LEN = 40

# -----------------------------
# LOAD DATA
# -----------------------------
df = pd.read_csv(DATA_PATH)
df["text"] = df["text"].astype(str)

# Encode source type
source_encoder = LabelEncoder()
df["source_type_enc"] = source_encoder.fit_transform(df["source_type"])

# Encode labels
label_encoder = LabelEncoder()
df["scam_label_enc"] = label_encoder.fit_transform(df["scam_label"])
severity_encoder = LabelEncoder()
df["severity_enc"] = severity_encoder.fit_transform(df["severity"])

# -----------------------------
# LOAD TOKENIZER
# -----------------------------
with open(TOKENIZER_PATH) as f:
    tokenizer = tf.keras.preprocessing.text.tokenizer_from_json(json.load(f))

sequences = tokenizer.texts_to_sequences(df["text"])
X_text = tf.keras.preprocessing.sequence.pad_sequences(sequences, maxlen=MAX_LEN)

X_structured = df[
    ["source_type_enc", "has_otp", "has_upi", "has_url", "has_urgency", "has_threat"]
].values

# -----------------------------
# LOAD MODEL
# -----------------------------
model = tf.keras.models.load_model(MODEL_PATH)

# -----------------------------
# PREDICT
# -----------------------------
preds = model.predict([X_text, X_structured])

pred_label = np.argmax(preds[0], axis=1)
pred_severity = np.argmax(preds[1], axis=1)

pred_otp = (preds[2] > 0.5).astype(int).flatten()
pred_upi = (preds[3] > 0.5).astype(int).flatten()
pred_url = (preds[4] > 0.5).astype(int).flatten()
pred_urgency = (preds[5] > 0.5).astype(int).flatten()
pred_threat = (preds[6] > 0.5).astype(int).flatten()

# -----------------------------
# EVALUATION
# -----------------------------
print("\n=== SCAM LABEL PERFORMANCE ===")
print(classification_report(df["scam_label_enc"], pred_label))

print("\n=== SEVERITY PERFORMANCE ===")
print(classification_report(df["severity_enc"], pred_severity))

print("\n=== FLAG ACCURACY ===")
print("OTP Accuracy:", accuracy_score(df["has_otp"], pred_otp))
print("UPI Accuracy:", accuracy_score(df["has_upi"], pred_upi))
print("URL Accuracy:", accuracy_score(df["has_url"], pred_url))
print("Urgency Accuracy:", accuracy_score(df["has_urgency"], pred_urgency))
print("Threat Accuracy:", accuracy_score(df["has_threat"], pred_threat))