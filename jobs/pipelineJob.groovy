pipeline {
    agent {
        node{
            label 'agent'
        }
    }
    
    stages {
        stage('Build') {
            steps {
                 echo "Job '${env.JOB_NAME}' (${env.BUILD_NUMBER}) executed on agent '${env.NODE_NAME}'"

                // Étapes de construction de votre pipeline
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying...'
                // Étapes de déploiement de votre pipeline
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