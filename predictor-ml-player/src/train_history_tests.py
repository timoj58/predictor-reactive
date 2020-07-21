import util.train_history_utils as train_history_utils


train_history_utils.add_history("country-matches-train-history.json", "england",
                                train_history_utils.create_history("success", 01, 01, 2009, 01, 01, 2018, "01-01-2018"))

train_history_utils.add_history("country-matches-train-history.json", "spain",
                                train_history_utils.create_history("fail", 01, 01, 2010, 01, 01, 2018, "01-01-2018"))


train_history_utils.add_history("country-matches-train-history.json", "england",
                                train_history_utils.create_history("fail", 01, 01, 2009, 01, 01, 2018, "10-01-2018"))
