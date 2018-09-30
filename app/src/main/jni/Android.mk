LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := constant
LOCAL_SRC_FILES := constant.c

include $(BUILD_SHARED_LIBRARY)