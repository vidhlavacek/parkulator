import requests
import time
from datetime import datetime, timezone, timedelta
import random

BASE_URL = "http://localhost:8080/location"

lat = random.uniform(45.29, 45.37)
lon = random.uniform(14.37, 14.51)

while True:
    now1 = datetime.now(timezone.utc)
    now2 = now1 + timedelta(seconds=6)

    lat2 = lat + random.uniform(-0.0010, 0.0010)
    lon2 = lon + random.uniform(-0.0010, 0.0010)

    payload = {
        "latitude1": lat,
        "longitude1": lon,
        "timestamp1": now1.isoformat().replace("+00:00", "Z"),
        "latitude2": lat2,
        "longitude2": lon2,
        "timestamp2": now2.isoformat().replace("+00:00", "Z"),
        "accuracy": round(random.uniform(3, 15), 2)
    }

    try:
        response = requests.post(BASE_URL, json=payload)
        print(response.status_code, response.text)
    except Exception as e:
        print("Greška:", e)

    lat = lat2
    lon = lon2

    time.sleep(0.5)