import dataset.match_dataset as match_dataset
import service.prediction_service as prediction_service


def predict(data, country, receipt):
    return prediction_service.predict_process(
        data=data,
        country=country,
        label='goals',
        label_values=match_dataset.GOALS,
        model_dir="match_goals",
        receipt=receipt)
