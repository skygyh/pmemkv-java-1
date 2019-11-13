#!/usr/bin/bash
javac -cp target/classes/  -h target/generated-sources/ src/main/java/io/pmem/pmemkv/Database.java
ls target/generated-sources/
echo "Please cp the header file io_pmem_pmemkv_Database.h to jni code path"
