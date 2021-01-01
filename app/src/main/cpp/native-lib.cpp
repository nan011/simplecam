#include <jni.h>
#include <cstring>
#include <iostream>
#include <vector>

void sortArr(double arr[], int indexes[], int n) {
    // Vector to store element
    // with respective present index
    std::vector<std::pair<double, int>> vp;

    // Inserting element in pair vector
    // to keep track of previous indexes
    vp.reserve(n);
    for (int i = 0; i < n; ++i) {
        vp.emplace_back(arr[i], i);
    }

    // Sorting pair vector
    sort(vp.begin(), vp.end());

    for (int i = 0; i < vp.size(); i++) {
        indexes[i] = vp[i].second;
    }
}

extern "C" JNIEXPORT jintArray JNICALL
Java_id_ui_ac_cs_mobileprogramming_nandhikaprayoga_simplecam_common_Utility_00024Companion_cppSort(
        JNIEnv *env,
        jobject instance,
        jdoubleArray list) {
    int n = env->GetArrayLength(list);

    double* arr = env->GetDoubleArrayElements(list, JNI_FALSE);
    int indexes[n];
    sortArr(arr, indexes, n);

    jintArray return_indexes = env->NewIntArray(n);
    jint temp[n];
    for (int i = 0; i < n; i++) {
        temp[i] = indexes[i];
    }
    env->SetIntArrayRegion(return_indexes, 0, n, temp);

    return return_indexes;
}