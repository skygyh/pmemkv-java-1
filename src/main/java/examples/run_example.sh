#!/bin/bash
HERE=`pwd`
if [ ! -d ${HERE}/../../../../target/classes/ ]
then
	echo "Please firstly run \"mvn compile\" at the root project path"
	exit 1
fi
javac -cp .:${HERE}/../../../../target/classes/ BasicExample.java -d ${HERE}/../../../../target/classes/
PMEM_IS_PMEM_FORCE=1 java -ea -Xms1G -cp ${HERE}/../../../../target/classes/ -Djava.library.path=/usr/local/lib examples.BasicExample
