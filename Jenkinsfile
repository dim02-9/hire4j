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
                git branch: 'newbranch2', url: 'https://github.com/dim02-9/hire4j.git'
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
                        customImage.push() // This will push all tags associated with customImage, including 'latest' if built that way
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
    sh "kubectl apply -f ${manifestDir}/app-deployment.yaml"  
    sh "kubectl apply -f ${manifestDir}/app-service.yaml"
    

    echo "Updating deployment image via kubectl set image..."
    sh """
      kubectl set image deployment/hire4j-app-deployment \
        hire4j-app-container=${env.FULL_IMAGE_NAME_WITH_TAG} --record
    """

    echo "Waiting for rollout to complete..."
    sh "kubectl rollout status deployment/hire4j-app-deployment --timeout=5m"

    echo "Deployment to Kops Staging complete."

                }
            }
        }
    }
}

        stage('4. Security Scanning & Reporting') {
            steps {
                script {
                    sh 'mkdir -p security-reports'
                    sh 'trivy fs . > security-reports/trivyfs.txt'
                    sh 'trivy config . > security-reports/trivyconfig.txt'
                    echo 'Archiving security reports...'
                    archiveArtifacts artifacts: 'security-reports/*.txt', allowEmptyArchive: true
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
          always {
             echo 'Cleaning up workspace...'
             cleanWs() 
         }
    }
}
