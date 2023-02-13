FROM adoptopenjdk/openjdk11:alpine

MAINTAINER Renan Miranda

ADD /build/distributions/internal-transfer-listener-boot-*.zip /internal-transfer-listener.zip

RUN unzip /internal-transfer-listener.zip && \
  rm -rf *.zip && \
  mv /internal-transfer-listener* /internal-transfer-listener

EXPOSE 11115

ENTRYPOINT [ "/internal-transfer-listener/bin/internal-transfer-listener" ]