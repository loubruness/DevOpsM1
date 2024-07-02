pipeline {
    agent any

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
                }
            }
        }

        stage('Checkout') {
            steps {
                // Checks out the project source code
                git url: 'https://github.com/dotnet-architecture/eShopOnWeb.git', branch: 'main'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker compose build'
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
                    docker.withRegistry("http://${DOCKER_REGISTRY}") {
                        // Push the image to your private registry
                        sh 'docker tag localhost:5001eshoppublicapi localhost:5001/eshoppublicapi:latest'
                        sh 'docker push localhost:5001/eshoppublicapi:latest'
                    }
                }
            }
        }
    }
    
    post {
        always {
            script {
                currentBuild.result = currentBuild.result ?: 'SUCCESS'
                
                // Envoi de la notification Slack
                slackSend color: currentBuild.result == 'SUCCESS' ? 'good' : 'danger', message: "Pipeline ${currentBuild.result}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL}) on agent '${env.NODE_NAME}'"
                
                // Autres actions à effectuer après l'exécution de la pipeline
                echo 'Pipeline finished!'
            }
        }
    }
}
}
