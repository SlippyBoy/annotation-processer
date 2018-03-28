# annotation-processor
android annotation-processor demo

# create annotation project
New -> New Module... -> Java Library

# create processor project
New -> New Module... -> Java Library

    dependencies {
        ...
        compileOnly 'com.google.auto.service:auto-service:1.0-rc4'
        implementation 'com.squareup:javapoet:1.10.0'
        implementation project(':annotation')
    }
# app dependencies
    dependencies {
        ...
        implementation project(':annotation')
        annotationProcessor project(':processor')
    }