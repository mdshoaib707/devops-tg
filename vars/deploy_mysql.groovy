def call(def agent, def branch, def project, def APPENV, def DEVOPSBRANCH, def APPPROJECT) {
  node(agent) {
    stage ('Checkout Helm Code') {
      cleanWs()
       checkout([$class: 'GitSCM', branches: [[name: DEVOPSBRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '', url: 'https://github.com/mdshoaib707/devops-tg.git']]])
    }
    
    stage ('Deploy Mediawiki/MySQL app in k8s') {
      sh """
      export KUBECONFIG=/home/ubuntu/.kube/config
      echo $pwd
      cd ${WORKSPACE}/helm/${APPPROJECT}
      
        elif [ \${APPPROJECT} = "mysql.*" ]; then
        DEPLOYED=\$(helm list | grep -E mysql-${APPENV} | grep DEPLOYED | wc -l)  
        if [ \${DEPLOYED} = 0 ]; then
          helm install --name mysql-${APPENV} -f values-${APPENV} mysql-${APPENV}
          echo Deployed!!!
        else
          helm upgrade -f values-${APPENV} mysql-${APPENV} mysql-${APPENV}
          echo Deployed!!!
        fi
      fi
      """
    }
  }
}
