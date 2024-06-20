pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git credentialsId: 'your-credentials-id', url: 'https://github.com/your-repo.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    docker.image('node:14').inside {
                        sh 'npm install'
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    docker.image('node:14').inside {
                        sh 'npm test'
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build('your-image-name')
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'docker-credentials-id') {
                        docker.image('your-image-name').push('latest')
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'docker-credentials-id') {
                        docker.image('your-image-name').run('-d -p 80:80')
                    }
                }
            }
        }
    }
}