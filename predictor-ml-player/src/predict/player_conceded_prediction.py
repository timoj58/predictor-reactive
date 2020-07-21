from predict.player_predict import predict as predict_process
import dataset.match_dataset as match_dataset
from util.config_utils import get_dir_cfg
import util.train_history_utils as train_history_utils


def predict(data, init, receipt):

 predict_process(
    data=data,
    init=init,
    label='conceded',
    label_values=match_dataset.CONCEDED,
    model_dir="conceded",
    receipt=receipt)

