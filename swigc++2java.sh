rm -rf src/main/java/com/bj58/spider/faiss/*.java swigfaiss4j.cpp
mkdir -p src/main/java/com/bj58/spider/faiss/
# swig -version == 4.0
# -I../faiss: 确保是faiss源码路径
swig -c++ -java -package com.bj58.spider.faiss -o swigfaiss4j.cpp  -outdir src/main/java/com/bj58/spider/faiss/ -Doverride= -I../faiss/ swigfaiss4j.i

