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
          sh 'env |sort'
          sh '''
            sed -i -e "s/\\${namespace}/$NAMESPACE/g" namespace.yaml
            sed -i -e "s/__WMICADMIN_PASS__/V2VsY29tZV8xMjM0Cg==/g" \
                -e "s/__CLOUDADMIN_PASS__/V2VsYzBtZQo=/g" \
                -e "s/\\${userPass}/c3BtYWRtaW4xMjMK/g" \
                -e "s/\\${namespace}/$NAMESPACE/g" repo_secret.yaml
            sed -e "s/\\${DOCKER_REPO}/$DOCKER_REPO/g" \
                -e "s/\\${tenantId}/$NAMESPACE/g" \
                -e "s/\\${TAG}/$TAG/g" repo_pod.yaml >repo_pod1.yaml
            cat namespace.yaml
            cat repo_secret.yaml
            cat repo_pod.yaml
            pwd ; ls -l
          '''
        }
      }
    }
    stage('Create ns and secrets') {
      steps {
        dir('pipeline/K8s') {
          withKubeConfig(credentialsId: 'k8scdm01') {
            sh '''
              pwd
              ls -l
              kubectl create -f namespace.yaml
              kubectl create -f repo_secret.yaml
              sleep 3
              kubectl get -f namespace.yaml
              kubectl get -f repo_secret.yaml
              env |sort
            '''
          }
        }
      }
    }
    stage('Run Pods') {
      agent {
        kubernetes {
          cloud 'k8scdm01'
          label 'repo'
          namespace '20190824'
          slaveConnectTimeout '60'
          serviceAccount 'default'
          yamlFile './pipeline/K8s/repo_pod1.yaml'
        }
      }
      steps {
        container('repo') {
          sh '''
            hostname
            pwd
            netstat -pntl
          '''
        }
      }
    }
  }
  post {
    always {
      echo 'Delete current namespace'
      dir('pipeline/K8s') {
        withKubeConfig(credentialsId: 'k8scdm01') {
          sh '''
            kubectl delete -f repo_dep.yaml || true
            kubectl delete -f repo_secret.yaml || true
            kubectl delete -f namespace.yaml
            kubectl get -f namespace.yaml
          '''
        }
      }
      echo 'Delete current workspace'
      sh 'pwd ; ls -l'
//      deleteDir() /* clean up our workspace */
      sh 'ls -l'
    }
  }
}
