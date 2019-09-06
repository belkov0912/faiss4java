# mac
SHAREDEXT   = dylib
# linux
#SHAREDEXT   = dylib

rm -rf swigfaiss4j.$SHAREDEXT

# -L/usr/local/lib: libfaiss.so/dylib 所在目录
# -I$JAVA_HOME/include/: 找jni.h
# $JAVA_HOME/include/linux/: 找jni_md.h
# linux
# g++ -std=c++11 -fPIC -fopenmp -m64 -Wall -g -O3 -Wextra -msse4 -mpopcnt -fpermissive -L/usr/local/lib -I../faiss/ -I$JAVA_HOME/include/ -I$JAVA_HOME/include/linux/ -lfaiss  swigfaiss4j.cpp -shared -o libswigfaiss4j.$SHAREDEXT
# mac
/usr/local/opt/llvm/bin/clang++ -std=c++11 -fPIC -fopenmp -m64 -Wall -g -O3 -Wextra -msse4 -mpopcnt  -L/usr/local/lib -I../faiss/ -I$JAVA_HOME/include/ -I$JAVA_HOME/include/darwin/ -lfaiss  swigfaiss4j.cpp -shared -o libswigfaiss4j.$SHAREDEXT

