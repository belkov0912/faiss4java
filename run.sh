rm -rf src/main/java/com/bj58/spider/faiss/* swigfaiss4j.cpp
#swig -c++ -java -package com.bj58.spider.faiss -o swigfaiss4j.cpp  -outdir src/main/java/com/bj58/spider/faiss/ -Doverride= -I../faiss/ swigfaiss4j.i

#g++ -std=c++11 -fPIC -fopenmp -m64 -Wall -g -O3 -Wextra -msse4 -mpopcnt  -L/usr/local/lib -I../faiss/ -I$JAVA_HOME/include/ -I$JAVA_HOME/include/darwin/ -lfaiss  swigfaiss4j.cpp -shared -o swigfaiss4j.dylib

