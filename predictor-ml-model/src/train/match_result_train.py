import logging

import dataset.match_dataset as match_dataset
import service.train_history_service as train_history_service
import service.training_service as training_service
from service.config_service import get_dir_cfg
from service.config_service import get_learning_cfg

logger = logging.getLogger(__name__)

local_dir = get_dir_cfg()['local']
history_file = get_dir_cfg()['country_results_train_history_file']


def train_country(country, receipt):
    learning_cfg = get_learning_cfg("match_result")
    train_history_service.init_history('in progress', learning_cfg)

    training_service.train_match(
        country=country,
        label='outcome',
        label_values=match_dataset.OUTCOMES,
        model_dir="match_result",
        receipt=receipt,
        history_file=history_file)
