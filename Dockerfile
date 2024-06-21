FROM jenkins/jenkins:2.452.2-jdk17

USER root
RUN apt-get update && apt-get install -y lsb-release
RUN curl -fsSLo /usr/share/keyrings/docker-archive-keyring.asc https://download.docker.com/linux/debian/gpg
RUN echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.asc] https://download.docker.com/linux/debian $(lsb_release -cs) stable" > /etc/apt/sources.list.d/docker.list
RUN apt-get update && apt-get install -y docker-ce-cli

# Créez le répertoire 
RUN mkdir -p /usr/share/jenkins/ref/plugins
RUN mkdir -p /usr/share/jenkins/ref/init.groovy.d
RUN mkdir -p /usr/share/jenkins/ref/jobs
RUN mkdir -p /usr/share/jenkins/ref/.ssh

# Copiez le fichier plugins.txt dans le répertoire des plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt

# Installez les plugins
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

# Copier les jobs 
COPY jobs /usr/share/jenkins/ref/jobs

# Copier les scripts d'initialisation
COPY init.groovy.d /usr/share/jenkins/ref/init.groovy.d

# Copier les fichier de la clé ssh
COPY .ssh /usr/share/jenkins/ref/.ssh

USER jenkins
