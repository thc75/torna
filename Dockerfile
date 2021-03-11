FROM java:8

MAINTAINER tanghc

VOLUME /tmp
VOLUME /torna

ADD server/boot/target/torna.jar torna/torna.jar
ADD front/dist torna/

# set jvm
ENV JAVA_OPTS="-server -Xmx512m -Xms512m -Djava.awt.headless=true"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /torna/torna.jar" ]