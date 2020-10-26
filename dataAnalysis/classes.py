import os
import numpy as np
import pandas as pd
from scipy import stats
import matplotlib.pyplot as plt


def get_stats(vals, eq):
    return \
f"""
Slope={vals[0]}                
Intercept={vals[1]}                
R value={vals[2]}
P value={vals[3]}
Standard Error={vals[4]}

Equation=> y = ${eq}
"""

def regression(x: np.ndarray, y: np.ndarray, type:str):
    """

    :param x:
    :param y:
    :param type: The type of function expected, linear, polynomial, log
    :return:
    """
    if type == 'log':
        outs = np.log(np.clip(y, 0.0001, 10000000))
    elif type == 'linear':
        outs = y
    elif type == 'exp':
        outs = np.exp(y)
    else:
        # poly todo
        outs = y

    slope, intercept, r_value, p_value, std_err = stats.linregress(x, outs)
    print('s ', slope, 'i', intercept)
    return slope, intercept, r_value, p_value, std_err, x, outs





class Data:
    def __init__(self):
        pass

    def get_eq(self,i):
        return ""
    def x_label(self, i):
        pass

    def y_label(self, name, unit, i):
        pass

    def title(self, name, unit, i):
        pass

    def get_count(self):
        return 1

    def main(self):
        for size in [4, 9, 16]:
            self.do(size)
        pass

    def get_out_name(self, size):
        return "abc"

    def do(self, size):
        out_name = self.get_out_name(size)
        name = f'classes/data/csvs/{out_name}'
        df = pd.read_csv(name)
        ins = df['input']

        basic_ops = df['basicOps']
        recursion_count = df['recursionCount']
        time = df['time']
        vals_1 = regression(ins, basic_ops, 'linear')
        vals_2 = regression(ins, recursion_count, 'linear')
        vals_3 = regression(ins, time, 'linear')

        dirname = 'output/' + out_name
        if not os.path.exists(dirname):
            os.makedirs(dirname)

        names = ['Basic Operations', 'Recursion Count', 'Time']
        units = ['number', 'number', 'ms']
        vs = [vals_1, vals_2, vals_3]
        ds = [basic_ops, recursion_count, time]
        i=-1;
        for name, unit, vals, data in zip(names, units, vs, ds):
            i+=1
            # first put down regression stats
            replaced = name.replace(' ', '')

            statistics = get_stats(vals, self.get_eq(i))
            with open(os.path.join(dirname, 'stats' + replaced + '.txt'), 'w+') as f:
                f.write(statistics)

            # now plots
            plt.figure()

            plt.plot(ins, data, label=name)
            plt.plot(ins, (ins) * vals[0] + vals[1], label="Regression_" + name)
            plt.xlabel('Number of empty Cells')
            plt.ylabel(name + f"({unit})")
            plt.title(name + f"({unit}) vs Number of empty Cells")
            plt.ylim(0);
            plt.legend()
            plt.savefig(os.path.join(dirname, replaced + '_linear.png'))

            plt.figure()


class V1_Best(Data):
    def __init__(self):
        super(V1_Best, self).__init__()

    def get_count(self):
        return 1

    def x_label(self, i):
        return 'Number of empty Cells'
    def y_label(self, name, unit, i):
        return name + f"({unit})"

    def title(self, name, unit, i):
        return name + f"({unit}) vs Number of empty Cells"

    def get_out_name(self, size):
        return f'v1_best_case_size{size}_count_1000'

    def get_eq(self,i):
        return "Slope * x + intercept"
