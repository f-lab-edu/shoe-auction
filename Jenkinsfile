pipeline {
  agent any
   environment {
          PATH = "/opt/gradle/gradle-6.3/bin:$PATH"
      }
  stages {
    stage('Build') {
      steps {
        echo 'Compile project'
           sh "chmod +x gradlew"
           sh "./gradlew clean build --no-daemon"
      }
    }

    stage('Test') {
      steps {
        echo 'Compile project'
        sh "chmod +x gradlew"
        sh "./gradlew test"
      }
    }

  }
}