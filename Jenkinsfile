pipeline {
  agent any
   environment {
          PATH = "/opt/gradle/gradle-6.3/bin:$PATH"
      }
  stages {
    stage('Build') {
      steps {
           sh "chmod +x gradlew"
           sh "./gradlew clean build --no-daemon"
           echo 'build success'

      }
    }

    stage('Test') {
      steps {
          sh "./gradlew test"
          echo 'test success'
      }
    }

  }
}