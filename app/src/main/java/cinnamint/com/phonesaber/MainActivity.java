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
import android.widget.Toast;
import android.widget.ToggleButton;


public class MainActivity extends Activity {

    public Context context;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static int SFX_option;
    public static boolean LowMemoryMode;
    public BroadcastReceiver screenBR;
    /*
    0 Star Wars
    1 Doctor Who
    2 Pokemon
    3 Indiana Jones
    4 Harry Potter
    5 Zelda
    6 Iron Man
    */
    RadioGroup radioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        radioGroup = (RadioGroup) findViewById(R.id.myOptions);


        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        // Grab the most recent SFX_option
        if (prefs != null) {
            // If no resourceNumber exists, default phonesaber
            int resourceNumber = prefs.getInt("resourceNumber", 0);
            SFX_option = resourceNumber;

            // On start, set the default phoneSaber settings to 0
            ((RadioButton) radioGroup.getChildAt(resourceNumber)).setChecked(true);
        }
        // Grab the most recent toggle option
        if (prefs != null) {
            // By default, LowMemoryMode is off
            LowMemoryMode = prefs.getBoolean("LowMemoryMode", false);

            ToggleButton LowMemoryModeButton = (ToggleButton)findViewById(R.id.LowMemoryToggle);
            LowMemoryModeButton.setChecked(LowMemoryMode);

            if(LowMemoryMode) {
                stopHighMemoryMode();
                startLowMemoryMode();
            } else {
                stopLowMemoryMode();
                startHighMemoryMode();
            }
        }

        ToggleButton toggle = (ToggleButton) findViewById(R.id.LowMemoryToggle);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("LowMemoryMode", true);
                    editor.commit();
                    stopHighMemoryMode();
                    startLowMemoryMode();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("LowMemoryMode", false);
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

    public void startHighMemoryMode() {
//        Toast.makeText(context, "startHighMemoryMode", Toast.LENGTH_LONG).show();
//
//        Intent notificationIntent = new Intent(this, UpdateService.class);
//        startService(notificationIntent);
    }

    public void stopHighMemoryMode() {
        //stopService(new Intent(this, UpdateService.class));
    }

    public void startLowMemoryMode() {
        Toast.makeText(context, "startLowMemoryMode", Toast.LENGTH_LONG).show();
//        // USING receiver.java
//        // Receiver continues
        IntentFilter filterDesiredActions = new IntentFilter();
        filterDesiredActions.addAction(Intent.ACTION_SCREEN_ON);
        filterDesiredActions.addAction(Intent.ACTION_SCREEN_OFF);
        screenBR = new receiver();
        registerReceiver(screenBR, filterDesiredActions);



        // Alarm restarts service
        Intent restartService = new Intent(context, UpdateService.class);
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(context, 1, restartService, PendingIntent.FLAG_ONE_SHOT);

        // Restart OFTEN
        AlarmManager alarmService = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 100, restartServicePI);
        alarmService.cancel(restartServicePI);
    }

    public void stopLowMemoryMode() {
        try {
            unregisterReceiver(screenBR);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
