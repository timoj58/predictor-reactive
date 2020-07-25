import service.training_service as training_service
import dataset.match_dataset as match_dataset
import service.train_history_service as train_history_service
from service.config_service import get_dir_cfg
from service.config_service import get_learning_cfg
import logging

logger = logging.getLogger(__name__)

local_dir = get_dir_cfg()['local']
history_file = get_dir_cfg()['country_goals_train_history_file']

def train_country(country, receipt):

    learning_cfg = get_learning_cfg("match_goals")
    train_history_service.init_history('in progress',learning_cfg)

    training_service.train_match(
        country=country,
        learning_config=learning_cfg,
        label='goals',
        label_values=match_dataset.GOALS,
        model_dir="match_goals",
        receipt=receipt,
        history_file=history_file)

