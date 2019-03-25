LOCAL_PATH := $(call my-dir)

ifndef BOARD_VNDK_VERSION

VNDK_SP_LIBRARIES := \
    libdexfile \
    libpng \
    libft2

VNDK_SP_LIBRARIES += \
    android.hardware.graphics.allocator@2.0

ifdef PLATFORM_VNDK_VERSION
vndk_sp_dir := vndk-sp-$(PLATFORM_VNDK_VERSION)
else
vndk_sp_dir := vndk-sp
endif

define define-vndk-sp-lib
include $$(CLEAR_VARS)
LOCAL_MODULE := $1.vndk-sp-gen
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_PREBUILT_MODULE_FILE := $$(call intermediates-dir-for,SHARED_LIBRARIES,$1,,,,)/$1.so
LOCAL_STRIP_MODULE := false
LOCAL_MULTILIB := first
LOCAL_MODULE_TAGS := optional
LOCAL_INSTALLED_MODULE_STEM := $1.so
LOCAL_MODULE_SUFFIX := .so
LOCAL_MODULE_RELATIVE_PATH := $(vndk_sp_dir)$(if $(filter $1,$(install_in_hw_dir)),/hw)
include $$(BUILD_PREBUILT)

ifneq ($$(TARGET_2ND_ARCH),)
ifneq ($$(TARGET_TRANSLATE_2ND_ARCH),true)
include $$(CLEAR_VARS)
LOCAL_MODULE := $1.vndk-sp-gen
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_PREBUILT_MODULE_FILE := $$(call intermediates-dir-for,SHARED_LIBRARIES,$1,,,$$(TARGET_2ND_ARCH_VAR_PREFIX),)/$1.so
LOCAL_STRIP_MODULE := false
LOCAL_MULTILIB := 32
LOCAL_MODULE_TAGS := optional
LOCAL_INSTALLED_MODULE_STEM := $1.so
LOCAL_MODULE_SUFFIX := .so
LOCAL_MODULE_RELATIVE_PATH := $(vndk_sp_dir)$(if $(filter $1,$(install_in_hw_dir)),/hw)
include $$(BUILD_PREBUILT)
endif # TARGET_TRANSLATE_2ND_ARCH is not true
endif # TARGET_2ND_ARCH is not empty
endef

# Add VNDK-SP libs to the list if they are missing
$(foreach lib,$(VNDK_SAMEPROCESS_LIBRARIES),\
    $(if $(filter $(lib),$(VNDK_SP_LIBRARIES)),,\
    $(eval VNDK_SP_LIBRARIES += $(lib))))

# Remove libz from the VNDK-SP list (b/73296261)
VNDK_SP_LIBRARIES := $(filter-out libz,$(VNDK_SP_LIBRARIES))

$(foreach lib,$(VNDK_SP_LIBRARIES),\
    $(eval $(call define-vndk-sp-lib,$(lib))))

install_in_hw_dir :=

include $(CLEAR_VARS)
LOCAL_MODULE := vndk-sp
LOCAL_MODULE_OWNER := google
LOCAL_MODULE_TAGS := optional
LOCAL_REQUIRED_MODULES := $(addsuffix .vndk-sp-gen,$(VNDK_SP_LIBRARIES))
include $(BUILD_PHONY_PACKAGE)
endif
