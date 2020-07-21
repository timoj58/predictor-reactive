import json
import os.path
import logging

logger = logging.getLogger(__name__)



def write_index(index, path):
    with open(path+'/index.json', 'w') as outfile:
        json.dump(index, outfile)

def read_index(path):
  if os.path.isfile(path+'/index.json'):
   with open(path+'/index.json') as f:
    return json.load(f)
  else:
      return {}

def process_index(index, filenames, path):
    #
    if index is not None:
     # need to check if a file is no longer in files, but is in index.  if so.  we turn it off.
     for attribute, value in index.items():
      if attribute not in filenames:
         if index[attribute]['active'] == True:
          logger.info('setting '+attribute+" to active = false")
          index[attribute]['active'] = False

     for file in filenames:
        if index.get(file) is not None:
         if 'train' in path:   #train is never active again.  vocab to fix.  its on day..
          index[file]['active'] = False #sort this out later
         else:
          index[file]['active'] = True #sort this out later
        else:
         add_to_index(index, file, path)
    else:
     index  = {}
     # just add all file elements
     for file in filenames:
      add_to_index(index, file, path)

    write_index(index, path)

def add_to_index(index, file, path):
    if file != 'index.json':
     if not os.path.isdir(path+'/'+file):
      key = {}
      key['active'] = True
      index[file] = key
