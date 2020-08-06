import logging

import dataset.match_dataset as match_dataset
import service.receipt_service as receipt_service
import service.training_service as training_service

logger = logging.getLogger(__name__)


def train(start, end, receipt):
    training_service.train(
        start=start,
        end=end,
        label='red',
        label_values=match_dataset.CARDS,
        model_dir="red",
        receipt=receipt)
