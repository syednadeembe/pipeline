pipeline {
  agent none 
  stages {

		stage('Checkout CloudDeployment Automation project') {
					agent {
						label 'vmlna01'
					}
					environment {
						workspace="/home/siqa/"
					}
					steps {
						script {
						    dir("${workspace}")
							{
								sh 'git clone --recursive http://irepo.eur.ad.sag/scm/devops/command-central.git'
								sh 'git clone --recursive http://irepo.eur.ad.sag/scm/devops/bpms-cluster.git -b siqa-trunk-bpms-cluster'
							}
						}
					}
				}
			}
		}