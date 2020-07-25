import service.receipt_service as receipt_service
import util.model_utils as model_utils
import model.match_model as match_model
import util.train_history_utils as train_history_utils


from service.config_service import get_dir_cfg
from service.config_service import get_learning_cfg

import logging

logger = logging.getLogger(__name__)
local_dir = get_dir_cfg()['local']


def create_train_path():
    train_path = get_dir_cfg()['train_path']

    return train_path

def create_data_range(learning_cfg, history_file):

    data_range = model_utils.real_time_range(
        start_day=train_history_utils.get_history(filename=history_file,key='default')['end_day'],
        start_month=train_history_utils.get_history(filename=history_file,key='default')['end_month'],
        start_year=train_history_utils.get_history(filename=history_file,key='default')['end_year'])

    return data_range

def get_range_details(range):

    range = range[1:] #remove first delim
    dates = range.split('/')
    start_date = dates[0]
    end_date = dates[1]
    start = start_date.split('-')
    end = end_date.split('-')

    return int(start[0]), int(start[1]), int(start[2]), int(end[0]), int(end[1]), int(end[2])


def get_next_in_range(range, data):

    next = False

    for val in range:

        if next:
            return val

        if val == data:
            next = True

    return data

def train(data_range, label, label_values, model_dir, train_path, receipt, history, history_file):

    for data in data_range:


        learning_cfg = get_learning_cfg(model_dir)


        train_filename = "train-players"+data.replace('/','-')+".csv"
        evaluate_filename = "train-players"+get_next_in_range(data_range, data).replace('/','-')+".csv"
        train_file_path = local_dir+train_path+train_filename
        evaluate_file_path = local_dir+train_path+evaluate_filename


        has_data = model_utils.create_csv(
            url=model_utils.EVENT_MODEL_URL,
            filename=train_file_path,
            range=data,
            aws_path=train_path)

        if learning_cfg['evaluate']:

            has_test_data = model_utils.create_csv(
                url=model_utils.EVENT_MODEL_URL,
                filename=evaluate_file_path,
                range=get_next_in_range(data_range,data),
                aws_path=train_path)

            if has_data == True and has_test_data == False:
                evaluate_filename = None
            else:
                logger.info('we can evaluate')

        if has_data:

            train_filename = train_path+train_filename
            if evaluate_filename is not None:
                evaluate_filename = train_path+evaluate_filename
            ##take a copy of our file if it doesnt exist.
            #if not is_on_file(test_file_path):
            #    copyfile(train_file_path,
            #             test_file_path)
            #    put_aws_file_with_path(train_path,test_filename)
            #    write_filenames_index_from_filename(test_file_path)
            # else:
            #    get_aws_file(train_path,  test_filename)

            match_model.create(
                train=True,
                label=label,
                label_values=label_values,
                model_dir=model_dir,
                train_filename=train_filename,
                test_filename=evaluate_filename,
                init=True)
        else:
            logger.info ('no data to train')

        #write the history...
        start_day, start_month, start_year, end_day, end_month, end_year = get_range_details(data)
        history = train_history_utils.create_history('Success - Partial', start_day, start_month, start_year, end_day, end_month, end_year)
        train_history_utils.add_history(history_file, 'default', history)

    if receipt is not None:
        receipt_service.put_receipt(receipt_service.TRAIN_RECEIPT_URL, receipt, None)

    history['status'] = "Success - Full"
    train_history_utils.add_history(history_file, 'default', history)

