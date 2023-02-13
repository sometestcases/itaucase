FROM adoptopenjdk/openjdk11:alpine

MAINTAINER Renan Miranda

ADD /build/distributions/balance-search-listener-boot-*.zip /balance-search-listener.zip

RUN unzip /balance-search-listener.zip && \
  rm -rf *.zip && \
  mv /balance-search-listener* /balance-search-listener

EXPOSE 11117

ENTRYPOINT [ "/balance-search-listener/bin/balance-search-listener" ]
