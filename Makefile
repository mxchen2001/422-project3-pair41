JFLAGS = -cd
JC = javac
J = java
PACKAGE = assignment3
MAIN = Main
# default HEAP size is 128MB
HEAP = 1024m
# default STACK size is 512KB
STACK = 1024m

default:
	$(JC) $(PACKAGE)/$(MAIN).java
test: clean default
	clear
	$(J) $(PACKAGE).$(MAIN)
testm: clean default
	clear
	$(J) -Xss$(STACK) -Xms$(HEAP) $(PACKAGE).$(MAIN)
output: clean default
	$(J) -Xss$(STACK) -Xms$(HEAP) $(PACKAGE).$(MAIN) > output.txt
clean:
	$(RM) $(PACKAGE)/*.class
rerun: clean default test