package demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;

public class MSGReceiver  extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
 

        Bundle extras = intent.getExtras();
        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("message", extras.getString("message"));
//        msgrcv.putExtra("fromu", extras.getString("fromu"));
//        msgrcv.putExtra("fromname", extras.getString("name"));
//
 
        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
        setResultCode(Activity.RESULT_OK);
    }
}