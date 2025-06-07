pipeline {
    agent {
        label 'kopsagent'
    }

    tools {
        maven 'MAVEN3.9'
        jdk 'JDK21'
    }

    environment {
        DOCKERHUB_USERNAME          = 'dganev9' 
        APP_NAME                    = 'hire4j-app'              
        DOCKER_IMAGE_NAME           = "${DOCKERHUB_USERNAME}/${APP_NAME}" 
        DOCKERHUB_CREDENTIALS_ID    = 'docker'     

        KOPS_KUBECONFIG_CREDENTIAL_ID = 'kubeconfig'
    }

    stages {
        stage('1. Checkout Code') {
            steps {
                
                echo 'Checking out code...'
                echo 'Checking out code from SCM...'
                checkout scm /
                echo 'Building Java application (.jar)...'
                sh 'mvn clean package -DskipTests'
                 
                
            }
        }

    
        

        stage('3. Build & Push Docker Image to Docker Hub') {
            environment {
                IMAGE_TAG = "build-${BUILD_NUMBER}" 
            }
            steps {
                script {
                    def dockerfilePath = '.'

                    echo "Building Docker image: ${DOCKER_IMAGE_NAME}:${IMAGE_TAG}"
                    def customImage = docker.build("${DOCKER_IMAGE_NAME}:${IMAGE_TAG}", dockerfilePath)

                    echo "Pushing Docker image to Docker Hub: ${DOCKER_IMAGE_NAME}:${IMAGE_TAG}"
                    docker.withRegistry("https://index.docker.io/v1/", env.DOCKERHUB_CREDENTIALS_ID) {
                        customImage.push() 
                    }
                    
                    env.FULL_IMAGE_NAME_WITH_TAG = "${DOCKER_IMAGE_NAME}:${IMAGE_TAG}"
                }
            }
        }

       stage('4. Deploy to Kops Kubernetes (Staging)') {
    steps {
        echo "Preparing to deploy image ${env.FULL_IMAGE_NAME_WITH_TAG} to Kops Staging environment"

        withCredentials([file(credentialsId: env.KOPS_KUBECONFIG_CREDENTIAL_ID, variable: 'KUBECONFIG_FILE_PATH')]) {
            withEnv(["KUBECONFIG=${env.KUBECONFIG_FILE_PATH}"]) {
                script {
                    def manifestDir = 'k8s'

                    echo "Applying prerequisite Kubernetes manifests..."
                    sh "kubectl apply -f ${manifestDir}/mysql-secret.yaml"
                    sh "kubectl apply -f ${manifestDir}/mysql-pvc.yaml"
                    sh "kubectl apply -f ${manifestDir}/mysql-deployment.yaml"
                    sh "kubectl apply -f ${manifestDir}/mysql-service.yaml"
                    sh "kubectl apply -f ${manifestDir}/app-service.yaml" 

                    echo "Updating application deployment manifest with new image tag..."
                    sh "sed -i 's|IMAGE_PLACEHOLDER|${env.FULL_IMAGE_NAME_WITH_TAG}|g' ${manifestDir}/app-deployment.yaml"

                    echo "Applying updated application deployment manifest..."
                    sh "kubectl apply -f ${manifestDir}/app-deployment.yaml --record"

                    echo "Waiting for deployment rollout..."
                    sh "kubectl rollout status deployment/hire4j-app-deployment --timeout=5m"

                    echo "Deployment to Kops Staging complete."
                }
            }
        }
    }
}

       
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
        }
        failure {
            echo 'Pipeline failed!'
        }
       
    }
}
