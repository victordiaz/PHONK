/*
 * Part of Phonk http://www.phonk.io
 * A prototyping platform for Android devices
 *
 * Copyright (C) 2013 - 2017 Victor Diaz Barrales @victordiaz (Protocoder)
 * Copyright (C) 2017 - Victor Diaz Barrales @victordiaz (Phonk)
 *
 * Phonk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Phonk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Phonk. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.phonk.runner.apprunner.interpreter;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableScript;
import org.mozilla.javascript.debug.Debugger;


// http://stackoverflow.com/questions/10246030/stopping-the-rhino-engine-in-middle-of-execution
public class ObservingDebugger implements Debugger {
    boolean isDisconnected = false;

    private DebugFrame debugFrame = null;

    public ObservingDebugger() {

    }

    public boolean isDisconnected() {
        return isDisconnected;
    }

    public void setDisconnected(boolean isDisconnected) {
        this.isDisconnected = isDisconnected;
        if (debugFrame != null) {
            ((ObservingDebugFrame) debugFrame).setDisconnected(isDisconnected);
        }
    }

    @Override
    public void handleCompilationDone(Context arg0, DebuggableScript arg1, String arg2) {
    }

    public DebugFrame getFrame(Context cx, DebuggableScript fnOrScript) {
        if (debugFrame == null) {
            debugFrame = new ObservingDebugFrame(isDisconnected);
        }
        return debugFrame;
    }
}

// internal ObservingDebugFrame class
class ObservingDebugFrame implements DebugFrame {
    boolean isDisconnected = false;

    ObservingDebugFrame(boolean isDisconnected) {
        this.isDisconnected = isDisconnected;
    }

    public boolean isDisconnected() {
        return isDisconnected;
    }

    public void setDisconnected(boolean isDisconnected) {
        this.isDisconnected = isDisconnected;
    }

    public void onEnter(
            Context cx, Scriptable activation, Scriptable thisObj, Object[] args
    ) {
    }

    public void onLineChange(Context cx, int lineNumber) {
        if (isDisconnected) {
            throw new RuntimeException("The project just stopped executing due to some errors :/");
        }
    }

    public void onExceptionThrown(Context cx, Throwable ex) {
    }

    public void onExit(
            Context cx, boolean byThrow, Object resultOrException
    ) {
    }

    @Override
    public void onDebuggerStatement(Context arg0) {
    }
}
