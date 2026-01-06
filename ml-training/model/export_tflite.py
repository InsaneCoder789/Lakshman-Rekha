import tensorflow as tf
import numpy as np

# Load trained Keras model
model = tf.keras.models.load_model("../output/scam_model.keras")

# Create converter
converter = tf.lite.TFLiteConverter.from_keras_model(model)

# Enable optimizations
converter.optimizations = [tf.lite.Optimize.DEFAULT]

# Keep input/output float for easy Android integration
converter.target_spec.supported_types = [tf.float32]

# Convert
tflite_model = converter.convert()

# Save model
with open("../../app/src/main/assets/scam_model.tflite", "wb") as f:
    f.write(tflite_model)

print("✅ TFLite model exported successfully")