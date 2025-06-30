import csv
import os
from datetime import datetime

def log_event(action, resource_id, info):
    log_folder = 'assignment-2'
    log_file = os.path.join(log_folder, 'log.csv')

    # 🔧 Create the folder if it doesn't exist
    os.makedirs(log_folder, exist_ok=True)

    timestamp = datetime.utcnow().isoformat()
    with open(log_file, 'a', newline='') as f:
        writer = csv.writer(f)
        writer.writerow([timestamp, action, resource_id, info])
