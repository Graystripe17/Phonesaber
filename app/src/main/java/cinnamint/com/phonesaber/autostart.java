package cinnamint.com.phonesaber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class autostart extends BroadcastReceiver {

    public void onReceive(Context context, Intent arg1) {
        Log.d("WINSTON", "New BOOT");
        Intent intentStartService = new Intent(context, UpdateService.class);
        // No need for put extra, default screenOff = false
        context.startService(intentStartService);
    }
}
