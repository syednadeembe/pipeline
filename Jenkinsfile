	pipeline 
{
  	agent {
			node 
			   {
				label 'vmlna02'
		       }
		  }
		  tools
		  {
		      
		     ant 'ant-1.9.7'
		  }

  environment 
  {
		workspace="/home/siqa/jenkins/checkout"
		installationDir="/home/siqa/sag"
	//	spmServerLocation=
	//	cceServerLocation=
  }
  stages 
  {

		stage('Cleanup '){
			steps{
/*				dir("${spmServerLocation}"){
			    }
			    dir("${cceServerLocation}"){

			    }
			    dir("${workspace}"){
			        sh 'rm -rf *'
					sh 'ls -l'
			    }
*/				dir("${installationDir}"){
					sh 'ls -l'
			        sh 'rm -rf *'
			    }
				
			}
		}	
		    
		    


		stage('Checkout Automation Project') 
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
		stage('Boot') 
		{				
					steps 
					{
						script 
						{
						dir('/home/siqa/jenkins/checkout/command-central/') 
							{
									sh 'ant boot'
                    		}
						}
					}
		}
		
				stage('Up') 
		{				
					steps 
					{
						script 
						{
						dir('/home/siqa/jenkins/checkout/command-central/') 
							{
									sh 'ant up'
                    		}
						}
					}
		}
	
  }
}				