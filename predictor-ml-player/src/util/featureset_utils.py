import tensorflow as tf
from util.config_utils import get_dir_cfg


def create_vocab_column(key, vocab):

    return tf.feature_column.indicator_column(
        tf.feature_column.categorical_column_with_vocabulary_list(
           key=key,
           vocabulary_list=vocab))


def create_category_indicator_column(key, filename):

    return tf.feature_column.indicator_column(
        tf.feature_column.categorical_column_with_vocabulary_file(
            key=key,
            vocabulary_file=filename,
            vocabulary_size=None))


def create_category_column(key, filename):

    return tf.feature_column.categorical_column_with_vocabulary_file(
        key=key,
        vocabulary_file=filename,
        vocabulary_size=None)



