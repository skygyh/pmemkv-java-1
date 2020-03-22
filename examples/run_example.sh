#!/bin/bash
javac -cp ../target/classes/ BasicExample.java
PMEM_IS_PMEM_FORCE=1 java -ea -Xms1G -cp .:../target/classes/ -Djava.library.path=/usr/local/lib BasicExample
