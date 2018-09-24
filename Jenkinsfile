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
	    cceAutomationTemplateLocation="/home/siqa/jenkins/checkout/command-central"
		bpmAutomationTemplateLocation="/home/siqa/jenkins/checkout/bpms-cluster"
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
		//			sh 'ls -l'
		//	        sh 'rm -rf *'
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
							//	sh 'git clone --recursive http://irepo.eur.ad.sag/scm/devops/command-central.git'
								sh 'git clone --recursive http://irepo.eur.ad.sag/scm/devops/bpms-cluster.git -b siqa-trunk-bpms-cluster'
							}
						}
					}
		}
	/*	stage('Boot') 
		{				
					steps 
					{
						script 
						{
						dir("${cceAutomationTemplateLocation}")
							{
									sh 'ant boot -Dbootstrap=blr -Dinstaller=cc-def-10.4-milestone-lnxamd64'
                    		}
						}
					}
		}
	*/	
		stage('Up') 
		{				
					steps 
					{
						script 
						{
						dir("${cceAutomationTemplateLocation}")
							{
									sh 'ant up'
                    		}
						}
					}
		}
/*		stage('Execute') 
		{				
					steps 
					{
						script 
						{
						dir("${bpmAutomationTemplateLocation}")
							{
									sh ''
                    		}
						}
					}
		}
*/	
  }
}				