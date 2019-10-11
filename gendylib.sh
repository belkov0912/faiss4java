# mac
SHAREDEXT=dylib
# linux
#SHAREDEXT=so

rm -rf swigfaiss4j.$SHAREDEXT

#改成自己的faiss安装目录
FAISS_INSTALL_DIR=/usr/local/

# -L$FAISS_INSTALL_DIR/lib: libfaiss.so/dylib 所在目录
# -I$JAVA_HOME/include/: 找jni.h
# $JAVA_HOME/include/linux/: 找jni_md.h

# linux
#CXX="g++ -std=c++11"
#CXXFLAGS="-fPIC -fopenmp -m64 -Wno-unused-parameter -Wno-unused-parameter -Wno-strict-aliasing -g -O3 -Wextra -msse4 -mpopcnt -fpermissive -L$FAISS_INSTALL_DIR/lib -I$FAISS_INSTALL_DIR/include/faiss/ -I$JAVA_HOME/include/ -I$JAVA_HOME/include/linux/ "
# mac
CXX="/usr/local/opt/llvm/bin/clang++ -std=c++11"
CXXFLAGS="-fPIC -fopenmp -m64 -Wall -g -O3 -Wextra -msse4 -mpopcnt -L$FAISS_INSTALL_DIR/lib -I$FAISS_INSTALL_DIR/include/faiss/ -I$JAVA_HOME/include/ -I$JAVA_HOME/include/darwin/"

${CXX} ${CXXFLAGS} -lfaiss  swigfaiss4j.cpp -shared -o libswigfaiss4j.$SHAREDEXT
