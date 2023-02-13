FROM adoptopenjdk/openjdk11:alpine

MAINTAINER Renan Miranda

ADD /build/distributions/internal-transfer-api-boot-*.zip /internal-transfer-api.zip

RUN unzip /internal-transfer-api.zip && \
  rm -rf *.zip && \
  mv /internal-transfer-api* /internal-transfer-api

EXPOSE 11114

ENTRYPOINT [ "/internal-transfer-api/bin/internal-transfer-api" ]