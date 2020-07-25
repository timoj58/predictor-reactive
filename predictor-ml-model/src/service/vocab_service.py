
from util.file_utils import get_aws_file
from util.file_utils import make_dir
import logging
from service.config_service import get_dir_cfg
import os.path
import os

logger = logging.getLogger(__name__)

local_dir = get_dir_cfg()['local']
TEAMS_FILE = 'team-vocab'

def create_vocab(filename, country):

    vocab_path = get_dir_cfg()['vocab_path']
    vocab_path = vocab_path.replace('<key>', country)

    filename =  local_dir+vocab_path+filename+".txt"

    head, tail = os.path.split(filename)
    logger.info('get from aws '+tail)
    #need to load the file from aws potentially
    get_aws_file(vocab_path, tail)

    return filename

