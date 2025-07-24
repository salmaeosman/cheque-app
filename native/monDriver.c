#include <jni.h>
#include <stdio.h>
#include <windows.h>
#include "bp_projetbanque_GestionCheque_MonDriver.h"

JNIEXPORT void JNICALL Java_bp_projetbanque_GestionCheque_MonDriver_hello(JNIEnv *env, jobject obj) {
#ifdef _WIN32
    MessageBox(0, "Hello from DLL!", "DLL", MB_OK);
#else
    printf("Hello from native code!\n");
#endif
}
