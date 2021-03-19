pipeline {
  agent any
   environment {
          PATH = "/opt/gradle/gradle-6.3/bin:$PATH"
      }
  stages {
    stage('Build') {
      steps {
           echo 'build success'
           sh "chmod +x gradlew"
           sh "./gradlew clean build --no-daemon --exclude-task test"
      }
    }

    stage('Test') {
      steps {
        echo 'test success'
        sh "./gradlew test"
      }
    }

  }
}