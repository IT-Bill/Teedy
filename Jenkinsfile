pipeline {
  agent any

  environment {
    // 在 Jenkins “Manage Credentials” 中配置的 Docker Hub 凭据 ID
    DOCKER_HUB_CREDENTIALS = 'dockerhub_credentials'
    // 镜像地址与标签
    IMAGE_NAME = 'itbill8888/teedy-dev'
    IMAGE_TAG  = '20250518'
  }

  stages {
    stage('Checkout') {
      steps {
        // 从 GitHub 上拉取 jenkins 分支
        git url: 'https://github.com/IT-Bill/Teedy.git',
            branch: 'jenkins'
      }
    }

    stage('Build Image') {
      steps {
        script {
          // 构建镜像，打上指定 tag
          docker.build("${IMAGE_NAME}:${IMAGE_TAG}")
        }
      }
    }

    stage('Push Image') {
      steps {
        script {
          // 登录并推送到 Docker Hub
          docker.withRegistry('https://registry.hub.docker.com', DOCKER_HUB_CREDS) {
            docker.image("${IMAGE_NAME}:${IMAGE_TAG}").push()
            // 可选：同时维护一个 latest 标签
            docker.image("${IMAGE_NAME}:${IMAGE_TAG}").push('latest')
          }
        }
      }
    }

    stage('Deploy Containers') {
      steps {
        script {
          // 启动三个容器，映射端口 8082/8083/8084 到容器内 8080
          [8082, 8083, 8084].each { port ->
            sh """
              docker rm -f teedy_${port} || true
              docker run -d \
                --name teedy_${port} \
                -p ${port}:8080 \
                ${IMAGE_NAME}:${IMAGE_TAG}
            """
          }
        }
      }
    }
  }

  post {
    success {
      echo "Practice12 CI/CD 流水线执行完毕：镜像已构建、推送，并在 8082/3/4 启动三实例。"
    }
  }
}
