from __future__ import annotations

import json

import numpy as np
import tensorflow as tf
from tensorflow.keras.callbacks import EarlyStopping, ReduceLROnPlateau
from tensorflow.keras.layers import (
    BatchNormalization,
    Concatenate,
    Conv1D,
    Dense,
    Dropout,
    Embedding,
    GlobalAveragePooling1D,
    GlobalMaxPooling1D,
    Input,
    SeparableConv1D,
    SpatialDropout1D,
)
from tensorflow.keras.models import Model

from common import (
    AUGMENTED_DATA_PATH,
    ACTION_LABELS,
    BATCH_SIZE,
    EMBED_DIM,
    EPOCHS,
    FLAG_COLUMNS,
    MAX_LEN,
    MAX_WORDS,
    METADATA_PATH,
    MODEL_PATH,
    OUTPUT_DIR,
    TOKENIZER_PATH,
    build_metadata,
    build_word_index,
    encode_dataframe,
    prepare_dataframe,
    save_metadata,
    save_tokenizer,
    seed_everything,
    split_dataframe,
    to_categorical,
)
from expand_dataset import ensure_augmented_dataset


HISTORY_PATH = OUTPUT_DIR / "training_history.json"


def build_targets(df, scam_type_count: int) -> dict[str, np.ndarray]:
    return {
        "is_scam": df["is_scam"].astype(np.float32).to_numpy(),
        "severity": to_categorical(df["severity_idx"], 5),
        "stage": to_categorical(df["stage_idx"], 3),
        "action": to_categorical(df["action_idx"], len(ACTION_LABELS)),
        "scam_type": to_categorical(df["scam_type_idx"], scam_type_count),
        "has_otp": df["has_otp"].astype(np.float32).to_numpy(),
        "has_upi": df["has_upi"].astype(np.float32).to_numpy(),
        "has_url": df["has_url"].astype(np.float32).to_numpy(),
        "has_qr": df["has_qr"].astype(np.float32).to_numpy(),
        "has_phone": df["has_phone"].astype(np.float32).to_numpy(),
        "has_threat": df["has_threat"].astype(np.float32).to_numpy(),
        "has_urgency": df["has_urgency"].astype(np.float32).to_numpy(),
    }


def create_model(vocab_size: int, scam_type_count: int) -> Model:
    inputs = Input(shape=(MAX_LEN,), dtype="int32", name="input_tokens")

    x = Embedding(vocab_size, EMBED_DIM, name="token_embedding")(inputs)
    x = SpatialDropout1D(0.2)(x)

    branch3 = SeparableConv1D(128, 3, padding="same", activation="relu")(x)
    branch5 = SeparableConv1D(128, 5, padding="same", activation="relu")(x)
    branch7 = SeparableConv1D(96, 7, padding="same", activation="relu")(x)

    merged = Concatenate(name="multi_scale_features")([branch3, branch5, branch7])
    merged = BatchNormalization()(merged)
    merged = Conv1D(128, 3, padding="same", activation="relu")(merged)

    pooled = Concatenate(name="pooled_features")(
        [GlobalAveragePooling1D()(merged), GlobalMaxPooling1D()(merged)]
    )
    pooled = Dense(256, activation="relu", name="shared_dense_1")(pooled)
    pooled = Dropout(0.3)(pooled)
    pooled = Dense(128, activation="relu", name="shared_dense_2")(pooled)
    shared = Dropout(0.2)(pooled)

    outputs = [
        Dense(1, activation="sigmoid", name="is_scam")(shared),
        Dense(5, activation="softmax", name="severity")(shared),
        Dense(3, activation="softmax", name="stage")(shared),
        Dense(len(ACTION_LABELS), activation="softmax", name="action")(shared),
        Dense(scam_type_count, activation="softmax", name="scam_type")(shared),
        Dense(1, activation="sigmoid", name="has_otp")(shared),
        Dense(1, activation="sigmoid", name="has_upi")(shared),
        Dense(1, activation="sigmoid", name="has_url")(shared),
        Dense(1, activation="sigmoid", name="has_qr")(shared),
        Dense(1, activation="sigmoid", name="has_phone")(shared),
        Dense(1, activation="sigmoid", name="has_threat")(shared),
        Dense(1, activation="sigmoid", name="has_urgency")(shared),
    ]

    model = Model(inputs=inputs, outputs=outputs, name="lakshman_rekha_multitask")
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=1e-3),
        loss={
            "is_scam": "binary_crossentropy",
            "severity": "categorical_crossentropy",
            "stage": "categorical_crossentropy",
            "action": "categorical_crossentropy",
            "scam_type": "categorical_crossentropy",
            "has_otp": "binary_crossentropy",
            "has_upi": "binary_crossentropy",
            "has_url": "binary_crossentropy",
            "has_qr": "binary_crossentropy",
            "has_phone": "binary_crossentropy",
            "has_threat": "binary_crossentropy",
            "has_urgency": "binary_crossentropy",
        },
        loss_weights={
            "is_scam": 1.6,
            "severity": 0.9,
            "stage": 1.0,
            "action": 1.1,
            "scam_type": 0.8,
            "has_otp": 0.8,
            "has_upi": 0.8,
            "has_url": 0.6,
            "has_qr": 0.5,
            "has_phone": 0.5,
            "has_threat": 0.7,
            "has_urgency": 0.7,
        },
        metrics={
            "is_scam": ["accuracy", tf.keras.metrics.AUC(name="auc")],
            "severity": ["accuracy"],
            "stage": ["accuracy"],
            "action": ["accuracy"],
            "scam_type": ["accuracy"],
            "has_otp": ["accuracy"],
            "has_upi": ["accuracy"],
            "has_url": ["accuracy"],
            "has_qr": ["accuracy"],
            "has_phone": ["accuracy"],
            "has_threat": ["accuracy"],
            "has_urgency": ["accuracy"],
        },
    )
    return model


def main() -> None:
    seed_everything()
    tf.random.set_seed(42)
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    raw_df = ensure_augmented_dataset()
    df = prepare_dataframe(raw_df)
    train_df, val_df, test_df = split_dataframe(df)

    word_index = build_word_index(train_df["text"], max_words=MAX_WORDS)
    save_tokenizer(word_index)

    scam_type_labels = sorted(df["scam_type"].astype(str).unique().tolist())
    metadata = build_metadata(
        scam_type_labels=scam_type_labels,
        vocab_size=min(MAX_WORDS, len(word_index) + 2),
        train_rows=len(train_df),
        val_rows=len(val_df),
        test_rows=len(test_df),
        dataset_path=AUGMENTED_DATA_PATH,
    )
    save_metadata(metadata)

    x_train = encode_dataframe(train_df, word_index, MAX_LEN)
    x_val = encode_dataframe(val_df, word_index, MAX_LEN)
    x_test = encode_dataframe(test_df, word_index, MAX_LEN)

    y_train = build_targets(train_df, len(scam_type_labels))
    y_val = build_targets(val_df, len(scam_type_labels))
    y_test = build_targets(test_df, len(scam_type_labels))

    model = create_model(vocab_size=metadata["vocab_size"], scam_type_count=len(scam_type_labels))
    history = model.fit(
        x_train,
        y_train,
        validation_data=(x_val, y_val),
        batch_size=BATCH_SIZE,
        epochs=EPOCHS,
        callbacks=[
            EarlyStopping(monitor="val_loss", patience=2, restore_best_weights=True),
            ReduceLROnPlateau(monitor="val_loss", patience=1, factor=0.5, min_lr=1e-5),
        ],
        verbose=2,
    )

    model.save(MODEL_PATH)
    HISTORY_PATH.write_text(json.dumps(history.history, indent=2))

    evaluation = model.evaluate(x_test, y_test, verbose=0, return_dict=True)
    evaluation_path = OUTPUT_DIR / "quick_test_metrics.json"
    evaluation_path.write_text(json.dumps(evaluation, indent=2))

    print(f"Saved model to {MODEL_PATH}")
    print(f"Saved tokenizer to {TOKENIZER_PATH}")
    print(f"Saved metadata to {METADATA_PATH}")
    print(f"Saved quick test metrics to {evaluation_path}")


if __name__ == "__main__":
    main()
