pipeline {
    agent any

    environment {
        // JAVA_HOME otomatik olarak mevcut dizinlerden birine ayarlanacak
        JAVA_HOME = ''
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Unit & Integration Tests') {
            steps {
                script {
                    def javaHomes = [
                        '/usr/lib/jvm/java-21-openjdk-amd64',
                        '/usr/lib/jvm/java-17-openjdk-amd64',
                        '/usr/lib/jvm/java-11-openjdk-amd64',
                        '/usr/lib/jvm/default-java',
                        '/usr/java/latest'
                    ]
                    for (jhome in javaHomes) {
                        if (fileExists(jhome)) {
                            env.JAVA_HOME = jhome
                            env.PATH = "${jhome}/bin:" + env.PATH
                            break
                        }
                    }
                }
                dir('backend') {
                    sh 'echo Using JAVA_HOME=$JAVA_HOME && java -version && mvn clean verify'
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
                    sh 'echo Using JAVA_HOME=$JAVA_HOME && java -version && mvn test -Dtest=*SeleniumTest'
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
