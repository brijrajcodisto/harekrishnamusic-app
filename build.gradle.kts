// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    // ADD THESE TWO LINES (ensure they use 'apply false')
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false

}