import requests
from requests.auth import HTTPDigestAuth
import json

from util.config_utils import get_analysis_cfg
from util.config_utils import get_dir_cfg


def get_teams(url):

    response = requests.get(url, headers={'groups': 'ROLE_AUTOMATION,', 'username': 'machine-learning'})
    values = response.json()

    teams = []

    for value in values:
        label = value['id']
        if label is not None:
            teams.append(label.encode('unicode_escape'))

    return teams

