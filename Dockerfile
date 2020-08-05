FROM openjdk:8-jdk-alpine
MAINTAINER Leo Li
WORKDIR /autodeploy
ADD start.sh /autodeploy
RUN apk update && \
	apk add curl bash && \
	curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl && \
	chmod +x ./kubectl && \
	mv ./kubectl /usr/local/bin/kubectl && \
	chmod +x /usr/local/bin/kubectl && \
	chmod +x /autodeploy/start.sh
COPY target/k8s-git-autodeploy-1.0.0.jar /autodeploy
CMD ["/bin/bash", "./start.sh"]