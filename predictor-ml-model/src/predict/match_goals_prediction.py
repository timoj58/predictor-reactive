from predict.match_predict import predict as predict_process
import dataset.match_dataset as match_dataset
import util.train_history_utils as train_history_utils

def predict(data, country, receipt):

    previous_vocab_date=train_history_utils.get_previous_vocab_date(country)

    return predict_process(
        data=data,
        country=country,
        label='goals',
        label_values=match_dataset.GOALS,
        model_dir="match_goals",
        previous_vocab_date=previous_vocab_date,
        receipt=receipt)
