pipeline {
  parameters {
    string(name: 'TAG', defaultValue: '10.3', description: 'Image Tag')
  }
  environment {
    TAG_SUFFIX = """${sh(
        returnStdout: true,
        script: 'date "+%0d"'
      ).trim()}"""
    BUILD_DATE = """${sh(
        returnStdout: true,
        script: 'date -u "+%Y-%m-%dT%H:%M:%SZ"'
      ).trim()}"""
  }
  agent {
    node {
      label 'DOCKER_HOST'
    }
  }
  stages {
    stage("Build CCBuilder") {
      steps {
        sh '''
        ls -l $WORKSPACE/pipeline/IntegrationServer/${TAG}.env
        if [[ -f $WORKSPACE/pipeline/IntegrationServer/${TAG}.env ]]
        then
          for var in `grep -v "#" $WORKSPACE/pipeline/IntegrationServer/${TAG}.env`
          do
            export $var
          done
        fi
        env |sort
        '''
      }
    }
  }
  post {
    always {
        echo 'Delete current workspace'
        sh 'pwd'
        sh 'ls -l'
        deleteDir() /* clean up our workspace */
        sh 'ls -l'
    }
  }
}
