import calendar
import datetime
import json
import logging
import os
import requests
from dataset.dataset_utils import eval_input_fn
from service.config_service import get_analysis_cfg
from service.config_service import get_dir_cfg
from util.file_utils import clear_directory
from util.file_utils import get_aws_file
from util.file_utils import is_on_file
from util.file_utils import on_finish
from util.file_utils import put_aws_file_with_path
from util.file_utils import write_csv
from util.file_utils import write_filenames_index_from_filename

logger = logging.getLogger(__name__)

local_dir = get_dir_cfg()['local']

EVENT_MODEL_URL = get_analysis_cfg()['team_model_url']


def real_time_range(start_day, start_month, start_year):
    start_date = datetime.date(start_year, start_month, start_day)
    end_date = datetime.date(start_year + 1, start_month, start_day)

    if end_date > datetime.date.today():
        end_date = datetime.date.today()

    return ['/' + start_date.strftime('%d-%m-%Y')
            + '/'
            + end_date.strftime('%d-%m-%Y')]


def add_months(sourcedate, months):
    month = sourcedate.month - 1 + months
    year = sourcedate.year + month // 12
    month = month % 12 + 1
    day = min(sourcedate.day, calendar.monthrange(year, month)[1])
    return datetime.date(year, month, day)


def create_csv(url, filename, start_date, end_date, aws_path):
    logger.info('getting csv data...' + filename)
    if is_on_file(filename):
        logger.info("csv file already created " + filename)
        head, tail = os.path.split(filename)
        return get_aws_file(head.replace(local_dir, '') + '/', tail)
    else:

        data = requests.get(url + '/' + start_date.strftime("%d-%m-%Y") + '/' + end_date.strftime("%d-%m-%Y"),
                            headers={'groups': 'ROLE_AUTOMATION,', 'username': 'machine-learning'})
        has_data = write_csv(filename, data.json())

        logger.info('created csv')
        head, tail = os.path.split(filename)
        put_aws_file_with_path(aws_path, tail)
        write_filenames_index_from_filename(filename)

        return has_data


def tidy_up(tf_models_dir, aws_model_dir, train_filename):
    # probably can tidy this all up.  in one call.
    if aws_model_dir is not None:
        on_finish(tf_models_dir, aws_model_dir)
    else:
        clear_directory(tf_models_dir)
    # training
    if train_filename is not None:
        clear_directory(os.path.dirname(local_dir + train_filename))

    return 'Ok'


def predict(classifier, predict_x, label_values):
    logger.info('predict data ' + json.dumps(predict_x))
    predictions = classifier.predict(
        input_fn=lambda: eval_input_fn(predict_x,
                                       labels=None,
                                       batch_size=1))
    template = ('\nPrediction is "{}" ({:.1f}%)')

    response = {}

    for pred_dict in predictions:
        class_id = pred_dict['class_ids'][0]
        # probability = pred_dict['probabilities'][class_id]

        index = 0
        for probability in pred_dict['probabilities']:
            # probability = pred_dict['probabilities'][class_id]
            item = {}
            item['label'] = label_values[index]
            item['score'] = '{:.1f}'.format(100 * probability)

            response[index] = item
            logger.info(template.format(label_values[index],
                                        100 * probability))

            index += 1

    return response
