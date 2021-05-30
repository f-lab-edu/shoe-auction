def mainBranch = false

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

      /* post {
        failure {
          slackSend (channel: SLACK_CHANNEL, color: '#F01717', message: "Test Failed '[${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        }
      } */
    }

    stage('Build') {
      steps {
        sh 'gradle clean build --exclude-task test --exclude-task asciidoctor'
        echo 'build success'
      }

      /* post {
        success {
          slackSend (channel: SLACK_CHANNEL, color: '#00FF00', message: "Successful testing and build '[${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        }

        failure {
          slackSend (channel: SLACK_CHANNEL, color: '#F01717', message: "Build Failed '[${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        }
      } */
    }

    stage('Check Branch') {
      when {
        branch 'develop'
      }

      steps {
        script {
          mainBranch = true
        }
      }
    }

    stage('Idle Port Stop') {
      when {
        expression {
          mainBranch
        }
      }

      steps {
        steps([$class: 'BapSshPromotionPublisherPlugin']) {
          sshPublisher(
            continueOnError: false, failOnError: true,
            publishers: [
              sshPublisherDesc(
                configName: "shoe-auction-reverse-proxy",
                verbose: true,
                transfers: [
                  sshTransfer(
                    execCommand: "sh start.sh"
                  )
                ]
              )
            ]
          )
        }
      }
    }

    stage('Deploy To Idle Port') {
      when {
        expression {
          mainBranch
        }
      }

      steps([$class: 'BapSshPromotionPublisherPlugin']) {
        sshPublisher(
          continueOnError: false, failOnError: true,
          publishers: [
            sshPublisherDesc(
              configName: "shoe-auction-reverse-proxy",
              verbose: true,
              transfers: [
                sshTransfer(
                  execCommand: "sh health.sh"
                )
              ]
            )
          ]
        )
      }
    }

    stage('Check Health And Switch Ports') {
      when {
        expression {
          mainBranch
        }
      }

      steps([$class: 'BapSshPromotionPublisherPlugin']) {
        sshPublisher(
          continueOnError: false, failOnError: true,
          publishers: [
            sshPublisherDesc(
              configName: "shoe-auction-reverse-proxy",
              verbose: true,
              transfers: [
                sshTransfer(
                  execCommand: "sh stop.sh"
                )
              ]
            )
          ]
        )
      }

      /* post {
        success {
          slackSend (channel: SLACK_CHANNEL, color: '#00FF00', message: "Health check and deployment successful '[${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        }

        failure {
          slackSend (channel: SLACK_CHANNEL, color: '#F01717', message: "Health check and deployment failure '[${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
        }
      } */
    }
  }
}