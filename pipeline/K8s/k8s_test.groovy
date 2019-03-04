pipeline {
  parameters {
    string(name: 'TAG', defaultValue: '10.3', description: 'Image Tag')
    string(name: 'NAMESPACE', defaultValue: '20190824', description: 'Namespace tobe created')
    string(name: 'DOCKER_REPO', defaultValue: 'cdr:5000', description: 'Docker Repo')
  }
  agent {
    node {
      label 'DOCKER_HOST'
    }
  }
  stages {
    stage('Update Yaml') {
      steps {
        dir('pipeline/K8s') {
          sh 'env |sort ; pwd ; ls -l'
          sh '''
            sed -i -e "s/\\${namespace}/$NAMESPACE/g" namespace.yaml
            sed -i -e "s/__WMICADMIN_PASS__/V2VsY29tZV8xMjM0Cg==/g" \
                -e "s/__CLOUDADMIN_PASS__/V2VsYzBtZQo=/g" \
                -e "s/\\${userPass}/c3BtYWRtaW4xMjMK/g" \
                -e "s/\\${namespace}/$NAMESPACE/g" repo_secret.yaml
            sed -i -e "s/\\${DOCKER_REPO}/$DOCKER_REPO/g" \
                -e "s/\\${tenantId}/$NAMESPACE/g" \
                -e "s/\\${TAG}/$TAG/g" repo_dep.yaml
            cat namespace.yaml
            cat repo_secret.yaml
            cat repo_dep.yaml
          '''
        }
      }
    }
    stage('Create Pods') {
      steps {
        dir('pipeline/K8s') {
          withKubeConfig(credentialsId: 'k8scdm01') {
            sh '''
              kubectl create -f namespace.yaml
              kubectl create -f repo_secret.yaml
              kubectl create -f repo_dep.yaml
              sleep 30
            '''
          }
        }
      }
    }
    stage('Run Pods') {
      steps {
        dir('pipeline/K8s') {
          withKubeConfig(credentialsId: 'k8scdm01') {
            sh '''
              kubectl get -f namespace.yaml
              kubectl get -f repo_secret.yaml
              kubectl get -f repo_dep.yaml
            '''
          }
        }
      }
    }
    stage('Delete Pods') {
      steps {
        dir('pipeline/K8s') {
          withKubeConfig(credentialsId: 'k8scdm01') {
            sh '''
              kubectl delete -f repo_dep.yaml
              kubectl delete -f repo_secret.yaml
              kubectl delete -f namespace.yaml
              kubectl get -f namespace.yaml
            '''
          }
        }
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
