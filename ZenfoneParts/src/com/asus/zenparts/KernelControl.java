/*
 * Copyright (C) 2014 SlimRoms Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.asus.zenparts;

import com.asus.zenparts.utils.FileUtils;

import java.io.File;

/*
 * Very ugly class which enables or disables for now
 * all gesture controls on kernel level.
 * We need to do it this way for now to do not break 3rd party kernel.
 * Kernel should have a better per gesture control but as long
 * this is not changed by the manufacture we would break gesture control on every
 * 3rd party kernel. Hence we do it this way for now.
 */

public final class KernelControl {

    private static final String[] GesturePaths = new String[] {
        "/proc/touchpanel/up_swipe_enable",
        "/proc/touchpanel/letter_c_enable",
        "/proc/touchpanel/letter_v_enable",
        "/proc/touchpanel/letter_s_enable",
        "/proc/touchpanel/letter_z_enable",
        "/proc/touchpanel/letter_w_enable",
        "/proc/touchpanel/letter_e_enable"
    };
    
    public static final String SLIDER_SWAP_NODE = "/proc/s1302/key_rep";

    private KernelControl() {
        // this class is not supposed to be instantiated
    }

    /**
     * Enable or disable gesture control.
     */
    
    public static void enableGesture(int id, boolean state) {
        if (new File(GesturePaths[id]).exists()) {
            FileUtils.writeLine(GesturePaths[id], state ? "1" : "0");
        }
    }

    /**
     * Do we have touch control at all?
     */

    public static boolean hasSlider() {
        return new File(SLIDER_SWAP_NODE).exists();
    }

}
