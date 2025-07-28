import os

def remove_files():
    os.remove('Summary-Processes1')
    os.remove('Summary-Results1')
    os.remove('Summary-Processes2')
    os.remove('Summary-Results2')
os.system('javac -nowarn ./p1/*.java')
os.system('javac -nowarn ./p2/*.java')

tests_file = 'tests.txt'
tests = {}


with open(tests_file) as tf:
    for line in tf.readlines():
        test_name, mark = line.split()
        tests[test_name] = int(mark)

def comp_file():
    with open('Summary-Processes1', 'r') as of, open('Summary-Processes2', 'r') as tf:
        of_lines = of.readlines()
        tf_lines = tf.readlines()
        if len(of_lines) != len(tf_lines):
            return 0
        for i in range(len(of_lines)):
            if of_lines[i] != tf_lines[i]:
                return 0
        return 1

for test_name in tests.keys():
    remove_files()
    print('\nStarting', test_name, '...')
    cmd = 'java -cp ./p1 Scheduling ' + test_name + '.conf'
    os.system(cmd)
    os.rename('Summary-Processes', 'Summary-Processes1')
    os.rename('Summary-Results', 'Summary-Results1')
    cmd = 'java -cp ./p2 Scheduling ' + test_name + '.conf'
    os.system(cmd)
    os.rename('Summary-Processes', 'Summary-Processes2')
    os.rename('Summary-Results', 'Summary-Results2')

    if comp_file():
        print('Test', test_name, 'passed')
    else:
        print('Test', test_name, 'failed')
        break
