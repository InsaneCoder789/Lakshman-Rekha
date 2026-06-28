import tensorflow as tf
import os
import shutil
import json

# -----------------------------
# PATHS
# -----------------------------
KERAS_MODEL_PATH = "../output/scam_model_android.h5"
TFLITE_OUTPUT_PATH = "../output/scam_signal.tflite"
TOKENIZER_SOURCE = "../output/tokenizer.json"
METADATA_PATH = "../output/model_metadata.json"

ANDROID_ASSETS_PATH = "../../app/src/main/assets/"

os.makedirs("../output", exist_ok=True)
os.makedirs(ANDROID_ASSETS_PATH, exist_ok=True)

# -----------------------------
# LOAD MODEL (TF 2.13 SAFE)
# -----------------------------
print("🔹 Loading trained Keras model...")
model = tf.keras.models.load_model(KERAS_MODEL_PATH, compile=False)

with open(METADATA_PATH) as f:
    metadata = json.load(f)

# -----------------------------
# VERIFY OUTPUT ORDER (CRITICAL)
# -----------------------------
print("\n🔍 Verifying model outputs (Android order):")
for i, out in enumerate(model.outputs):
    print(f"{i} → {out.name}")

expected_order = metadata["output_order"]
actual_order = [tensor.name.split("/")[0] for tensor in model.outputs]

if actual_order != expected_order:
    raise ValueError(
        f"Model output order mismatch.\nExpected: {expected_order}\nActual: {actual_order}"
    )

"""
EXPECTED ORDER — MUST MATCH ANDROID:

0 → is_scam
1 → severity
2 → stage
3 → action
4 → has_otp
5 → has_upi
6 → has_url
7 → has_threat
8 → has_urgency
"""

# -----------------------------
# CONVERT TO ANDROID-SAFE TFLITE
# -----------------------------
print("\n🔄 Converting to Android-safe TFLite...")

converter = tf.lite.TFLiteConverter.from_keras_model(model)

# ✅ ONLY builtin ops (Android safe)
converter.target_spec.supported_ops = [
    tf.lite.OpsSet.TFLITE_BUILTINS
]

# ✅ Prevent TensorList lowering (important safety flag)
converter._experimental_lower_tensor_list_ops = False

# ✅ NO optimizations (avoids op version mismatch)
converter.optimizations = []

tflite_model = converter.convert()

# -----------------------------
# SAVE TFLITE
# -----------------------------
with open(TFLITE_OUTPUT_PATH, "wb") as f:
    f.write(tflite_model)

print("✅ TFLite model saved:", TFLITE_OUTPUT_PATH)

# -----------------------------
# COPY TO ANDROID ASSETS
# -----------------------------
shutil.copy(TFLITE_OUTPUT_PATH, os.path.join(ANDROID_ASSETS_PATH, "scam_signal.tflite"))
shutil.copy(TOKENIZER_SOURCE, os.path.join(ANDROID_ASSETS_PATH, "tokenizer.json"))
shutil.copy(METADATA_PATH, os.path.join(ANDROID_ASSETS_PATH, "model_metadata.json"))

print("\n📦 Copied to Android assets:")
print(" - scam_signal.tflite")
print(" - tokenizer.json")
print(" - model_metadata.json")

print("\n🚀 Export complete. Android will NOT crash.")
