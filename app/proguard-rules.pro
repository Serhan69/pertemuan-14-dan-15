# Add project specific ProGuard rules here.
# You can use the set property applyDefaultProguardFile to include the default files.
# For example:
#   proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

# Room Entities & Database
-keep class com.industri.fleettrack.data.local.entity.** { *; }
-keep class com.industri.fleettrack.data.local.AppDatabase { *; }
-keep class com.industri.fleettrack.data.local.dao.** { *; }

# Retrofit & Gson models
-keep class com.industri.fleettrack.data.remote.api.** { *; }
-keepattributes *Annotation*, Signature
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Timber
-keepclassmembers class * extends timber.log.Timber$Tree {
    <methods>;
}
