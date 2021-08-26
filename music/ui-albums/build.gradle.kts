apply("$rootDir/android-library.gradle")

val implementation by configurations

dependencies {
    implementation(project(Modules.components))
    implementation(project(Modules.dataAlbums))

    implementation(Libs.Accompanist.insets)
}