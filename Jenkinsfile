pipeline {
  agent any
   environment {
          PATH = "/opt/gradle/gradle-6.3/bin:$PATH"
      }
  stages {
    stage('Build') {
      steps {
        sh './gradlew clean build'
        echo 'build success!'
      }
    }

    stage('Test') {
      steps {
        sh './gradlew test'
        echo 'Test Success!'
      }
    }

  }
}