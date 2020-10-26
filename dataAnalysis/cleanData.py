import os
import pandas as pd
def cleanAllData():
    # this multiplies all of the times by 10, to make sure that the time is acutally in ms
    if not os.path.exists('data/csvs_timemult10'):
        os.makedirs('data/csvs_timemult10')
    for file in os.listdir('data/csvs'):
        print (file)
        df = pd.read_csv('data/csvs/' + file)
        df['time'] = df['time'] * 10
        df.to_csv('data/csvs_timemult10/' + file)

if __name__ == '__main__':
    cleanAllData()