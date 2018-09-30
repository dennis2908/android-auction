#include <jni.h>

// Mock
JNIEXPORT jstring JNICALL
Java_com_appschef_baseproject_util_JNIUtil_getWebUrlMock(JNIEnv *env, jobject instance) {
    return (*env)->NewStringUTF(env, "http://www.google.com/");
}

JNIEXPORT jstring JNICALL
Java_com_appschef_baseproject_util_JNIUtil_getApiUrlMock(JNIEnv *env, jobject instance) {
    return (*env)->NewStringUTF(env, "http://private-778487-alvinrusliappschef.apiary-mock.com/");
}

JNIEXPORT jstring JNICALL
Java_com_appschef_baseproject_util_JNIUtil_getApiKeyMock(JNIEnv *env, jobject instance) {
    return (*env)->NewStringUTF(env, "mymockapikey");
}


// Staging
JNIEXPORT jstring JNICALL
Java_com_appschef_baseproject_util_JNIUtil_getWebUrlStaging(JNIEnv *env, jobject instance) {
    return (*env)->NewStringUTF(env, "http://www.google.com/");
}

JNIEXPORT jstring JNICALL
Java_com_appschef_baseproject_util_JNIUtil_getApiUrlStaging(JNIEnv *env, jobject instance) {
    return (*env)->NewStringUTF(env, "http://private-778487-alvinrusliappschef.apiary-mock.com/");
}

JNIEXPORT jstring JNICALL
Java_com_appschef_baseproject_util_JNIUtil_getApiKeyStaging(JNIEnv *env, jobject instance) {
    return (*env)->NewStringUTF(env, "mymockapikey");
}


// Production
JNIEXPORT jstring JNICALL
Java_com_appschef_baseproject_util_JNIUtil_getWebUrlProduction(JNIEnv *env, jobject instance) {
    return (*env)->NewStringUTF(env, "http://www.google.com/");
}

JNIEXPORT jstring JNICALL
Java_com_appschef_baseproject_util_JNIUtil_getApiUrlProduction(JNIEnv *env, jobject instance) {
    return (*env)->NewStringUTF(env, "http://private-778487-alvinrusliappschef.apiary-mock.com/");
}

JNIEXPORT jstring JNICALL
Java_com_appschef_baseproject_util_JNIUtil_getApiKeyProduction(JNIEnv *env, jobject instance) {
    return (*env)->NewStringUTF(env, "myproductionapikey");
}