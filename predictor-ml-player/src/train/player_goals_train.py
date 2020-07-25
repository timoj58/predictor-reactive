import logging

import dataset.match_dataset as match_dataset
import service.receipt_service as receipt_service
import service.training_service as training_service

logger = logging.getLogger(__name__)



def train(start, end, receipt):
    logger.info('started train')

    training_service.train(
        start=start,
        end=end,
        label='goals',
        label_values=match_dataset.SCORE,
        model_dir="goals",
        receipt=receipt)

    receipt_service.put_receipt(receipt_service.TRAIN_RECEIPT_URL, receipt, None)
