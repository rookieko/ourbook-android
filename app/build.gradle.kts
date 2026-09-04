

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// FCM 플러그인은 google-services.json 이 있을 때만 적용한다.
// 이 저장소에는 해당 파일이 포함되지 않으므로, 없으면 푸시 알림 없이 빌드된다.
// 사용하려면 Firebase 콘솔에서 받은 파일을 app/google-services.json 에 둔다.
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}

android {
    namespace = "com.example.ourbook"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ourbook"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
        // AGP 8 부터 기본 비활성. BuildConfig.DEBUG 로 로깅 수준을 가르기 위해 켠다
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")

    // TODO: Add the dependencies for Firebase products you want to use

    // https://mvnrepository.com/artifact/com.github.bumptech.glide/compiler
    implementation("com.github.bumptech.glide:annotations:4.16.0")
    // https://mvnrepository.com/artifact/com.github.bumptech.glide/glide
    implementation("com.github.bumptech.glide:glide:4.16.0")


    // https://mvnrepository.com/artifact/androidx.core/core-splashscreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries

    // https://mvnrepository.com/artifact/com.google.android.material/material
    implementation("com.google.android.material:material:1.9.0")

    // RecyclerView 라이브러리
    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    // https://mvnrepository.com/artifact/com.squareup.moshi/moshi
    implementation("com.squareup.moshi:moshi:1.15.0")
    // https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-gson
//    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-moshi
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    // 툴팁
    implementation("com.github.skydoves:balloon:1.6.4")


    implementation("com.squareup.moshi:moshi:1.15.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("org.greenrobot:eventbus:3.3.1")
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}