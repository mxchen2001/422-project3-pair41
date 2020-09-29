import sys, getopt

def oneWordDiff(word1, word2):
    counter = 0
    for i in range(0, 5):
        if(word1[i] != word2[i]):
            counter += 1
    if (counter == 1): 
        return False
    else: 
        return True

class bcolors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m' # yellow
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'

def main(argv):
    try:
        args = argv[0]
    except getopt.GetoptError:
        print('test.py <inputfile>')
        sys.exit(2)
    print("Using: " + args)
    wordLadder = open(args, 'r') 
    Lines = wordLadder.readlines() 

    result = bcolors.OKGREEN + "words form a word latter" + bcolors.ENDC
    count = 0
    previousWord = str()
    for Line in Lines: 
        # print("{}".format(Line.strip())) 
        if(count > 1):
            if(oneWordDiff(Line.strip(), previousWord)):
                result = bcolors.FAIL + "Error: " + Line.strip() + " and " + previousWord + " don't have a one letter difference at lines: " + str(count) + " and " + str(count + 1) + bcolors.ENDC
        count += 1
        previousWord = Line.strip()

    print(result)

if __name__ == "__main__":
   main(sys.argv[1:])
