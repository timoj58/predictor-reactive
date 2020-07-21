import requests
from requests.auth import HTTPDigestAuth
import json
from util.config_utils import get_vocab_cfg
from util.file_utils import is_on_file
from util.file_utils import put_aws_file_with_path
from util.file_utils import get_aws_file
from util.file_utils import write_filenames_index_from_filename
from util.file_utils import make_dir
import datetime
from datetime import date, timedelta
import logging
from util.config_utils import get_dir_cfg
import os.path
import os

logger = logging.getLogger(__name__)


ALL_TEAMS_URL = get_vocab_cfg()['team_vocab_url']
PLAYERS_URL = get_vocab_cfg()['player_vocab_url']

local_dir = get_dir_cfg()['local']
TEAMS_FILE = 'team-vocab'
PLAYERS_FILE = 'players-vocab'


def create_vocab(url, filename, player):

  vocab_path = get_dir_cfg()['vocab_path']

  filename =  local_dir+vocab_path+filename+".txt"

  if not is_on_file(filename):

    response = requests.get(url,headers={'groups': 'ROLE_AUTOMATION,', 'username': 'machine-learning'})
    values = response.json()
    logger.info('vocab is not on file')
    make_dir(filename)
    with open(filename, 'w') as f:
            for value in values:
                label = value['id']
                if label is not None:
                    f.write(label)
                    f.write('\n')

    # now put file away.
    head, tail = os.path.split(filename)
    put_aws_file_with_path(vocab_path, tail)
    write_filenames_index_from_filename(filename)

  else:
    head, tail = os.path.split(filename)
    logger.info('get from aws '+tail)
    #need to load the file from aws potentially
    get_aws_file(vocab_path, tail)

  return filename


