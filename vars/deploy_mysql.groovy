def call(def agent, def branch, def project, def APPENV, def DEVOPSBRANCH, def APPPROJECT) {
  node(agent) {
    stage ('Checkout Helm Code') {
      cleanWs()
       checkout([$class: 'GitSCM', branches: [[name: DEVOPSBRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '', url: 'https://github.com/mdshoaib707/devops-tg.git']]])
    }
    
    stage ('Deploy MySQL in k8s') {
      sh """
      export PATH=/var/lib/jenkins/.local/bin:$PATH
      export KUBECONFIG=/var/lib/jenkins/kube-config
      
      cd ${WORKSPACE}/helm/${APPPROJECT}
      echo $pwd
      ls
           
      DEPLOYED=\$(helm list | grep -E mysql-${APPENV} | grep DEPLOYED | wc -l)  
      if [ \${DEPLOYED} = 0 ]; then
          helm install -f values-${APPENV}.yaml --name mysql-${APPENV} .
          echo Deployed!!!
      else
          helm upgrade -f values-${APPENV}.yaml mysql-${APPENV} .
          echo Deployed!!!
      fi
      
      """
      
    }
  }
}
