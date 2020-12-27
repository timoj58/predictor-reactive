import featureset.featureset_utils as featureset_utils


def create_feature_columns(team_vocab):
    # sort out the featulre columns
    feature_columns = []

    feature_columns.append(featureset_utils.create_category_indicator_column('home', team_vocab))
    feature_columns.append(featureset_utils.create_category_indicator_column('away', team_vocab))

    return feature_columns
