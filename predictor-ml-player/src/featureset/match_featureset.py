from featureset.featureset_utils import create_category_indicator_column
from featureset.featureset_utils import create_vocab_column

feature_columns = None


def create_feature_columns(team_vocab, player_vocab):
    global feature_columns

    if feature_columns is None:
        # sort out the feature columns
        feature_columns = [create_category_indicator_column('player', player_vocab),
                           create_category_indicator_column('opponent', team_vocab),
                           create_vocab_column('home', ['home', 'away'])]

    return feature_columns
