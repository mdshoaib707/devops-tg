def call(def agent, def branch, def project) {
  node(agent) {
    stage ('Checkout code') {
      cleanWs()
      checkout([$class: 'GitSCM', branches: [[name: branch]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '', url: 'https://github.com/mdshoaib707/code-tw.git']]])
    }

    stage ('Docker Image Build') {
      sh """
      cd ${project}
      docker rm \$(docker ps -aq) -f || true
      docker rmi \$(docker images -aq) -f || true
      docker build -t jenkins:latest .
      """
    }

    stage ('Docker Image Push') {
      sh """
      docker tag jenkins:latest mdshoaib707/mediawiki-tg:${BUILD_NUMBER}
      docker tag jenkins:latest mdshoaib707/mediawiki-tg:latest
      docker push mdshoaib707/mediawiki-tg:${BUILD_NUMBER}
      docker push mdshoaib707/mediawiki-tg:latest
      """
    }
  }
}
