import requests
from requests.auth import HTTPDigestAuth
from util.file_utils import write_csv

from util.config_utils import get_analysis_cfg
from util.config_utils import get_analysis_cfg
from util.file_utils import put_aws_file_with_path
from util.file_utils import write_filenames_index_from_filename
from util.dataset_utils import eval_input_fn
import datetime
from datetime import date, timedelta
import logging
from util.config_utils import get_dir_cfg
from util.file_utils import clear_directory
from util.file_utils import on_finish
from util.file_utils import is_on_file
from util.file_utils import get_aws_file
import os
import calendar
import json

logger = logging.getLogger(__name__)

local_dir = get_dir_cfg()['local']

EVENT_MODEL_URL = get_analysis_cfg()['team_model_url']


def real_time_range(start_day, start_month, start_year):

    start_date = datetime.date(start_year, start_month, start_day)

    return ['/'+ start_date.strftime('%d-%m-%Y')
     +'/'
     + (datetime.date.today()).strftime('%d-%m-%Y')]


def create_range(increment, learning_cfg):


    end_date = datetime.date(learning_cfg['end_year'], learning_cfg['end_month'], learning_cfg['end_day'])
    start_date = datetime.date(learning_cfg['start_year'], learning_cfg['start_month'], learning_cfg['start_day'])

    ranges = []

    no_of_months = diff_month(start_date, end_date) / increment

    temp_end_date = start_date

    for month in range(0, int(no_of_months)):

       temp_end_date = add_months(temp_end_date, increment)
       ranges.append('/'+start_date.strftime('%d-%m-%Y')+'/'+temp_end_date.strftime('%d-%m-%Y'))
       start_date = temp_end_date

    ##number of months between dates
    ranges.append('/'+temp_end_date.strftime('%d-%m-%Y')+'/'+end_date.strftime('%d-%m-%Y'))

    return ranges

def diff_month(d2, d1):
    return (d1.year - d2.year) * 12 + d1.month - d2.month


def add_months(sourcedate,months):
    month = sourcedate.month - 1 + months
    year = sourcedate.year + month // 12
    month = month % 12 + 1
    day = min(sourcedate.day,calendar.monthrange(year,month)[1])
    return datetime.date(year,month,day)


def create_csv(url, filename, range, aws_path):

    logger.info ('getting csv data...'+filename)
    if is_on_file(filename):
        logger.info("csv file already created "+filename)
        head, tail = os.path.split(filename)
        return get_aws_file(head.replace(local_dir,'')+'/',tail)
    else:

     data = requests.get(url+range, headers={'groups': 'ROLE_AUTOMATION,', 'username': 'machine-learning'})
     has_data = write_csv(filename, data)

     logger.info ('created csv')
     head, tail = os.path.split(filename)
     put_aws_file_with_path(aws_path,tail)
     write_filenames_index_from_filename(filename)

     return has_data


def tidy_up(tf_models_dir, aws_model_dir, team_file, train_filename):
    #probably can tidy this all up.  in one call.
    if aws_model_dir is not None:
        on_finish(tf_models_dir, aws_model_dir)
    else:
        clear_directory(tf_models_dir)
    #also get rid of the vocab files and training / testing files.
    #vocab
    if team_file is not None:
        clear_directory(os.path.dirname(team_file))
    #training
    if train_filename is not None:
        clear_directory(os.path.dirname(local_dir+train_filename))


def predict(classifier, predict_x, label_values):
    logger.info('predict data '+json.dumps(predict_x))
    predictions = classifier.predict(
        input_fn=lambda: eval_input_fn(predict_x,
                                                     labels=None,
                                                     batch_size=1))
    template = ('\nPrediction is "{}" ({:.1f}%)')

    response = {}

    for pred_dict in predictions:
        class_id = pred_dict['class_ids'][0]
        #probability = pred_dict['probabilities'][class_id]

        index = 0
        for probability in pred_dict['probabilities'] :
            #probability = pred_dict['probabilities'][class_id]
            item = {}
            item['label'] = label_values[index]
            item['score'] = '{:.1f}'.format(100 * probability)

            response[index] = item
            logger.info(template.format(label_values[index],
                                        100 * probability))

            index += 1

    return response

