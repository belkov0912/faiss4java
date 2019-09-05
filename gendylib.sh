rm -rf swigfaiss4j.dylib
/usr/local/opt/llvm/bin/clang++ -std=c++11 -fPIC -fopenmp -m64 -Wall -g -O3 -Wextra -msse4 -mpopcnt  -L/usr/local/lib -I../faiss/ -I$JAVA_HOME/include/ -I$JAVA_HOME/include/darwin/ -lfaiss  swigfaiss4j.cpp -shared -o libswigfaiss4j.dylib

