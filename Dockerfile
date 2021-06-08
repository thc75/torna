FROM java:8

MAINTAINER tanghc
EXPOSE 7700

VOLUME /tmp
RUN mkdir -p /torna/config

ADD server/boot/target/torna.jar torna/torna.jar
ADD front/dist torna/dist

# set jvm
ENV JAVA_OPTS="-server -Xmx512m -Xms512m -Djava.awt.headless=true -Duser.timezone=Asia/Shanghai"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /torna/torna.jar --spring.config.location=/torna/config/application.properties" ]