package io.phonk.runner.apprunner.api.media.midi;


import android.media.midi.MidiDeviceInfo;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MidiInput {
    /**
     * Unique id of a device input. If multiple devices are available, this id will always be unique
     */
    private final String deviceInputId;

    /**
     * A human readable name for the midi input
     */
    private final String name;

    /**
     * Device id
     */
    private final int deviceId;

    /**
     * The port number for a given midi device
     */
    private final int portNumber;

    public MidiInput(final MidiDeviceInfo midiDeviceInfo, final int portNumber) {
        this.deviceInputId = midiDeviceInfo.getId() + "-" + portNumber;
        this.name = computeMidiInputName(midiDeviceInfo, portNumber);
        this.deviceId = midiDeviceInfo.getId();
        this.portNumber = portNumber;
    }

    private String computeMidiInputName(final MidiDeviceInfo midiDeviceInfo, final int portNumber) {
        final MidiDeviceInfo.PortInfo portInfo = midiDeviceInfo.getPorts()[portNumber];
        final String portName = portInfo != null ? portInfo.getName() : null;
        return "#" + deviceId
                + ", " + getDescription(midiDeviceInfo)
                + "[" + portNumber + "]"
                + ", " + portName;
    }

    private String getDescription(final MidiDeviceInfo midiDeviceInfo) {
        final String description = midiDeviceInfo.getProperties()
                .getString(MidiDeviceInfo.PROPERTY_NAME);
        if (description == null) {
            return midiDeviceInfo.getProperties()
                    .getString(MidiDeviceInfo.PROPERTY_MANUFACTURER) + ", "
                    + midiDeviceInfo.getProperties()
                    .getString(MidiDeviceInfo.PROPERTY_PRODUCT);
        }
        return description;
    }

    public String getDeviceInputId() {
        return deviceInputId;
    }

    public String getName() {
        return name;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public int getPortNumber() {
        return portNumber;
    }
}
