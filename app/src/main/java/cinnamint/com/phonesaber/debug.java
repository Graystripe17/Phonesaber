package cinnamint.com.phonesaber;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Gaga on 1/10/2016.
 */
public class debug extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new receiver(), filter);

        // start grabbing
        Log.d("WINSTON", "DEBUG SERVICE CREATED");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Toast.makeText(getApplicationContext(), "RADIONUMBER" + MainActivity.SFX_option, Toast.LENGTH_LONG).show();
        Log.d("WINSTON", "oSC DEBUGGGGG");
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
