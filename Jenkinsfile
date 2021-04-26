pipeline {
  agent any
  environment {
        PATH = "/opt/gradle/gradle-6.3/bin:$PATH"
        SLACK_CHANNEL = '#jenkins-notification'
    }
  stages {
    stage('Git Checkout') {
              steps {
                  checkout scm
                  echo 'Git Checkout Success!'
              }
    }
    stage('Test') {
        steps {
            sh 'gradle test'
            echo 'test success'
        }
      }
    stage('Build') {
      steps {
           sh 'gradle clean build --exclude-task test --exclude-task asciidoctor'
           echo 'build success'
      }
    }
  }


}