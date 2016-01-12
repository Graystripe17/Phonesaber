package cinnamint.com.phonesaber;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    public Context context;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static int SFX_option;
    private int count = 0;
    /*
    0 Star Wars
    1 Doctor Who
    2 Pokemon
    3 Indiana Jones
    4 Harry Potter
    5 Zelda
    6 Thor
    */
    RadioGroup radioGroup;
    int mNotificationId = 001;
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyMgr;
    Notification note8;
    Notification note11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        radioGroup = (RadioGroup) findViewById(R.id.myOptions);


        // Grab the most recent SFX_option
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        if (prefs != null) {
            // If no resourceNumber exists, default phonesaber
            int resourceNumber = prefs.getInt("resourceNumber", 0);
            SFX_option = resourceNumber;

            // On start, set the default phoneSaber settings to 0
            ((RadioButton) radioGroup.getChildAt(resourceNumber)).setChecked(true);
        }


        // USING receiver.java
        // Receiver continues
        IntentFilter filterDesiredActions = new IntentFilter();
        filterDesiredActions.addAction(Intent.ACTION_SCREEN_ON);
        filterDesiredActions.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new receiver(), filterDesiredActions);


        // HANDLES ONLY USER_PRESENT
        // Receiver killed onTaskRemoved
        BroadcastReceiver UnlockReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) return;
                String actionName = intent.getAction();
                if (actionName != null) {
                    if (actionName.equals("android.intent.action.USER_PRESENT")) {
                        MediaPlayer mediaPlayerM;
                        switch (SFX_option) {
                            case 0:
                                mediaPlayerM = MediaPlayer.create(context, R.raw.swing_slow);
                                mediaPlayerM.start();
                                Toast.makeText(context, "Center Play", Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                        }
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        registerReceiver(UnlockReceiver, filter);

/**
 * LOW MEMORY MODE
// */
//        // Alarm restarts service
//        Intent restartService = new Intent(context, UpdateService.class);
//        restartService.setPackage(getPackageName());
//        PendingIntent restartServicePI = PendingIntent.getService(context, 1, restartService, PendingIntent.FLAG_ONE_SHOT);
//
//        // Restart OFTEN
//        AlarmManager alarmService = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmService.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 100, restartServicePI);
//        alarmService.cancel(restartServicePI);

/**
 * HIGH MEMORY MODE
 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // FOREGROUND Notification
            mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            note11 = new Notification.Builder(context)
                    .setContentTitle("YEE HAW")
                    .setContentText("MICHAEL JACKSON - SHAMONE")
                    .setSmallIcon(R.drawable.notification_template_icon_bg).build();
            mNotifyMgr.notify(mNotificationId, note11);
        }
        else {
            note8 =
                    new Notification(R.drawable.pepe, getString(R.string.noticeMe),
                            System.currentTimeMillis());

            PendingIntent i=PendingIntent.getActivity(this, 0,
                    new Intent(this, UpdateService.class),
                    0);
            note8.setLatestEventInfo(getApplicationContext(), getString(R.string.title), getString(R.string.message), i);
            note8.number=++count;
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


        // DEBUG PURPOSES
        /*
        Intent debug = new Intent(context, debug.class);
        debug.setPackage(getPackageName());
        PendingIntent debugPI = PendingIntent.getService(context, 1, debug, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager debugAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        debugAlarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 100, 1000, debugPI);
        */
        // DEBUG PURPOSES







        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.phonesaber:
                        setSFXoption(0);
                        break;
                    case R.id.phonedriver:
                        setSFXoption(1);
                        break;
                    case R.id.pokephone:
                        setSFXoption(2);
                        break;
                    default:
                        setSFXoption(6);
                        break;
                }
            }
        });

    }

    public void setSFXoption(int option) {
        Log.d(UpdateService.TAG, "setSFX to " + option);
        SFX_option = option;

        // update SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt("resourceNumber", option);
        editor.commit();
    }


//    @Override
//    public void onPause() {
//    }
//
//    @Override
//    public void onResume() {
//
//    }
//
//    @Override
//    public void onDestroy() {
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
