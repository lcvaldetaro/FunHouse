plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.google.services) apply false
}

subprojects {
    tasks.matching { it.name == "compileCommonMainKotlinMetadata" }.all {
        enabled = false
    }

    pluginManager.withPlugin("com.android.base") {
        extensions.findByType(com.android.build.api.dsl.CommonExtension::class.java)?.let { ext ->
            ext.testOptions.unitTests.isIncludeAndroidResources = true
        }
        extensions.findByType(com.android.build.api.variant.AndroidComponentsExtension::class.java)?.let { ext ->
            ext.beforeVariants { variantBuilder ->
                (variantBuilder as? com.android.build.api.variant.HasUnitTestBuilder)?.enableUnitTest = true
            }
        }
    }
}
