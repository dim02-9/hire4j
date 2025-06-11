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
        DOCKERHUB_CREDENTIALS_ID    = 'docker'
        KOPS_KUBECONFIG_CREDENTIAL_ID = 'kubeconfig'
        K8S_DEPLOYMENT_NAME         = 'hire4j-app-deployment'
        // IMPORTANT: Change this to the container name inside your app-deployment.yaml
        K8S_CONTAINER_NAME          = 'hire4j-app-container' 
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
            // This environment variable will be created and made available to subsequent stages
            environment {
                FULL_IMAGE_NAME_WITH_TAG = ""
            }
            steps {
                script {
                    // Define the image tag once using the commit hash for uniqueness
                    def imageTag = "build-${env.BUILD_NUMBER}-${env.GIT_COMMIT.substring(0, 7)}"
                    // Construct the full image name and assign it to the environment variable
                    env.FULL_IMAGE_NAME_WITH_TAG = "${DOCKERHUB_USERNAME}/${APP_NAME}:${imageTag}"
                    
                    echo "Building Docker image: ${env.FULL_IMAGE_NAME_WITH_TAG}"
                    docker.build(env.FULL_IMAGE_NAME_WITH_TAG, '.')

                    // Use the correct credentials ID from the environment block
                    docker.withRegistry('https://index.docker.io/v1/', env.DOCKERHUB_CREDENTIALS_ID) {
                        echo "Pushing Docker image to Docker Hub: ${env.FULL_IMAGE_NAME_WITH_TAG}"
                        docker.image(env.FULL_IMAGE_NAME_WITH_TAG).push()
                    }
                }
            }
        }

        stage('4. Deploy to Kops Kubernetes') {
            steps {
                echo "Preparing to deploy image ${env.FULL_IMAGE_NAME_WITH_TAG} to Kops environment"

                withCredentials([file(credentialsId: env.KOPS_KUBECONFIG_CREDENTIAL_ID, variable: 'KUBECONFIG_FILE_PATH')]) {
                    withEnv(["KUBECONFIG=${env.KUBECONFIG_FILE_PATH}"]) {
                        
                        echo "Applying image ${env.FULL_IMAGE_NAME_WITH_TAG} to deployment..."
                        
                        // Use the variables defined in the environment block for clarity and consistency
                        sh "kubectl set image deployment/${env.K8S_DEPLOYMENT_NAME} ${env.K8S_CONTAINER_NAME}=${env.FULL_IMAGE_NAME_WITH_TAG}"
                        
                        echo "Waiting for deployment rollout..."
                        sh "kubectl rollout status deployment/${env.K8S_DEPLOYMENT_NAME} --timeout=5m"

                        echo "Deployment to Kops Staging complete."
                    }
                }
            }
        }
    } // This brace correctly closes the 'stages' block

    post {
        success {
            echo 'Pipeline completed successfully!'
            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
} // This brace correctly closes the 'pipeline' block
