import csv
import requests

data = requests.get('http://localhost:8092/players/fantasy',
                    headers={'groups': 'ROLE_AUTOMATION,', 'username': 'machine-learning'})

with open('players-vocab.txt', 'w') as f:
    fnames = ['id']
    writer = csv.DictWriter(f, fieldnames=fnames, extrasaction='ignore')

    for row in data.json():
        writer.writerow(row)
