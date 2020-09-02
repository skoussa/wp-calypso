package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'RunAllUnitTests'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("RunAllUnitTests")) {
    expectSteps {
        script {
            name = "Prepare environment"
            scriptContent = """
                set -e
                export HOME="/calypso"
                export NODE_ENV="test"
                export CHROMEDRIVER_SKIP_DOWNLOAD=true
                export PUPPETEER_SKIP_DOWNLOAD=true
                export npm_config_cache=${'$'}(yarn cache dir)
                
                # Update node
                . "${'$'}NVM_DIR/nvm.sh" --install
                nvm use
                
                # Install modules
                yarn install
            """.trimIndent()
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerImage = "%docker_image%"
            dockerRunParameters = "-u %env.UID%"
        }
        script {
            name = "Code hygiene"
            scriptContent = """
                set -e
                set -x
                export HOME="/calypso"
                export NODE_ENV="test"
                
                # Update node
                . "${'$'}NVM_DIR/nvm.sh"
                
                # Prevent uncommited changes
                DIRTY_FILES=${'$'}(git status --porcelain 2>/dev/null)
                if [ ! -z "${'$'}DIRTY_FILES" ]; then
                	echo "Repository contains uncommitted changes: "
                	echo "${'$'}DIRTY_FILES"
                	echo "You need to checkout the branch, run 'yarn' and commit those files."
                	exit 1
                fi
                
                # Code style
                FILES_TO_LINT=${'$'}(git diff --name-only --diff-filter=d refs/remotes/origin/master...HEAD | grep -E '^(client/|server/|packages/)' | grep -E '\.[jt]sx?${'$'}' || exit 0)
                echo ${'$'}FILES_TO_LINT
                if [ ! -z "${'$'}FILES_TO_LINT" ]; then
                	yarn run eslint --format junit --output-file "./test_results/eslint/results.xml" ${'$'}FILES_TO_LINT
                fi
                
                # Run type checks
                yarn run tsc --project client/landing/gutenboarding
            """.trimIndent()
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerImage = "%docker_image%"
            dockerRunParameters = "-u %env.UID%"
        }
        script {
            name = "Run unit tests"
            scriptContent = """
                set -e
                export JEST_JUNIT_OUTPUT_NAME="results.xml"
                export HOME="/calypso"
                export NODE_ENV="test"
                
                # Update node
                . "${'$'}NVM_DIR/nvm.sh"
                
                # Run client tests
                JEST_JUNIT_OUTPUT_DIR="./test_results/client" yarn test-client --maxWorkers=${'$'}JEST_MAX_WORKERS --ci --reporters=default --reporters=jest-junit --silent
                
                # Run packages tests
                JEST_JUNIT_OUTPUT_DIR="./test_results/packages" yarn test-packages --maxWorkers=${'$'}JEST_MAX_WORKERS --ci --reporters=default --reporters=jest-junit --silent
                
                # Run server tests
                JEST_JUNIT_OUTPUT_DIR="./test_results/server" yarn test-server --maxWorkers=${'$'}JEST_MAX_WORKERS --ci --reporters=default --reporters=jest-junit --silent
                
                # Run Editing Toolkit tests
                cd apps/editing-toolkit
                JEST_JUNIT_OUTPUT_DIR="../../test_results/editing-toolkit" yarn test:js --reporters=default --reporters=jest-junit  --maxWorkers=${'$'}JEST_MAX_WORKERS
            """.trimIndent()
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerImage = "%docker_image%"
            dockerRunParameters = "-u %env.UID%"
        }
        script {
            name = "Build artifacts"
            scriptContent = """
                set -e
                export HOME="/calypso"
                export NODE_ENV="test"
                
                # Update node
                . "${'$'}NVM_DIR/nvm.sh"
                
                # Build o2-blocks
                (cd apps/o2-blocks/ && yarn build --output-path="../../artifacts/o2-blocks")
                
                # Build wpcom-block-editor
                (cd apps/wpcom-block-editor/ && yarn build --output-path="../../artifacts/wpcom-block-editor")
                
                # Build notifications
                (cd apps/notifications/ && yarn build --output-path="../../artifacts/notifications")
            """.trimIndent()
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerImage = "%docker_image%"
            dockerRunParameters = "-u %env.UID%"
        }
    }
    steps {
        update<ScriptBuildStep>(0) {
            clearConditions()
            dockerPull = true
        }
        update<ScriptBuildStep>(1) {
            clearConditions()
            dockerPull = true
        }
        update<ScriptBuildStep>(2) {
            clearConditions()
            scriptContent = """
                set -e
                export JEST_JUNIT_OUTPUT_NAME="results.xml"
                export HOME="/calypso"
                
                unset NODE_ENV
                unset CALYPSO_ENV
                
                # Update node
                . "${'$'}NVM_DIR/nvm.sh"
                
                # Run client tests
                JEST_JUNIT_OUTPUT_DIR="./test_results/client" yarn test-client --maxWorkers=${'$'}JEST_MAX_WORKERS --ci --reporters=default --reporters=jest-junit --silent
                
                # Run packages tests
                JEST_JUNIT_OUTPUT_DIR="./test_results/packages" yarn test-packages --maxWorkers=${'$'}JEST_MAX_WORKERS --ci --reporters=default --reporters=jest-junit --silent
                
                # Run server tests
                JEST_JUNIT_OUTPUT_DIR="./test_results/server" yarn test-server --maxWorkers=${'$'}JEST_MAX_WORKERS --ci --reporters=default --reporters=jest-junit --silent
                
                # Run Editing Toolkit tests
                cd apps/editing-toolkit
                JEST_JUNIT_OUTPUT_DIR="../../test_results/editing-toolkit" yarn test:js --reporters=default --reporters=jest-junit  --maxWorkers=${'$'}JEST_MAX_WORKERS
            """.trimIndent()
            dockerPull = true
        }
        update<ScriptBuildStep>(3) {
            clearConditions()
            dockerPull = true
        }
    }

    triggers {
        val trigger1 = find<VcsTrigger> {
            vcs {
            }
        }
        trigger1.apply {
            branchFilter = """
                +:pull/*
                +:master
                +:trunk
            """.trimIndent()
        }
    }

    features {
        val feature1 = find<PullRequests> {
            pullRequests {
                vcsRootExtId = "${DslContext.settingsRoot.id}"
                provider = github {
                    authType = token {
                        token = "credentialsJSON:c834538f-90ff-45f5-bbc4-64406f06a28d"
                    }
                    filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER
                }
            }
        }
        feature1.apply {
            enabled = false
            provider = github {
                serverUrl = ""
                authType = token {
                    token = "credentialsJSON:803e3446-004d-4f66-a444-80bdc18dbd64"
                }
                filterTargetBranch = ""
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER_OR_COLLABORATOR
            }
        }
        add {
            commitStatusPublisher {
                vcsRootExtId = "${DslContext.settingsRoot.id}"
                publisher = github {
                    githubUrl = "https://api.github.com"
                    authType = personalToken {
                        token = "credentialsJSON:c834538f-90ff-45f5-bbc4-64406f06a28d"
                    }
                }
                param("github_oauth_user", "scinos")
            }
        }
    }
}
