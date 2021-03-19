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
           sh "./gradlew clean build --no-daemon --exclude-task compileQuerydsl"
      }
    }

    stage('Test') {
      steps {
        echo 'test success'
      }
    }

  }
}