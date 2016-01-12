package cinnamint.com.phonesaber;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Gaga on 1/2/2016.
 */
public class UpdateService extends Service {

    public Context context;
    public MediaPlayer mediaPlayerA;
    public MediaPlayer mediaPlayerD;

    public static final String TAG = "WINSTON";

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new receiver(), filter);

        // start grabbing
        Log.d(TAG, "UpdateServiceCreated!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();

        SharedPreferences prefs = getSharedPreferences(MainActivity.MY_PREFS_NAME, MODE_PRIVATE);
        int resourceNumber = prefs.getInt("resourceNumber", 0);
        Log.d(TAG, "oSC SharedPrefs Res#" + resourceNumber);


        if(intent != null) {
            boolean screenOn = intent.getBooleanExtra("screen_state", false);
            // Might give significant overhead
            // 21.9 MB RAM
            if (!screenOn) {
                KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                if(myKM.inKeyguardRestrictedInputMode()) {
                    // It is locked
                    // This check ensures it will not play at night

                    // ACTIVATE
                    switch (resourceNumber) {
                        case 0:
                            mediaPlayerA = MediaPlayer.create(context, R.raw.activate);
                            mediaPlayerA.start();
                            break;
                        case 1:
                            mediaPlayerA = MediaPlayer.create(context, R.raw.ss_open);
                            mediaPlayerA.start();
                            break;
                        case 2:
                            mediaPlayerA = MediaPlayer.create(context, R.raw.pokeball_out);
                            mediaPlayerA.start();
                            break;
                    }

                }
            } else {

                // DEACTIVATE
                switch(resourceNumber) {
                    case 0:
                        mediaPlayerD = MediaPlayer.create(context, R.raw.deactivate);
                        mediaPlayerD.start();
                        break;
                    case 1:
                        mediaPlayerD = MediaPlayer.create(context, R.raw.ss_close);
                        mediaPlayerD.start();
                        break;
                    case 2:
                        mediaPlayerD = MediaPlayer.create(context, R.raw.pokeball_in);
                        mediaPlayerD.start();
                        break;
                }


            }
        }

        // Tells OS to recreate the service AND redeliver the same intent
        // START_STICKY: Restarts when available memory with null intent
        // START_REDELIVER_INTENT: Restarts when available memory continuing intent
        Toast.makeText(context, "RADIONUMBER" + MainActivity.SFX_option, Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStartCommand");
        return START_REDELIVER_INTENT;
        // return super.onStartCommand(intent, flags, startId);
    }

    // Like seriously learn what this is
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        context = getApplicationContext();
        Intent restartService = new Intent(context, this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(context, 1, restartService, PendingIntent.FLAG_ONE_SHOT);

        // Restart once killed by android or user
        AlarmManager alarmService = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);
        // ^Try setRepeating
        Toast.makeText(context, "onTaskRemoved", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onTaskRemoved");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mediaPlayerA.release();
        mediaPlayerA = null;
        mediaPlayerD.release();
        mediaPlayerD = null;
        Toast.makeText(this, "Phonesaber Off", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
