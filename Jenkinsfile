pipeline {
  agent any
   environment {
          PATH = "/opt/gradle/gradle-6.3/bin:$PATH"
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
           sh "chmod +x gradlew"
           sh "./gradlew build -x test"
           echo 'build success'

      }
    }
  }
}