def call(def agent, def branch, def project) {
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
      app = docker.build("mdshoaib707/mediwiki-tg")
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
  }
}
