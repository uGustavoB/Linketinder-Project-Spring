pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '15'))
        disableConcurrentBuilds()
        timestamps()
    }

    stages {
        stage('Checkout PR') {
            steps {
                checkout scm
            }
        }

        stage('Build ms-nucleo') {
            steps {
                dir('ms-nucleo') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean build --no-daemon'
                }
            }
        }

        stage('Build ms-competencia') {
            steps {
                dir('ms-competencia') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean build --no-daemon'
                }
            }
        }

        stage('Build Frontend (Angular)') {
            steps {
                dir('frontend') {
                    nodejs('Node20') {
                        sh 'npm install'
                        sh 'npm run build'
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline de validacao do PR finalizada.'
        }
        success {
            echo 'Todos os builds e testes passaram com sucesso.'
        }
        failure {
            echo 'Falha na compilacao ou nos testes de algum modulo. Verifique os logs.'
        }
    }
}
