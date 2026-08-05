from __future__ import annotations

import shutil

import tensorflow as tf

from common import (
    ANDROID_ASSETS_PATH,
    METADATA_PATH,
    MODEL_PATH,
    TFLITE_OUTPUT_PATH,
    TOKENIZER_PATH,
    load_metadata,
)


def main() -> None:
    metadata = load_metadata()
    model = tf.keras.models.load_model(MODEL_PATH, compile=False)

    actual_order = list(model.output_names)
    if actual_order != metadata["output_order"]:
        raise ValueError(
            "Model output order mismatch.\n"
            f"Expected: {metadata['output_order']}\n"
            f"Actual: {actual_order}"
        )

    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS]
    converter.optimizations = []
    converter._experimental_lower_tensor_list_ops = False

    tflite_model = converter.convert()
    TFLITE_OUTPUT_PATH.write_bytes(tflite_model)

    ANDROID_ASSETS_PATH.mkdir(parents=True, exist_ok=True)
    shutil.copy(TFLITE_OUTPUT_PATH, ANDROID_ASSETS_PATH / "scam_signal.tflite")
    shutil.copy(TOKENIZER_PATH, ANDROID_ASSETS_PATH / "tokenizer.json")
    shutil.copy(METADATA_PATH, ANDROID_ASSETS_PATH / "model_metadata.json")

    print(f"Saved TFLite model to {TFLITE_OUTPUT_PATH}")
    print(f"Copied artifacts to {ANDROID_ASSETS_PATH}")


if __name__ == "__main__":
    main()
