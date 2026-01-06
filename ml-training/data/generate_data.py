import random
import csv
import os

OUTPUT_DIR = "../data"
os.makedirs(OUTPUT_DIR, exist_ok=True)

BANKS = ["SBI", "HDFC", "ICICI", "AXIS", "PNB"]
TELCOS = ["Jio", "Airtel", "Vi", "BSNL"]
DEPTS = ["Income Tax", "Electricity Board", "Police", "Telecom Dept"]
RELATIONS = ["son", "daughter", "brother", "relative"]
AMOUNTS = ["5000", "10000", "25000", "50000"]

# ---------------- TEMPLATES ----------------

SCAM_TEXTS = [
    "Your {bank} account is blocked. Share OTP immediately.",
    "Dear customer, KYC pending. Click link now.",
    "I am your {relation}, met with accident. Send money urgently.",
    "{dept} calling regarding illegal activity.",
    "SIM verification failed. Press 1 now.",
    "Congratulations you won lottery of Rs {amount}.",
    "Your ATM card expired today. Confirm details.",
    "Refund pending. Share OTP to receive money."
]

SUSPICIOUS_TEXTS = [
    "Courier delivery failed. Confirm address.",
    "Electricity bill unpaid. Pay today.",
    "Income tax notice pending.",
    "Press 9 to talk to executive.",
    "Your account activity looks unusual."
]

SAFE_TEXTS = [
    "Recharge successful.",
    "Salary credited to your account.",
    "Appointment confirmed with doctor.",
    "Loan EMI reminder.",
    "Thank you for using our service."
]

URL_SCAM = [
    "http://{bank}-kyc-update.xyz",
    "http://{bank}-verify-login.com",
    "http://{dept}-refund-fast.in"
]

URL_SAFE = [
    "https://www.{bank}.co.in",
    "https://www.irctc.co.in",
    "https://www.uidai.gov.in"
]

# ---------------- HELPERS ----------------

def render(template):
    return template.format(
        bank=random.choice(BANKS),
        dept=random.choice(DEPTS),
        relation=random.choice(RELATIONS),
        amount=random.choice(AMOUNTS)
    )

def generate_rows(n):
    rows = []
    for _ in range(n):
        r = random.random()
        if r < 0.4:
            rows.append((render(random.choice(SCAM_TEXTS)), "scam"))
        elif r < 0.7:
            rows.append((render(random.choice(SUSPICIOUS_TEXTS)), "suspicious"))
        else:
            rows.append((render(random.choice(SAFE_TEXTS)), "safe"))
    return rows

def generate_urls(n):
    rows = []
    for _ in range(n):
        if random.random() < 0.6:
            rows.append((render(random.choice(URL_SCAM)), "scam"))
        else:
            rows.append((render(random.choice(URL_SAFE)), "safe"))
    return rows

# ---------------- WRITE FILES ----------------

def write_csv(name, rows):
    with open(os.path.join(OUTPUT_DIR, name), "w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(["text", "label"])
        writer.writerows(rows)

write_csv("sms.csv", generate_rows(500))
write_csv("whatsapp.csv", generate_rows(500))
write_csv("calls.csv", generate_rows(500))
write_csv("urls.csv", generate_urls(500))

print("✅ Generated 2000+ samples successfully")