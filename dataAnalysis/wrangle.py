import os

import pandas as pd
import scipy
from scipy import stats
import numpy as np
import matplotlib.pyplot as plt

import statsmodels.api as sm

def regression(x: np.ndarray, y: np.ndarray, type:str, interceptZero=False):
    """

    :param x:
    :param y:
    :param type: The type of function expected, linear, polynomial, log
    :return:
    """
    if interceptZero:
        # do curve fit like a champ
        opts, pcov = scipy.optimize.curve_fit(lambda x, a: np.exp(x * a) , x, y)
        perr = np.sqrt(np.diag(pcov)).squeeze()
        # perr is std dev
        return 1, (opts[0]), 0, 0, perr, x, y
        print('opts', opts)
        a, rs, tmp, tmp2 = np.linalg.lstsq(x[:,np.newaxis], y)
        return a, 0, rs, 0, 0, x, y
    if type == 'quadratic':
        coeffs = np.polyfit(x, y, 2, cov=False)
        # perr = np.sqrt(np.diag(V)).squeeze()
        return coeffs[0], coeffs[1], coeffs[2], 0, 0, x, y
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


def wrangle_csv_v2(name, type, x_axis_name, y_axis_name):
    df = pd.read_csv(name)

    slope, intercept, r_value, p_value, std_err, x,y  = regression(df['input'], df['time'], type)
    df.reset_index(drop=True, inplace=True)
    # print(df)
    print(slope, intercept, r_value,p_value, std_err)
    # plt.plot(df['input'], df['time'], label='data')
    # plt.plot(df['input'], df['input'] * slope + intercept, label='regression_line')

    plt.plot(x, y, label='data')
    plt.plot(x, x * slope + intercept, label='regression_line')
    plt.legend()
    plt.show()


def wrangle_csv(name, x_axis_name, y_axis_name):
    df = pd.read_csv(name)
    x = np.log(np.clip(df['time'], 0.0001, 10000000))
    print(x)
    slope, intercept, r_value, p_value, std_err = stats.linregress(df['input'], x)
    df.reset_index(drop=True, inplace=True)
    print(df)
    print(slope, intercept, r_value,p_value, std_err)
    plt.plot(df['input'], x, label='data')
    plt.plot(df['input'], df['input'] * slope + intercept, label='regression_line')
    plt.legend()
    plt.show()

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


def v1_best_case(do_plots):
    def do_things(size):
        out_name = f'v1_best_case_size{size}_count_1000'
        name = f'data/csvs/{out_name}'
        df = pd.read_csv(name)
        ins = df['input']

        basic_ops = df['basicOps']
        recursion_count = df['recursionCount']
        time = df['time']
        # plt.plot(ins, basic_ops)
        vals_1 = regression(ins, basic_ops, 'linear')
        vals_2 = regression(ins, recursion_count, 'linear')
        vals_3 = regression(ins, time, 'linear')

        # now plot those
        # now save these
        dirname = 'output/' + out_name
        if not os.path.exists(dirname):
            os.makedirs(dirname)

        names = ['Basic Operations', 'Recursion Count', 'Time']
        units = ['number', 'number', 'ms']
        vs = [vals_1, vals_2, vals_3]
        ds = [basic_ops, recursion_count, time]
        for name, unit, vals, data in zip(names, units, vs, ds):
            if (not do_plots): break;
            # first put down regression stats
            replaced = name.replace(' ', '')

            statistics = \
                f"""
    Slope={vals[0]}                
    Intercept={vals[1]}                
    R value={vals[2]}
    P value={vals[3]}
    Standard Error={vals[4]}

    Equation=y = Slope * x + intercept
    """
            with open(os.path.join(dirname, 'stats'+replaced+'.txt'), 'w+') as f:
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

        return [ins] + ds

    alls = []
    for size in [4, 9, 16]:
        alls.append(do_things(size))
    return alls;


def v2_worst_case_no_stopping(do_plots=True):
    def do_things(size):
        out_name = f'v2_worst_case_size{size}_count_1000_numtodo_9'
        name = f'data/csvs/{out_name}'
        df = pd.read_csv(name)
        ins = df['input']

        basic_ops = df['basicOps']
        recursion_count = df['recursionCount']
        time = df['time']

        if size == 16 and False:
            ins = ins[:7]
            basic_ops = basic_ops[:7]
            recursion_count = recursion_count[:7]
            time = time[:7]

        # only 8.
        ins = ins[:8]
        basic_ops = basic_ops[:8]
        recursion_count = recursion_count[:8]
        time = time[:8]


        # plt.plot(ins, basic_ops)
        # exp = np.exp(ins)
        exp = np.power(size, ins) # TODO
        vals_1 = regression(exp, basic_ops, 'linear')
        vals_2 = regression(exp, recursion_count, 'linear')
        vals_3 = regression(exp, time, 'linear')

        # now plot those
        print(vals_1)
        # now save these
        dirname = 'output/' + out_name
        if not os.path.exists(dirname):
            os.makedirs(dirname)

        names = ['Basic Operations', 'Recursion Count', 'Time']
        units = ['number', 'number', 'ms']
        vs = [vals_1, vals_2, vals_3]
        ds = [basic_ops, recursion_count, time]
        for name, unit, vals, data in zip(names, units, vs, ds):
            if not do_plots: break
            # first put down regression stats
            replaced = name.replace(' ', '')

            statistics = get_stats(vals, f'Slope * {size}^x + intercept')
            with open(os.path.join(dirname, 'stats'+replaced+'.txt'), 'w+') as f:
                f.write(statistics)

            # now plots
            plt.figure()

            plt.plot(ins, data, label=name)
            plt.plot(ins, exp * vals[0] + vals[1], label="Regression_"+name)
            plt.xlabel('Number of empty Cells')
            plt.ylabel(name + f"({unit})")
            plt.title(name + f"({unit}) vs Number of empty Cells")
            plt.legend()
            plt.savefig(os.path.join(dirname, replaced+'_linear.png'))

            plt.figure()

            plt.plot(exp, data, label=name)
            plt.plot(exp, exp * vals[0] + vals[1], label="Regression_" + name)
            plt.xlabel('{size}^(Number of empty Cells)')
            plt.ylabel(name + f"({unit})")
            plt.title(name + f"({unit}) vs {size}^(Number of empty Cells)")
            plt.legend()
            plt.savefig(os.path.join(dirname, replaced + '_exp.png'))
        return [ins] + ds
    alls = []
    for size in [4, 9, 16]:
        alls.append(do_things(size))
    return alls


def v3_average_case(do_plots=True):
    def do_things(size):
        n = 70
        c = 1000
        if size == 4:
            n = 15
        elif size == 16:
            n = 130
            # c = 100

        out_name = f'v5_average_case_size{size}_count_{c}_numtodo_{n}'
        name = f'data/csvs/{out_name}'
        df = pd.read_csv(name)

        ins = np.array(df['input'])
        basic_ops = df['basicOps']
        recursion_count = df['recursionCount']
        time = df['time']

        if (size == 9):
            ins = ins[:55]
            basic_ops = basic_ops[:55]
            recursion_count = recursion_count[:55]
            time = time[:55]
        # plt.plot(ins, basic_ops)
        # exp = np.exp(ins)
        # exp = np.power(size, ins) # TODO
        # lns1 = np.log(np.maximum(basic_ops,0.0001))
        # lns2 = np.log((np.maximum(recursion_count,0.0001)))
        # lns3 = np.log((np.maximum(time,0.0001)))
        # vals_1 = regression(ins, lns1, 'linear', True)
        # vals_2 = regression(ins, lns2, 'linear', True)
        # vals_3 = regression(ins, lns3, 'linear', True)

        if size == 4:
            vals_1 = regression(ins, basic_ops, 'linear')
            vals_2 = regression(ins, recursion_count, 'linear')
            vals_3 = regression(ins, time, 'linear')
        else:
            vals_1 = regression(ins, basic_ops, 'linear', True)
            vals_2 = regression(ins, recursion_count, 'linear', True)
            vals_3 = regression(ins, time, 'linear', True)

        # now plot those
        print('vals1 ', vals_1)
        print('vals2 ', vals_2)
        print('vals3 ', vals_3)
        # return
        # now save these
        dirname = 'output/' + out_name
        if not os.path.exists(dirname):
            os.makedirs(dirname)

        names = ['Basic Operations', 'Recursion Count', 'Time']
        units = ['number', 'number', 'ms']
        vs = [vals_1, vals_2, vals_3]
        ds = [basic_ops, recursion_count, time]
        for name, unit, vals, data in zip(names, units, vs, ds):
            if not do_plots: break
            # first put down regression stats
            replaced = name.replace(' ', '')

            statistics = get_stats(vals, f'exp(intercept * x)' if size != 4 else f'Slope * x + intercept')
            with open(os.path.join(dirname, 'stats'+replaced+'.txt'), 'w+') as f:
                f.write(statistics)

            # now plots
            plt.figure()
            # TODO MICHAEL CHANGE THE EQUATION AND ALL THAT
            #reg  =exp * vals[0] + vals[1]
            # reg = [e * vals[0] + vals[1] for e in exp]
            if size == 4:
                reg = ins * vals[0] + vals[1]
            else:
                reg = np.exp(ins * vals[1]) * vals[0]
            exp = np.exp(ins * vals[1]) * vals[0]
            plt.plot(ins, data, label=name)
            plt.plot(ins, reg, label="Regression_"+name)
            plt.xlabel('Number of empty Cells')
            plt.ylabel(name + f"({unit})")
            plt.title(name + f"({unit}) vs Number of empty Cells")
            plt.legend()
            plt.savefig(os.path.join(dirname, replaced+'_linear.png'))

            plt.figure()

            coeff = round(vals[1] * 100) / 100.0
            plt.plot(exp, data, label=name)
            plt.plot(exp, reg, label="Regression_" + name)
            plt.xlabel(f'exp({coeff} * Number of empty Cells)')
            plt.ylabel(name + f"({unit})")
            plt.title(name + f"({unit}) vs exp({coeff} * Number of empty Cells)")
            plt.legend()
            plt.savefig(os.path.join(dirname, replaced + '_exp.png'))
        return [ins] + ds
    alls = []
    for size in [4, 9, 16]:
        alls.append(do_things(size))
    return alls


def v4_boards(do_plots=True):
    def plot_final(alls):
        ins = np.array([4, 9, 16])
        ins *= ins # temp, number of cells
        titles = ['Number of Empty Cells', 'Number of Basic Operations', 'Recursion Count', 'Time(ms)']
        ds = [[np.average(dd[i]) for dd in alls] for i in range(1, len(titles))]
        for d, t in zip(ds, titles[1:]):
            if (t == 'Time(ms)'):
                name = 'Time'
            else:
                name = t.replace(' ','')
            replaced = name
            vals = regression(ins ** 2, d, 'linear')
            # print("INS ", ins)
            # vals = regression(ins, d, 'quadratic')
            # print('coeffs', vals[:3], )
            statistics = get_stats(vals, f'Slope * x^2 + intercept')
            out_name = f'v4_boards_all'
            dirname = 'output/' + out_name
            if not os.path.exists(dirname):
                os.makedirs(dirname)
            with open(os.path.join(dirname, 'stats'+replaced+'.txt'), 'w+') as f:
                f.write(statistics)
            name_to_save = os.path.join(dirname, replaced+'.png')
            plt.figure()
            plt.plot(ins, d, label=name)
            plt.plot(ins, ins ** 2 * vals[0] + vals[1], label="Regression_"+name)
            # plt.plot(ins, ins * ins * vals[0] + vals[1], label="Regression_"+name)
            # plt.plot(ins, ins * ins * vals[0] + vals[1] * ins + vals[2], label="Regression_"+name)
            plt.ylabel(t)
            plt.xlabel("Number of Cells")
            plt.legend()
            plt.title('Average ' + t +" vs " + "Number of cells")
            plt.savefig(name_to_save)
            plt.show()

    def do_things(size):
        if type(size) != int:
            plot_final(alls)
            return
        count = 1000
        out_name = f'v4_boards{size}_count_{count}'
        name = f'data/csvs/{out_name}'
        df = pd.read_csv(name)
        ins = df['input']

        basic_ops = df['basicOps']
        recursion_count = df['recursionCount']
        time = df['time']
        dirname = 'output/' + out_name
        if not os.path.exists(dirname):
            os.makedirs(dirname)
        def do_plot_box(data, x_label, y_label, title, name_to_save):
            plt.figure()
            plt.boxplot(data, showfliers=False)
            plt.ylabel(y_label)
            plt.xlabel(x_label)
            plt.title(title)
            plt.savefig(os.path.join(dirname, name_to_save))
            plt.show()

        def do_plot_bar(data, x_label, y_label, title, name_to_save):
            plt.figure()
            plt.hist(data)
            plt.ylabel(y_label)
            plt.xlabel(x_label)
            plt.title(title)
            plt.savefig(os.path.join(dirname, name_to_save))
            plt.show()

        ds = [ins, basic_ops, recursion_count, time]
        titles = ['Number of Empty Cells', 'Number of Basic Operations', 'Recursion Count', 'Time(ms)']
        for d, t in zip(ds, titles):
            if not do_plots: break
            if (t == 'Time(ms)'):
                name = 'Time'
            else:
                name = t.replace(' ','')
            tmp1 = 'Box plot of '
            tmp2 = 'Bar chart of '
            do_plot_box(d, t, 'Count',tmp1 + t, name + "_box.png")
            # now plot a bar chart
            do_plot_bar(d, t, 'Count',tmp2 + t, name + "_bar.png")
        if 1 == 1:
            plt.figure()
            abc = zip(ins, basic_ops)
            abc = sorted(abc, key=lambda s: s[0])
            tmp1, tmp2 = [], []
            for i, j in abc:
                tmp1.append(i)
                tmp2.append(j)
            plt.plot(tmp1, tmp2)
            plt.ylabel("MEEP TEST basic ops " + str(size))
            plt.xlabel("MEEP TEST 2 empty cells")
            plt.title("REE")
            # plt.savefig(os.path.join(dirname, name_to_save))
            plt.show()
        return ds
    alls = []
    for size in [4, 9, 16]:
        alls.append(do_things(size))
    # # now other stuff
    # ins = [4, 6, 9]
    # outs = [np.average(dd[1]) for dd in alls]
    # print(ins, outs)
    #
    # plt.plot(ins, outs)
    # plt.show()
    do_things(alls)
    return alls

def make_combined_graphs():
    all_datas = [v1_best_case(False), v2_worst_case_no_stopping(False), v3_average_case(False)]
    all_names = ['bestCase', 'worstCase', 'averageCase']

    for proper_name, dataset in zip(all_names, all_datas):
        ds_4, ds_9, ds_16 = dataset
        labels = ['4', '9', '16']
        names = ['Basic Operations', 'Recursion Count', 'Time']
        units = ['number', 'number', 'ms']
        dirname = 'output/v5/' +proper_name
        if not os.path.exists(dirname):
            os.makedirs(dirname)
        for index, (name, unit) in enumerate(zip(names, units)):
            for a, l in zip([ds_4, ds_9, ds_16], labels):
                plt.plot(a[0], a[1+index], label=f"{l}x{l} board")
            plt.xlabel('Number of empty Cells')
            plt.ylabel(name + f"({unit})")
            plt.title(name + f"({unit}) vs Number of empty Cells")
            plt.legend()
            plt.savefig(dirname + "/" + name.replace(" ", '') +".png")
            plt.show()

def make_more_combined_graphs():
    all_datas = [v1_best_case(False), v2_worst_case_no_stopping(False), v3_average_case(False)]
    # print("MEEP")
    # print(all_datas[-1][-1][0])
    # print(all_datas[-1][-1][1])
    # print("MEEP2")
    # print(all_datas[-2][-1][0])
    # print(all_datas[-2][-1][1])
    # return
    all_names = ['bestCase', 'worstCase', 'averageCase']
    dirname = 'output/v6/'
    names = ['Basic Operations', 'Recursion Count', 'Time']
    units = ['number', 'number', 'ms']
    if not os.path.exists(dirname):
        os.makedirs(dirname)
    for index, (name, unit) in enumerate(zip(names, units)):
        plt.figure()
        for proper_name, dataset in list(zip(all_names, all_datas))[1:]:
            ds_4, ds_9, ds_16 = dataset
            labels = ['4', '9', '16']
            for a, l in list(zip([ds_4, ds_9, ds_16], labels)):
                plt.plot(a[0], a[1+index], label=f"{l}x{l} board " + proper_name)
            plt.xlabel('Number of empty Cells')
            plt.ylabel(name + f"({unit})")
            plt.title(name + f"({unit}) vs Number of empty Cells")
            plt.legend()
        plt.savefig(dirname + "/" + "all_" + name.replace(' ', '') + ".png")
    plt.show()


if __name__ == '__main__':
    # v4_boards()
    # exit()
    # v1_best_case(True)
    v1_best_case(True)
    v2_worst_case_no_stopping()
    v3_average_case()
    v4_boards()
    make_combined_graphs()
    make_more_combined_graphs()
    exit()
    v4_boards();
    best_data = v1_best_case()
    worst_data = v2_worst_case_no_stopping()

    plt.figure()

    for i in range(1,len(best_data)):
        plt.plot(best_data[i][0], best_data[i][1], label=str(i))
        # plt.plot(wd[0], wd[1], label='wc')
        plt.legend()
    plt.show()

    # wrangle_csv_v2('data/v10_actual_best_100_81_9_100basicOps-26-09-2020.csv', 'linear','open cells', 'basic operations')
    # wrangle_csv_v2('data/v10_actual_best_100_256_16_100basicOps-26-09-2020.csv', 'linear','open cells', 'basic operations')
    # wrangle_csv('data/v10_actual_best_100_256_16_100basicOps-26-09-2020.csv', 'open cells', 'basic operations')
    # wrangle_csv('data/v10_actual_best_100_256_16_100basicOps-26-09-2020.csv', 'open cells', 'basic operations')
    # wrangle_csv_v2('data/v6_worst_case_no_stop_2_9_9_2-23-09-2020.csv', 'log','open cells', 'basic operations')
    # wrangle_csv_v2('data/v6_worst_case_no_stop_2_7_16_2-23-09-2020.csv', 'log','open cells', 'basic operations')
    # wrangle_csv_v2('data/v6_worst_case_no_stop_2_7_16_2-23-09-2020.csv', 'exp','open cells', 'basic operations')
    # wrangle_csv('data/v10_actual_best_100_81_9_100basicOps-26-09-2020.csv', 'open cells', 'basic operations')