pipeline {
  agent any

  stages {

   stage('Test') {
        steps {
            sh "./gradlew test"
            echo 'test success'
        }
      }

    stage('Build') {
      steps {
           sh "chmod +x gradlew"
           sh "./gradlew build -x test --no-daemon"
           echo 'build success'

      }
    }
  }
}