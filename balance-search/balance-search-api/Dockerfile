FROM adoptopenjdk/openjdk11:alpine

MAINTAINER Renan Miranda

ADD /build/distributions/balance-search-api-boot-*.zip /balance-search-api.zip

RUN unzip /balance-search-api.zip && \
  rm -rf *.zip && \
  mv /balance-search-api* /balance-search-api

EXPOSE 11116

ENTRYPOINT [ "/balance-search-api/bin/balance-search-api" ]
