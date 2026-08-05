# 🛡️ Lakshman Rekha — Real-Time Scam Protection for Calls, Messages & Digital Actions (Android)

**Lakshman Rekha** is an Android application that protects users — especially seniors and vulnerable individuals — from **phone scams, OTP fraud, UPI manipulation, phishing messages, and social-engineering attacks**.

Unlike traditional spam detectors, Lakshman Rekha works at the **moment of decision**.  
It detects *intent, pressure, and risky user actions in real time* and intervenes **before damage happens**.

Inspired by real scam patterns seen daily in India, Lakshman Rekha is designed around **prevention, interruption, and education**, not post-fraud reporting.



<img width="1683" height="934" alt="image" src="https://github.com/user-attachments/assets/1ef38a78-bf40-47a8-9ca4-2154b64a8ecb" />


---

## 🎯 Problem Statement

Modern scams do not rely on ignorance.  
They rely on **timing, fear, urgency, and manipulation**.

Common attack patterns include:
- Fake customer-care & police calls
- OTP and UPI payment pressure during calls
- “Account blocked” or “legal case” threats
- Links sent during live conversations
- Remote app installation coercion

Why existing tools fail:
- Truecaller only labels numbers
- Keyword filters miss context
- Malware scanners ignore social engineering

**Scams are not single events. They are sequences.**

Lakshman Rekha is built to detect and stop those sequences.

---

## 🧠 Core Insight

> “The most dangerous moment is not when a message arrives —  
> it’s when the user is being pushed to act.”

Lakshman Rekha does **not** ask:
> “Is this message a scam?”

It asks:
> **“Is someone pushing the user to take a dangerous action right now?”**

That shift defines the entire system.

---

## 🧠 Detection Philosophy (Hybrid System)

Lakshman Rekha uses a **hybrid detection engine**:

- ✅ Rule-based real-time signals (behavior & context)
- ✅ On-device ML inference (intent & semantic understanding)
- ❌ No single keyword triggers action
- ❌ ML never directly blocks the user

All outputs flow through a **deterministic risk-scoring layer**.

---

## 🛡️ Core Features

### 🔍 Real-Time Scam Detection
- **Notification Monitoring**  
  Reads notifications from SMS, WhatsApp, and banking apps.
- **On-Screen Text Analysis (Accessibility)**  
  Detects scam prompts *inside apps*, not just notifications.
- **Context Awareness**
  - Call ongoing
  - Unknown caller
  - UPI opened during call
  - OTP visible
  - Rapid app switching

Each signal contributes weighted risk — no binary decisions.

---

### ⚠️ Risk Scoring Engine
- Score range: **0–100**
- Signals combined deterministically:
  - Call context
  - User behavior
  - ML confidence
- Threat mapping:

| Score | Threat Level |
|------|-------------|
| 0–20 | SAFE |
| 21–45 | CAUTION |
| 46–70 | RISKY |
| 71–100 | DANGEROUS |

This minimizes false positives and avoids unnecessary panic.

---

### 🧑‍🤝‍🧑 Protection Modes (User-Selectable)

| Mode | Designed For | Behavior |
|-----|-------------|---------|
| 🤝 **Saathi Mode** | Tech-aware users | Silent monitoring, logs only |
| 🟡 **Lakshman Mode** | Average users | Visual warnings + explanations |
| 🛑 **Raksha Mode** | Seniors & vulnerable users | Full intervention + SOS |

Modes control **response behavior**, not detection logic.

---

### 🚨 Real-Time Intervention
- **Overlay Warnings (SYSTEM_ALERT_WINDOW)**  
  High-visibility banners over *any app*.
- **Emergency Overlays**  
  Triggered for dangerous scenarios in Raksha Mode.
- **Senior-Friendly Dismissal**  
  Tap anywhere to dismiss.

---

### 👨‍👩‍👧 Family & Emergency Safety
- **Trusted Contacts System**
  - Family members added during onboarding
- **One-Tap Actions**
  - Call trusted contact
  - Block source
  - Report scam
- **SOS Trigger**
  - Volume-button sequence
  - Emergency SMS auto-sent to trusted contacts
- **Post-Call Safety Summary**
  - Clear explanation of what happened
  - English + Hindi guidance

---

## 🧠 Machine Learning (On-Device)

### Model Characteristics
- **Framework:** TensorFlow Lite
- **Runs fully offline**
- **Latency:** ~15 ms on-device for short text windows
- **Input:** Normalized text tokens (64 tokens)
- **No cloud, no audio, no storage**

### ML Outputs (Signals Only)
- Scam probability
- Severity (1–5)
- Scam stage (Lure / Action / Threat)
- Requested intent
- Scam family classification
- Binary flags:
  - OTP
  - UPI
  - URL
  - QR payment flow
  - Callback number
  - Threat language
  - Urgency

> ML provides signals only.  
> Final decisions are rule-based and deterministic.

### August 2026 ML Upgrade

Lakshman Rekha now uses a rebuilt multi-task on-device ML pipeline with a shared
normalization/tokenization contract between Python training and Android inference.

- **Training corpus:** expanded from `296` seed samples to `30,296` labeled rows
- **Model architecture:** multi-scale convolutional text encoder with shared trunk + 12 heads
- **Primary heads:** `is_scam`, `severity`, `stage`, `action`, `scam_type`
- **Binary heads:** `has_otp`, `has_upi`, `has_url`, `has_qr`, `has_phone`, `has_threat`, `has_urgency`
- **Artifact contract:** `tokenizer.json` + `model_metadata.json` + `scam_signal.tflite`
- **Evaluation:** separate train / validation / holdout split with exported reports

### Holdout Snapshot (August 5, 2026)

- **Scam detection macro F1:** `1.00`
- **Severity macro F1:** `0.9935`
- **Stage macro F1:** `0.9934`
- **Action macro F1:** `0.9743`
- **Scam type macro F1:** `0.9346`
- **Flag accuracy range:** `96.2%` to `99.9%`

### Training Pipeline

The ML stack under [`ml-training/model`](/Users/rohanc/AndroidStudioProjects/LakshmanRekha/ml-training/model)
now includes:

- `expand_dataset.py` for deterministic large-scale dataset augmentation
- `common.py` for shared normalization, vocabulary, metadata, and split utilities
- `train.py` for model training and metric export
- `evaluate.py` for holdout evaluation reports
- `export_tflite.py` for Android-safe artifact export

Generated outputs are written to
[`ml-training/output`](/Users/rohanc/AndroidStudioProjects/LakshmanRekha/ml-training/output),
and the runtime assets used by the app are synced into
[`app/src/main/assets`](/Users/rohanc/AndroidStudioProjects/LakshmanRekha/app/src/main/assets).

---

## ♿ Accessibility & Senior-First Design

- Large readable text
- High-contrast overlays
- Color + emoji risk cues
- Minimal interaction required
- Works even if notifications are ignored

---

## 🌐 Language Support

Current:
- English
- Hindi

Planned:
- Regional Indian languages
- Device-locale based auto-selection

---

## 📱 Tech Stack

### Android
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** Service-based, decoupled layers
- **Minimum SDK:** Android 7 (API 24)

### Core Android Components
- AccessibilityService
- NotificationListenerService
- Overlay windows
- Foreground & background services
- SMS Manager
- Text-to-Speech
- Volume-key event hooks

### ML
- TensorFlow Lite
- Fully on-device inference

---

## 🧱 Architecture Overview
Signals → ML + Rules → Risk Score → Mode Logic → Action

Decoupled layers:
- Ingestion
- Inference
- Decision
- Action

---

## 🚦 Feature Status

| Feature | Status |
|------|------|
| Hybrid risk scoring | ✅ |
| On-device ML | ✅ |
| Overlay warnings | ✅ |
| Trusted contacts & SOS | ✅ |
| Multi-mode protection | ✅ |
| Call audio analysis | ❌ Planned |
| Smartwatch alerts | ❌ Planned |

---

## 🔐 Privacy & Ethics

- No call recording
- No message storage
- No cloud uploads
- Memory purged after sessions
- DPDP-compliant by design

---

## 🚀 Future Enhancements
- Regional language ML models
- One-tap 1930 cybercrime reporting
- Smartwatch haptic alerts
- Federated learning without raw data sharing

---

## 🏁 Final Note

**Lakshman Rekha is not just an app.**

It is a **real-time digital boundary** —  
drawn exactly at the moment when a user is most vulnerable.

Just like the original Lakshman Rekha,  
it warns you *before* you cross the line.

🛡️🇮🇳
