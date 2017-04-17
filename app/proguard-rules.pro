# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/liujie_gyh/Documents/work/application/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-printconfiguration proguardConfig.txt   #输出混淆的配置到文件里面，最终的混淆配置并不止这个文件里面的这些内容
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-dontshrink
-dontoptimize
-dontnote android.net.http.**
-dontnote org.apache.http.**
-dontnote org.apache.commons.codec.**

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class android.support.v4.view.ViewPager{*;}
-dontwarn android.support.**

-keep class sun.misc.Unsafe { *; }
-keep class com.hct.zc.bean.** { *; }
-keep class com.hct.zc.http.result.** { *; }

-keep public class [your_pkg].R$*{
    public static final int *;
}
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-keep class android.support.**{*;}
-dontwarn ***
#-dontwarn com.google.protobuf.**
