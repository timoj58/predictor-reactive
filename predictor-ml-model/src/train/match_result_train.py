import util.model_utils as model_utils
import util.cache_utils as cache_utils
import dataset.match_dataset as match_dataset
import util.receipt_utils as receipt_utils
import util.training_utils as training_utils
import util.train_history_utils as train_history_utils
from util.config_utils import get_dir_cfg
from util.config_utils import get_learning_cfg
import logging

logger = logging.getLogger(__name__)


local_dir = get_dir_cfg()['local']
history_file = get_dir_cfg()['country_results_train_history_file']

def train_country(country, receipt):

   learning_cfg = get_learning_cfg(country, "match_result")

   previous_vocab_date=train_history_utils.get_previous_vocab_date(country)
   history = train_history_utils.init_history('in progress',learning_cfg)

   training_utils.train_match(
                        country=country,
                        data_range=training_utils.create_data_range(learning_cfg=learning_cfg, history_file=history_file, country=country),
                        label='outcome',
                        label_values=match_dataset.OUTCOMES,
                        model_dir="match_result",
                        train_path=training_utils.create_train_path(country),
                        receipt=receipt,
                        history=history,
                        previous_vocab_date=previous_vocab_date,
                        history_file=history_file)
