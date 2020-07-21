from util.config_utils import get_dir_cfg
from util.index_utils import process_index, read_index
import os.path
import os
import requests
import logging
import csv
import time
import boto3
from botocore.exceptions import ClientError


logger = logging.getLogger(__name__)

s3_client = boto3.client('s3')

aws = get_dir_cfg()['aws']
aws_url = get_dir_cfg()['aws_url']
aws_bucket = get_dir_cfg()['aws_bucket']

local_dir = get_dir_cfg()['local']


def on_finish(tf_models_dir, aws_model_dir):
    logger.info(' write index '+tf_models_dir)
    write_filenames_index(tf_models_dir)
    try:
     write_filenames_index(tf_models_dir+'/eval')
    except Exception as e:
      logger.info('eval dir not created')

    logger.info(' put aws files '+aws_model_dir)
    put_aws_files_from_dir(aws_model_dir+'/')
    try:
     put_aws_files_from_dir(aws_model_dir+'/eval/')
    except Exception as e:
        logger.info('eval dir not created')

    logger.info(' clearing directory')
    clear_directory(tf_models_dir)


def clear_directory(path):
   if aws:
    for file in os.listdir(path):
      if not os.path.isdir(path+'/'+file):
       if file != "index.json": #dont delete the index...doh.
        logger.info(' deleting '+file)
        file_path = os.path.join(path, file)
        os.unlink(file_path)


def get_indexes(path):
    if  os.path.isfile(path+'/index.json'):
     logger.info('we have an index file')
     return read_index(path)
    return {}

def is_in_index(path, filename):
    index = read_index(path)
    return index.get(filename)

def write_filenames_index_from_filename(filename):
    head, tail = os.path.split(filename)
    write_filenames_index(head)

def write_filenames_index(path):
   logger.info("index for "+path)
   index = get_indexes(path)
   files = os.listdir(path)

   process_index(index, files, path)

def get_aws_file(path, filename):
   if aws:
    logger.info('getting aws file '+aws_url+filename)
    download_file(path+filename, local_dir+path+filename, retry_count=3)
 #   response = get_file(url=aws_url+path+filename, retry_count=3)
 #   if response.status_code == 200:
 #    with open(local_dir+path+filename, 'wb') as f:
 #     f.write(response.content)
 #   elif response.status_code == 404:
 #     open(local_dir+path+filename, 'a').close()

    return os.path.getsize(local_dir+path+filename) > 0


def put_aws_files_from_dir(path):
  logger.info('getting indexes for '+local_dir+path)
  indexes = get_indexes(local_dir+path)
  for attribute, value in indexes.items():
     if value['active'] == True:
      put_aws_file_with_path(path, attribute)

def put_aws_file_with_path(aws_path, filename):
    if aws:
        head, tail = os.path.split(filename)
        logger.info('putting file to aws - '+aws_url+aws_path+tail)

        s3_call_with_error_handling(aws_path, filename)

       # with open(local_dir+aws_path+filename,'rb') as filedata:
       #   s3_call_with_error_handling(aws_url+aws_path+tail, filedata)
          #requests.put(aws_url+aws_path+tail, data=filedata, headers={})

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
     writer = csv.writer(f)
     reader = csv.reader(data.text.splitlines())

     for row in reader:
      writer.writerow(row)
      has_data = True

    return has_data


#def put_file(url, filedata):
#    try:
#        requests.put(url, data=filedata, headers={})
#        return None
#    except requests.exceptions.HTTPError as err:
#        logger.info('put failed')
#        return err
#    except requests.exceptions.ConnectionError as conn_err:
#        logger.info('put failed')
#        return conn_err

#def get_file(url, retry_count):
#    try:
#        response = requests.get(url, headers={})
#        return response
#    except requests.exceptions.HTTPError as err:
#        logger.info('put failed')
#        time.sleep(1)
#        if retry_count > 0:
#            return get_file(url, retry_count - 1)
#        else:
#            raise err
#    except requests.exceptions.ConnectionError as conn_err:
#        logger.info('put failed')
#        time.sleep(1)
#        if retry_count > 0:
#            return get_file(url, retry_count - 1)
#        else:
#            raise conn_err

#def s3_call_with_error_handling(url, filedata):
#    retry_count = 0
#
#    result = False
#
#    while retry_count < 3 and result is not None:
#        result = put_file(url, filedata)
#        if result is not None:
#            time.sleep(1)
#
#        retry_count = retry_count + 1
#
#    if result is not None:
#        raise result

def s3_call_with_error_handling(aws_path, filename):
    retry_count = 0

    result = False

    while retry_count < 3 and result is not None:
        # result = put_file(url, filedata)
        result = upload_file(aws_path, filename)
        if result is not None:
            time.sleep(1)

        retry_count = retry_count + 1

    if result is not None:
        raise result


#replacment s3 client utils
def download_file(key, filepath, retry_count):
    try:
      logger.info('trying to access '+key+' '+filepath)
      s3_client.download_file(aws_bucket, key, filepath)
    except ClientError as e:
        logger.info('get failed')
        time.sleep(1)
        if retry_count > 0:
            return download_file(key, filepath, retry_count - 1)
        else:
            raise err

#need download equivalent.  can reduce code a lot.
def upload_file(aws_path, filename):
    try:
      logger.info('file '+local_dir+aws_path+filename)

      s3_client.upload_file(local_dir+aws_path+filename, aws_bucket, aws_path+filename)
      return None
    except ClientError as e:
      raise err






