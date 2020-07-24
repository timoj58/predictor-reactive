import util.model_utils as model_utils
import util.cache_utils as cache_utils
import util.receipt_utils as receipt_utils
import dataset.match_dataset as match_dataset
import service.training_service as training_service
import service.train_history_service as train_history_service
from util.config_utils import get_dir_cfg
from util.config_utils import get_learning_cfg
import logging

logger = logging.getLogger(__name__)


local_dir = get_dir_cfg()['local']
history_file = get_dir_cfg()['player_goals_train_history_file']


def train(receipt):

    logger.info('started train')

    learning_cfg = get_learning_cfg("goals")

    train_history_service.init_history('in progress',learning_cfg)


    training_service.train(
        #data_range=training_utils.create_data_range(learning_cfg=learning_cfg, history_file=history_file),
        label='goals',
        label_values=match_dataset.SCORE,
        model_dir="goals",
        #train_path=training_utils.create_train_path(),
        receipt=receipt,
        #history=history,
        history_file=history_file)

    receipt_utils.put_receipt(receipt_utils.TRAIN_RECEIPT_URL, receipt, None)
