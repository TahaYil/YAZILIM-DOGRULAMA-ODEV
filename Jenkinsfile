pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Unit Tests') {
            steps {
                sh '''
                docker run --rm \
                  -v "$PWD/backend":/app \
                  -w /app \
                  maven:3.9-eclipse-temurin-21 \
                  mvn clean test -Dtest=*Test -DfailIfNoTests=false
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Integration Tests') {
            steps {
                sh '''
                docker run --rm \
                  -v "$PWD/backend":/app \
                  -w /app \
                  maven:3.9-eclipse-temurin-21 \
                  mvn test -Dtest=*ServiceTest,*ControllerTest,TshirtSatisApplicationTests -DfailIfNoTests=false
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker compose build'
            }
        }

        stage('Start Docker Compose') {
            steps {
                sh '''
                docker compose up -d
                sleep 20
                '''
            }
        }

        stage('Selenium Tests') {
            steps {
                sh '''
                docker run --rm \
                  -v "$PWD/backend":/app \
                  -w /app \
                  maven:3.9-eclipse-temurin-21 \
                  mvn test -Dtest=*SeleniumTest
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'backend/target/surefire-reports/*.xml'
                }
            }
        }
    }

    post {
        always {
            sh 'docker compose down -v'
        }
    }
}
