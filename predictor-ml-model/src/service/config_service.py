import logging
import yaml

logger = logging.getLogger(__name__)

with open("config.yml", 'r') as ymlfile:
    cfg = yaml.load(ymlfile)


def get_dir_cfg():
    return cfg['base']


def get_analysis_cfg():
    return cfg['analysis']


def get_auth_cfg():
    return cfg['auth']


def get_receipt_cfg():
    return cfg['receipt']


def get_learning_cfg(type):
    config_by_types = cfg['learning']
    learning = config_by_types[type]
    default = learning['default']
    response = {}

    for attribute, value in default.items():
        response[attribute] = value

    return response
