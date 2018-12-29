def call(def agent, def branch, def project, def APPENV, def DEVOPSBRANCH, def APPPROJECT) {
  node(agent) {
    stage ('Checkout code') {
      cleanWs()
      checkout([$class: 'GitSCM', branches: [[name: branch]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '', url: 'https://github.com/mdshoaib707/code-tw.git']]])
    }

    stage ('Build Image') {
      sh """
      cd ${WORKSPACE}
      echo "Cleaning up old docker images"
      docker rm \$(docker ps -aq) -f || true
      docker rmi \$(docker images -aq) -f || true
      """
      app = docker.build("mdshoaib707/mediawiki-tg")
    }

    stage ('Push Image') {
      docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
        app.push("${env.BUILD_NUMBER}")
        app.push("latest")
      }
    }
    
    stage ('Delete images locally') {
      sh """
      docker rm \$(docker ps -aq) -f || true
      docker rmi \$(docker images -aq) -f || true
      """
    }
    
    stage ('Checkout Helm Code') {
      cleanWs()
       checkout([$class: 'GitSCM', branches: [[name: DEVOPSBRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '', url: 'https://github.com/mdshoaib707/devops-tg.git']]])
    }
    
    stage ('Deploy Mediawiki/MySQL app in k8s') {
      sh """
      export KUBECONFIG=/home/ubuntu/.kube/config
      echo $pwd
      cd ${WORKSPACE}/helm/${APPPROJECT}
      
      if [ \${APPPROJECT} = "mediawiki.*" ]; then
        DEPLOYED=\$(helm list | grep -E mediawiki-${APPENV} | grep DEPLOYED | wc -l)  
        if [ \${DEPLOYED} = 0 ]; then
          helm install --name mediawiki-${APPENV} -f values-${APPENV} mediawiki-${APPENV}
          echo Deployed!!!
        else
          helm upgrade -f values-${APPENV} mediawiki-${APPENV} mediawiki-${APPENV}
          echo Deployed!!!
        fi
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
