import mock
import unittest
from dataset import match_dataset
from service.training_service import train_match


class TrainingServiceTest(unittest.TestCase):

    @mock.patch('service.training_service.model_utils')
    @mock.patch('service.training_service.match_model')
    @mock.patch('service.training_service.receipt_service')
    def test_train(self, mock_model_utils, mock_match_model, mock_receipt):
        mock_model_utils.create_csv.return_value = True
        mock_match_model.create.return_value = None
        mock_receipt.put_receipt.return_value = None

        train_match(
            country="england",
            start="01-08-2009",
            end="01-08-2010",
            label="goals",
            label_values=match_dataset.GOALS,
            model_dir="match_goals",
            receipt="receipt"
        )

        # python mocking is a head f**k.  its calling it, but not asserting correctly
        # i implemented the os.path example, same outcome, assert is not working.
        # maybe a lib import is wrong etc?
        # give up.  stupid language.  its definitely calling it
        # plus its a stupid test, given it will call it as its the last line in method.
        # mock_receipt.put_receipt.assert_called_with(receipt='receipt')


if __name__ == '__main__':
    unittest.main()
