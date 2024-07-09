### Pre-requisites:

- Generate an ssh key from the directory in which the docker-compose file is usign the following command in a terminal (you have to create the directory .ssh if it doesn't already exist).
  ``ssh-keygen -f .ssh/id_rsa``
- Replace the value of 'JENKINS_AGENT_SSH_PUBKEY' in the docker-compose.yml file by the ssh key generated in the file 'id_rsa.pub' (all the content of the file without quotes, even the = user@Desktop-WHATEVER)
- Replace the value of 'SLACK_SECRET_TOKEN' at the line 14 of the docker-compose.yml file to ``xoxb-7350845836756-7397050358100-bvjpyeQmtgHXGqD4dhwV2LvG``
- Launch Docker Desktop
- If you want to test the Slack notifications without having to create your own bot, you may join this workspace : https://join.slack.com/t/devopsm1efrei/shared_invite/zt-2mdg2sw3n-VoRCBS6k5Dld~_zzvhA4mQ and witness the notifications sent in the #projet-jenkins channel
  

### Start command:
In the root repository (where the docker-compose.yml file is) :
* To execute without debugging: ``docker-compose up -d``
* To execute with the build of images (for the first time) : ``docker-compose up --build``

### Access Jenkins:
Once the containers are up and running, you can access jenkins at the following address : ``localhost:8080``
If it is your first time, you may have to pass the installation of plugins as they are automaticcaly installed. 

Now, you can connect using the following credentials : username = admin, password = admin
![image](https://github.com/loubruness/DevOpsM1/assets/94390007/a93cbd51-c240-4739-b2ce-9d7f88383646)

Once you are connected, you can skip this step by clicking on the cross in the top-right corner as all the necessary plug-ins are already installed :
![image](https://github.com/loubruness/DevOpsM1/assets/94390007/a7edec0e-9f3f-4a00-92ef-88f0bd0c0080)

Then you can start using Jenkins by clicking on the blue button :
![image](https://github.com/loubruness/DevOpsM1/assets/94390007/7ac1fd3d-4dbe-4021-9c58-7edc68423f48)

### Run the pipeline :
As you arrive on the homepage you can find your pipeline job already configured and ready to run. You can run it by pressing the green arrow at the right end of the row :
![image](https://github.com/loubruness/DevOpsM1/assets/94390007/ff8e0af2-2bf4-40da-8cd5-699aafb9dab8)

We decided to take this github as an example application to build, test and deploy : https://github.com/dotnet-architecture/eShopOnWeb.git
So when you run the pipeline, it will build images taken from this application, push them to the local registry, execute the test of the application and finally deploy it in a distinct docker-compose that will represent your final-server. You should be able to access it on the port 5107 of your host machine.

### Stages of the pipeline

## Prepare environment
In this stage, the pipeline starts by adjusting the permissions of the Docker socket of the host machine, so that it can use it. Then, a Slack notification is sent to inform that the pipeline has started, with information such as the name of the job, the number of the build (to make it easier to access the logs if there is any issue) and the name of the agent in which the pipeline is executed (in our case jankins-agent).

### Stop command:
To stop the running containers, you need to do Ctrl + C in the terminal you used to execute the ``docker-compose up`` command.

To delete the containers, you can execute the following command in your root repository :
``docker-compose down``

