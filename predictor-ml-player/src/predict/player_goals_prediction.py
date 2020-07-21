from predict.player_predict import predict as predict_process
import dataset.match_dataset as match_dataset
import util.train_history_utils as train_history_utils

def predict(data, init, receipt):

    predict_process(
        data=data,
        init=init,
        label='goals',
        label_values=match_dataset.SCORE,
        model_dir="goals",
        receipt=receipt)
