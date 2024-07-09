### Pre-requisites:

- Generate an ssh key from the directory in which the docker-compose file is using the following command in a terminal (you have to create the directory .ssh if it doesn't already exist).
  ``ssh-keygen -f .ssh/id_rsa``
- Replace the value of 'JENKINS_AGENT_SSH_PUBKEY' in the docker-compose.yml file by the ssh key generated in the file 'id_rsa.pub' (all the content of the file without quotes, even the = user@Desktop-WHATEVER)

- For slack notifications :
  * If you want to use your own bot in your own Slack channel, please follow the part "Creating your app" of this tutorial : https://plugins.jenkins.io/slack/ and replace the value of 'SLACK_SECRET_TOKEN' at the line 14 of the docker-compose.yml file by the 'Bot User OAuth Access Token' you'll get at the step 8.
  * Else, if you want to test the Slack notifications without having to create your own bot, you may join this workspace : https://join.slack.com/t/devopsm1efrei/shared_invite/zt-2mdg2sw3n-VoRCBS6k5Dld~_zzvhA4mQ . Replace the value of 'SLACK_SECRET_TOKEN' at the line 14 of the docker-compose.yml file by ``xoxb-7350845836756-7397050358100-bvjpyeQmtgHXGqD4dhwV2LvG`` and witness the notifications sent in the #projet-jenkins channel.
- Launch Docker Desktop

### Start command:
In the root repository (where the docker-compose.yml file is) :
* To execute with the build of images (for the first time): ``docker-compose up -d --build``
* To execute in the background: ``docker-compose up -d``


### Access Jenkins:
Once the containers are up and running, you can access jenkins at the following address : ``localhost:8080``
If it is your first time, you may have to pass the installation of plugins as they are automatically installed. 

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
So when you run the pipeline, it will build images taken from this application, push them to the local registry, execute the test of the application and finally deploy it in a distinct docker-compose that will represent your final-server (all these steps will be explained in details in the next section). You should be able to access it on the port 5106 of your host machine.

### Stages of the pipeline

#### Prepare environment
In this stage, the pipeline starts by adjusting the permissions of the Docker socket of the host machine, so that it can use it. Then, a Slack notification is sent to inform that the pipeline has started, with information such as the name of the job, the number of the build (to make it easier to access the logs if there is any issue) and the name of the agent in which the pipeline is executed (in our case jankins-agent).

#### Checkout
In this stage, we check out the souce code of the project using the url of the git (precising which branch we want to use), and send a Slack notification once the checkout is completed.

#### Build Docker Image
In this stage, we execute the docker compose build command in the jenkins-agent. This will execute the docker-compose.yml file present in the github repository, and will build the images necessary to execute the application. Once again, when the images are built we send a Slack notification.

#### Tests
In this stage, we start by creating a container specially for running the tests. To do so, we use the image 'mcr.microsoft.com/dotnet/sdk:8.0' to be able to execute the tests of the app we chose. In this container we execute the command ``dotnet test`` that will directly execute the tests present in the 'tests' directory of the git repository.
We get the exit code of the command and act accordingly : if at least one test failed, we make the result of the pipeline a Failure and abort it. Else, we send a Slack notification to indicate that all the tests have been passed successfully.
With the actual set up the tests will always pass, if you want to test a pipeline with tests that will fail you may change the line at the checkout stage for this : ``git url: 'https://github.com/hugopanel/eshoponweb-testfails.git', branch: 'main'``

#### Push Images to Registry
If the tests passed, we push the images we built earlier inside our local registry (represented by the private_registry container). To do so, we use the ``docker tag`` command to give a tag to the images we built and ``docker push`` to actively push them into the registry. Then, we send a Slack notification to indicate that this stage is completed.

#### Deploy
Using the local registry in which we just pushed the images, we run the containers by using the command ``docker compose up -d`` (the -d is used to run the containers in the background). Since we are always using the docker socket of the host, docker compose up -d creates new containers on the host for the deployed app. This would have been the same if we executed docker compose up -d in a container that represented our deployment server.
![image](https://github.com/loubruness/DevOpsM1/assets/94390007/c7b2f03e-55e4-4bd7-a46a-93930834374c)
This command creates a distinct network with 3 different containers : eshopwebmvc-1 that contains the front-end of the app and is mapped to the port 5106 of the host machine, eshoppublicapi-1 that contains the API (aka back-end) of the app and is mapped on the port 5200 of the host machine, and sqlserver-1 that contains the database server and is mapped on the port 1433 of the host machine.
Once this is done, we send a notification to Slack with the link to access the front-end of the app we deployed.

We can access the front on our local machine once we have run the pipeline at the following address : http://localhost:5106/
This should redirect to this page :
![image](https://github.com/loubruness/DevOpsM1/assets/94390007/6b60e3af-4e89-4723-8fe8-11cbc8fdfc7e)

#### Post
At the end of the pipeline, the docker test-container is stopped and deleted. A final Slack notification is sent to inform that the job is finished and the outcome (success or failure).

### Delete containers command:
To delete the containers, you can execute the following command in your root repository :
``docker-compose down``

