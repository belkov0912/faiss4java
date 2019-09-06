export LD_LIBRARY_PATH="."
JAVA="java"
#JAVA="/opt/soft/jdk/jdk1.8.0_66/bin/java"
JAR="target/faiss4java-1.0.0-jar-with-dependencies.jar"

ulimit -c unlimited
$JAVA -Djava.library.path="." -classpath $JAR com.bj58.spider.faiss4j.FaissIndex "part-00000" "128"
