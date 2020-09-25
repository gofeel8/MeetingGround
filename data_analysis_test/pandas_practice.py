import pandas as pd
import pprint

data = pd.read_pickle('./data/data_list.pkl').values.tolist()
pprint.pprint(data)