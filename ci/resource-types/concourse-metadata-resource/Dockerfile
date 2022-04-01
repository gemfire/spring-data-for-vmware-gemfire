FROM alpine

RUN apk --no-cache add \
  bash \
  jq \
  util-linux
RUN mkdir -p /opt/resource
ADD files/check files/in files/out /opt/resource/
