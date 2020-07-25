from predict.player_predict import predict as predict_process
import dataset.match_dataset as match_dataset


def predict(data, init, receipt):


    predict_process(
        data=data,
        init=init,
        label='red',
        label_values=match_dataset.CARDS,
        model_dir="red",
        receipt=receipt)

