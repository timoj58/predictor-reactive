from featureset.featureset_utils import create_category_indicator_column
from featureset.featureset_utils import create_vocab_column


def create_feature_columns(team_vocab, player_vocab):
    # sort out the featulre columns
    feature_columns = []

    feature_columns.append(create_category_indicator_column('player', player_vocab))
    feature_columns.append(create_category_indicator_column('opponent', team_vocab))
    feature_columns.append(create_vocab_column('home', ['home', 'away']))

    return feature_columns
