Pod::Spec.new do |spec|
    spec.name                     = 'openfeedback_m3'
    spec.version                  = '1.0'
    spec.homepage                 = ''
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = ''
    spec.vendored_frameworks      = 'build/cocoapods/framework/openfeedback_m3.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '14.1'
    spec.dependency 'FirebaseAuth'
    spec.dependency 'FirebaseFirestore'
                
    if !Dir.exist?('build/cocoapods/framework/openfeedback_m3.framework') || Dir.empty?('build/cocoapods/framework/openfeedback_m3.framework')
        raise "

        Kotlin framework 'openfeedback_m3' doesn't exist yet, so a proper Xcode project can't be generated.
        'pod install' should be executed after running ':generateDummyFramework' Gradle task:

            ./gradlew :openfeedback-m3:generateDummyFramework

        Alternatively, proper pod installation is performed during Gradle sync in the IDE (if Podfile location is set)"
    end
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':openfeedback-m3',
        'PRODUCT_MODULE_NAME' => 'openfeedback_m3',
    }
                
    spec.script_phases = [
        {
            :name => 'Build openfeedback_m3',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
    spec.resources = ['build/compose/ios/openfeedback_m3/compose-resources']
end