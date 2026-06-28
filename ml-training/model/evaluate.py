import json
import numpy as np
import pandas as pd
import tensorflow as tf

from sklearn.metrics import accuracy_score, classification_report, confusion_matrix
from sklearn.model_selection import train_test_split


DATA_PATH = "../data/public_unified_multimodal.csv"
MODEL_PATH = "../output/scam_model_android.h5"
TOKENIZER_PATH = "../output/tokenizer.json"
METADATA_PATH = "../output/model_metadata.json"


def load_metadata():
    with open(METADATA_PATH) as f:
        return json.load(f)


def build_dataset(metadata):
    df = pd.read_csv(DATA_PATH)
    df["text"] = df["text"].fillna("").astype(str)

    stage_map = metadata["stage_map"]
    action_map = metadata["action_map"]

    df["stage_idx"] = df["scam_stage"].map(stage_map)
    df["action_idx"] = df["requested_action"].map(action_map)
    df = df.dropna(subset=["stage_idx", "action_idx"]).copy()

    df["stage_idx"] = df["stage_idx"].astype(int)
    df["action_idx"] = df["action_idx"].astype(int)

    with open(TOKENIZER_PATH) as f:
        tokenizer = tf.keras.preprocessing.text.tokenizer_from_json(f.read())

    sequences = tokenizer.texts_to_sequences(df["text"])
    x = tf.keras.preprocessing.sequence.pad_sequences(
        sequences,
        maxlen=metadata["max_len"],
        padding="post"
    )

    y = {
        "is_scam": df["is_scam"].astype(int).values,
        "severity": (df["severity"].astype(int) - 1).values,
        "stage": df["stage_idx"].values,
        "action": df["action_idx"].values,
        "has_otp": df["has_otp"].astype(int).values,
        "has_upi": df["has_upi"].astype(int).values,
        "has_url": df["has_url"].astype(int).values,
        "has_threat": df["has_threat"].astype(int).values,
        "has_urgency": df["has_urgency"].astype(int).values,
    }

    return train_test_split(
        x,
        y["is_scam"],
        y["severity"],
        y["stage"],
        y["action"],
        y["has_otp"],
        y["has_upi"],
        y["has_url"],
        y["has_threat"],
        y["has_urgency"],
        test_size=0.2,
        random_state=metadata["seed"]
    )


def main():
    metadata = load_metadata()
    split = build_dataset(metadata)
    (
        _x_train, x_test,
        _y_is_scam_train, y_is_scam_test,
        _y_severity_train, y_severity_test,
        _y_stage_train, y_stage_test,
        _y_action_train, y_action_test,
        _y_otp_train, y_otp_test,
        _y_upi_train, y_upi_test,
        _y_url_train, y_url_test,
        _y_threat_train, y_threat_test,
        _y_urgency_train, y_urgency_test
    ) = split

    model = tf.keras.models.load_model(MODEL_PATH, compile=False)
    preds = model.predict(x_test, verbose=0)

    pred_is_scam = (preds[0].reshape(-1) > 0.5).astype(int)
    pred_severity = np.argmax(preds[1], axis=1)
    pred_stage = np.argmax(preds[2], axis=1)
    pred_action = np.argmax(preds[3], axis=1)
    pred_otp = (preds[4].reshape(-1) > 0.5).astype(int)
    pred_upi = (preds[5].reshape(-1) > 0.5).astype(int)
    pred_url = (preds[6].reshape(-1) > 0.5).astype(int)
    pred_threat = (preds[7].reshape(-1) > 0.5).astype(int)
    pred_urgency = (preds[8].reshape(-1) > 0.5).astype(int)

    print("\n=== IS_SCAM ===")
    print(classification_report(y_is_scam_test, pred_is_scam, digits=4))

    print("\n=== SEVERITY ===")
    print(classification_report(y_severity_test, pred_severity, digits=4))
    print("Confusion matrix:")
    print(confusion_matrix(y_severity_test, pred_severity))

    print("\n=== STAGE ===")
    print(classification_report(y_stage_test, pred_stage, digits=4))

    print("\n=== ACTION ===")
    print(classification_report(y_action_test, pred_action, digits=4))

    print("\n=== BINARY FLAGS ===")
    for name, truth, pred in [
        ("has_otp", y_otp_test, pred_otp),
        ("has_upi", y_upi_test, pred_upi),
        ("has_url", y_url_test, pred_url),
        ("has_threat", y_threat_test, pred_threat),
        ("has_urgency", y_urgency_test, pred_urgency),
    ]:
        print(f"{name}: accuracy={accuracy_score(truth, pred):.4f}")
        print(classification_report(truth, pred, digits=4))


if __name__ == "__main__":
    main()
