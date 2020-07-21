import dataset.match_dataset as match_dataset
import featureset.match_featureset as match_featureset
import util.vocab_utils as vocab_utils
import util.classifier_utils as classifier_utils
import util.dataset_utils as dataset_utils
from util.config_utils import get_dir_cfg
from util.config_utils import get_learning_cfg
from util.model_utils import tidy_up
from util.model_utils import predict


import logging
import json
import time


logger = logging.getLogger(__name__)
local_dir = get_dir_cfg()['local']

def create(train, label, label_values, model_dir, train_filename, test_filename, init):

    aws_model_dir = 'models/'+model_dir
    tf_models_dir = local_dir+'/'+aws_model_dir

    learning_cfg = get_learning_cfg(model_dir)

    logger.info(learning_cfg)

    logger.info('team vocab started...')
    team_file = vocab_utils.create_vocab(
        url=vocab_utils.ALL_TEAMS_URL,
        filename=vocab_utils.TEAMS_FILE,
        player='default')
    logger.info('team vocab completed')


    logger.info('player vocab started...')
    player_file = vocab_utils.create_vocab(
     url=vocab_utils.PLAYERS_URL,
     filename=vocab_utils.PLAYERS_FILE,
     player='default')
    logger.info('[player vocab completed')

    # and the other numerics.  they will be read from a CSV / or direct from mongo more likely.  yes.  from mongo.
    # and review checkpoints, to only train with the newest data?  or build from scratch.  lets see.
    #need to add the label field too.

    feature_columns = match_featureset.create_feature_columns(team_vocab=team_file, player_vocab=player_file)


    # Build 2 hidden layer DNN with 10, 10 units respectively.  (from example will enrich at some point).
    classifier = classifier_utils.create(
        feature_columns=feature_columns,
        classes=len(label_values),
        model_dir=aws_model_dir,
        learning_cfg=learning_cfg,
        init=init)

    if train:

        logger.info(label_values)

        if learning_cfg['evaluate'] and test_filename is not None:
            (train_x, train_y), (test_x, test_y) = match_dataset.load_data(
                train_path=local_dir+train_filename,
                test_path=local_dir+test_filename,
                y_name=label,
                convert=label_values)

        else:
            (train_x, train_y) = match_dataset.load_train_data(
                train_path=local_dir+train_filename,
                y_name=label,
                convert=label_values)

        # Train the Model.
        classifier.train(
            input_fn=lambda:dataset_utils.train_input_fn(train_x, train_y,learning_cfg['batch_size']),steps=learning_cfg['steps'])

        if learning_cfg['evaluate'] and test_filename is not None:
         # Evaluate the model.   not much use anymore.  but could use the first test file.  makes sense
         eval_result = classifier.evaluate(
             input_fn=lambda:dataset_utils.eval_input_fn(test_x, test_y,learning_cfg['batch_size']))

         logger.info('\nTest set accuracy: {accuracy:0.3f}\n'.format(**eval_result))

         if learning_cfg['aws_debug']:
          with open(local_dir+'sample.json') as f:
           sample = json.load(f)


          predict(
             classifier=classifier,
             predict_x=sample,
             label_values=label_values)

        if init:
         logger.info('tidying up')
         tidy_up(
            tf_models_dir=tf_models_dir,
            aws_model_dir=aws_model_dir,
            team_file=team_file,
            train_filename=train_filename)

         time.sleep(30)


    return classifier






