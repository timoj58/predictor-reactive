import logging
import tensorflow as tf

from service.config_service import get_dir_cfg
from util.file_utils import get_aws_file
from util.file_utils import get_indexes

logger = logging.getLogger(__name__)

local_dir = get_dir_cfg()['local']


def init_models(model_dir):
    logger.info('calling init '+model_dir)
    indexes = get_indexes(model_dir)
    for attribute, value in indexes.items():
        if (value['active'] == True):
            get_aws_file(model_dir + '/', attribute)

    indexes = get_indexes(model_dir + '/eval')
    for attribute, value in indexes.items():
        if (value['active'] == True):
            get_aws_file(model_dir + '/eval/', attribute)


def create(feature_columns, classes, model_dir, learning_cfg, init):
    logger.info('model dir for classifier ' + local_dir + model_dir)

    logger.info('tensorflow version ' + tf.__version__)

    if init:
        init_models(model_dir)

    return tf.estimator.DNNClassifier(
        feature_columns=feature_columns,
        hidden_units=learning_cfg['hidden_units'],
        n_classes=classes,
        model_dir=local_dir + model_dir)

