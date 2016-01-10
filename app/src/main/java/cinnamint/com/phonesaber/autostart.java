package cinnamint.com.phonesaber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class autostart extends BroadcastReceiver {

    public void onReceive(Context context, Intent arg1) {
        Intent intentStartService = new Intent(context, UpdateService.class);
        // No need for put extra, default screenOff = false
        context.startService(intentStartService);
    }
}
