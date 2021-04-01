#!/bin/bash
HERE=`pwd`
if [ ! -d ${HERE}/../../../../target/classes/ ]
then
	echo "Please firstly run \"mvn compile\" at the root project path"
	exit 1
fi
pushd `pwd`
cd ../../../../
mvn compile
popd
export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Djava.library.path=/usr/local/lib"
export  LD_LIBRARY_PATH=LD_LIBRARY_PATH:/usr/local/lib64
export PMEMOBJ_CONF="sds.at_create=0"
export PMEMBLK_CONF="sds.at_create=0"
javac -cp .:${HERE}/../../../../target/classes/ BasicExample.java -d ${HERE}/../../../../target/classes/
PMEM_IS_PMEM_FORCE=1 java -ea -Xms1G -cp ${HERE}/../../../../target/classes/ -Djava.library.path=/usr/local/lib examples.BasicExample
