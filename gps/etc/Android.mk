
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := gps.conf
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH  := $(TARGET_OUT_VENDOR)/etc/
LOCAL_SRC_FILES := gps.conf

include $(BUILD_PREBUILT)

include $(CLEAR_VARS)

LOCAL_MODULE := apdr.conf
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH  := $(TARGET_OUT_VENDOR)/etc/
LOCAL_SRC_FILES := apdr.conf

include $(BUILD_PREBUILT)

include $(CLEAR_VARS)

LOCAL_MODULE := flp.conf
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH  := $(TARGET_OUT_VENDOR)/etc/
LOCAL_SRC_FILES := flp.conf

include $(BUILD_PREBUILT)

include $(CLEAR_VARS)

LOCAL_MODULE := izat.conf
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH  := $(TARGET_OUT_VENDOR)/etc/
LOCAL_SRC_FILES := izat.conf

include $(BUILD_PREBUILT)

include $(CLEAR_VARS)

LOCAL_MODULE := lowi.conf
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH  := $(TARGET_OUT_VENDOR)/etc/
LOCAL_SRC_FILES := lowi.conf

include $(BUILD_PREBUILT)

include $(CLEAR_VARS)

LOCAL_MODULE := sap.conf
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH  := $(TARGET_OUT_VENDOR)/etc/
LOCAL_SRC_FILES := sap.conf

include $(BUILD_PREBUILT)

include $(CLEAR_VARS)

LOCAL_MODULE := xtwifi.conf
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_MODULE_PATH  := $(TARGET_OUT_VENDOR)/etc/
LOCAL_SRC_FILES := xtwifi.conf

include $(BUILD_PREBUILT)
