FROM adoptopenjdk/openjdk11:alpine

MAINTAINER Renan Miranda

ADD /build/distributions/balance-manager-listener-boot-*.zip /balance-manager-listener.zip

RUN unzip /balance-manager-listener.zip && \
  rm -rf *.zip && \
  mv /balance-manager-listener-* /balance-manager-listener

EXPOSE 11113

ENTRYPOINT [ "/balance-manager-listener/bin/balance-manager-listener" ]