# an example of compiling a java class
CLASSES=$(patsubst %.java,%.class,$(wildcard *.java))


all:: ${CLASSES}
	chmod +x ./SIM

