import json
import requests
import logging

from service.config_service import get_receipt_cfg

logger = logging.getLogger(__name__)

TRAIN_RECEIPT_URL = get_receipt_cfg()['train_receipt_url']
PREDICT_RECEIPT_URL = get_receipt_cfg()['predict_receipt_url']


def put_receipt(url, receipt, result):
    if result is not None:
        logger.info('sending result '+url)
        headers = {'Content-type': 'application/json', 'Accept': 'text/plain', 'groups': 'ROLE_AUTOMATION,',
                   'username': 'machine-learning'}
        requests.put(url, data=json.dumps(result), headers=headers)
    else:
        requests.put(url + "?id=" + receipt, data={},
                     headers={'groups': 'ROLE_AUTOMATION,', 'username': 'machine-learning'})
