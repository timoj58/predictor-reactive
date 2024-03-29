import boto3
import csv
import logging
import os
import os.path
from botocore.exceptions import ClientError

from service.config_service import get_dir_cfg
from service.index_service import process_index, read_index

logger = logging.getLogger(__name__)

s3_client = boto3.client('s3')

aws = get_dir_cfg()['aws']
aws_url = get_dir_cfg()['aws_url']
aws_bucket = get_dir_cfg()['aws_bucket']

local_dir = get_dir_cfg()['local']


def on_finish(tf_models_dir, aws_model_dir):
    logger.info(' on finish write index ' + tf_models_dir)
    write_filenames_index(local_dir + aws_model_dir)
    try:
        write_filenames_index(local_dir + aws_model_dir + '/eval')
    except Exception as e:
        logger.info('eval dir not created')

    logger.info(' put aws files ' + aws_model_dir)
    put_aws_files_from_dir(aws_model_dir + '/')
    try:
        put_aws_files_from_dir(aws_model_dir + '/eval/')
    except Exception as e:
        logger.info('eval dir not created')

    logger.info(' clearing directory')
    clear_directory(tf_models_dir)


def clear_directory(path):
    if aws:
        for file in os.listdir(path):
            if not os.path.isdir(path + '/' + file):
                # if file != "index.json":  # dont delete the index...doh.  index is now going to AWS.
                logger.info(' deleting ' + file)
                file_path = os.path.join(path, file)
                os.unlink(file_path)


def get_indexes(path):
    # try and load it from AWS.  need to handle it not being present.
    try:
        if get_aws_file(path, '/index.json'):
            # need to fix all the path mess crap.
            return read_index(local_dir + path)
    except Exception as e:
        return {}
    return {}


def is_in_index(path, filename):
    index = get_indexes(path)
    return index.get(filename)


def write_filenames_index_from_filename(filename):
    head, tail = os.path.split(filename)
    write_filenames_index(head)


def write_filenames_index(path):
    logger.info("index for " + path)
    index = read_index(path)
    files = os.listdir(path)

    process_index(index, files, path)


def get_aws_file(path, filename):
    if aws:
        logger.info('getting aws file ' + path + filename)
        download_file(path + filename, local_dir + path + filename)

    return os.path.getsize(local_dir + path + filename) > 0


def put_aws_files_from_dir(path):
    logger.info('getting indexes for ' + local_dir + path)
    indexes = read_index(local_dir + path)
    for attribute, value in indexes.items():
        if value['active'] == True:
            put_aws_file_with_path(path, attribute)
    # finally save the index file
    if os.path.isfile(local_dir + path + 'index.json'):
        put_aws_file_with_path(path, 'index.json')


def put_aws_file_with_path(aws_path, filename):
    if aws:
        upload_file(aws_path, filename)


def is_on_file(filename):
    if aws is False:
        return os.path.isfile(filename)
    else:
        head, tail = os.path.split(filename)
        return is_in_index(head, tail)


def make_dir(filename):
    if not os.path.exists(os.path.dirname(filename)):
        os.makedirs(os.path.dirname(filename))


def write_csv(filename, data):
    make_dir(filename)
    has_data = False
    with open(filename, 'w') as f:
        fnames = ['home', 'away', 'outcome', 'goals']
        writer = csv.DictWriter(f, fieldnames=fnames)

        for row in data:
            writer.writerow(row)
            has_data = True

    return has_data


# replacment s3 client utils
def download_file(key, filepath):
    try:
        logger.info('trying to access ' + key + ' ' + filepath)
        s3_client.download_file(aws_bucket, key, filepath)
    except ClientError as e:
        logger.info('get failed')
        raise err


# need download equivalent.  can reduce code a lot.
def upload_file(aws_path, filename):
    try:
        logger.info('file uploading ' + local_dir + aws_path + filename)
        s3_client.upload_file(local_dir + aws_path + filename, aws_bucket, aws_path + filename)
        return None
    except ClientError as e:
        raise err
