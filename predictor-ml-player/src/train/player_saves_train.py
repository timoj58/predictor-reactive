import logging

import dataset.match_dataset as match_dataset
import service.receipt_service as receipt_service
import service.train_history_service as train_history_service
import service.training_service as training_service
from service.config_service import get_dir_cfg
from service.config_service import get_learning_cfg

logger = logging.getLogger(__name__)

local_dir = get_dir_cfg()['local']
history_file = get_dir_cfg()['player_saves_train_history_file']


def train(receipt):
    learning_cfg = get_learning_cfg("saves")

    train_history_service.init_history('in progress', learning_cfg)

    training_service.train(
        label='saves',
        label_values=match_dataset.SAVES,
        model_dir="saves",
        receipt=receipt,
        history_file=history_file)

    receipt_service.put_receipt(receipt_service.TRAIN_RECEIPT_URL, receipt, None)
