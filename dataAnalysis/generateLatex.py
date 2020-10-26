import glob
import os
IMAGE_SIZE=0.5


def generate_v1():
    # single_thing =\
# r"""
# \begin{figure}[H]
#     \includegraphics[width="""+str(IMAGE_SIZE)+r"""\textwidth,center]{IMAGE}
#     \caption{PRE_CAPTION $CAPTION$}
# \end{figure}
# """
    single_thing= \
r"""\subfloat[PRE_CAPTION $CAPTION$]{
\includegraphics[width="""+str(IMAGE_SIZE)+r"""\linewidth, height=0.35\linewidth]{IMAGE}
}"""
    names = ['Basic Operations', 'Recursion Count', 'Time']

    final  = ""
    final += r'\begin{figure}[H]' + "\n" + "\centerfloat\n"
    for name in names:
        # final += r'\paragraph{' + name + r'}\mbox{}\\'
        print("I")
        for size in [4, 9, 16]:
            dir = f'output/v1_best_case_size{size}_count_1000'
            replaced = name.replace(' ', '')
            statsName = 'stats' + replaced + ".txt"
            chart = replaced + "_linear.png"
            imgPath = os.path.join(dir, chart)

            with open(os.path.join(dir, statsName), 'r+') as f:
                first, eq = f.read().split('\n\n')
                l1, l2 = first.split('\n')[1:3]
                l1 = float(l1.split('=')[-1].strip())
                l2 = float(l2.split('=')[-1].strip())

                s = 10000
                l1 = str(round(l1 * s) / s)
                l2 = str(round(l2 * s) / s)

                eq = eq.strip().split('n=')[-1].replace('Slope', l1).replace("intercept", l2)
                eq = eq.replace('e-', r'\text{e-}').replace('e+', r'\text{e}').replace('*', r' \times ')
                if '+ -' in eq:
                    eq = eq.replace('+', '')  # replace + with ''
                # print(eq)
            final += single_thing.replace('IMAGE', imgPath)\
                         .replace("PRE_CAPTION", "{0} x {0} board.".format(size))\
                         .replace('CAPTION', eq) + "\n"
            # if size == 9: final +="\n"
        final += r'\caption{' + name + r'}' + "\n"

    final += r'\end{figure}' + "\n"
    # print(final)
    final = final.replace('$$', '')
    with open("generated/v1.tex", 'w+') as f:
        f.write(final)



def generate_v2_better():
    single_thing = \
        r"""\subfloat[PRE_CAPTION $CAPTION$]{
        \includegraphics[width=""" + str(IMAGE_SIZE) + r"""\linewidth, height=0.35\linewidth]{IMAGE}
}"""
    names = ['Basic Operations', 'Recursion Count', 'Time']

    final = ""
    final += r'\begin{figure}[H]' + "\n" + "\centerfloat"

    for name in names:
        # final += r'\paragraph{' + name + r'}\mbox{}\\'
        print("I")
        final += '\n'
        count = -1
        for size in [4, 9, 16]:
            dir = f'output/v2_worst_case_size{size}_count_1000_numtodo_9'
            replaced = name.replace(' ', '')
            statsName = 'stats' + replaced + ".txt"
            with open(os.path.join(dir, statsName), 'r+') as f:
                first, eq = f.read().split('\n\n')
                l1, l2 = first.split('\n')[1:3]
                l1 = float(l1.split('=')[-1].strip())
                l2 = float(l2.split('=')[-1].strip())

                # l1 = str(round(l1 * 10000) / 10000)
                l1 = "{:.2e}".format(l1)
                l2 = "{:.2e}".format(l2)
                # l2 = str(round(l2 * 100) / 100)

                eq = eq.strip().split('n=>')[-1].replace('$', '') \
                    .replace('Slope', l1).replace("intercept", l2)

                eq = eq.replace('e-', r'\text{e-}').replace('e+', r'\text{e}').replace('*', r' \times ')

                if '+ -' in eq:
                    eq = eq.replace('+', '')  # replace + with ''
                # print(eq)

            for suffix in ['linear', 'exp'][:1]:
                count += 1;
                chart = replaced + "_" + suffix + ".png"
                imgPath = os.path.join(dir, chart)
                # if count % 2 == 0: final += '\n'

                final += single_thing.replace('IMAGE', imgPath) \
                             .replace("PRE_CAPTION", "{0} x {0} board.".format(size)) \
                             .replace('CAPTION', eq) + "\n"
        # print(final)
        final += r'\caption{' + name + r'}'
    final += r'\end{figure}' + '\n'
    final = final.replace('$$', '')
    with open("generated/v2.tex", 'w+') as f:
        f.write(final)



def generate_v3():
    single_thing = \
r"""\subfloat[PRE_CAPTION $CAPTION$]{
\includegraphics[width=""" + str(IMAGE_SIZE) + r"""\linewidth, height=0.35\linewidth]{IMAGE}
}"""
    names = ['Basic Operations', 'Recursion Count', 'Time']

    final  = ""
    final += r'\begin{figure}[H]' + "\n" + "\centerfloat\n"
    for name in names:
        # final += r'\paragraph{' + name + r'}\mbox{}\\'
        print("I")
        count = -1
        for size in [4, 9, 16]:
            n = 70
            c = 1000
            if size == 4:
                n = 15
            elif size == 16:
                n = 130
            dir = f'output/v5_average_case_size{size}_count_{c}_numtodo_{n}'
            replaced = name.replace(' ', '')
            statsName = 'stats' + replaced + ".txt"
            with open(os.path.join(dir, statsName), 'r+') as f:
                first, eq = f.read().split('\n\n')
                l1, l2 = first.split('\n')[1:3]
                l1 = float(l1.split('=')[-1].strip())
                l2 = float(l2.split('=')[-1].strip())
                s = 10000
                l1 = str(round(l1 * s) / s)
                l2 = str(round(l2 * s) / s)

                eq = eq.strip().split('n=>')[-1].replace('$','')\
                    .replace('Slope', l1).replace("intercept", l2)
                # print(eq)
                eq = eq.replace('e-', r'\text{e-}').replace('e+', r'\text{e}').replace('*', r' \times ')

                if '+ -' in eq:
                    eq = eq.replace('+', '')  # replace + with ''

            for suffix in ['linear', 'exp'][:1]:
                count+=1;
                chart = replaced + "_"+suffix+".png"
                imgPath = os.path.join(dir, chart)
                # if count % 2 == 0: final+='\n'

                final += single_thing.replace('IMAGE', imgPath)\
                             .replace("PRE_CAPTION", "{0} x {0} board.".format(size))\
                             .replace('CAPTION', eq) + "\n"
        final += r'\caption{' + name + r'}'
        # print(final)
    final += r'\end{figure}' + '\n'
    final = final.replace('$$', '')
    with open("generated/v3.tex", 'w+') as f:
        f.write(final)



def generate_v4_better():
    single_thing = \
r"""\subfloat[PRE_CAPTION $CAPTION$]{
\includegraphics[width=""" + str(0.5) + r"""\linewidth, height=0.35\linewidth]{IMAGE}
}"""
    names = ['Basic Operations', 'Recursion Count', 'Time']
    names = ['Number of Empty Cells', 'Number of Basic Operations', 'Recursion Count', 'Time']
    final = ""
    def temp(suffix, final):
        for name in names:
            # final += r'\paragraph{' + name + r'}\mbox{}\\'
            print("I")
            final += '\n'
            final+=r'\begin{figure}[H]' + "\n" + "\centerfloat\n"
            for size in [4, 9, 16]:
                dir = f'output/v4_boards{size}_count_{1000}'
                replaced = name.replace(' ', '')
                chart = replaced + "_" + suffix + ".png"
                imgPath = os.path.join(dir, chart)
                # if count % 2 == 0: final += '\n'

                final += single_thing.replace('IMAGE', imgPath) \
                             .replace("PRE_CAPTION", "{0} x {0} board.".format(size)) \
                             .replace('CAPTION', '') + "\n"
            # for size in [4, 9, 16]:final = temp_f('box',final)

            final += r'\caption{' + name + r'}'
            final += r'\end{figure}' + '\n'
        return final

    final = temp('bar', final)
    final = temp('box', final)
    # now add the other, i.e. the boards on one graph
    names = ['Number of Basic Operations', 'Recursion Count', 'Time']

    final += r'\paragraph{' + "Single graphs" + r'}'  +"\n"
    final += "Here we combine the above data to show the relationship between the number of cells and the average of our metrics. " \
             r"This was obtained by averaging our metrics per board size and plotting that vs the number of cells (e.g. $9 \times 9$ has $81$ cells)";
    final += r'\begin{figure}[H]' + "\n" + "\centerfloat\n"
    for name in names:
        # final += r'\paragraph{' + name + r'}\mbox{}\\'
        print("I")
        count = -1
        dir = f'output/v4_boards_all'
        replaced = name.replace(' ', '')
        statsName = 'stats' + replaced + ".txt"
        with open(os.path.join(dir, statsName), 'r+') as f:
            first, eq = f.read().split('\n\n')
            l1, l2 = first.split('\n')[1:3]
            l1 = float(l1.split('=')[-1].strip())
            l2 = float(l2.split('=')[-1].strip())

            s = 10000
            l1 = str(round(l1 * s) / s)
            l2 = str(round(l2 * s) / s)

            eq = eq.strip().split('n=>')[-1].replace('$', '') \
                .replace('Slope', l1).replace("intercept", l2)

            if '+ -' in eq:
                eq = eq.replace('+', '') #replace + with ''
            # print(eq)

        # count += 1
        chart = replaced + ".png"
        imgPath = os.path.join(dir, chart)
        # if count % 2 == 0: final += '\n'

        final += single_thing.replace('IMAGE', imgPath) \
                     .replace("PRE_CAPTION", '') \
                     .replace('CAPTION', eq) + "\n"
    final += r'\end{figure}' + '\n'
    final = final.replace('$$', '')
    with open("generated/v4.tex", 'w+') as f:
        f.write(final)


def generate_v5():
    single_thing = \
r"""\subfloat[PRE_CAPTION $CAPTION$]{
\includegraphics[width=""" + str(0.5) + r"""\linewidth,height=0.35\linewidth]{IMAGE}
}"""
    names = ['Basic Operations', 'Recursion Count', 'Time']
    final = ""
    final += r'\subsection{' + "Split by case" + r'}'
    pretty_cases = ['Best Case', 'Average Case','Worst Case' ]
    final += r'\begin{figure}[H]' + "\n" + "\centerfloat\n"
    for case, pcase in zip(['bestCase', 'averageCase', 'worstCase'], pretty_cases):
        final += '\n'
        count = -1
        for name in names:
            # final += r'\paragraph{' + name + r'}\mbox{}\\'
            print("I")
            replaced = name.replace(' ', '')
            dir = f'output/v5/'+case
            count += 1
            chart = replaced + ".png"
            imgPath = os.path.join(dir, chart)
            # if count % 3 == 0: final += '\n'

            final += single_thing.replace('IMAGE', imgPath)\
                         .replace("PRE_CAPTION", pcase)\
                         .replace('CAPTION', "") + "\n"
        final += r'\caption{' + pcase + r'}'

    final += r'\end{figure}' + '\n'
    final = final.replace('$$', '')
    with open("generated/v5.tex", 'w+') as f:
        f.write(final)


def generate_v6():
    single_thing = \
r"""\subfloat[PRE_CAPTION $CAPTION$]{
\includegraphics[width=""" + str(0.5) + r"""\linewidth]{IMAGE}
}"""
    names = ['Basic Operations', 'Recursion Count', 'Time']
    final = ""
    final += '\n'
    final += r'\begin{figure}[H]' + "\n" + "\centerfloat\n"
    count = -1
    for name in names:
        # final += r'\paragraph{' + name + r'}\mbox{}\\'
        print("I")
        replaced = name.replace(' ', '')
        dir = f'output/v6/'
        count += 1
        chart = "all_"+replaced + ".png"
        imgPath = os.path.join(dir, chart)
        # if count % 3 == 0: final += '\n'

        final += single_thing.replace('IMAGE', imgPath)\
                     .replace("PRE_CAPTION", name)\
                     .replace('CAPTION', "") + "\n"
    final += r'\end{figure}' + '\n'
    final = final.replace('$$', '')
    with open("generated/v6.tex", 'w+') as f:
        f.write(final)

def camel_case_to_multiple_words(s):
    ans = ""
    for c in s:
        if ans == "":
            ans+=c
            continue
        if 65 <= ord(c) <= 92:
            ans += " "
        ans += c
    return ans

def generate_regression_analysis():
    """
    This generates regression analysis for v1, v2, v3
    :return:
    """
    single_table_row = r'{} & {} & {} & {} & {} & {} & {} & {} & {} \\'
    header = single_table_row.format('Case', 'x', 'y','Size',  'a', 'b', 'r', r'$\sigma$', 'equation')
    final_string =\
r"""
\begin{tabular}{ """+"|c" * (header.count('&')+1)+'|' + """ } 
""" + header
    dir_names = ['v1', 'v2', 'v5_', 'v4_boards_all']

    all_data = []

    for temp_d in dir_names:
        dirs = glob.glob('output/' + temp_d + "*")
        for d in dirs:
            if temp_d == 'v4_boards_all':
                case = 'Boards'
                size = 0
            else:
                case = (d.split('_')[1])
                size = int(d.split('_')[3][4:])
            print("Working on Size: ", size, "case", case)
            # get all text files
            all_files = glob.glob(d + "/*.txt")
            print("ABC ", all_files, d, d + "/*.txt")
            for f in all_files:
                if 'stats.txt' in f: continue # don't use stats.txt, old
                metric_name = f.split('/')[-1][5:-4]
                with open(f, 'r') as file:
                    text = file.readlines()
                def after_eq(s):
                    # returns the text after the equals sign
                    # return s
                    return s[s.find('=')+1:].replace('$', '')
                lines = [after_eq(l_.strip()) for l_ in text if l_.strip() !='']
                slope, intercept, r, p, stderr, eq = lines
                def pr(n):
                    if float(n) == 0: return ''
                    x = 1000000
                    return round(float(n)*x)/x
                slope = pr(slope)
                intercept = pr(intercept)
                r = pr(r)
                p = pr(p)
                if p == 0.0: p = ''

                stderr = pr(stderr)
                eq = eq.replace('slope', 'a').replace('Slope', 'a')
                eq = eq.replace('intercept', 'b').replace('Intercept', 'b').replace('>', '')
                # final_string += "\n" + single_table_row.format(
                #     case, size, '\# of EC',metric_name, slope, intercept, r, p, stderr, '$'+eq+'$'
                #  )
                all_data.append([case, '\# of Empty Cells',camel_case_to_multiple_words(metric_name),size,  slope, intercept, r, stderr, '$'+eq+'$'])
                print(lines)
            print(all_files)
    # now sort all data
    def sort_key(li):
        case, y, size = li[0], li[2], li[3]
        if case == 'best': case_num = 0
        elif case == 'worst': case_num = 1
        elif case == 'average': case_num = 2
        else: case_num = 3
        y_num = ord(y[0]) # arbitrary
        return (case_num * 1000000 + y_num * 100 + size)
    all_data = sorted(all_data, key=sort_key)
    i=0
    got_boards = False
    for d in all_data:
        if d[3] == 0: d[3] = ''
        if i != 0: d[1] = ''
        if i%3 != 0 :
            if d[0] != 'Boards':
                d[2] = ''
        else:
            final_string += "\n\hline"
        if  d[0]  == "Boards" and not got_boards:
            d[1] = 'Number of Cells'
            got_boards = True
            print("MEEP ", i, case, d, single_table_row.format(*d))
        if d[4] == 1.0:
            d[4] = ''
        final_string += "\n" + single_table_row.format(*d)
        i+=1
    final_string+="\n" +r"\end{tabular}"
    print(final_string)
    with open('generated/regression.tex', 'w+') as f:
        f.write(final_string)

"""
TODO USE THIS
\begin{figure}
\subfloat[first]{
\includegraphics[width=65mm]{output/v3_average_case_size9_count_100_numtodo_55/BasicOperations_linear.png}
}
\subfloat[second]{
\includegraphics[width=65mm]{output/v3_average_case_size9_count_100_numtodo_55/BasicOperations_linear.png}
}
\hspace{0mm}
\subfloat[third]{
\includegraphics[width=65mm]{output/v3_average_case_size9_count_100_numtodo_55/BasicOperations_linear.png}
}
"""
if __name__ == '__main__':
    generate_regression_analysis()

    # exit()
    generate_v1()
    generate_v2_better()
    generate_v3()
    generate_v4_better()
    generate_v5()
    generate_v6()
    generate_regression_analysis()
    #
    exit()
    # generate_v5()
    generate_v1()
    generate_v2()
    generate_v3()
    generate_v4()
    generate_v5()
    generate_v6()
