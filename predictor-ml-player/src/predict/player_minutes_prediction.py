from predict.player_predict import predict as predict_process
import dataset.match_dataset as match_dataset


def predict(data, init, receipt):

 predict_process(
    data=data,
    init=init,
    label='minutes',
    label_values=match_dataset.MINUTES,
    model_dir="minutes",
    receipt=receipt)

