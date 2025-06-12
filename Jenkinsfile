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

// ==================== STAGE 4: SECURITY SCANNING & REPORTING (CORRECTED) ====================
        stage('4. Security Scanning & Reporting') {
            steps {
                script {
                    // Create a directory for the reports
                    sh 'mkdir -p security-reports'
                    
                    // Step 1: Download the official HTML template from Trivy's GitHub repository.
                    // This ensures the template file is always available locally.
                    echo 'Downloading Trivy HTML report template...'
                    sh 'wget https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/html.tpl -O html.tpl'

                    // Step 2: Run the scans using the LOCAL template file.
                    // Notice the --template flag now points to "./html.tpl"
                    
                    // Run the IaC scan and save the report as HTML.
                    echo 'Scanning IaC files and generating report...'
                    sh 'trivy config --format template --template "./html.tpl" -o security-reports/iac-report.html .'

                    // Run the image scan and save the report as HTML.
                    echo 'Scanning Docker image and generating report...'
                    sh "trivy image --format template --template "./html.tpl" --severity CRITICAL,HIGH,MEDIUM,LOW -o security-reports/image-report.html ${FULL_IMAGE_NAME_WITH_TAG}"

                    // Step 3: Archive the generated reports.
                    echo 'Archiving security reports...'
                    archiveArtifacts artifacts: 'security-reports/*.html', allowEmptyArchive: true
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
         // always {
            // echo 'Cleaning up workspace...'
            // cleanWs() // Cleans the workspace after the build
        // }
    }
}
