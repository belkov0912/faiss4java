# faiss4java

### Introduction

原始[faiss](https://github.com/facebookresearch/faiss)只支持c++和python，本项目支持v1.5.3版本faiss java接口，主要参考[faiss4j](https://github.com/thenetcircle/faiss4j.git)。
> 以下过程在Mac Mojave系统下执行

### Building faiss
1. 安装faiss源码，参考[官网](https://github.com/facebookresearch/faiss/blob/master/INSTALL.md)

```
➜ git clone -b v1.5.3 https://github.com/facebookresearch/faiss.git
➜ cd faiss
➜ ./configure --without-cuda   #注意1
➜ make all && make install
➜ make py && make py install
```
2. 测试openblas库，

```
errors = ... （不用管）
info=0000064b00000000
Lapack uses xx-bit integers
```
出现如上结果，说明成功了，直接跳到Buiding faiss4java部分。  
3. 如出现下面错误：
```
➜ make misc/test_blas
➜ ./misc/test_blas
dyld: Library not loaded: @rpath/libopenblas_nehalemp-r0.2.19.dylib
  Referenced from: /Users/jiananliu/test/faiss/./misc/test_blas
  Reason: image not found
[1]    89887 abort      ./misc/test_blas
```
后面在java中System.loadLibrary("swigfaiss4j");的时候也会报上面类似的错误。
虽然上面执行./configure会显示

```
...
checking for sgemm_ in -lopenblas... yes
...
```
  
所以，需要指定--with-blas参数，即
```
➜ ./configure --without-cuda --with-blas=/usr/local/opt/openblas/lib/libopenblas.dylib
➜ rm -f misc/test_blas  #make clean不能删除这个
➜ make misc/test_blas
➜ ./misc/test_blas
```

对应的openblas路径可以用如下命令查找：

```
➜ brew info openblas
```
重新编译faiss源码。
### Building faiss4java
1. 注意faiss4java和faiss同级目录，确保swig版本是4.0。

```
➜ git clone https://github.com/belkov0912/faiss4java.git
# swig c++转成java接口，并生成swigfaiss4j.cpp
➜ ./swigc++2java.sh
# 生成动态库，mac后缀是dylib，linux是so
➜ ./gendylib.sh
```

2. 编译完java接口，用idea打开会提示如下错误：

```
  protected xxxclass(long cPtr, boolean cMemoryOwn) {
    super(swigfaissJNI.IndexReplicas_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }
  
  Error:java: 已在类 com.bj58.spider.faiss.xxx 中定义了构造器 xxx(long,boolean)
```
直接把提示的构造器删除就可以了（可能swig解析c++模版类有关，不太清楚原理，欢迎大家指教）。

3. 运行com.bj58.spider.faiss.tests.Examples，需要配置java.library.path

```
#VM options中加入
-Djava.library.path="/xxx/faiss4j" 
```
运行结果如下：
```
19-09-03 18:45:43 [main] INFO  Examples:49 - is_trained = true
19-09-03 18:45:43 [main] INFO  Examples:51 - ntotal = 10
19-09-03 18:45:43 [main] INFO  Examples:62 - search 5 first vector of xb
19-09-03 18:45:44 [main] INFO  Examples:64 - Vectors:
0	|0.00000 0.897081 0.0970035 0.0361408 0.149420 
1	|0.00100000 0.286766 0.871114 0.127865 0.528833 
2	|0.00200000 0.332409 0.414103 0.197723 0.603372 
...
```



