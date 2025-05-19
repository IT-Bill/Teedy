pipeline {
  agent any
  environment {
    // DockerHub 凭据 ID
    DOCKER_HUB_CREDS = 'dockerhub_credentials'
    // Teedy 镜像
    IMAGE = 'itbill8888/teedy-dev:20250518'
  }
  stages {
    stage('Checkout') {
      steps { git url: 'https://github.com/IT-Bill/Teedy.git', branch: 'jenkins-k8s' }
    }
    stage('Deploy to K8s') {
      steps {
        script {
          // 登录 DockerHub 并 pull 镜像
          docker.withRegistry('https://registry.hub.docker.com', DOCKER_HUB_CREDS) {
            docker.image(IMAGE).pull()
          }
          // 应用 k8s 清单
          sh 'kubectl apply -f k8s/deployment-teedy.yaml'
          sh 'kubectl apply -f k8s/service-teedy.yaml'
        }
      }
    }
    stage('Verify') {
      steps {
        sh 'kubectl rollout status deployment/teedy'
        sh 'kubectl get pods,svc -o wide'
      }
    }
  }
  post {
    success {
      echo "Practice13 CI/CD 完成：Teedy 已部署到 K8s。"
    }
  }
}
