import os
import filecmp

path_to_src = '*.java'
tests_file = 'tests.txt'
tests = {} #test -> mark for the test
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
marks = marks + 1

######### Run tests ########
for test_name in tests.keys():
    print('\nStarting', test_name, '...')
    cmd = 'java Scheduling ' + test_name + '.conf'
    ret = os.system(cmd)
    if ret:
        print('Runtime Error in Test ' + test_name)
        continue

    with open('Summary-Processes', 'r') as f1, open(test_name + '.test', 'r') as f2:
        content1 = f1.read()
        content2 = f2.read()
        passed = content1 == content2

    print('Passed:', passed)

    # with open(test_name+'.test') as testfp:
    passed = filecmp.cmp('Summary-Processes', test_name+'.test', shallow=False)

    if passed:
        print(test_name, 'passed.', 'Marks:', tests[test_name])
        marks += tests[test_name]
    else:
        print(test_name, 'failed.')

# process = subprocess.Popen(['javac', '-version'], stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
# process = subprocess.Popen('pwd', stdout=subprocess.PIPE, stderr=subprocess.PIPE)
# stdout, stderr = process.communicate()
# print(stderr.decode('UTF-8'))

# for test_name in tests.keys():





