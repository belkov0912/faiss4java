export LD_LIBRARY_PATH="."
JAVA="java"
#JAVA="/opt/soft/jdk/jdk1.8.0_66/bin/java"
$JAVA -Djava.library.path="." -classpath target/faiss4java-1.0.0-jar-with-dependencies.jar com.bj58.spider.faiss4j.cf_index "part-00000"
