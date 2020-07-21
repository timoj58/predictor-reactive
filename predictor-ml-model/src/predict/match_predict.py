import util.receipt_utils as receipt_utils
import model.match_model as match_model
from util.config_utils import get_dir_cfg
import util.model_utils as model_utils
import logging

local_dir = get_dir_cfg()['local']
logger = logging.getLogger(__name__)


def predict(data, country, label, label_values,  model_dir, previous_vocab_date, receipt):

#def create(type, country, train, label, label_values, model_dir, train_filename, test_filename, outcome, previous_vocab_date):
    # there is no guarantee the predict is on same day as the train.  so we need the history
    classifier =  match_model.create(
                   country=country,
                   train=False,
                   label=label,
                   label_values=label_values,
                   model_dir=model_dir,
                   train_filename='',
                   test_filename='',
                   previous_vocab_date=previous_vocab_date)

    home = []
    away = []
    outcomes = []

    # Generate predictions from the model
    home.append(data['home'])
    away.append(data['away'])

    #print(data)


    expected = [0]
    predict_x = {
        'home': home,
        'away': away
    }

    response = model_utils.predict(
        classifier=classifier,
        predict_x=predict_x,
        label_values=label_values)


    match_model.tidy_up(local_dir+'/models/'+model_dir+'/'+country,None, None, None)
    receipt_utils.put_receipt(receipt_utils.PREDICT_RECEIPT_URL, receipt,response)
