package cinnamint.com.phonesaber;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Gaga on 1/2/2016.
 */
public class UpdateService extends Service {

    public Context context;
    public MediaPlayer mediaPlayerA;
    public MediaPlayer mediaPlayerD;
    int mNotificationId = 001;
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyMgr;
    Notification note8;
    Notification note11;
    private int count = 0;

    public static final String TAG = "WINSTON";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new receiver(), filter);

        // start grabbing
        Log.d(TAG, "UpdateServiceCreated!");


        /**
         * HIGH POWER
         * */
        if (Build.VERSION.SDK_INT >= 16) {

//            Intent notificationIntent = new Intent(this, UpdateService.class);
//            PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
//            mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            note11 = new Notification.Builder(context)
//                    .setContentTitle("Phonesaber")
//                    .setContentText("Phonesaber initiated!")
//                    .setSmallIcon(R.drawable.center_ps)
//                    .setContentIntent(pendingIntent).build();
            // Android developer guides
            Notification notification = new Notification(R.drawable.center_ps, "Phonesaber", System.currentTimeMillis());
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            notification.setLatestEventInfo(this, "Phonesaber", "Phonesaber Initiated!", pendingIntent);
            notification.flags |= Notification.FLAG_NO_CLEAR;
            startForeground(mNotificationId, notification);
            // Android developer guides

            //startForeground(mNotificationId, note11);
        } else if (Build.VERSION.SDK_INT >= 11
                && Build.VERSION.SDK_INT <= 15) {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
            mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            note11 = new Notification.Builder(context)
                    .setContentTitle("Phonesaber")
                    .setContentText("Phonesaber initiated!")
                    .setSmallIcon(R.drawable.center_ps)
                    .setContentIntent(pendingIntent).getNotification();
            mNotifyMgr.notify(mNotificationId, note11);
        } else {
            note8 = new Notification(R.drawable.center_ps, getString(R.string.noticeMe),
                            System.currentTimeMillis());

            PendingIntent i = PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class),
                    0);
            note8.setLatestEventInfo(getApplicationContext(), getString(R.string.title), getString(R.string.message), i);
            note8.number = ++count;
            note8.flags |= Notification.FLAG_AUTO_CANCEL;
            note8.flags |= Notification.DEFAULT_SOUND;
            note8.flags |= Notification.DEFAULT_VIBRATE;
            note8.ledARGB = 0xff0000ff;
            note8.flags |= Notification.FLAG_SHOW_LIGHTS;

            // Now invoke the Notification Service
            String notifService = Context.NOTIFICATION_SERVICE;
            NotificationManager mgr =
                    (NotificationManager) getSystemService(notifService);
            mgr.notify(mNotificationId, note8);
        }
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
                    // If mediaPlayerA is already playing, abort
                    if(mediaPlayerA != null) {
                        if (mediaPlayerA.isPlaying()) {
                            Log.d(TAG, "IsPlaying");
                            return START_STICKY;
                        }
                    }
                    switch (resourceNumber) {
                        case 0:
                            mediaPlayerA = MediaPlayer.create(context, R.raw.activateloud);
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
                        case 3:
                            mediaPlayerA = MediaPlayer.create(context, R.raw.indiana_jones_open_whip);
                            mediaPlayerA.start();
                            break;
                        case 4:
                            mediaPlayerA = MediaPlayer.create(context, R.raw.hp_spell_cast_stupephone);
                            mediaPlayerA.start();
                            break;
                        case 5:
                            mediaPlayerA = MediaPlayer.create(context, R.raw.sword_unsheath);
                            mediaPlayerA.start();
                            break;
                        case 6:
                            mediaPlayerA = MediaPlayer.create(context, R.raw.iron_man_suit_up);
                            mediaPlayerA.start();
                            break;
                    }

                }
            } else {
                // DEACTIVATE
                // Check if already playing
                if(mediaPlayerD != null) {
                    if (mediaPlayerD.isPlaying()) {
                        Log.d(TAG, "D sticky");
                        return START_STICKY;
                    }
                }
                switch(resourceNumber) {
                    case 0:
                        mediaPlayerD = MediaPlayer.create(context, R.raw.deactivateloud);
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
                    case 3:
                        mediaPlayerD = MediaPlayer.create(context, R.raw.indiana_jones_withdraw_whip);
                        mediaPlayerD.start();
                        break;
                    case 4:
                        mediaPlayerD = MediaPlayer.create(context, R.raw.hp_spell_down);
                        mediaPlayerD.start();
                        break;
                    case 5:
                        mediaPlayerD = MediaPlayer.create(context, R.raw.sword_sheath);
                        mediaPlayerD.start();
                        break;
                    case 6:
                        mediaPlayerD = MediaPlayer.create(context, R.raw.iron_man_power_down);
                        mediaPlayerD.start();
                        break;
                }
            }
        } else {
            // Null intent
            Toast.makeText(context, "Null Intent", Toast.LENGTH_LONG).show();
        }



        // Tells OS to recreate the service AND redeliver the same intent
        // START_STICKY: Restarts when available memory with null intent
        // START_REDELIVER_INTENT: Restarts when available memory continuing intent
//        Toast.makeText(context, "RADIONUMBER" + MainActivity.SFX_option, Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
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
//        Toast.makeText(context, "onTaskRemoved", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onTaskRemoved");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Intent immortal = new Intent(context, receiver.class);
        sendBroadcast(immortal);
        if(mediaPlayerA != null) mediaPlayerA.release();
        mediaPlayerA = null;
        if(mediaPlayerD != null) mediaPlayerD.release();
        mediaPlayerD = null;
        Log.d(TAG, "onDestroy");
        Toast.makeText(context, "onDESTROY called DEBUG", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}
