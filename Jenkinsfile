pipeline 
{
  	agent {
			node 
			   {
				label 'vmlna02'
		    }
		  }
  environment 
  {
		workspace="/home/siqa/jenkins/checkout"
  }
  stages 
  {

		stage('Cleanup'){
			steps{
			    dir("${workspace}"){
			        sh 'rm -rf *'
			    }

			}

		    
		    
		}

		stage('Checkout CloudDeployment Automation project') 
		{
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
					steps 
					{
						script 
						{
						dir('/home/siqa/jenkins/checkout/command-central/') 
							{
									sh '$pwd'
									sh 'ant boot'
                    		}
						}
					}
		}
	
  }
}		