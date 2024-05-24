#!/bin/bash

sudo apt-get update -qq
sudo apt-get install -qq -y vim git wget curl

curl https://packages.gitlab.com/install/repositories/gitlab/gitlab-ce/script.deb.sh | sudo bash

sudo apt-get update -qq

export LC_CTYPE=en_US.UTF-8
export LC_ALL=en_US.UTF-8
export GITLAB_OMNIBUS_CONFIG="external_url 'http://192.168.33.10'"

sudo apt install -y gitlab-ce
sudo gitlab-ctl reconfigure

# Set initial root password
sudo gitlab-rails runner "user = User.where(id: 1).first; user.password = '076R#kN9N2-;'; user.password_confirmation = '076R#kN9N2-;'; user.save!"

# Set external url
# sudo gitlab-rails runner "external_url = 'http://192.168.33.10; external_url.save!'