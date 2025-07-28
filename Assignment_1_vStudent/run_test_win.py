import os
import filecmp

path_to_src = '*.java'
tests_file = 'tests.txt'
tests = {}  # test -> mark for the test
marks = 0

with open(tests_file) as tf:
    for line in tf.readlines():
        test_name, mark = line.split()
        tests[test_name] = int(mark)

########## Compile #########


cmd = 'javac -nowarn ' + path_to_src
ret = os.system(cmd)

if ret:
    print('Compilation error')
    exit()
marks = marks + 30


######### Run tests ########

def comp_file(test_name):
    with open('Summary-Processes', 'r') as of, open(test_name + '.test', 'r') as tf:
        of_lines = of.readlines()
        tf_lines = tf.readlines()
        if len(of_lines) != len(tf_lines):
            return 0
        for i in range(len(of_lines)):
            if of_lines[i] != tf_lines[i]:
                return 0
        return 1


for test_name in tests.keys():
    print('\nStarting', test_name, '...')
    cmd = 'java Scheduling ' + test_name + '.conf'
    ret = os.system(cmd)
    if ret:
        print('Runtime Error in Test ' + test_name)
        continue

    # with open(test_name+'.test') as testfp:
    passed = comp_file(test_name)

    if passed:
        print(test_name, 'passed.', 'Marks:', tests[test_name])
        marks += tests[test_name]
    else:
        print(test_name, 'failed.')

print('Full Marks:', str(marks) + '/' + str(sum(tests.values()) + 30))

# process = subprocess.Popen(['javac', '-version'], stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
# process = subprocess.Popen('pwd', stdout=subprocess.PIPE, stderr=subprocess.PIPE)
# stdout, stderr = process.communicate()
# print(stderr.decode('UTF-8'))

# for test_name in tests.keys():
