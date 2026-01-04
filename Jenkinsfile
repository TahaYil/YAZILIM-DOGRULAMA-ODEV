pipeline {
    agent any



    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Unit & Integration Tests') {
            agent {
                docker {
                    image 'maven:3.9-amazoncorretto-21'
                    args '-v $HOME/.m2:/root/.m2'
                }
            }
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
            agent {
                docker {
                    image 'maven:3.9-amazoncorretto-21'
                    args '-v $HOME/.m2:/root/.m2'
                }
            }
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
