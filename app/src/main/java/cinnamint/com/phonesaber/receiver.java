package cinnamint.com.phonesaber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Gaga on 1/2/2016.
 */
public class receiver extends BroadcastReceiver {

    private boolean screenOff = false;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(UpdateService.TAG, "onReceive");
        Toast.makeText(context, "receiver", Toast.LENGTH_SHORT);


        String actionName = intent.getAction();
        if(actionName != null) {
            if(actionName.equals(Intent.ACTION_SCREEN_OFF)) {
                screenOff = true;
            } else if (actionName.equals(Intent.ACTION_SCREEN_ON)) {
                screenOff = false;
            }
            Intent i = new Intent(context, UpdateService.class);
            i.putExtra("screen_state", screenOff);
            context.startService(i);

//            if (actionName.equals("android.intent.action.ACTION_SCREEN_ON")) {
//                MediaPlayer mediaPlayer1 = MediaPlayer.create(context, R.raw.activate);
//                mediaPlayer1.start();
////                mediaPlayer1.release();
////                mediaPlayer1 = null;
//                Toast.makeText(context, "1", Toast.LENGTH_LONG).show();
//            } else if (actionName.equals("android.intent.action.ACTION_SCREEN_OFF")) {
//                MediaPlayer mediaPlayer2 = MediaPlayer.create(context, R.raw.deactivate);
//                mediaPlayer2.start();
//                Toast.makeText(context, "2", Toast.LENGTH_LONG).show();
//            } else if (actionName.equals("android.intent.action.USER_PRESENT")) {
//                MediaPlayer mediaPlayer3 = MediaPlayer.create(context, R.raw.swing_slow);
//                mediaPlayer3.start();
//                Toast.makeText(context, "3", Toast.LENGTH_LONG).show();
//            }
        }

    }
}
