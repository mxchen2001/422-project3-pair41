#!/usr/bin/env python2

import sys
import os
import datetime
import shlex
import subprocess
import tempfile
import glob
import shutil
import fnmatch
import csv
from zipfile import ZipFile
import xml.etree.ElementTree as xmlparser


def copy_all_contents(src, dest):
    glob_str1 = os.path.join(src, '*')
    glob_str2 = os.path.join(src, '.*')
    for path in glob.glob(glob_str1) + glob.glob(glob_str2):
        if os.path.isdir(path):
            name = os.path.basename(path)
            shutil.copytree(path, os.path.join(dest, name))
        else:
            shutil.copy2(path, dest)

def safe_unzip(zip_file, dest='.', overwrite=True):
    zf = ZipFile(zip_file, 'r')
    try:
        for member in zf.infolist():
            member_dest = os.path.join(dest, member.filename)
            abs_member_dest = os.path.abspath(member_dest)
            if abs_member_dest.startswith(os.path.abspath(dest)):
                if overwrite or not os.path.exists(member_dest):
                    zf.extract(member, dest)
    finally:
        zf.close()

def calculate_score(xml_paths, test_score_vals):
    failed_tests = []
    for xml_path in xml_paths:
        root = xmlparser.parse(xml_path).getroot()
        failed_tests += [t.attrib['classname'].replace('$', '.') + '.' + t.attrib['name']
                         for t in root.findall('testcase')
                         if t.findall('error') or t.findall('failure')]

    detract_score = 0
    for testname in failed_tests:
        val = test_score_vals.get(testname)
        if val:
            detract_score += val

    max_score = sum(test_score_vals.values())
    score = max_score - detract_score
    percentage = int(round(100 * (score / float(max_score)))) if max_score else 0
    return (score, max_score, percentage)

def gen_test_results(xml_paths):
    ret = ''
    for xml_path in xml_paths:
        root = xmlparser.parse(xml_path).getroot()
        ret += 'TEST SUITE --------------------------------------------------\n'
        ret += '{0} tests: {1} errors: {2} skipped: {3} failures: {4}\n'.format(
            root.attrib['name'],
            root.attrib['tests'],
            root.attrib['errors'],
            root.attrib['skipped'],
            root.attrib['failures']
        )
        ret += '-------------------------------------------------------------\n'
        ret += '\n'
        for t in root.findall('testcase'):
            errors = t.findall('error')
            failures = t.findall('failure')
            if errors or failures:
                ret += (
                    ('ERROR: ' if errors else 'FAILURE: ') +
                    t.attrib['classname'].replace('$', '.') +
                    '.' + t.attrib['name'] + '\n'
                )
                if errors:
                    ret += errors[0].text + '\n'
                if failures:
                    ret += failures[0].text + '\n'
            else:
                ret += (
                    'PASS: ' +
                    t.attrib['classname'].replace('$', '.') +
                    '.' + t.attrib['name'] + '\n'
                )
        ret += '\n'
    return ret

def main():
    # Check arguments
    if len(sys.argv) < 3:
        print(
            'Grade a bunch of student .zip submissions at once.\n' +
            'Usage: python grade.py <project_dir> <zips_dir>'
        )
        exit(1)

    project_dir = os.path.abspath(sys.argv[1])
    if not os.path.isdir(project_dir):
        print('Project folder was not found at {0}'.format(project_dir))
        exit(1)

    packages_dir = os.path.join(project_dir, 'src/main/java')
    if not os.path.isdir(packages_dir):
        print('Packages folder was not found at {0}'.format(packages_dir))
        exit(1)

    submissions_dir = os.path.abspath(sys.argv[2])
    if not os.path.isdir(submissions_dir):
        print('Submissions folder was not found at {0}'.format(submissions_dir))
        exit(1)

    # Create a results directory
    timestamp = datetime.datetime.now().strftime('%Y%m%dT%H%M%S')
    results_dir = os.path.abspath('./grade_results_{0}'.format(timestamp))
    os.mkdir(results_dir)
    print('Results will be in {0}'.format(results_dir))

    # Install Maven dependencies
    os.chdir(os.path.join(project_dir, '..'))
    print('Installing Maven dependencies')
    mvn_install_parent = subprocess.Popen(
        shlex.split('mvn -pl .,score-annotation,test-utils clean install'),
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT
    )
    (mvn_output, unused) = mvn_install_parent.communicate()
    if mvn_install_parent.returncode != 0:
        print('\n' + mvn_output)
        raise Exception('Failed to install Maven dependencies.')

    # Collect the unit test score weights
    print('Collecting score weight of each test')
    mvn_clean = subprocess.Popen(
        shlex.split('mvn clean'),
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        cwd=project_dir
    )
    (mvn_output, unused) = mvn_clean.communicate()
    if mvn_clean.returncode != 0:
        print('\n' + mvn_output)
        raise Exception('Failed to run mvn clean in project folder.')

    mvn_compiler = subprocess.Popen(
        shlex.split('mvn compiler:testCompile@run-score-annotation-processor-only'),
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        cwd=project_dir
    )
    (mvn_output, annotation_processor_output) = mvn_compiler.communicate()
    lines = annotation_processor_output.split('\n')
    tups = [tuple(line.split(':')) for line in lines if line]
    test_score_vals = dict(
        (classname + '.' + name, int(val)) for (classname, name, val) in tups
    )
    if not test_score_vals:
        raise Exception(
            'No score weights were found. Perhaps there were no tests, ' +
            'none of the tests were annotated with @Score, ' +
            'or the Java compiler failed to parse one of the test classes.'
        )
    print(annotation_processor_output)

    # Create a flag that will be set if resetting fails
    reset_failed = False

    # Create a temp dir
    packages_orig_copy_dir = tempfile.mkdtemp()

    try:
        # Copy instructor-provided files into the temp dir for resetting between
        # grading submissions
        print('Copying the contents of {0} into {1}'.format(
            packages_dir, packages_orig_copy_dir
        ))
        copy_all_contents(packages_dir, packages_orig_copy_dir)

        # Grade loop
        print('Beginning to grade\n')
        grades = {}
        for zip_path in glob.glob(os.path.join(submissions_dir, '*.zip')):
            filename = os.path.basename(zip_path)
            submission_name = os.path.splitext(filename)[0]
            print('Grading {0}'.format(filename))

            # Create a result folder for the submission
            submission_results_dir = os.path.join(results_dir, submission_name)
            os.mkdir(submission_results_dir)

            try:
                # Unzip the submission
                safe_unzip(zip_path, packages_dir, False)

                # Clean the submission
                for dirpath, dirnames, filenames in os.walk(packages_dir):
                    for dirname in fnmatch.filter(dirnames, '__MACOSX'):
                        shutil.rmtree(os.path.join(dirpath, dirname))

                # Run tests
                mvn_test = subprocess.Popen(
                    shlex.split('mvn clean test'),
                    stdout=subprocess.PIPE,
                    stderr=subprocess.STDOUT,
                    cwd=project_dir
                )
                (mvn_output, unused) = mvn_test.communicate()

                # Save Maven's output
                output_file_path = os.path.join(submission_results_dir, 'maven_output.txt')
                with open(output_file_path, 'w') as f:
                    f.write(mvn_output)

                # Save Maven Surefire's reports
                surefire_dir = os.path.join(project_dir, 'target', 'surefire-reports')
                copy_all_contents(surefire_dir, submission_results_dir)

                # Calculate the submission's grade
                xml_paths = glob.glob(os.path.join(submission_results_dir, '*.xml'))
                if xml_paths:
                    (score, max_score, grade) = calculate_score(xml_paths, test_score_vals)
                    grades[submission_name] = grade
                    print('{0}/{1}: {2}%'.format(score, max_score, grade))
                else:
                    grades[submission_name] = 0
                    print(
                        'No test reports found! ' +
                        'Compilation failed or JUnit crashed during testing ' +
                        '(see maven_output.txt): 0%'
                    )
                print('')

                # Generate the full test results file
                test_results_str = gen_test_results(xml_paths)
                output_file_path = os.path.join(submission_results_dir, 'FULL_TEST_RESULTS.txt')
                with open(output_file_path, 'w') as f:
                    f.write(test_results_str)
            finally:
                try:
                    # Reset
                    shutil.rmtree(packages_dir)
                    shutil.copytree(packages_orig_copy_dir, packages_dir)
                except:
                    reset_failed = True
                    raise

        # Generate Canvas CSV
        csv_path = os.path.join(results_dir, 'Grades.csv')
        print('Generating Canvas CSV at {0}'.format(csv_path))
        with open(csv_path, 'w') as f:
            writer = csv.writer(f)
            writer.writerows([
                ['Student', 'ID', 'SIS User ID', 'SIS Login ID', 'Section', 'assignment'],
                ['', '', '', '', '', 'Manual Posting'],
                ['    Points Possible', '', '', '', '', 100]
            ])
            for submission_name in grades:
                # Example submission names from Canvas:
                # lastfirst_4337541_54310684_[student's zip filename]
                # lastfirst_LATE_4337541_54310684_[student's zip filename]
                # lastfirst_LATE_4337541_54310684_[student's zip filename]-2
                split = submission_name.split('_')
                student_canvas_id = ''

                if len(split) > 2 and split[1].isdigit():
                    student_canvas_id = split[1]
                elif len(split) > 3 and split[1] == 'LATE' and split[2].isdigit():
                    student_canvas_id = split[2]

                if student_canvas_id:
                    writer.writerow(
                        ['', student_canvas_id, '', '', '', grades[submission_name]]
                    )
                else:
                    print((
                        '"{0}" is not a Canvas submission filename and its ' +
                        'grade will not be added to the Canvas CSV'
                    ).format(submission_name))
    finally:
        if reset_failed:
            print('!!!!!!!!!!! IMPORTANT !!!!!!!!!!!')
            print((
                'An exception occurred while resetting {0}, which means a ' +
                'submission was left unzipped in the folder.\n'
            ).format(packages_dir))
            print('DO NOT RUN THE SCRIPT AGAIN UNTIL YOU CLEAN UP MANUALLY.\n')
            print((
                'To clean up manually, delete {0} if it exists and replace ' +
                'it with a renamed copy of {1}. Then delete {1}.\n'
            ).format(packages_dir, packages_orig_copy_dir))
            print((
                'The corresponding Unix commands are:\n' +
                'rm -rf {0}\n' +
                'mv {1} {0}\n'
            ).format(packages_dir, packages_orig_copy_dir))
        else:
            # Delete the temp dir
            print('Deleting {0}'.format(packages_orig_copy_dir))
            shutil.rmtree(packages_orig_copy_dir)

if __name__ == '__main__':
    main()
