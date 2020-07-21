import util.featureset_utils as featureset_utils


def create_feature_columns(team_vocab, player_vocab):
 # sort out the featulre columns
 feature_columns = []

 feature_columns.append(featureset_utils.create_category_indicator_column('player', player_vocab))
 feature_columns.append(featureset_utils.create_category_indicator_column('opponent', team_vocab))
 feature_columns.append(featureset_utils.create_vocab_column('home', ['home', 'away']))

 return feature_columns


