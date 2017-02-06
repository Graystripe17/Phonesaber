package cinnamint.com.phonesaber;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;



public class MainActivity extends Activity {
    // TODO: Add in control panel
    // TODO: On/off switch

    public Context context;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static int SFX_option;
    public static boolean LOW_MEMORY_MODE;
    public static boolean RUMBLE;
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
    Switch rumble;
    Switch running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        toggle = (ToggleButton) findViewById(R.id.LowMemoryToggle);
        rumble = (Switch) findViewById(R.id.RumbleSwitch);
        running = (Switch) findViewById(R.id.RunningSwitch);
        radioGroup = (RadioGroup) findViewById(R.id.myOptions);
        radioGroup.setEnabled(false);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        if (prefs != null) {
            int resourceNumber = prefs.getInt("resourceNumber", 0);
            SFX_option = resourceNumber;

            ((RadioButton) radioGroup.getChildAt(resourceNumber)).setChecked(true);

            LOW_MEMORY_MODE = prefs.getBoolean("LOW_MEMORY_MODE", false);

            toggle.setChecked(LOW_MEMORY_MODE);

            if (LOW_MEMORY_MODE) {
                stopHighMemoryMode();
                startLowMemoryMode();
            } else {
                stopLowMemoryMode();
                startHighMemoryMode();
            }

            RUMBLE = prefs.getBoolean("RUMBLE", false);
            if (Build.VERSION.SDK_INT >= 14) {
                rumble.setChecked(RUMBLE);
            }
        }


        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean("LOW_MEMORY_MODE", isChecked);
                editor.commit();

                if (isChecked) {
                    stopHighMemoryMode();
                    startLowMemoryMode();
                } else {
                    startHighMemoryMode();
                    // Be wary of order
                    stopLowMemoryMode();
                }
            }
        });

        rumble.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                RUMBLE = b;
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean("RUMBLE", RUMBLE);
                editor.commit();
            }
        });

        running.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    turnOn();
                } else {
                    turnOff();
                }
            }
        });


        /**
         * User Present BroadcastReceiver
         * Only works occasionally
         */
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
                                mediaPlayerM.release();
                                break;
                            case 4:
                                mediaPlayerM = MediaPlayer.create(context, R.raw.hp_spell_cast_light);
                                mediaPlayerM.start();
                                mediaPlayerM.release();
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

        turnOn();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void startHighMemoryMode() {
        Intent notificationIntent = new Intent(this, UpdateService.class);
        startService(notificationIntent);
    }

    public void stopHighMemoryMode() {
        turnOff();
    }

    public void startLowMemoryMode() {
        // USING Receiver.java
        // Receiver continues
        IntentFilter filterDesiredActions = new IntentFilter();
        filterDesiredActions.addAction(Intent.ACTION_SCREEN_ON);
        filterDesiredActions.addAction(Intent.ACTION_SCREEN_OFF);
        screenBR = new Receiver();
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
        alarmService.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 10*60*1000, AlarmManager.INTERVAL_HOUR * 9, restartServicePI);
        //alarmService.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, 24*60*60*1000, restartServicePI);
    }

    public void stopLowMemoryMode() {
        try {
            unregisterReceiver(screenBR);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void turnOff() {
        stopService(new Intent(new Intent(this, UpdateService.class)));

        rumble.setEnabled(false);

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(false);
        }
    }

    public void turnOn() {
        startHighMemoryMode();

        // After the app is initiated for the first time, plant an Alarm
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_HOUR * 9, AlarmManager.INTERVAL_HOUR * 9, alarmIntent);

        rumble.setEnabled(true);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(true);
        }

    }

    public void setSFXoption(int option) {
        Log.d(UpdateService.TAG, "setSFX to " + option);
        SFX_option = option;

        // update SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt("resourceNumber", option);
        editor.commit();
    }

}
