import logging
import model.match_model as match_model
import model.model_utils as model_utils
import pandas as pd
import service.receipt_service as receipt_service
from datetime import datetime
from service.config_service import get_dir_cfg
from service.config_service import get_learning_cfg

logger = logging.getLogger(__name__)
local_dir = get_dir_cfg()['local']


def create_train_path():
    train_path = get_dir_cfg()['train_path']

    return train_path


def train(start, end, label, label_values, model_dir, receipt):
    train_path = create_train_path()

    start_date = datetime.strptime(start, '%d-%m-%Y')
    end_date = datetime.strptime(end, '%d-%m-%Y')

    next_date = end_date + pd.DateOffset(years=1)

    if end_date > datetime.now():
        end_date = datetime.now()

    learning_cfg = get_learning_cfg(model_dir)

    train_filename = "train-players-" + start_date.strftime("%d-%m-%Y") + '-' + end_date.strftime("%d-%m-%Y") + ".csv"
    evaluate_filename = "train-players-" + end_date.strftime("%d-%m-%Y") + '-' + next_date.strftime(
        "%d-%m-%Y") + ".csv"
    train_file_path = local_dir + train_path + train_filename
    evaluate_file_path = local_dir + train_path + evaluate_filename

    has_data = model_utils.create_csv(
        url=model_utils.EVENT_MODEL_URL,
        filename=train_file_path,
        start_date=start_date,
        end_date=end_date,
        aws_path=train_path)

    if learning_cfg['evaluate']:

        has_test_data = model_utils.create_csv(
            url=model_utils.EVENT_MODEL_URL,
            filename=evaluate_file_path,
            start_date=end_date,
            end_date=next_date,
            aws_path=train_path)

        if has_data == True and has_test_data == False:
            evaluate_filename = None
        else:
            logger.info('we can evaluate')

    if has_data:

        train_filename = train_path + train_filename
        if evaluate_filename is not None:
            evaluate_filename = train_path + evaluate_filename

        match_model.create(
            train=True,
            label=label,
            label_values=label_values,
            model_dir=model_dir,
            train_filename=train_filename,
            test_filename=evaluate_filename,
            init=True)
    else:
        logger.info('no data to train')

    if receipt is not None:
        receipt_service.put_receipt(receipt_service.TRAIN_RECEIPT_URL, receipt, None)
