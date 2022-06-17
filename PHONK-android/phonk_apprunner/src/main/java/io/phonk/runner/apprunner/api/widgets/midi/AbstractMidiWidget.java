package io.phonk.runner.apprunner.api.widgets.midi;

import android.os.Build;

import androidx.annotation.RequiresApi;

import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;

@RequiresApi(api = Build.VERSION_CODES.M)
public abstract class AbstractMidiWidget extends ProtoBase {
    public AbstractMidiWidget(final AppRunner appRunner) {
        super(appRunner);
    }

    protected int numberToInt(final Object number) {
        return numberToInt(number, 0);
    }

    protected int numberToInt(final Object number, final int defaultValue) {
        if (number == null) {
            return defaultValue;
        }
        return ((Number) number).intValue();
    }
}
