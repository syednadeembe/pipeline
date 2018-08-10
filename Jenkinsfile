pipeline 
{
  agent none
  stages 
  {

		stage('Checkout CloudDeployment Automation project') 
		{
					agent 
					{
						label 'vmlna01'
					}
					environment 
					{
						workspace="/home/siqa/jenkins/checkout"
					}
					steps 
					{
						script 
						{
						    dir("${workspace}")
							{
								sh 'git clone --recursive http://irepo.eur.ad.sag/scm/devops/command-central.git'
								sh 'git clone --recursive http://irepo.eur.ad.sag/scm/devops/bpms-cluster.git -b siqa-trunk-bpms-cluster'
							}
						}
					}
		}
		stage('boot') 
		{
				
					agent 
					{
						label 'vmlna01'
					}
					environment 
					{
						workspace="/home/siqa/jenkins/checkout"
					}
					steps 
					{
						script 
						{
						dir('/home/siqa/jenkins/checkout/bpms-cluster/') 
							{
									sh 'ant -file build.xml boot'
                    		}
						}
					}
		}
	
  }
}		