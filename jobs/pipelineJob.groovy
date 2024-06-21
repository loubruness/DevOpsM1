pipeline {
    agent any

    environment {
        DOCKER_HOST = 'tcp://docker:2376'
        DOCKER_CERT_PATH = '/certs/client'
        DOCKER_TLS_VERIFY = '1'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                script {
                    docker.build('my-app')
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    docker.image('my-app').inside {
                        sh 'make test'
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    docker.image('my-app').inside {
                        sh 'make deploy'
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
