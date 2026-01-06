import pandas as pd
import numpy as np
import tensorflow as tf
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from sklearn.model_selection import train_test_split
from sklearn.utils.class_weight import compute_class_weight
import json
import os

# ---------------- CONFIG ----------------
MAX_LEN = 40
VOCAB_SIZE = 3000        # ↓ reduced for lightweight phones
EPOCHS = 25
BATCH_SIZE = 8

DATA_DIR = "../data"
OUTPUT_DIR = "../output"
os.makedirs(OUTPUT_DIR, exist_ok=True)

# ---------------- LABEL MAP ----------------
LABEL_MAP = {
    "safe": 0,
    "suspicious": 1,
    "scam": 2
}

# ---------------- LOAD DATA ----------------
files = ["whatsapp.csv", "urls.csv", "calls.csv", "sms.csv"]

dfs = []
for f in files:
    path = os.path.join(DATA_DIR, f)
    df = pd.read_csv(path)[["text", "label"]]

    df["label"] = (
        df["label"]
        .astype(str)
        .str.lower()
        .map(LABEL_MAP)
    )

    if df["label"].isnull().any():
        raise ValueError(f"❌ Unknown label in {f}")

    dfs.append(df)

data = pd.concat(dfs).dropna()

texts = data["text"].astype(str).tolist()
labels = np.array(data["label"].tolist())

# ---------------- TOKENIZE ----------------
tokenizer = Tokenizer(
    num_words=VOCAB_SIZE,
    oov_token="<OOV>",
    filters='!"#$%&()*+,-./:;<=>?@[\\]^_`{|}~\t\n'
)
tokenizer.fit_on_texts(texts)

sequences = tokenizer.texts_to_sequences(texts)
X = pad_sequences(sequences, maxlen=MAX_LEN)

# ---------------- SPLIT ----------------
X_train, X_val, y_train, y_val = train_test_split(
    X, labels, test_size=0.2, random_state=42, stratify=labels
)

# ---------------- CLASS WEIGHTS (CRITICAL) ----------------
class_weights = compute_class_weight(
    class_weight="balanced",
    classes=np.unique(y_train),
    y=y_train
)

class_weights = dict(enumerate(class_weights))

# ---------------- MODEL ----------------
model = tf.keras.Sequential([
    tf.keras.layers.Embedding(
        input_dim=VOCAB_SIZE,
        output_dim=48,          # ↓ smaller embedding
    ),
    tf.keras.layers.GlobalAveragePooling1D(),
    tf.keras.layers.Dense(32, activation="relu"),
    tf.keras.layers.Dense(3, activation="softmax")
])

model.build(input_shape=(None, MAX_LEN))
model.summary()

model.compile(
    optimizer=tf.keras.optimizers.Adam(learning_rate=0.001),
    loss="sparse_categorical_crossentropy",
    metrics=["accuracy"]
)

# ---------------- TRAIN ----------------
model.fit(
    X_train,
    y_train,
    validation_data=(X_val, y_val),
    epochs=EPOCHS,
    batch_size=BATCH_SIZE,
    class_weight=class_weights,
    verbose=2
)

# ---------------- SAVE ----------------
model.save(f"{OUTPUT_DIR}/scam_model.keras")

with open(f"{OUTPUT_DIR}/tokenizer.json", "w") as f:
    json.dump(tokenizer.to_json(), f)

print("✅ Training complete")