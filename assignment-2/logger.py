import csv
from datetime import datetime

def log_event(action, resource_id, info=None):
    timestamp = datetime.utcnow().isoformat() + 'Z'
    with open('assignment-2/log.csv', 'a', newline='') as f:
        writer = csv.writer(f)
        writer.writerow([timestamp, action, resource_id, info])
