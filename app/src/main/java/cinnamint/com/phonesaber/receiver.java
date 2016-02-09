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
//        Toast.makeText(context, "receiver", Toast.LENGTH_SHORT);


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
        }

    }
}
