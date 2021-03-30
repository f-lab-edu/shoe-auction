pipeline {
  agent any
  environment {
        PATH = "/opt/gradle/gradle-6.3/bin:$PATH"
    }
  stages {

    stage('Build') {
      steps {

           sh 'gradle clean build --exclude-task test --exclude-task asciidoctor'
           echo 'build success'

      }
    }
  }
}