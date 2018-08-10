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
						dir('/home/siqa/jenkins/checkout/bpms-cluster/') 
							{
									sh 'ant boot'
                    		}
						}
					}
		}
	
  }
}		