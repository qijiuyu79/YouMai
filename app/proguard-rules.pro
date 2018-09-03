# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/lyn/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5

# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses

# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose

# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify

# 保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses

# 避免混淆泛型
-keepattributes Signature

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*

-keep public class * extends android.app.Activity                               # 保持哪些类不被混淆
-keep public class * extends android.app.Application                            # 保持哪些类不被混淆
-keep public class * extends android.app.Service                                # 保持哪些类不被混淆
-keep public class * extends android.content.BroadcastReceiver                  # 保持哪些类不被混淆
-keep public class * extends android.content.ContentProvider                    # 保持哪些类不被混淆
-keep public class * extends android.app.backup.BackupAgentHelper               # 保持哪些类不被混淆
-keep public class * extends android.preference.Preference                      # 保持哪些类不被混淆
-keep public class com.android.vending.licensing.ILicensingService              # 保持哪些类不被混淆
#如果有引用v4包可以添加下面这行
 -keep public class * extends android.support.v4.app.Fragment

 # 保留support下的所有类及其内部类
 -keep class android.support.** {*;}

 # 保留继承的
 -keep public class * extends android.support.v4.**
 -keep public class * extends android.support.v7.**
 -keep public class * extends android.support.annotation.**

 # 保留R下面的资源
#  -keep class **.R$* {*;}

 # 保留本地native方法不被混淆
 -keepclasseswithmembernames class * {
     native <methods>;
 }

 # 保留在Activity中的方法参数是view的方法，
 # 这样以来我们在layout中写的onClick就不会被影响
 -keepclassmembers class * extends android.app.Activity{
     public void *(android.view.View);
 }

 # 保留枚举类不被混淆
 -keepclassmembers enum * {
     public static **[] values();
     public static ** valueOf(java.lang.String);
 }

 # 保留我们自定义控件（继承自View）不被混淆
 -keep public class * extends android.view.View{
     *** get*();
     void set*(***);
     public <init>(android.content.Context);
     public <init>(android.content.Context, android.util.AttributeSet);
     public <init>(android.content.Context, android.util.AttributeSet, int);
 }

 # 保留Parcelable序列化类不被混淆
 -keep class * implements android.os.Parcelable {
     public static final android.os.Parcelable$Creator *;
 }

 # 保留Serializable序列化的类不被混淆
 -keepclassmembers class * implements java.io.Serializable {
     static final long serialVersionUID;
     private static final java.io.ObjectStreamField[] serialPersistentFields;
     !static !transient <fields>;
     !private <fields>;
     !private <methods>;
     private void writeObject(java.io.ObjectOutputStream);
     private void readObject(java.io.ObjectInputStream);
     java.lang.Object writeReplace();
     java.lang.Object readResolve();
 }

 # 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
 -keepclassmembers class * {
     void *(**On*Event);
     void *(**On*Listener);
 }

 # webView处理，项目中没有使用到webView忽略即可
 -keepclassmembers class fqcn.of.javascript.interface.for.webview {
     public *;
 }
 -keepclassmembers class * extends android.webkit.webViewClient {
     public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
     public boolean *(android.webkit.WebView, java.lang.String);
 }
 -keepclassmembers class * extends android.webkit.webViewClient {
     public void *(android.webkit.webView, jav.lang.String);
 }

 #如果引用了v4或者v7包
 -dontwarn android.support.**

 #忽略警告
 -ignorewarning

 #混淆前后的映射
 -printmapping mapping.txt

#不混淆实体类
 -keep class com.youmai.project.bean.** { *; }

 # Retrofit
 -dontwarn retrofit2.**
 -keep class retrofit2.** { *; }
 -keepattributes Signature
 -keepattributes Exceptions

-keep class com.baidu.**{*;}
-keep class com.alipay.**{*;}
-keep class vi.com.gdi.bgl.android.**{*;}
-keep class org.json.alipay.**{*;}
-keep class com.tencent.**{*;}
-keep class com.umeng.**{*;}
-keep class com.sina.**{*;}
-dontwarn com.baidu.**
-dontwarn com.alipay.**
-dontwarn org.json.alipay.**
-dontwarn com.tencent.**
-dontwarn com.umeng.**
-dontwarn com.sina.**
-dontwarn vi.com.gdi.bgl.android.**
-keepattributes Signature


-keepattributes SourceFile,LineNumberTable
-keep class com.inmobi.** { *; }
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**
-dontwarn com.squareup.picasso.**
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient{
public *;
}

-dontwarn com.squareup.okhttp.**

