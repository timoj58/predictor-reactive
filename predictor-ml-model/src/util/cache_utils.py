import requests
from requests.auth import HTTPDigestAuth
import json

from util.config_utils import get_analysis_cfg
from util.config_utils import get_dir_cfg


COUNTRIES_URL = get_analysis_cfg()['countries_url']
COMPETITIONS_BY_COUNTRY_URL = get_analysis_cfg()['comps_by_country_url']

HEADERS={'groups': 'ROLE_AUTOMATION,', 'username': 'machine-learning'}

def get_countries(url):

    response = requests.get(url, headers=HEADERS)
    values = response.json()

    countries = []

    for value in values:
     countries.append(value['country'])

    return countries

def get_teams(url, country):

    response = requests.get(url+"?country="+country, headers={'groups': 'ROLE_AUTOMATION,', 'username': 'machine-learning'})
    values = response.json()

    teams = []

    for value in values:
        label = value['id']
        if label is not None:
            teams.append(label.encode('unicode_escape'))

    return teams


def get_competitions_per_country(url, country):
    print(url)
    response = requests.get(url+"?country="+country, headers={'groups': 'ROLE_AUTOMATION,', 'username': 'machine-learning'})
    return response.json()['count']

