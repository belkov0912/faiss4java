rm -rf src/main/java/com/bj58/spider/faiss/*.java swigfaiss4j.cpp
mkdir -p src/main/java/com/bj58/spider/faiss/
swig -c++ -java -package com.bj58.spider.faiss -o swigfaiss4j.cpp  -outdir src/main/java/com/bj58/spider/faiss/ -Doverride= -I../faiss/ swigfaiss4j.i


