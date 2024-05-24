#!/bin/bash

sudo apt-get update -qq

# Install Docker
wget -O get-docker.sh https://get.docker.com/ 
sh get-docker.sh
curl -L "https://github.com/docker/compose/releases/download/1.25.3/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# Download the binary for your system
sudo curl -L --output /usr/local/bin/gitlab-runner https://gitlab-runner-downloads.s3.amazonaws.com/latest/binaries/gitlab-runner-linux-arm64

# Give it permission to execute
sudo chmod +x /usr/local/bin/gitlab-runner

# Create a GitLab Runner user
sudo useradd --comment 'GitLab Runner' --create-home gitlab-runner --shell /bin/bash

# Install and run as a service
sudo gitlab-runner install --user=gitlab-runner --working-directory=/home/gitlab-runner
sudo gitlab-runner start

# Allow some time for network services to initialize
sleep 10

# Register the GitLab Runner
sudo /usr/local/bin/gitlab-runner register --non-interactive --url "http://192.168.33.10" --token "glrt-xnDsBZ5CeyLVw_zHFiz-" --executor "docker" --description "my-runner" --docker-image "alpine:latest"