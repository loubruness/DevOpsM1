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
                        docker.image("eshopwebmvc").push()
                    }
                    docker.withRegistry("http://${DOCKER_REGISTRY}") {
                        // Push the image to your private registry
                        docker.image("eshoppublicapi").push()
                    }
                }
            }
        }
    }
}