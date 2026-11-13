
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
                    nodejs('Node26') {
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

            withCredentials([
                string(credentialsId: 'telegram-bot-token',
                       variable: 'TELEGRAM_TOKEN'),
                string(credentialsId: 'telegram-chat-linketinder-id,
                       variable: 'TELEGRAM_CHAT_ID')
            ]) {
                sh '''
                    curl -sS -X POST \
                    "https://api.telegram.org/bot${TELEGRAM_TOKEN}/sendMessage" \
                    -d "chat_id=${TELEGRAM_CHAT_ID}" \
                    --data-urlencode "parse_mode=HTML" \
                    --data-urlencode "text=✅ <b>PIPELINE APROVADA</b>%0A%0AProjeto: ${JOB_NAME}%0ABuild: #${BUILD_NUMBER}%0AStatus: SUCCESS%0A%0A<a href=\"${BUILD_URL}\">Visualizar execução</a>"
                '''
            }
        }

        failure {
            echo 'Falha na compilacao ou nos testes de algum modulo.'

            withCredentials([
                string(credentialsId: 'telegram-bot-token',
                       variable: 'TELEGRAM_TOKEN'),
                string(credentialsId: 'telegram-chat-linketinder-id',
                       variable: 'TELEGRAM_CHAT_ID')
            ]) {
                sh '''
                    curl -sS -X POST \
                    "https://api.telegram.org/bot${TELEGRAM_TOKEN}/sendMessage" \
                    -d "chat_id=${TELEGRAM_CHAT_ID}" \
                    --data-urlencode "parse_mode=HTML" \
                    --data-urlencode "text=❌ <b>PIPELINE COM FALHA</b>%0A%0AProjeto: ${JOB_NAME}%0ABuild: #${BUILD_NUMBER}%0AStatus: FAILURE%0A%0A<a href=\"${BUILD_URL}\">Visualizar logs da execução</a>"
                '''
            }
        }
    }
}
