pipeline {
  agent none 
  stages {

		stage('Checkout CloudDeployment Automation project') {
					agent {
						label 'vmlna01'
					}
					environment {
						workspace="mosy2"
					}
					steps {
						script {
						    dir('mosy2}')
							{
								sh 'git clone --recursive http://irepo.eur.ad.sag/scm/devops/command-central.git'
								sh 'git clone --recursive http://irepo.eur.ad.sag/scm/devops/bpms-cluster.git -b siqa-trunk-bpms-cluster'
							}
						}
					}
				}
			}
		}