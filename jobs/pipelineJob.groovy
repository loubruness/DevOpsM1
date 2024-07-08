pipeline {
    agent {
        node {
            label 'agent'
        }
    }

    environment {
        DOCKER_REGISTRY = 'localhost:5001' // Change this to your private registry's address
        IMAGE_NAME = 'my-project-image' // Change this to your desired image name
    }

    stages {
        stage('Prepare Environment') {
            steps {
                script {
                    // Adjust the permissions of the Docker socket
                    sh 'sudo chmod 666 /var/run/docker.sock'
                    slackSend color: 'good', message: "Pipeline started: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL}) on agent '${env.NODE_NAME}'"
                }
            }
        }

        stage('Checkout') {
            steps {
                // Checks out the project source code
                git url: 'https://github.com/dotnet-architecture/eShopOnWeb.git', branch: 'main' // WORKING REPO
                // git url: 'https://github.com/hugopanel/eshoponweb-testfails.git', branch: 'main' // FAILING REPO (same repo, with one failing test)
                script{
                    slackSend color: 'good', message: "Checkout completed"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker compose build'
                script{
                    slackSend color: 'good', message: "Docker image built"
                }
            }
        }

        stage('Test Project') {
            steps {
                script {
                    def sdkImage = 'mcr.microsoft.com/dotnet/sdk:8.0'
                    def exitCode = sh(script: "docker run jenkins_temp/my-pipeline-job:/app --name test-container ${sdkImage} /bin/sh -c 'dotnet test /app/eShopOnWeb.sln'", returnStatus: true)
                    if (exitCode != 0) {
                        currentBuild.result = 'FAILURE'
                        error 'Tests failed. Aborting build.'
                    }
                }
            }
        }

        stage('Push Image to Registry') {
            steps {
                script {
                    // Log in to your Docker registry
                    docker.withRegistry("http://${DOCKER_REGISTRY}") {
                        // Push the image to your private registry
                        sh 'docker tag localhost:5001eshopwebmvc localhost:5001/eshopwebmvc:latest'
                        sh 'docker push localhost:5001/eshopwebmvc:latest'
                    }
                    slackSend color: 'good', message: "Image pushed to registry"

                    docker.withRegistry("http://${DOCKER_REGISTRY}") {
                        // Push the image to your private registry
                        sh 'docker tag localhost:5001eshoppublicapi localhost:5001/eshoppublicapi:latest'
                        sh 'docker push localhost:5001/eshoppublicapi:latest'
                    }

                    slackSend color: 'good', message: "Image pushed to registry"
                }
            }
        }
    }
    
    post { // 
        always {
            script {
                sh 'docker stop test-container'
                sh 'docker rm test-container'
                
                currentBuild.result = currentBuild.result ?: 'SUCCESS'
                
                // Envoi de la notification Slack
                slackSend color: currentBuild.result == 'SUCCESS' ? 'good' : 'danger', message: "Pipeline ${currentBuild.result}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL}) on agent '${env.NODE_NAME}'"
                
                // Autres actions à effectuer après l'exécution de la pipeline
                echo 'Pipeline finished!'
            }
        }
    }
}

