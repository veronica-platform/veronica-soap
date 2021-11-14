pipeline {
    agent {  label 'docker' }
    stages {
        stage('Setup') {
            steps {
                rtMavenDeployer(
                    id: 'deployer',
                    serverId: 'rp-artifactory-server',
                    releaseRepo: 'libs-release-local',
                    snapshotRepo: 'libs-snapshot-local'
                )
                rtMavenResolver(
                    id: 'resolver',
                    serverId: 'rp-artifactory-server',
                    releaseRepo: 'libs-release',
                    snapshotRepo: 'libs-snapshot'
                )
            }
        }
        stage('Build') {
            steps {
                configFileProvider([configFile(fileId: 'rp_maven_settings', variable: 'MAVEN_SETTINGS')]) {
                    rtMavenRun (
                        tool: 'MAVEN_3.8.3',
                        pom: 'pom.xml',
                        goals: '-s $MAVEN_SETTINGS clean install',
                        deployerId: 'deployer',
                        resolverId: 'resolver'
                    )
                }
            }
        }
        stage('Release') {
            steps {
                rtPublishBuildInfo (
                    serverId: 'rp-artifactory-server'
                )
                rtAddInteractivePromotion (
                    serverId: 'rp-artifactory-server',
                    buildName: JOB_NAME,
                    buildNumber: BUILD_NUMBER
                )
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
