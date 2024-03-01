# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 指定代码的压缩级别
-optimizationpasses 5
-dontusemixedcaseclassnames
# 是否混淆第三方jar
-dontskipnonpubliclibraryclasses
-dontpreverify
-keepattributes SourceFile,LineNumberTable
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# keep 使用 webview 的类的所有的内部类
-keepclassmembers   class com.goldnet.mobile.activity.InfoDetailActivity$*{
    *;
}

# 保持哪些类不被混淆
-keep class android.** {*; }
-keep public class * extends android.view
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.pm
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#ACRA specifics
# we need line numbers in our stack traces otherwise they are pretty useless
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# ACRA needs "annotations" so add this...
-keepattributes *Annotation*

# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclasseswithmembers class * {
    void onClick*(...);
}
-keepclasseswithmembers class * {
    *** *Callback(...);
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# 保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# http client
-keep class org.apache.http.** {*; }


# fastjson
-keep class com.alibaba.fastjson.**{*;}
# keep 所有的 javabean
-keep class com.goldnet.mobile.entity.**{*;}
-keep class com.card.lp_server.base.**{*;}
-keep class com.card.lp_server.room.entity.**{*;}
# keep 泛型
-keepattributes Signature


-keep class org.apache.commons.lang.**{*;}



