# faiss4java

#### Introduction

原始[faiss](https://github.com/facebookresearch/faiss)只支持c++和python，本项目支持最新版本faiss（1.5.3）java接口，主要参考[faiss4j](https://github.com/thenetcircle/faiss4j.git)。
> 以下程序在Mac系统下执行，

#### Building faiss
##### 安装faiss源码，参考[官网](https://github.com/facebookresearch/faiss/blob/master/INSTALL.md)

```
➜ git clone https://github.com/facebookresearch/faiss.git
➜ cd faiss
➜ ./configure --without-cuda   #注意1
➜ make all && make install
➜ make py && make py install
```

#### Building faiss4java
注意faiss4java和faiss同级目录

```
git clone https://github.com/belkov0912/faiss4java.git
./run.sh
```




**注意1**: 虽然上面执行configure会显示

```
...
checking for sgemm_ in -lopenblas... yes
...
```
但当测试blas库时，会报如下错误：
```
➜ make misc/test_blas
➜ ./misc/test_blas
dyld: Library not loaded: @rpath/libopenblas_nehalemp-r0.2.19.dylib
  Referenced from: /Users/jiananliu/test/faiss/./misc/test_blas
  Reason: image not found
[1]    89887 abort      ./misc/test_blas
```
后面在java中System.loadLibrary("swigfaiss4j");的时候也会报上面类似的错误。  
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

