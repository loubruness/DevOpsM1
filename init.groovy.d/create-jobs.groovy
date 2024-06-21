#!groovy

import jenkins.model.*
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition

def instance = Jenkins.getInstance()

def pipelineJobName = 'my-pipeline-job'
def pipelineJob = instance.getItem(pipelineJobName)

if (pipelineJob == null) {
    pipelineJob = instance.createProject(WorkflowJob, pipelineJobName)
    def pipelineScript = """
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
                // Spécifiez ici votre propre SCM checkout, par exemple:
                git url: 'https://your-repo-url.git', branch: 'main'
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
            deleteDir() // Utiliser deleteDir() à la place de cleanWs()
        }
    }
}
"""
    def flowDefinition = new CpsFlowDefinition(pipelineScript, true)
    pipelineJob.setDefinition(flowDefinition)
    pipelineJob.save()
}

instance.save()
