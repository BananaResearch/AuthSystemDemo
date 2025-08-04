pipeline {
    agent any
    environment {
        DOCKER_CREDENTIALS = credentials('aliyun_docker')
        IMAGE_TAG = ''
    }
    parameters {
        gitParameter type: 'PT_BRANCH',
                    name: 'A_BRANCH',
                    branchFilter: 'origin/(.*)',
                    defaultValue: 'master',
                    description: 'Choose a branch to checkout',
                    selectedValue: 'DEFAULT',
                    sortMode: 'DESCENDING_SMART'
    }
    stages {
        stage('Build') {
            steps {
                git branch: params.A_BRANCH,
                    credentialsId: 'ssh_github',
                    url: 'git@codeup.aliyun.com:6810853ea8498cb89770d435/AuthSystemDemo.git'
                script {
                    def image_tag = sh(script: '''
                        githash=\$(git rev-parse --short HEAD)
                        imagetime=\$(date +"%Y%m%d%H%M")
                        imagetag=\$githash-\$imagetime
                        echo \$imagetag
                    ''', returnStdout: true).trim()
                    echo "image_tag: ${image_tag}"
                    IMAGE_TAG = image_tag
                }
                script {
                    // 生成镜像标签
                    sh """
                    imagetag=$IMAGE_TAG
                    imagename=auth-system-demo
                    docker_registry=baresearch-registry.cn-beijing.cr.aliyuncs.com
                    echo "Image tag: \$imagetag"
                    # 登录到 Docker 镜像仓库
                    echo ${env.DOCKER_CREDENTIALS_PSW} | docker login -u ${env.DOCKER_CREDENTIALS_USR} --password-stdin \$docker_registry
                    # 构建 Docker 镜像
                    docker build -t \$imagename:\$imagetag .
                    # 给镜像打标签
                    docker tag \$imagename:\$imagetag \$docker_registry/baresearch/\$imagename:\$imagetag
                    # 推送镜像到 Docker 镜像仓库
                    docker push \$docker_registry/baresearch/\$imagename:\$imagetag
                    """
                }
            }
        }
        stage('Deploy dev') {
            steps {
                echo 'Deploy dev...'
                sh """
                sed -i 's/{{imagetag}}/${IMAGE_TAG}/g' k8s.yml
                ls /var/run/secrets/kubernetes.io/serviceaccount
                kubectl apply -f k8s.yml
                """
            }
        }
        stage('Testing') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}