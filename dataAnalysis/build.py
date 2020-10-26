# Builds the directory structure to submit

# structure is like:
"""
submit/
    images/
    .tex files
    output/ -> images
    generated/ -> generated latex
"""
import os
import shutil

def doAll(name):
    # assumes name does not exist
    if os.path.exists(name):
        # os.removedirs(name)
        raise Exception(name + " already exists")
    os.makedirs(name)
    # latex files
    shutil.copy('../text/v3.tex', name + "/" + "main.tex")
    shutil.copy('../text/bibliography.bib', name)
    # images
    os.system(f'cp -r ../text/images  {name}/')
    os.system(f'cp -r generated  {name}/')
    os.system(f'cp -r output  {name}/')
    # need to do sed
    os.system(f'''sed -i  '' 's_../text_._g'  {name}/main.tex ''')
    os.system(r'''sed -i  '' 's_\graphicspath{ {../dataAnalysis/} }__g'  '''+name+'''/main.tex ''')
    os.system('''sed -i  '' 's_../dataAnalysis_._g'  '''+name+'''/main.tex ''')



if __name__ == '__main__':
    doAll('submits/v2_10_24_08_45')