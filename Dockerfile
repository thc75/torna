FROM java:8
VOLUME /tmp
VOLUME /torna

ADD server/boot/target/torna.jar torna/torna.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/torna/torna.jar"]