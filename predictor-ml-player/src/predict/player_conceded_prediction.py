from service.predict_service import predict as predict_process

import dataset.match_dataset as match_dataset


def predict(data, init, receipt):
    predict_process(
        data=data,
        init=init,
        label='conceded',
        label_values=match_dataset.CONCEDED,
        model_dir="conceded",
        receipt=receipt)
