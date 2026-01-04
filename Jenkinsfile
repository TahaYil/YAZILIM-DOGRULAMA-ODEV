pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-11-openjdk-amd64'
        PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Unit & Integration Tests') {
            steps {
                dir('backend') {
                    sh 'mvn clean verify'
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
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
                sh 'docker compose up -d'
                sh 'sleep 20'
            }
        }
        stage('Selenium Tests') {
            steps {
                dir('backend') {
                    sh 'mvn test -Dtest=*SeleniumTest'
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
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
