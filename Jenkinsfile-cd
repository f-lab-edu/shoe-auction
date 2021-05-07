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

    stage('Deploy)' {
        steps {
         script {
          sh "ssh root -p 8080 root@106.10.51.20 -T sh < /root/app/step1/deploy.sh"
         }
      }
    }
  }

  post {
    success {
         slackSend (channel: SLACK_CHANNEL, color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
         }

    failure {
         slackSend (channel: SLACK_CHANNEL, color: '#F01717', message: "FAILURE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
         }
  }
}