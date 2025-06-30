import csv

with open('assignment-2/log.csv', newline='') as f:
    reader = csv.reader(f)
    rows = list(reader)

print(f"{'Timestamp':<25} {'Action':<20} {'Resource ID':<20} Info")
print('-' * 90)

for row in rows:
    timestamp, action, resource_id, info = row
    print(f"{timestamp:<25} {action:<20} {resource_id:<20} {info}")
