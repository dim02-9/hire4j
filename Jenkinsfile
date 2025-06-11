pipeline {
    agent {
        label 'kopsagent'
    }

    tools {
        maven 'MAVEN3.9'
        jdk 'JDK21'
    }

    environment {
        DOCKERHUB_USERNAME            = 'dganev9' 
        APP_NAME                      = 'hire4j-app'
        DOCKERHUB_CREDENTIALS_ID      = 'docker'
        KOPS_KUBECONFIG_CREDENTIAL_ID = 'kubeconfig'
        K8S_DEPLOYMENT_NAME           = 'hire4j-app-deployment'
        K8S_CONTAINER_NAME            = 'hire4j-app-container'
    }

    stages {
        stage('1. Checkout Code') {
            steps {
                echo 'Checking out code from SCM...'
                checkout scm 
            }
        }

        stage('2. Build Java Application') {
            steps {
                echo 'Building Java application (.jar)...'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('3. Build & Push Docker Image') {
            steps {
                script {
                    // Define the full image name with its tag in a local variable.
                    def imageTag = "build-${env.BUILD_NUMBER}-${env.GIT_COMMIT.substring(0, 7)}"
                    def fullImageNameWithRepo = "${env.DOCKERHUB_USERNAME}/${env.APP_NAME}:${imageTag}"

                    // Use this local variable for all subsequent steps within this stage.
                    echo "Building Docker image: ${fullImageNameWithRepo}"
                    docker.build(fullImageNameWithRepo, '.')

                    docker.withRegistry('https://index.docker.io/v1/', env.DOCKERHUB_CREDENTIALS_ID) {
                        echo "Pushing Docker image to Docker Hub: ${fullImageNameWithRepo}"
                        docker.image(fullImageNameWithRepo).push()
                    }
                    
                    // Pass the image name to the next stage by assigning it to a new environment variable.
                    env.IMAGE_TO_DEPLOY = fullImageNameWithRepo
                }
            }
        }

        stage('4. Deploy to Kops Kubernetes') {
            steps {
                // Use the variable created in the previous stage.
                echo "Preparing to deploy image ${env.IMAGE_TO_DEPLOY} to Kops environment"

                withCredentials([file(credentialsId: env.KOPS_KUBECONFIG_CREDENTIAL_ID, variable: 'KUBECONFIG_FILE_PATH')]) {
                    withEnv(["KUBECONFIG=${env.KUBECONFIG_FILE_PATH}"]) {
                        
                        echo "Applying image ${env.IMAGE_TO_DEPLOY} to deployment..."
                        
                        // Use the new variable in the kubectl command.
                        sh "kubectl set image deployment/${env.K8S_DEPLOYMENT_NAME} ${env.K8S_CONTAINER_NAME}=${env.IMAGE_TO_DEPLOY}"
                        
                        echo "Waiting for deployment rollout..."
                        sh "kubectl rollout status deployment/${env.K8S_DEPLOYMENT_NAME} --timeout=5m"

                        echo "Deployment to Kops Staging complete."
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
