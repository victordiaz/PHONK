package io.phonk.runner.apprunner.api.widgets.midi;

import android.os.Build;

import androidx.annotation.RequiresApi;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.api.media.midi.PMidiController;
import io.phonk.runner.apprunner.api.widgets.PSlider;

@RequiresApi(api = Build.VERSION_CODES.M)
@PhonkClass(description = "When moved, this slider controls things with CC messages on channel 0 (default)")
public class CcSlider extends AbstractMidiWidget {

    private final PSlider pSlider;
    private final PMidiController pMidiController;
    private int channel = 0;
    private int cc;

    public CcSlider(final PMidiController pMidiController,
                    final Object x,
                    final Object y,
                    final Object w,
                    final Object h,
                    final int cc) {
        super(pMidiController.getAppRunner());
        this.pMidiController = pMidiController;
        this.cc = cc;
        pSlider = getAppRunner().pUi
                .addSlider(x, y, w, h)
                .range(PMidiController.MIN_CC_VALUE, PMidiController.MAX_CC_VALUE)
                .verticalMode(true)
                .mode("drag")
                .onChange(this::midiAction);
    }

    @PhonkMethod(description = "sets the channel of the CC messages", example = "channel [0-15]")
    public CcSlider withChannel(final int channel) {
        this.channel = channel;
        return this;
    }

    @PhonkMethod(description = "sets the text of the slider")
    public CcSlider withText(final String text) {
        pSlider.text(text);
        return this;
    }

    @PhonkMethod(description = "sets the CC to control", example = "74 for filter, 71 for timbre...")
    public CcSlider withCc(final int cc) {
        this.cc = cc;
        return this;
    }

    @PhonkMethod(description = "access to the original PSlider object to further customize it if needed")
    public PSlider slider() {
        return pSlider;
    }

    private void midiAction(final ReturnObject r) {
        pMidiController.midiCommand(PMidiController.STATUS_CC + channel, cc, numberToInt(r.get("value")));
    }

    @Override
    public void __stop() {

    }
}
