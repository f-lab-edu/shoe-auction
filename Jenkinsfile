pipeline {
  agent any
  environment {
        PATH = "/opt/gradle/gradle-6.3/bin:$PATH"
    }
  stages {

   stage('Test') {
        steps {
            sh 'gradle test'
            echo 'test success'
        }
      }

    stage('Build') {
      steps {

           sh 'gradle clean build --exclude-task test'
           echo 'build success'

      }
    }
  }
}