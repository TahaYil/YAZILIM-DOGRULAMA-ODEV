pipeline {
    agent any

    environment {
        BACKEND_DIR = "${WORKSPACE}/backend"
        MAVEN_IMAGE = "maven:3.9-eclipse-temurin-21"
    }

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
                  --platform linux/amd64 \
                  -v "$BACKEND_DIR":/app \
                  -w /app \
                  $MAVEN_IMAGE \
                  mvn clean test -Dtest=*Test -DfailIfNoTests=false
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Controller & Service Tests') {
            steps {
                sh '''
                docker run --rm \
                  --platform linux/amd64 \
                  -v "$BACKEND_DIR":/app \
                  -w /app \
                  $MAVEN_IMAGE \
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
                  --platform linux/amd64 \
                  -v "$BACKEND_DIR":/app \
                  -w /app \
                  $MAVEN_IMAGE \
                  mvn test -Dtest=*SeleniumTest -DfailIfNoTests=false
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
