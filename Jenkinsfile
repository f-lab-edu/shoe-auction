pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh '''./gradlew clean build
  echo \'Build Success!\''''
      }
    }

    stage('Test') {
      steps {
        sh '''./gradlew test
echo \'Test Success!\''''
      }
    }

  }
}