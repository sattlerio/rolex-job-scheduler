FROM docker-builds.sattler.io/sbt_parent
MAINTAINER Georg Victor Sattler

ARG BUILD_NUMBER
ARG BUILD_ID
ARG BUILD_TAG
ARG GIT_COMMIT

ARG DEPLOYER_USER
ARG DEPLOYER_PASS

ADD . /code
WORKDIR /code

EXPOSE 8080

RUN sbt clean assembly

RUN chmod 755 /code/docker-entrypoint.sh
ENTRYPOINT ["/code/docker-entrypoint.sh"]
CMD ["server", "config.yaml"]
