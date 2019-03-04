pipeline {
  parameters {
    string(name: 'TAG', defaultValue: '10.3', description: 'Image Tag')
  }
  agent {
    node {
      label 'DOCKER_HOST'
    }
  }
  stages {
    stage("Build CCBuilder") {
      steps {
        sh 'mkdir ${WORKSPACE}/ccbuilder || true'
        dir ('ccbuilder') {
          checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CleanBeforeCheckout']], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/SoftwareAG/sagdevops-templates.git']]])
        }
        sh 'ls -l ${WORKSPACE}'
        dir('ccbuilder/infrastructure') {
          sh '''
            envFile=${WORKSPACE}/pipeline/IntegrationServer/${TAG}.env
            chmod u+x ${envFile}
            if [[ -f ${envFile} && -x ${envFile} ]]
            then
              . ${envFile}
            else
              exit_error "Error: ${envFile} does not exists or do not have execute permissions"
            fi
            env |sort
            sed -i -e "s#${REPLACE_REPO_FIX_URL}#${REPO_FIX_URL}#g" \
                -e "s#${REPLACE_REPO_PRODUCT_URL}#${REPO_PRODUCT_URL}#g" ${TAG}.staging.yml
            cat ${TAG}.staging.yml
            cat staging.yml
            docker-compose build --no-cache builder
          '''
        }
        sh '''
          envFile=${WORKSPACE}/pipeline/IntegrationServer/${TAG}.env
          . ${envFile}
          docker tag softwareag/commandcentral-builder:${TAG} daerepository03.eur.ad.sag:4443/${DTR_ORG}/commandcentral-builder:${TAG}_${TAG_SUFFIX}
          docker tag softwareag/commandcentral-builder:${TAG} daerepository03.eur.ad.sag:4443/${DTR_ORG}/commandcentral-builder:${TAG}${VERSION}
        '''
      }
    }
    stage("Push CCBuilder") {
      steps {
        script {
          docker.withRegistry('https://daerepository03.eur.ad.sag:4443', 'DTR_cdep-dev') {
            sh '''
              envFile=${WORKSPACE}/pipeline/IntegrationServer/${TAG}.env
              . ${envFile}
              docker push daerepository03.eur.ad.sag:4443/${DTR_ORG}/commandcentral-builder:${TAG}_${TAG_SUFFIX}
              docker push daerepository03.eur.ad.sag:4443/${DTR_ORG}/commandcentral-builder:${TAG}${VERSION}
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
      echo "Info: Delete created image"
      sh 'docker rmi softwareag/commandcentral-builder:${TAG}'
      sh 'docker rmi daerepository03.eur.ad.sag:4443/${DTR_ORG}/commandcentral-builder:${TAG}_${TAG_SUFFIX}'
      sh 'docker rmi daerepository03.eur.ad.sag:4443/${DTR_ORG}/commandcentral-builder:${TAG}${VERSION}'
    }
  }
}
