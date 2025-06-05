// Jenkinsfile for Kops Deployment with Docker Hub
pipeline {
    agent {
        label 'kopsagent' // Your Jenkins agent label
    }

    tools {
        maven 'MAVEN3.9'
        jdk 'JDK21'
    }

    environment {
        DOCKERHUB_USERNAME          = 'dganev9' // Replace with your Docker Hub username
        APP_NAME                    = 'hire4j-app'              // Your application's image name
        DOCKER_IMAGE_NAME           = "${DOCKERHUB_USERNAME}/${APP_NAME}" // e.g., yourusername/hire4j-app
        DOCKERHUB_CREDENTIALS_ID    = 'docker'     // The ID of the Jenkins credential you created

        // Jenkins Credential ID for your Kops kubeconfig file
        KOPS_KUBECONFIG_CREDENTIAL_ID = '331e48da-5345-4575-8b1e-b27c83a9e61c'
    }

    stages {
        stage('1. Checkout Code') {
            steps {
                
                echo 'Checking out code...'
                git branch: 'newbranch', url: 'https://github.com/dim02-9/hire4j.git'
                
            }
        }

        stage('2. Build Java Application') {
            steps {
                dir('hire4j') {
                    
                          echo 'Building Java application (.jar)...'
                   // Assuming pom.xml is at the root of the checked-out code:
                         sh 'mvn clean package -DskipTests'
                 
                    
                }
             
            }
        }

        // Optional: AI Code Analysis stage if you have one
        // stage('X. AI Code Analysis') { ... }

        stage('3. Build & Push Docker Image to Docker Hub') {
            environment {
                IMAGE_TAG = "build-${BUILD_NUMBER}" // Unique tag for each build
            }
            steps {
                script {
                    // Assuming Dockerfile is at the root of the checked-out code
                    def dockerfilePath = '.'

                    echo "Building Docker image: ${DOCKER_IMAGE_NAME}:${IMAGE_TAG}"
                    def customImage = docker.build("${DOCKER_IMAGE_NAME}:${IMAGE_TAG}", dockerfilePath)

                    echo "Pushing Docker image to Docker Hub: ${DOCKER_IMAGE_NAME}:${IMAGE_TAG}"
                    docker.withRegistry("https://index.docker.io/v1/", env.DOCKERHUB_CREDENTIALS_ID) {
                        customImage.push() // This will push all tags associated with customImage, including 'latest' if built that way
                        // To push a specific tag if you added more:
                        // customImage.push("${IMAGE_TAG}")
                    }
                    
                    // Store the full image name with tag for the deployment stage
                    env.FULL_IMAGE_NAME_WITH_TAG = "${DOCKER_IMAGE_NAME}:${IMAGE_TAG}"
                }
            }
        }

        stage('4. Deploy to Kops Kubernetes (Staging)') {
    steps {
        script {
            echo "Applying Kubernetes manifests for image ${env.FULL_IMAGE_NAME_WITH_TAG} to Kops Staging environment"

            withCredentials([kubeconfigContent(credentialsId: env.KOPS_KUBECONFIG_CREDENTIAL_ID, variable: 'KUBECONFIG_CONTENTS')]) {
                sh '''
                    echo "${KUBECONFIG_CONTENTS}" > ./kubeconfig-temp-kops
                    export KUBECONFIG=./kubeconfig-temp-kops

                    kubectl apply -f k8s/mysql-secret.yaml
                    kubectl apply -f k8s/mysql-deployment.yaml
                    kubectl apply -f k8s/mysql-service.yaml

                    
                    kubectl set image deployment/hire4j-app-deployment hire4j-app-container=${FULL_IMAGE_NAME_WITH_TAG} --record
                    kubectl apply -f k8s/app-deployment.yaml # Apply any other changes in the file

                                        
                    kubectl apply -f k8s/app-service.yaml

                    echo "Waiting for deployment rollout..."
                    kubectl rollout status deployment/hire4j-app-deployment --timeout=5m

                    rm ./kubeconfig-temp-kops
                '''
            }
            echo "Deployment to Kops Staging complete."
        }
    }
}

        // Optional: stage('Approval for Production') { ... }
        // Optional: stage('Deploy to Kops Kubernetes (Production)') { ... }
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
            cleanWs() // Cleans the workspace after the build
        }
    }
}
