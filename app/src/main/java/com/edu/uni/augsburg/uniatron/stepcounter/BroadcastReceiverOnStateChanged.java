package com.edu.uni.augsburg.uniatron.stepcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * The BroadCastReceiver starts the StepCountService as soon as boot is completed.
 *
 * @author Leon Wöhrl
 */
public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)  ) {

            // fixes crash on post Android O devices; services cannot be started in background!
            // IllegalStateException: Not allowed to start service Intent { StepCountService }: app is in background
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, StepCountService.class));
            } else {
                context.startService(new Intent(context, StepCountService.class));
            }
        }
    }
}
