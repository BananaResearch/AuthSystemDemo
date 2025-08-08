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
                echo 'Starting testing...'
                script {
                    def TIMEOUT = 60
                    def SLEEPTIME = 5

                    def gitCommitHash = sh(script: '''
                        git rev-parse HEAD
                    ''', returnStdout: true).trim()

                    def response = httpRequest(
                        url: 'http://accurate-testing-service.testing-demo.svc.baresearch.local:8000/analyze_commit',
                        acceptType: 'APPLICATION_JSON_UTF8',
                        contentType: 'APPLICATION_JSON',
                        httpMode: 'POST',
                        requestBody: """{
                            "commit_sha": "${gitCommitHash}"
                        }"""
                    )
                    
                    if (response.status != 200) {
                        error '调用测试失败...'
                    }

                    echo "Response Body: ${response.content}"
                    def contentJson = readJSON text: response.content
                    def task_id = contentJson.task_id
                    echo "Task id: ${contentJson.task_id}"
                    
                    timeout(time: TIMEOUT, unit: 'SECONDS') {
                        while (true) {
                            def taskResponse = httpRequest(
                                url: 'http://accurate-testing-service.testing-demo.svc.baresearch.local:8000/task_status/' +  contentJson.task_id,
                                acceptType: 'APPLICATION_JSON_UTF8'
                            )
                            
                            if (taskResponse.status != 200) {
                                error '调用测试失败...'
                            }
                            def resultJson = readJSON text: taskResponse.content
                            echo "任务状态：${resultJson.status}"
                            
                            if (resultJson.status == 'completed') {
                                echo '测试成功！'
                                break
                            } else if (resultJson.status == 'failed') {
                                error '测试失败：${resultJson.error}'
                                break
                            }
                            
                            sleep SLEEPTIME;
                        }
                    }
                }
            }
        }
        stage('Deploy Prod') {
            steps {
                sleep 3
                echo 'Deploy Prod Success...'
            }
        }
    }
}