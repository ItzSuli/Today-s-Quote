# kotlinx.serialization keeps its generated serializers on the annotated classes.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class com.itzsuli.todaysquote.data.** {
    *** Companion;
}
-keepclasseswithmembers class com.itzsuli.todaysquote.data.** {
    kotlinx.serialization.KSerializer serializer(...);
}
