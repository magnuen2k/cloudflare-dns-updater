FROM openjdk:17.0.2

COPY target/dns-updater.jar .

CMD java -jar dns-updater.jar
