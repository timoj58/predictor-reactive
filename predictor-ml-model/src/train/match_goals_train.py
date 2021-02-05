import dataset.match_dataset as match_dataset
import logging
import service.training_service as training_service

logger = logging.getLogger(__name__)


def train_country(country, start, end, receipt):
    training_service.train_match(
        country=country,
        start=start,
        end=end,
        label='goals',
        label_values=match_dataset.GOALS,
        model_dir="match_goals",
        receipt=receipt)
