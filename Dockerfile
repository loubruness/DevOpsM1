FROM jenkins/jenkins:2.452.2-jdk17

USER root
RUN apt-get update && apt-get install -y lsb-release
RUN curl -fsSLo /usr/share/keyrings/docker-archive-keyring.asc https://download.docker.com/linux/debian/gpg
RUN echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.asc] https://download.docker.com/linux/debian $(lsb_release -cs) stable" > /etc/apt/sources.list.d/docker.list
RUN apt-get update && apt-get install -y docker-ce-cli

# Créez le répertoire des plugins
RUN mkdir -p /usr/share/jenkins/ref/plugins

# Copiez le fichier plugins.txt dans le répertoire des plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt

# Installez les plugins
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

USER jenkins
