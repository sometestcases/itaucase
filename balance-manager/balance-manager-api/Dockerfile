FROM adoptopenjdk/openjdk11:alpine

MAINTAINER Renan Miranda

ADD /build/distributions/balance-manager-api-boot-*.zip /balance-manager-api.zip

RUN unzip /balance-manager-api.zip && \
  rm -rf *.zip && \
  mv /balance-manager-api-* /balance-manager-api

EXPOSE 11112

ENTRYPOINT [ "/balance-manager-api/bin/balance-manager-api" ]