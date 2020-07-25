import logging
from datetime import datetime

import model.match_model as match_model
import model.model_utils as model_utils
import service.receipt_service as receipt_service
import service.train_history_service as train_history_service
from service.config_service import get_dir_cfg
from service.config_service import get_learning_cfg

logger = logging.getLogger(__name__)
local_dir = get_dir_cfg()['local']


def create_train_path(country):
    train_path = get_dir_cfg()['train_path']
    train_path = train_path.replace('<key>', country)

    return train_path


def train_match(country, label, label_values, model_dir, receipt, history_file):
    train_path = create_train_path(country)

    start_date = datetime.date(
        train_history_service.get_history(filename=history_file, key=country)['end_year'],
        train_history_service.get_history(filename=history_file, key=country)['end_month'],
        train_history_service.get_history(filename=history_file, key=country)['end_day']
    )
    end_date = datetime.date(
        train_history_service.get_history(filename=history_file, key=country)['end_year'] + 1,
        train_history_service.get_history(filename=history_file, key=country)['end_month'],
        train_history_service.get_history(filename=history_file, key=country)['end_day']
    )

    next_date = datetime.date(
        train_history_service.get_history(filename=history_file, key=country)['end_year'] + 2,
        train_history_service.get_history(filename=history_file, key=country)['end_month'],
        train_history_service.get_history(filename=history_file, key=country)['end_day']
    )

    if end_date > datetime.now():
        end_date = datetime.now()

    learning_cfg = get_learning_cfg(model_dir)

    train_filename = "train-matches" + start_date.strftime("%d-%m-%Y") + '-' + end_date.strftime("%d-%m-%Y") + ".csv"
    evaluate_filename = "train-matches" + + end_date.strftime("%d-%m-%Y") + '-' + next_date.strftime(
        "%d-%m-%Y") + ".csv"
    train_file_path = local_dir + train_path + train_filename
    evaluate_file_path = local_dir + train_path + evaluate_filename

    has_data = model_utils.create_csv(
        url=model_utils.EVENT_MODEL_URL + country,
        filename=train_file_path,
        start_date=start_date,
        end_date=end_date,
        aws_path=train_path)

    if learning_cfg['evaluate']:

        has_test_data = model_utils.create_csv(
            url=model_utils.EVENT_MODEL_URL + country,
            filename=evaluate_file_path,
            start_date=end_date,
            end_date=next_date,
            aws_path=train_path)

        if has_data == True and has_test_data == False:
            evaluate_filename = None

    if has_data:

        train_filename = train_path + train_filename
        if evaluate_filename is not None:
            evaluate_filename = train_path + evaluate_filename

        match_model.create(
            country=country,
            train=True,
            label=label,
            label_values=label_values,
            model_dir=model_dir,
            train_filename=train_filename,
            test_filename=evaluate_filename)
    else:
        logger.info('no data to train')

    # write the history...
    history = train_history_service.create_history(
        'Success - Partial',
        start_date.strftime("%d"),
        start_date.strftime("%m"),
        start_date.strftime("%Y"),
        end_date.strftime("%d"),
        end_date.strftime("%m"),
        end_date.strftime("%Y"));

    train_history_service.add_history(history_file, country, history)

    if receipt is not None:
        receipt_service.put_receipt(receipt_service.TRAIN_RECEIPT_URL, receipt, None)

    history['status'] = "Success - Full"
    train_history_service.add_history(history_file, country, history)
