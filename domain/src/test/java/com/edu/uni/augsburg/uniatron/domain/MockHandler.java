package com.edu.uni.augsburg.uniatron.domain;

import android.os.Handler;
import android.os.Message;

public class MockHandler extends Handler {

    @Override
    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        dispatchMessage(msg);
        return true;
    }
}
