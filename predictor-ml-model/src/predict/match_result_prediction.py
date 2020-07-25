import dataset.match_dataset as match_dataset
import service.prediction_service as prediction_service


def predict(data, country, receipt):
    prediction_service.predict_process(
        data=data,
        country=country,
        label='outcome',
        label_values=match_dataset.OUTCOMES,
        model_dir="match_result",
        receipt=receipt)
