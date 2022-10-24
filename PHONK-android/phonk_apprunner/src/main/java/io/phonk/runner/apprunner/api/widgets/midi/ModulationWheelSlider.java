package io.phonk.runner.apprunner.api.widgets.midi;

import android.os.Build;

import androidx.annotation.RequiresApi;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apprunner.api.media.midi.PMidiController;

@RequiresApi(api = Build.VERSION_CODES.M)
@PhonkClass(description = "When moved, this slider controls modulation wheel on channel 0 (default)")
public class ModulationWheelSlider extends CcSlider {

    public static final int MODULATION_WHEEL_CC = 1;
    public static final String MODULATION_WHEEL_SLIDER_TEXT = "mod";

    public ModulationWheelSlider(final PMidiController pMidiController,
                                 final Object x,
                                 final Object y,
                                 final Object w,
                                 final Object h) {
        super(pMidiController, x, y, w, h, MODULATION_WHEEL_CC);
        slider().text(MODULATION_WHEEL_SLIDER_TEXT);
    }
}
