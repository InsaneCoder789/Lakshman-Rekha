from __future__ import annotations

import json
import random
import re
from collections import Counter

import pandas as pd

from common import (
    AUGMENTED_DATA_PATH,
    DATASET_REPORT_PATH,
    TARGET_NEW_ROWS,
    load_base_dataframe,
    seed_everything,
)


URL_DOMAINS_RISKY = [
    "verify-now.co",
    "secure-link.in",
    "kyc-update.in",
    "bill-alert.in",
    "gpay-help.in",
    "pay-safe.co",
    "courier-check.in",
    "customer-care.live",
    "otp-verify.in",
    "upi-support.co",
]

URL_DOMAINS_SAFE = [
    "amazon.in",
    "dtdc.in",
    "sbi.co.in",
    "hdfcbank.com",
    "icicibank.com",
    "airtel.in",
    "jio.com",
    "indiapost.gov.in",
]

UPI_HANDLES = ["oksbi", "okhdfcbank", "okicici", "okaxis", "ybl", "ibl", "axl", "paytm"]
BANKS = ["SBI", "HDFC", "ICICI", "Axis", "PNB", "Canara", "Kotak", "Union Bank"]
TELCOS = ["Airtel", "Jio", "Vi", "BSNL"]
COURIERS = ["Blue Dart", "DTDC", "Delhivery", "India Post", "Ekart", "XpressBees"]
MARKETPLACES = ["OLX", "Quikr", "Facebook Marketplace", "Meesho", "Telegram Resale"]
AUTHORITIES = ["Cyber Cell", "Police Department", "CBI", "Income Tax", "TRAI", "ED"]
REMOTE_APPS = ["AnyDesk", "TeamViewer", "QuickSupport", "RustDesk"]
UTILITIES = ["BSES", "Tata Power", "BESCOM", "MSEB", "BWSSB", "Jal Board"]
JOB_PORTALS = ["Naukri", "Indeed", "LinkedIn", "Apna Jobs", "WorkIndia"]
LOTTERY_BRANDS = ["Mega Bumper Draw", "Festive Bonanza", "Lucky Winner Program", "Reward Vault"]
RELATIONS = ["Mom", "Dad", "Mummy", "Papa", "Bhai", "Didi", "Uncle", "Aunty"]
NAMES = ["Rohit", "Aman", "Neha", "Priya", "Kiran", "Ravi", "Sonal", "Arjun"]
SHORTENERS = ["bit.ly", "tinyurl.com", "rebrand.ly", "cutt.ly"]

EN_URGENCY = [
    "within 10 minutes",
    "today itself",
    "immediately",
    "right now",
    "before service suspension",
    "before midnight",
]

HI_URGENCY = [
    "abhi turant",
    "aaj hi",
    "10 minute ke andar",
    "jaldi karo",
    "service band hone se pehle",
]

THREATS = [
    "failure will lead to account freeze",
    "non-compliance may trigger legal action",
    "service will be disconnected",
    "your account will be blocked",
    "police verification will start",
]

SAFE_MESSAGES = [
    "Please use the official app for details.",
    "No action is required if this was expected.",
    "For help, call the official helpline printed on your card.",
    "Track the update only from the company app or website.",
]

SEED_PHRASE_SWAPS = {
    "pending": ["awaiting", "in process", "stuck", "queued"],
    "share": ["send", "forward", "provide"],
    "confirm": ["verify", "reconfirm", "validate"],
    "update": ["refresh", "complete", "submit"],
    "today": ["today", "right away", "this evening"],
    "urgent": ["urgent", "important", "high priority"],
}


def random_amount(rng: random.Random, severity: int) -> str:
    ranges = {
        1: (99, 2000),
        2: (200, 5000),
        3: (500, 15000),
        4: (1000, 40000),
        5: (5000, 150000),
    }
    low, high = ranges.get(int(severity), (500, 10000))
    return f"₹{rng.randint(low, high):,}".replace(",", "")


def random_url(rng: random.Random, safe: bool) -> str:
    domain = rng.choice(URL_DOMAINS_SAFE if safe else URL_DOMAINS_RISKY)
    path = "".join(rng.choice("abcdefghijklmnopqrstuvwxyz0123456789") for _ in range(rng.randint(7, 12)))
    return f"https://{domain}/{path}"


def random_upi(rng: random.Random) -> str:
    handle = rng.choice(UPI_HANDLES)
    prefix = "".join(rng.choice("abcdefghijklmnopqrstuvwxyz") for _ in range(rng.randint(5, 8)))
    return f"{prefix}@{handle}"


def random_phone(rng: random.Random) -> str:
    return f"+91-{rng.randint(70000, 99999)}-{rng.randint(10000, 99999)}"


def replace_seed_phrases(text: str, rng: random.Random) -> str:
    updated = text
    for base, variants in SEED_PHRASE_SWAPS.items():
        if base.lower() in updated.lower() and rng.random() < 0.45:
            replacement = rng.choice(variants)
            updated = re.sub(base, replacement, updated, flags=re.IGNORECASE)
    return updated


def apply_language_flavor(text: str, language: str, rng: random.Random) -> str:
    if language != "Hinglish":
        return text

    replacements = {
        "please": "plz",
        "immediately": "abhi turant",
        "today": "aaj",
        "now": "abhi",
        "share": "bhejo",
        "update": "update karo",
        "call": "call karo",
        "official": "official",
    }

    updated = text
    for source, target in replacements.items():
        if source in updated.lower() and rng.random() < 0.5:
            updated = re.sub(source, target, updated, flags=re.IGNORECASE)

    if rng.random() < 0.35:
        updated = f"{updated} {rng.choice(['jaldi', 'haan ji', 'samjhe?', 'thik hai'])}"
    return updated


def scam_entity(scam_type: str, rng: random.Random) -> str:
    if "Telecom" in scam_type:
        return rng.choice(TELCOS)
    if "Delivery" in scam_type:
        return rng.choice(COURIERS)
    if "Tech Support" in scam_type:
        return rng.choice(REMOTE_APPS)
    if "Loan/Investment" in scam_type:
        return rng.choice(["QuickCash", "Gold Return Desk", "Secure Invest", "Profit Desk"])
    if "Marketplace" in scam_type:
        return rng.choice(MARKETPLACES)
    if "Bank" in scam_type:
        return rng.choice(BANKS)
    if "Lottery" in scam_type:
        return rng.choice(LOTTERY_BRANDS)
    if "Job Offer" in scam_type:
        return rng.choice(JOB_PORTALS)
    if "Government" in scam_type:
        return rng.choice(AUTHORITIES)
    if "Family/Friend" in scam_type:
        return rng.choice(RELATIONS)
    if "Legitimate: Bank Alert" in scam_type:
        return rng.choice(BANKS)
    if "Legitimate: Delivery Update" in scam_type:
        return rng.choice(COURIERS)
    if "Legitimate: Service Notification" in scam_type:
        return rng.choice(TELCOS + UTILITIES)
    return rng.choice(["Service Desk", "Support Team", "Official Notice"])


def scam_prompt(row: dict, rng: random.Random) -> str:
    action = row["requested_action"]
    urgency = rng.choice(HI_URGENCY if row["language"] == "Hinglish" else EN_URGENCY)
    amount = random_amount(rng, int(row["severity"]))
    if action == "Share OTP":
        return f"share the OTP sent to your phone {urgency}"
    if action == "Pay via UPI":
        return f"pay {amount} via UPI {random_upi(rng)} {urgency}"
    if action == "Install remote-support app":
        return f"install {rng.choice(REMOTE_APPS)} and share the session code {urgency}"
    if action == "Share ID docs":
        return f"send Aadhaar and PAN photo on WhatsApp {random_phone(rng)} {urgency}"
    if action == "Call number":
        return f"call support at {random_phone(rng)} {urgency}"
    if action == "Visit official app/website":
        return f"open the official portal {random_url(rng, safe=True)} for details"
    if action == "Scan QR / approve collect request":
        return f"scan QR or approve collect request for {amount} {urgency}"
    if action == "No action (info only)":
        return rng.choice(SAFE_MESSAGES)
    return f"open {random_url(rng, safe=False)} {urgency}"


def mutate_entities(text: str, row: dict, rng: random.Random) -> str:
    safe = row["is_scam"] == "0"
    mutated = replace_seed_phrases(text, rng)
    mutated = re.sub(r"https?://\S+|www\.\S+", random_url(rng, safe=safe), mutated)
    mutated = re.sub(r"\b[a-z0-9._-]{2,}@[a-z]{2,}\b", random_upi(rng), mutated, flags=re.IGNORECASE)
    mutated = re.sub(r"(?<!\w)(?:\+91[-\s]?)?\d[\d\-\s]{7,}\d(?!\w)", random_phone(rng), mutated)
    mutated = re.sub(r"₹\s?\d[\d,]*", random_amount(rng, int(row["severity"])), mutated)
    entity = scam_entity(row["scam_type"], rng)
    known_entities = BANKS + TELCOS + COURIERS + AUTHORITIES + REMOTE_APPS + UTILITIES + JOB_PORTALS + LOTTERY_BRANDS
    for candidate in known_entities:
        if candidate.lower() in mutated.lower():
            mutated = re.sub(re.escape(candidate), entity, mutated, flags=re.IGNORECASE)
            break
    return mutated


def render_structured_text(row: dict, rng: random.Random) -> str:
    entity = scam_entity(row["scam_type"], rng)
    action_line = scam_prompt(row, rng)
    safe = row["is_scam"] == "0"
    threat_line = rng.choice(THREATS) if row["has_threat"] == "1" else ""
    urgent_line = rng.choice(HI_URGENCY if row["language"] == "Hinglish" else EN_URGENCY) if row["has_urgency"] == "1" else ""

    if safe:
        if row["modality"] == "WhatsApp":
            text = f"{entity}: Update for your account or delivery.\nUser: Thanks, I will check the official app.\n{entity}: {rng.choice(SAFE_MESSAGES)}"
        elif row["modality"] == "AudioTranscript":
            text = f"[Automated Voice] {entity} update. {rng.choice(SAFE_MESSAGES)}"
        elif row["modality"] == "Call":
            text = f"Agent: {entity} support update for your account. Senior: Okay. Agent: {rng.choice(SAFE_MESSAGES)}"
        else:
            text = f"{entity}: {rng.choice(SAFE_MESSAGES)}"
    else:
        if row["modality"] == "WhatsApp":
            text = (
                f"Scammer: {entity} notice. {action_line}.\n"
                f"Senior: I want to confirm first.\n"
                f"Scammer: Do it now. {urgent_line or 'This is urgent.'}"
            )
        elif row["modality"] == "AudioTranscript":
            prefix = "[Automated Voice]" if rng.random() < 0.5 else "[Voicemail]"
            text = f"{prefix} {entity} alert. {action_line}."
        elif row["modality"] == "Call":
            text = (
                f"Agent: This is from {entity}. {action_line}. "
                f"Senior: Why so urgent? Agent: {threat_line or 'Delay will cause account issues.'}"
            )
        else:
            text = f"{entity}: {action_line}. {threat_line} {urgent_line}".strip()

    return apply_language_flavor(text, row["language"], rng)


def generate_variant(row: dict, rng: random.Random, variant_idx: int) -> dict:
    if rng.random() < 0.55:
        text = mutate_entities(row["text"], row, rng)
        if row["is_scam"] == "1" and rng.random() < 0.35:
            text = f"{text} {scam_prompt(row, rng)}."
        text = apply_language_flavor(text, row["language"], rng)
    else:
        text = render_structured_text(row, rng)

    sample = dict(row)
    sample["sample_id"] = f"AUG{variant_idx:05d}"
    sample["timestamp"] = ""
    sample["text"] = text.strip()
    sample["source_origin"] = row["sample_id"]
    sample["augmentation_id"] = variant_idx
    return sample


def build_sampling_pool(df: pd.DataFrame, label_value: str, target_count: int, rng: random.Random) -> list[dict]:
    subset = df[df["is_scam"].astype(str) == label_value].copy()
    bucket = (
        subset["requested_action"].astype(str)
        + "|"
        + subset["severity"].astype(str)
        + "|"
        + subset["language"].astype(str)
        + "|"
        + subset["modality"].astype(str)
    )
    counts = Counter(bucket)
    subset["weight"] = bucket.map(lambda value: 1.0 / counts[value])
    weights = subset["weight"].to_list()
    records = subset.to_dict(orient="records")
    return rng.choices(records, weights=weights, k=target_count)


def generate_augmented_dataset(target_new_rows: int = TARGET_NEW_ROWS) -> pd.DataFrame:
    seed_everything()
    rng = random.Random(42)
    base_df = load_base_dataframe()

    scam_target = int(target_new_rows * 0.6)
    legit_target = target_new_rows - scam_target
    sampled_rows = build_sampling_pool(base_df, "1", scam_target, rng)
    sampled_rows.extend(build_sampling_pool(base_df, "0", legit_target, rng))
    rng.shuffle(sampled_rows)

    seen = set((row["text"], row["sample_id"]) for row in base_df.to_dict(orient="records"))
    generated_rows = []
    cursor = 0

    while len(generated_rows) < target_new_rows:
        seed_row = sampled_rows[cursor % len(sampled_rows)]
        candidate = generate_variant(seed_row, rng, len(generated_rows) + 1)
        fingerprint = (
            candidate["text"],
            candidate["is_scam"],
            candidate["scam_type"],
            candidate["requested_action"],
        )
        if fingerprint not in seen:
            seen.add(fingerprint)
            generated_rows.append(candidate)
        cursor += 1

    augmented_df = pd.DataFrame(generated_rows)
    combined_df = pd.concat([base_df, augmented_df], ignore_index=True)

    AUGMENTED_DATA_PATH.parent.mkdir(parents=True, exist_ok=True)
    combined_df.to_csv(AUGMENTED_DATA_PATH, index=False)

    report = {
        "base_rows": int(len(base_df)),
        "generated_rows": int(len(augmented_df)),
        "combined_rows": int(len(combined_df)),
        "is_scam_distribution": combined_df["is_scam"].astype(str).value_counts().to_dict(),
        "modality_distribution": combined_df["modality"].astype(str).value_counts().to_dict(),
        "language_distribution": combined_df["language"].astype(str).value_counts().to_dict(),
        "severity_distribution": combined_df["severity"].astype(str).value_counts().to_dict(),
        "action_distribution": combined_df["requested_action"].astype(str).value_counts().to_dict(),
    }
    DATASET_REPORT_PATH.write_text(json.dumps(report, indent=2))
    return combined_df


def ensure_augmented_dataset() -> pd.DataFrame:
    if AUGMENTED_DATA_PATH.exists():
        return pd.read_csv(AUGMENTED_DATA_PATH)
    return generate_augmented_dataset()


if __name__ == "__main__":
    df = generate_augmented_dataset()
    print(f"Generated combined dataset with {len(df)} rows at {AUGMENTED_DATA_PATH}")
