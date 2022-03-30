FROM alpine:3.14

COPY --from=hashicorp/packer:1.4.5 /bin/packer /usr/local/bin/packer135
COPY --from=hashicorp/packer:1.4.5 /bin/packer /usr/local/bin/packer
COPY --from=google/cloud-sdk:alpine /google-cloud-sdk /google-cloud-sdk
ENV PATH /google-cloud-sdk/bin:$PATH

RUN apk --no-cache add \
      bash \
      curl \
      git \
      jq \
      openssh-client \
      python3 \
      py3-pip \
      sed \
      tar \
      terraform \
      util-linux \
      unzip \
      zip \
      rsync \
  && gcloud config set core/disable_usage_reporting true \
  && gcloud config set component_manager/disable_update_check true \
  && gcloud config set metrics/environment github_docker_image \
  && gcloud components install -q beta \
  && pip3 install awscli
