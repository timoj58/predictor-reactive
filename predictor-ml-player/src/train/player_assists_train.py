import dataset.match_dataset as match_dataset
import logging
import service.training_service as training_service

logger = logging.getLogger(__name__)


def train(start, end, receipt):
    training_service.train(
        start=start,
        end=end,
        label='assists',
        label_values=match_dataset.SCORE,
        model_dir="assists",
        receipt=receipt)
