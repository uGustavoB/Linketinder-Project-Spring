pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build ms-nucleo') {
            steps {
                dir('ms-nucleo') {
                    sh 'chmod +x gradlew'
                    sh './gradlew build'
                }
            }
        }

        stage('Build ms-competencia') {
            steps {
                dir('ms-competencia') {
                    sh 'chmod +x gradlew'
                    sh './gradlew build'
                }
            }
        }

        stage('Build Frontend (Angular)') {
            steps {
                dir('frontend') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finalizado.'
        }
        success {
            echo 'Todos os builds e testes passaram com sucesso!'
        }
        failure {
            echo 'Houve uma falha em alguma etapa do pipeline.'
        }
    }
}