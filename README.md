# 🛡️ Lakshman Rekha — Senior Protection Against Scams & Fraud (Android)

Lakshman Rekha is an Android application designed to protect Indian senior citizens from phone-based scams, phishing messages, fake links, and social engineering fraud.  
The app acts as a real-time digital guardian, detecting, interrupting, and explaining scam attempts in simple language that seniors understand.

Inspired by real stories of Indian grandparents being targeted daily, Lakshman Rekha focuses on **prevention, interruption, and human-first guidance**, not just detection.

---

## 🎯 Problem Statement

Indian seniors are frequently targeted by:
- Fake customer-care calls
- OTP theft scams
- KYC / bank account blocking messages
- WhatsApp forwards with malicious links
- Emotional manipulation (“relative in distress” scams)

The consequences include:
- Financial loss
- Emotional trauma
- Loss of trust in technology

Lakshman Rekha is built to **actively intervene**, not just warn.

---

## 🧠 Core Philosophy

> “If a senior doesn’t understand what’s happening, the app must step in and protect.”

Design priorities:
- Minimal interaction
- Loud, visible intervention
- Plain-language explanations
- Human fallback (family contact)

---

## 🛡️ Core Features

### 🔍 Scam Detection & Interruption
- **Notification Monitoring**  
  Detects scam keywords in WhatsApp, SMS, and banking notifications.
- **On-Screen Scam Detection (Accessibility)**  
  Reads visible text on screen to detect scam prompts even inside apps.
- **Real-Time Overlay Warnings**  
  Displays a high-visibility warning popup on top of any app.
- **Emergency Intervention (Planned)**  
  Loud alerts even in silent mode to immediately grab attention.

---

### 🧑‍🤝‍🧑 Protection Modes

| Mode | Description |
|-----|------------|
| 🛑 Raksha Mode | Strong protection, aggressive warnings, auto-interrupt |
| 🟡 Lakshman Mode | Balanced protection with explanations |
| 🤝 Saathi Mode | Guidance-only mode for tech-aware seniors |

Modes are selectable from the home screen.

---

## ♿ Accessibility & Senior-First Design

- Large readable text
- High-contrast warnings
- Emoji + color-based risk cues
- Minimal taps required
- Works even if notifications are ignored
- Designed for Android 7+ low to mid-range phones

Planned:
- Voice warnings
- One-tap call to trusted family member

---

## 🌐 Multi-Language Vision

Current:
- English + Hindi (prototype)

Planned:
- Bengali
- Punjabi
- Marathi
- Tamil
- Gujarati
- Auto language detection based on device locale

---

## 📱 Tech Stack

### Android App
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** Service-based (Notification + Accessibility + Overlay)
- **Minimum SDK:** Android 7 (API 24)
- **Target Devices:** Low to mid-range Android phones

### Core Android Components
- NotificationListenerService
- AccessibilityService
- Overlay (SYSTEM_ALERT_WINDOW)
- Background & foreground services

---

## 🧱 Project Structure
```bash
app/
├── java/com/lakshmanrekha/protect
│   ├── ui/                # Compose UI (Home, future screens)
│   ├── services/          # Notification & Overlay services
│   ├── accessibility/     # Screen reader service
│   ├── detection/         # Scam detection logic
│   ├── modes/             # Raksha / Lakshman / Saathi logic
│   ├── languages/         # Multi-language dictionaries (planned)
│   └── utils/             # AppConfig, helpers
│
├── res/
│   ├── xml/               # Accessibility config
│   ├── values/            # Strings, themes
│   └── mipmap/            # App icons
│
└── AndroidManifest.xml
```
---

## 🚦 Current Feature Status

| Feature | Status |
|------|------|
| Home screen & mode selection | ✅ Working |
| Notification scam detection | ✅ Working |
| Overlay warning popup | ✅ Working |
| Accessibility screen scanning | 🟡 Partial |
| Silent-mode emergency alert | ❌ Planned |
| URL scam detection | ❌ Planned |
| Call shield (voice scams) | ❌ Planned |
| Trusted contact system | ❌ Planned |
| Full multi-language UI | ❌ Planned |

---

## 🧪 Example Scenarios

### Scenario 1: Scam Message on WhatsApp
1. Senior receives: “Your KYC is blocked, send OTP now”
2. Notification listener detects scam keywords
3. Red overlay warning appears instantly
4. Senior is alerted before reacting

### Scenario 2: Scam Text Visible on Screen
1. Scammer asks OTP during chat
2. Accessibility service detects “OTP” text
3. Overlay interrupts the flow

---

## 🔐 Privacy & Ethics

- No scraping of personal messages
- No storage of contacts without consent
- No uploading of message content
- On-device detection first
- Privacy-first by design

---

## 🚀 Future Enhancements

### Detection
- Risk scoring instead of keyword-only logic
- Scam pattern learning (offline rules)
- URL homoglyph detection (e.g., pa¥tm, paytм)

### Protection
- Auto call cut during OTP prompts
- Loud alarms overriding silent mode
- Family notification for high-risk events

### UX
- Voice-based warnings
- Elder-friendly UI redesign
- “Teach Me” micro-lessons on scams

---

## 🧪 Testing Plan

- Android 7–13 device testing
- WhatsApp, SMS, Dialer testing
- Realistic scam simulations
- Senior usability testing

---

## 🏁 Deployment Vision

- APK distribution for pilot testing
- NGO / family-group pilots
- No Play Store dependency initially

---

## ⚠️ Disclaimer

Lakshman Rekha provides assistance and warnings, not absolute security.  
Always verify suspicious activity with trusted family members or authorities.

---

## ❤️ Final Note

**Lakshman Rekha is not just an app.**  
It is a digital boundary — a line of protection — for those who need it most.

> *“Technology should protect the vulnerable, not confuse them.”*

🛡️🇮🇳
