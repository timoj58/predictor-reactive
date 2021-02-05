import dataset.dataset_utils as dataset_utils
import dataset.match_dataset as match_dataset
import featureset.match_featureset as match_featureset
import logging
import service.vocab_service as vocab_service
import util.classifier_utils as classifier_utils
from model.model_utils import tidy_up
from service.config_service import get_dir_cfg
from service.config_service import get_learning_cfg

logger = logging.getLogger(__name__)
local_dir = get_dir_cfg()['local']


def create(country, train, label, label_values, model_dir, train_filename, test_filename):
    logger.info('create match model called')
    learning_cfg = get_learning_cfg(model_dir)

    aws_model_dir = 'models/' + model_dir + '/' + country
    tf_models_dir = local_dir + '/' + aws_model_dir

    logger.info('creating vocab')

    team_file = vocab_service.create_vocab(
        filename=vocab_service.TEAMS_FILE,
        country=country)

    feature_columns = match_featureset.create_feature_columns(team_vocab=team_file)

    # Build 2 hidden layer DNN with 10, 10 units respectively.  (from example will enrich at some point).
    classifier = classifier_utils.create(
        feature_columns=feature_columns,
        classes=len(label_values),
        model_dir=aws_model_dir,
        learning_cfg=learning_cfg)

    if train:
        logger.info('training started')

        if learning_cfg['evaluate'] and test_filename is not None:
            logger.info('load dataset - evaluate mode')
            (train_x, train_y), (test_x, test_y) = match_dataset.load_data(
                train_path=local_dir + train_filename,
                test_path=local_dir + test_filename,
                y_name=label,
                convert=label_values)

        else:
            logger.info('load dataset - normal mode')
            (train_x, train_y) = match_dataset.load_train_data(
                train_path=local_dir + train_filename,
                y_name=label,
                convert=label_values)

        # Train the Model.
        logger.info('training the model')
        classifier.train(
            input_fn=lambda: dataset_utils.train_input_fn(train_x, train_y, learning_cfg['batch_size']),
            steps=learning_cfg['steps'])

        if learning_cfg['evaluate'] and test_filename is not None:
            logger.info('evaluate')
            # Evaluate the model.   not much use anymore.  but could use the first test file.  makes sense
            eval_result = classifier.evaluate(
                input_fn=lambda: dataset_utils.eval_input_fn(test_x, test_y, learning_cfg['batch_size']))

            logger.info('\nTest set accuracy: {accuracy:0.3f}\n'.format(**eval_result))

        tidy_up(
            tf_models_dir=tf_models_dir,
            aws_model_dir=aws_model_dir,
            team_file=team_file,
            train_filename=train_filename)

    return classifier
