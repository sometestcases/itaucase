FROM adoptopenjdk/openjdk11:alpine

MAINTAINER Renan Miranda

ADD /build/distributions/account-api-boot-*.zip /account-api.zip

RUN unzip /account-api.zip && \
  rm -rf *.zip && \
  mv /account-api* /account-api

EXPOSE 11111

ENTRYPOINT [ "/account-api/bin/account-api" ]