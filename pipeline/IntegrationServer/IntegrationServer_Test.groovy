pipeline {
  parameters {
    string(name: 'TAG', defaultValue: '10.3', description: 'Product Version')
    string(name: 'CC_TAG', defaultValue: '10.3.1', description: 'Product Version')
    string(name: 'DTR_ORG', defaultValue: 'cdep-dev', description: 'DTR Organization')
    string(name: 'MINOR_VERSION', defaultValue: '.1', description: 'Minor Version')
    string(name: 'FIXES', defaultValue: 'wMFix.OSGI.Platform,wMFix.ALL.SIN,wMFix.SPM,wMFix.CCShared,wMFix.LARSharedBundles,wMFix.integrationServer.Core,wMFix.integrationServer.SPM,wMFix.NUMClient.CommonLibraries,wMFix.NUMRepository.SharedBundles,wMFix.jdbcAdapter,wMFix.integrationServer.ART,wMFix.MOCpieBundle', description: 'List of Fixes')
  }
  environment {
    TAG = "${params.TAG}"
    CC_TAG= "${params.CC_TAG}"
    MINOR_VERSION = "${params.MINOR_VERSION}"
    DTR_ORG = "${params.DTR_ORG}"
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
    stage('Test IS') {
      environment {
        TEMP_DIR = """${sh(
            returnStdout: true,
            script: 'mktemp -d -u -p $WORKSPACE -t tmp_XXXXX'
          ).trim()}"""
        MYVARNAME_USR="1724"
      }
      agent {
        docker {
          registryUrl "https://daerepository03.eur.ad.sag:4443"
          registryCredentialsId 'DTR_cdep-dev'
          image "daerepository03.eur.ad.sag:4443/${DTR_ORG}/integration-server:${TAG}${MINOR_VERSION}"
          args '-e DEBUG=1'
        }
      }
      steps {
        sh 'env'
        sh 'ls -l $WORKSPACE'
        sh 'mkdir $TEMP_DIR'
        sh 'cd pipeline/IntegrationServer ;sh ./IS_TestPkgs.sh'
      }
    }
  }
}
