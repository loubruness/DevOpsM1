pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'localhost:5001' // Change this to your private registry's address
        IMAGE_NAME = 'my-project-image' // Change this to your desired image name
    }

    stages {
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
}