from sklearn.metrics import classification_report
import numpy as np

preds = model.predict(X_val)
y_pred = np.argmax(preds, axis=1)

print(classification_report(
    y_val, y_pred,
    target_names=["safe", "suspicious", "scam"]
))