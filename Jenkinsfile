pipeline {
  agent any
   environment {
      }


  stages {

   stage('Test') {
        steps {
            sh "./gradlew test"
            echo 'test success'
        }
      }

    stage('Build') {
      steps {
           sh "./gradlew clean build"
           echo 'build success'

      }
    }
  }
}