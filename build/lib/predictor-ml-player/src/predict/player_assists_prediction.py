from service.predict_service import predict as predict_process

import dataset.match_dataset as match_dataset


def predict(data, init, receipt):
    predict_process(
        data=data,
        init=init,
        label='assists',
        label_values=match_dataset.SCORE,
        model_dir="assists",
        receipt=receipt)
