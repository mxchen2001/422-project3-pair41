JFLAGS = -cd
JC = javac
J = java
PACKAGE = assignment3
MAIN = Main

default:
	$(JC) $(PACKAGE)/$(MAIN).java
test: clean default
	clear
	$(J) $(PACKAGE).$(MAIN)
clean:
	$(RM) $(PACKAGE)/*.class
rerun: clean default test