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


import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;
import org.mozilla.javascript.debug.Debugger;

import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.permissions.PermissionNotGrantedException;
import io.phonk.runner.base.utils.MLog;

public class AppRunnerInterpreter {

    private static final String TAG = AppRunnerInterpreter.class.getSimpleName();

    //result after line execution
    public static final int RESULT_OK = 1;
    public static final int RESULT_ERROR = 2;
    public static final int RESULT_PERMISSION_ERROR = 3;
    public static final int RESULT_NOT_CAPABLE = 4;

    //rhino stuff
    private static ScriptContextFactory mScriptContextFactory;
    AppRunner mAppRunner;
    private Context rhino = null;
    private Scriptable scope;
    private InterpreterInfo mInterpreterListener;
    // public ObservingDebugger observingDebugger;

    public AppRunnerInterpreter(AppRunner appRunner) {
        this.mAppRunner = appRunner;
        init();
    }

    //init Rhino context
    public void init() {
        //this can be initiated only once
        if (mScriptContextFactory == null) {
            mScriptContextFactory = new ScriptContextFactory();
            ContextFactory.initGlobal(mScriptContextFactory);
        }
        mScriptContextFactory.setInterpreter(this);

        rhino = Context.enter();
        // observingDebugger = new ObservingDebugger();
        // rhino.setDebugger(observingDebugger, new Integer(0));
        // rhino.setGeneratingDebug(true);

        // give some android love
        rhino.setOptimizationLevel(-1);

        scope = rhino.initStandardObjects();

        //let rhino do some java <-> js transformations for us
        rhino.getWrapFactory().setJavaPrimitiveWrap(false);
    }

    // we will use this method for normal script execution
    public void eval(String jscode, String origin) {
        try {
            Object result = rhino.evaluateString(scope, jscode, origin, 1, null);
            MLog.d("result", "" + result);
            processResult(RESULT_OK, "");
        } catch (org.mozilla.javascript.EvaluatorException e) {
            processResult(RESULT_ERROR, e.getMessage());
        } catch (org.mozilla.javascript.EcmaError e) {
            processResult(RESULT_ERROR, e.getMessage());
        } catch (Exception e) {
            processResult(RESULT_ERROR, e.getMessage());
        } finally {
            // processResult(RESULT_ERROR, "kabum!");
            // stop();
        }
    }

    // we will use this method for live coding
    public void eval(String jscode) {
        eval(jscode, "liveCoding");
    }

    public void addJavaObjectToJs(String name, Object obj) {
        ScriptableObject.putProperty(scope, name, Context.javaToJS(obj, scope));
    }

    public Object getJsFunction(String name) {
        Object function = scope.get(name, scope);
        return function;
    }

    public void callJsFunction(String name, Object... params) {
        Object obj = getJsFunction(name);
        if (obj instanceof Function) {
            Function function = (Function) obj;
            // NativeObject result = (NativeObject)
            function.call(rhino, scope, scope, params);
            processResult(RESULT_OK, "");
        }
    }

    public Object getObject(String name) {
        Object obj = scope.get(name, scope);
        if (obj == Scriptable.NOT_FOUND) {
            return null;
        }
        return obj;
    }

    public void processResult(int resultType, String message) {
        MLog.d(TAG, "processResult: " + resultType + " " + message);
        String resultClean = "";
        switch (resultType) {
            case RESULT_OK:
                break;
            //basically we throw here the exception errors
            case RESULT_ERROR:
                if (mInterpreterListener != null) mInterpreterListener.onError(resultType, message);
                break;
            case RESULT_PERMISSION_ERROR:
                if (mInterpreterListener != null) mInterpreterListener.onError(resultType, message);
                break;
        }
    }

    /*
     * Native arrays
     */
    public Scriptable newNativeArray() {
        return rhino.newArray(scope, 0);
    }

    public Scriptable newNativeArrayFrom(Object[] obj) {
        return rhino.newArray(scope, obj);
    }

    /*
     *   Errors and misc
     */
    public interface InterpreterInfo {
        void onError(int resultType, Object message);
    }

    public void addListener(InterpreterInfo listener) {
        this.mInterpreterListener = listener;
    }

    // TODO not ready yet
    public void addDebugger(Debugger debugger) {
        rhino.setDebugger(debugger, scope);
    }

    public void stop() {
        Context.exit();
    }


    public class ScriptContextFactory extends ContextFactory {
        private AppRunnerInterpreter mAppRunnerInterpretter;

        ScriptContextFactory() {
            super();
        }

        @Override
        protected boolean hasFeature(Context cx, int featureIndex) {

            switch (featureIndex) {
                case Context.FEATURE_STRICT_EVAL:
                    return true;

                case Context.FEATURE_STRICT_VARS:
                    return true;
            }

            return super.hasFeature(cx, featureIndex);
        }

        public void setInterpreter(AppRunnerInterpreter appRunnerInterpreter) {
            mAppRunnerInterpretter = appRunnerInterpreter;
        }

        @Override
        protected Object doTopCall(Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            try {
                return super.doTopCall(callable, cx, scope, thisObj, args);
            } catch (WrappedException e) {
                MLog.e(TAG, "ContextFactory catched error: " + e);
                // mAppRunnerInterpretter.stop();

                if (e.getWrappedException() instanceof PermissionNotGrantedException) {
                    String message = ((Throwable) e).getMessage();
                    message = message.replace("io.phonk.runner.apprunner.api.PermissionNotGrantedException:", "");

                    mAppRunnerInterpretter.processResult(RESULT_PERMISSION_ERROR, message);
                } else {
                    String message = ((Throwable) e).getMessage();
                    message = message.replace("io.phonk.runner.apprunner.api.P", "");

                    mAppRunnerInterpretter.processResult(RESULT_ERROR, message);
                }
                return e;
            } catch (org.mozilla.javascript.EcmaError e) {
                String message = ((Throwable) e).getMessage();
                message = message.replace("io.phonk.runner.apprunner.api.P", "");
                mAppRunnerInterpretter.processResult(RESULT_ERROR, message);
                // mAppRunnerInterpretter.stop();
                return e;
            } catch (org.mozilla.javascript.EvaluatorException e) {
                mAppRunnerInterpretter.processResult(RESULT_ERROR, e.getMessage());

                return e;
            } finally {
                // observingDebugger.setDisconnected(true);
                // mAppRunnerInterpretter.stop();
                // observingDebugger.setDisconnected(true);
                MLog.d("stop", "bye bye");
                // mAppRunner.byebye();
            }
        }
    }


}
