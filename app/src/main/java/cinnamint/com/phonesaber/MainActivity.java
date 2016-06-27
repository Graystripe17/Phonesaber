package cinnamint.com.phonesaber;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;


public class MainActivity extends Activity {
    // TODO: Add in control panel
    // TODO: On/off switch
    // TODO: Fix memory leaks
    // TODO: Fix battery life

    public Context context;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static int SFX_option;
    public static boolean LOW_MEMORY_MODE;
    public BroadcastReceiver screenBR;
    private AlarmManager alarmMgr;

    /*
    0 Star Wars
    1 Doctor Who
    2 Pokemon
    3 Indiana Jones
    4 Harry Potter
    5 Zelda
    6 Iron Man
    */

    // UI Variables
    RadioGroup radioGroup;
    ToggleButton toggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        radioGroup = (RadioGroup) findViewById(R.id.myOptions);
        toggle = (ToggleButton) findViewById(R.id.LowMemoryToggle);


        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        // Grab the most recent SFX_option
        if (prefs != null) {
            // If no resourceNumber exists, default phonesaber
            int resourceNumber = prefs.getInt("resourceNumber", 0);
            SFX_option = resourceNumber;

            // On start, set the default phoneSaber settings to 0
            ((RadioButton) radioGroup.getChildAt(resourceNumber)).setChecked(true);


            // Grab the most recent toggle option
            // By default, LOW_MEMORY_MODE is off
            LOW_MEMORY_MODE = prefs.getBoolean("LOW_MEMORY_MODE", false);

            toggle.setChecked(LOW_MEMORY_MODE);

            if(LOW_MEMORY_MODE) {
                stopHighMemoryMode();
                startLowMemoryMode();
            } else {
                stopLowMemoryMode();
                startHighMemoryMode();
            }
        }

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("LOW_MEMORY_MODE", true);
                    editor.commit();
                    stopHighMemoryMode();
                    startLowMemoryMode();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("LOW_MEMORY_MODE", false);
                    editor.commit();
                    startHighMemoryMode();
                    // Be wary of order
                    stopLowMemoryMode();
                }
            }
        });



/**
 * User Present
 */
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
                                break;
                            case 4:
                                mediaPlayerM = MediaPlayer.create(context, R.raw.hp_spell_cast_light);
                                mediaPlayerM.start();
                                break;
                        }
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        registerReceiver(UnlockReceiver, filter);

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
                    case R.id.indianaphone:
                        setSFXoption(3);
                        break;
                    case R.id.stupephone:
                        setSFXoption(4);
                        break;
                    case R.id.masterphone:
                        setSFXoption(5);
                        break;
                    case R.id.ironphone:
                        setSFXoption(6);
                        break;
                    default:
                        setSFXoption(0);
                        break;
                }
            }
        });


        // After the app is initiated for the first time, plant an Alarm
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_DAY, AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    public void setSFXoption(int option) {
        Log.d(UpdateService.TAG, "setSFX to " + option);
        SFX_option = option;

        // update SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt("resourceNumber", option);
        editor.commit();
    }

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

    public void startHighMemoryMode() {
        Intent notificationIntent = new Intent(this, UpdateService.class);
        startService(notificationIntent);
    }

    public void stopHighMemoryMode() {
        stopService(new Intent(this, UpdateService.class));
    }

    public void startLowMemoryMode() {
        // USING receiver.java
        // Receiver continues
        IntentFilter filterDesiredActions = new IntentFilter();
        filterDesiredActions.addAction(Intent.ACTION_SCREEN_ON);
        filterDesiredActions.addAction(Intent.ACTION_SCREEN_OFF);
        screenBR = new receiver();
        registerReceiver(screenBR, filterDesiredActions);

        // Alarm restarts service
        Intent restartService = new Intent(context, UpdateService.class);
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(context, 1, restartService, PendingIntent.FLAG_ONE_SHOT);
        Log.d("WINSTON", "Restart Service Initiated");

        // Restart OFTEN
        AlarmManager alarmService = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Elapsed realtime wakeup counts from boot tome
        // RTC wakeup uses System.currentTimeMillis()
        alarmService.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 10*60*1000, AlarmManager.INTERVAL_HOUR * 3, restartServicePI);
        //alarmService.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, 24*60*60*1000, restartServicePI);
    }

    public void stopLowMemoryMode() {
        try {
            unregisterReceiver(screenBR);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
