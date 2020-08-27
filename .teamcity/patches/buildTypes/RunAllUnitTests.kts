package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'RunAllUnitTests'
in the root project, and delete the patch script.
*/
create(DslContext.projectId, BuildType({
    id("RunAllUnitTests")
    name = "Run all unit tests"
    description = "test"

    artifactRules = """
        test_results => test_results
        artifacts => artifacts
    """.trimIndent()

    vcs {
        root(DslContext.settingsRoot)

        cleanCheckout = true
    }

    steps {
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
            dockerImage = "automattic/wp-calypso-ci:1.0.5"
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
            dockerImage = "automattic/wp-calypso-ci:1.0.5"
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
                
                # Run FSE tests
                cd apps/full-site-editing
                JEST_JUNIT_OUTPUT_DIR="../../test_results" yarn test:js --reporters=default --reporters=jest-junit  --maxWorkers=${'$'}JEST_MAX_WORKERS
            """.trimIndent()
            dockerImagePlatform = ScriptBuildStep.ImagePlatform.Linux
            dockerImage = "automattic/wp-calypso-ci:1.0.5"
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
            dockerImage = "automattic/wp-calypso-ci:1.0.5"
            dockerRunParameters = "-u %env.UID%"
        }
    }

    features {
        feature {
            type = "xml-report-plugin"
            param("xmlReportParsing.reportType", "junit")
            param("xmlReportParsing.reportDirs", "test_results/**/*.xml")
        }
        perfmon {
        }
    }
}))

