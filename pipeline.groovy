pipeline {
    agent {
        node {
            label 'agent'
        }
    }

    environment {
        DOCKER_REGISTRY = 'localhost:5001'
    }

    stages {
        stage('Prepare Environment') {
            steps {
                script {
                    sh 'sudo chmod 666 /var/run/docker.sock'
                    echo 'Environment prepared'
                }
            }
        }

        stage('Checkout') {
            steps {
                git url: 'https://github.com/dotnet-architecture/eShopOnWeb.git', branch: 'main'
                echo 'Code checked out'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker compose build'
                echo 'Docker images built'
            }
        }

        stage('Push Image to Registry') {
            steps {
                script {
                    docker.withRegistry("http://${DOCKER_REGISTRY}") {
                        // Push the image to your private registry
                        sh 'docker tag localhost:5001eshopwebmvc localhost:5001/eshopwebmvc:latest'
                        sh 'docker push localhost:5001/eshopwebmvc:latest'
                    }
                    // slackSend color: 'good', message: "Image pushed to registry"

                    docker.withRegistry("http://${DOCKER_REGISTRY}") {
                        // Push the image to your private registry
                        sh 'docker tag localhost:5001eshoppublicapi localhost:5001/eshoppublicapi:latest'
                        sh 'docker push localhost:5001/eshoppublicapi:latest'
                    }
                }
            }
        }

        stage('Start Final Server') {
            steps {
                script {
                    def finalServerStatus = sh(script: "docker inspect -f '{{.State.Running}}' final-server", returnStdout: true).trim()
                    if (finalServerStatus != 'true') {
                        sh 'docker compose up -d final-server'
                        echo 'Final server started'
                    } else {
                        echo 'Final server is already running'
                    }
                }
            }
        }

        stage('Deploy to Final Server') {
            steps {
                script {
                    // Copy the Docker Compose file to final-server
                    sh 'docker cp docker-compose.yml final-server:/var/www/html/docker-compose.yml'

                    // Run Docker Compose commands inside final-server
                    sh '''
                    docker exec final-server /bin/bash -c "
                    docker pull localhost:5001/eshoppublicapi:latest
                    docker pull localhost:5001/eshopwebmvc:latest
                    docker compose -f /var/www/html/docker-compose.yml up -d
                    "
                    '''
                    echo 'App deployed to final-server'
                }
            }
        }
    }

    post {
        always {
            script {
                currentBuild.result = currentBuild.result ?: 'SUCCESS'
                echo "Pipeline ${currentBuild.result}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL}) on agent '${env.NODE_NAME}'"
            }
        }
    }
}




// pipeline {
//     agent {
//         node {
//             label 'agent'
//         }
//     }

//     environment {
//         DOCKER_REGISTRY = 'localhost:5001'
//         IMAGE_NAME = 'my-project-image'
//     }

//     stages {
//         stage('Prepare Environment') {
//             steps {
//                 script {
//                     sh 'sudo chmod 666 /var/run/docker.sock'
//                     //slackSend color: 'good', message: "Pipeline started: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL}) on agent '${env.NODE_NAME}'"
//                 }
//             }
//         }

//         stage('Checkout') {
//             steps {
//                 git url: 'https://github.com/dotnet-architecture/eShopOnWeb.git', branch: 'main'
//                 // script {
//                 //     //slackSend color: 'good', message: "Checkout completed"
//                 // }
//             }
//         }

//         stage('Build Docker Image') {
//             steps {
//                 sh 'docker compose build'
//                 // script {
//                 //     slackSend color: 'good', message: "Docker image built"
//                 // }
//             }
//         }

//         stage('Push Image to Registry') {
//             steps {
//                 script {
//                     docker.withRegistry("http://${DOCKER_REGISTRY}") {
//                         sh 'docker tag localhost:5001/eshopwebmvc localhost:5001/eshopwebmvc:latest'
//                         sh 'docker push localhost:5001/eshopwebmvc:latest'
//                     }
//                     //slackSend color: 'good', message: "eshopwebmvc Image pushed to registry"

//                     docker.withRegistry("http://${DOCKER_REGISTRY}") {
//                         sh 'docker tag localhost:5001/eshoppublicapi localhost:5001/eshoppublicapi:latest'
//                         sh 'docker push localhost:5001/eshoppublicapi:latest'
//                     }
//                     //slackSend color: 'good', message: "eshoppublicapi Image pushed to registry"
//                 }
//             }
//         }

//         stage('Start Final Server') {
//             steps {
//                 script {
//                     def finalServerStatus = sh(script: "docker inspect -f '{{.State.Running}}' final-server", returnStdout: true).trim()
//                     if (finalServerStatus != 'true') {
//                         sh 'docker compose up -d final-server'
//                     }
//                 }
//             }
//         }

//         stage('Deploy to Final Server') {
//             steps {
//                 script {
//                     sh 'docker compose up -d eshopwebmvc eshoppublicapi'
//                     //slackSend color: 'good', message: "Deployment to final-server completed"
//                     echo 'App deployed'
//                 }
//             }
//         }
//     }

//     post {
//         always {
//             script {
//                 currentBuild.result = currentBuild.result ?: 'SUCCESS'
//                 //slackSend color: currentBuild.result == 'SUCCESS' ? 'good' : 'danger', message: "Pipeline ${currentBuild.result}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL}) on agent '${env.NODE_NAME}'"
//                 echo 'Pipeline finished!'
//             }
//         }
//     }
// }
