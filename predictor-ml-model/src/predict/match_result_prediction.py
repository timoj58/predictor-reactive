from predict.match_predict import predict as predict_process
import dataset.match_dataset as match_dataset
from util.config_utils import get_dir_cfg
import util.train_history_utils as train_history_utils


def predict(data, country, receipt):

 previous_vocab_date=train_history_utils.get_previous_vocab_date(country)

 predict_process(
    data=data,
    country=country,
    label='outcome',
    label_values=match_dataset.OUTCOMES,
    model_dir="match_result",
    previous_vocab_date=previous_vocab_date,
    receipt=receipt)

