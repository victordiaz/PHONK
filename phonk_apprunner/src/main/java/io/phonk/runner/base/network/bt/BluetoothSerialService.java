

/*


    //write data
    public void write(byte[] out) {
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    //write data
    public void writeInt(int out) {
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
            return;
        }
        r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.writeInt(out);
    }



*/


