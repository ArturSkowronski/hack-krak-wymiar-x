package demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.Fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kontakt.sdk.android.ble.discovery.BluetoothDeviceEvent;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.RemoteBluetoothDevice;

import java.util.Collections;
import java.util.List;

import demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.R;
import demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.Views.AnimatedCircle;
import demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.audio.AudioCallback;
import demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.audio.AudioInterface;
import demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.audio.MediaPlayerAudioHandler;
import demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.beacons.BeaconEventListener;
import demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.beacons.EventListenerSInterface;

public class MainFragment extends Fragment implements View.OnClickListener, AudioCallback {
    public MainFragment() {
        audioInterface = new MediaPlayerAudioHandler(this);
    }

    AnimatedCircle circle;

    TextView circleText;
    TextView header;
    TextView bottomText;
    ImageView soundImage;

    AudioInterface audioInterface;

    String NAMESPACE_1_ID = "c7826da6bc5b71e0893e";
    String NAMESPACE_2_ID = "e7826da6bc5b71e0893e";

    private static final String TAG = "EDDY_BEACON_LISTENER";
    private boolean region1seen = false;
    private boolean region2seen = false;
    private boolean gameEnded = true;
    private boolean startedGame = true;
    private Handler handler = new Handler();

    private int i = 0;
    private void checkPermissionAndStart(int amount) {
        int checkSelfPermissionResult = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if (PackageManager.PERMISSION_GRANTED == checkSelfPermissionResult) {
            //already granted
            startScan(amount);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //we should show some explanation for user here
            } else {
                //request permission
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);
        circle = (AnimatedCircle) rootView.findViewById(R.id.custom_circle);
        circleText = (TextView) rootView.findViewById(R.id.circleText);
        header = (TextView) rootView.findViewById(R.id.headingTextView);
        circle.setOnClickListener(this);
        soundImage = (ImageView) rootView.findViewById(R.id.soundImage);
        soundImage.setVisibility(View.INVISIBLE);

        bottomText = (TextView) rootView.findViewById(R.id.bottomText);
        bottomText.setOnClickListener(this);

        KontaktSDK.initialize(getContext());
        Bundle bundle = this.getArguments();
        int amount = 0;
        if(bundle!=null) amount = bundle.getInt("amount", 0);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, new IntentFilter("Msg"));

        checkPermissionAndStart(amount);

        handler.postDelayed(runnable, 1000);

        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (100 == requestCode) {
                //same request code as was in request permission
                Bundle bundle = this.getArguments();
                int amount = 0;
                if(bundle!=null) amount = bundle.getInt("amount", 0);
                startScan(amount);
            }

        } else {

            //not granted permission
            //show some explanation dialog that some features will not work
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.custom_circle:
                if (startedGame) {
                    audioInterface.play();
                } else {
                    setStageListen();
                    if (!region1seen && !gameEnded) {
                        setMessageToUser("Pierwszy raz w regionie 1");
                        Log.d(TAG, "Region 1");
                        region1seen = true;
                        audioInterface.start(getContext(), R.raw.sound1);
                    }
                    startedGame = true;
                }
                break;
            case R.id.bottomText:
                i++;
                if (i == 5) {
                    reset();
                    soundImage.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), "Reset!", Toast.LENGTH_LONG).show();
                    audioInterface.stop();
                    circle.stopAnimation();
                    setStageStart();
                    i = 0;
                }
                break;
        }
    }

    private void reset() {
        region1seen = false;
        region2seen = false;
        gameEnded = false;
        startedGame = false;
    }

    private void startScan(final int amount) {
        BeaconEventListener region1 = new BeaconEventListener(getActivity(), NAMESPACE_1_ID);
        BeaconEventListener region2 = new BeaconEventListener(getActivity(), NAMESPACE_2_ID);

        region1.onEvent(new EventListenerSInterface() {
            @Override
            public void doAction(BluetoothDeviceEvent event, String namespace) {
                on1stRegionEvent(event, amount, namespace);
            }
        });

        region2.onEvent(new EventListenerSInterface() {
            @Override
            public void doAction(BluetoothDeviceEvent event, String namespace) {
                on2ndRegionEvent(event, amount, namespace);
            }
        });
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

    private void on1stRegionEvent(BluetoothDeviceEvent bluetoothDeviceEvent, int amount, String add) {

        String TAGLocal = TAG + add;
        List<? extends RemoteBluetoothDevice> deviceList = bluetoothDeviceEvent.getDeviceList();
        amount= 0;

        switch (bluetoothDeviceEvent.getEventType()) {
            case SPACE_ENTERED:
//                if (deviceList.size() > amount) enteredFirstRegion(TAGLocal);
                break;
            case DEVICE_DISCOVERED:
                logging("DEVICE_DISCOVERED", TAGLocal, deviceList);
                break;
            case DEVICES_UPDATE:
                logging("DEVICES_UPDATE", TAGLocal, deviceList);
//                if (deviceList.size() > amount) enteredFirstRegion(TAGLocal);
                break;
            case DEVICE_LOST:
                logging("DEVICE_LOST", TAGLocal, deviceList);
                break;
            case SPACE_ABANDONED:
                logging("ABANDONED", TAGLocal, deviceList);
                break;
        }
    }

    private void enteredFirstRegion(String TAGLocal) {

        if (region2seen && region1seen && !gameEnded) {
            setMessageToUser("Wróciłeś na start. Koniec gry");
            gameEnded = true;
            Log.d(TAGLocal, "Koniec");
            audioInterface.start(getContext(), R.raw.sound3a1);
            setStageFinish();
        }
    }

    private void on2ndRegionEvent(BluetoothDeviceEvent bluetoothDeviceEvent, int amount, String add) {
        amount= 2;
        String TAGLocal = TAG + add;
        List<? extends RemoteBluetoothDevice> deviceList = bluetoothDeviceEvent.getDeviceList();

        switch (bluetoothDeviceEvent.getEventType()) {
            case SPACE_ENTERED:
                logging("SPACE_ENTERED", TAGLocal, deviceList);
//                if (deviceList.size() > amount) enteredSecondRegion(TAGLocal);
                break;
            case DEVICE_DISCOVERED:
                logging("DEVICE_DISCOVERED", TAGLocal, deviceList);
                break;
            case DEVICES_UPDATE:
                logging("DEVICES_UPDATE", TAGLocal, deviceList);

//                if (deviceList.size() > amount) enteredSecondRegion(TAGLocal);
                break;
            case DEVICE_LOST:
                logging("DEVICE_LOST", TAGLocal, deviceList);
                break;
            case SPACE_ABANDONED:
                logging("SPACE_ABANDONED", TAGLocal, deviceList);
                break;
        }
    }

    private void enteredSecondRegion(String TAGLocal) {
        if (!region2seen && region1seen && !gameEnded) {
            setMessageToUser("Jesteś w drugim regionie");
            Log.d(TAGLocal, "Region 2");
            region2seen = true;
            audioInterface.start(getContext(), R.raw.sound2a2);
        }
    }

    private void logging(String label, String TAGLocal, List<? extends RemoteBluetoothDevice> deviceList) {
        Log.d(TAGLocal, label + ":" + deviceList.size());
        for (RemoteBluetoothDevice bluetoothDevice : deviceList) {
            Log.d(TAGLocal, "Device: " + bluetoothDevice.getUniqueId() + ":" + bluetoothDevice.getName());
        }
    }

    private void setMessageToUser(String text) {
//        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();

    }

    private void setStageStart() {
        header.setText(getString(R.string.start_text));
        circleText.setText(getString(R.string.circle_text_start));
        soundImage.setVisibility(View.INVISIBLE);

    }

    private void setStageListen() {
        circleText.setText("");
        header.setText(getString(R.string.listen_text));
        soundImage.setVisibility(View.VISIBLE);
    }

    private void setStageFinish() {
        header.setText(getString(R.string.finish_text));
        circleText.setText(getString(R.string.circle_text_finish));
        soundImage.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStart(long duration) {

        circle.setProgress(0);
        circle.animateTo(100, duration);

    }

    @Override
    public void onProgress(long position) {


    }

    @Override
    public void onBufferChange(int percent) {
        // circle.animateTo(0);

    }

    public void setColor() {

    }

    @Override
    public void onFinished() {
        circle.setProgress(0);

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            i = 0;
            handler.postDelayed(this, 1500);
        }
    };

    @Override
    public void onDestroy() {
        audioInterface.stop();
        super.onDestroy();
    }


    private BroadcastReceiver onNotice= new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
      Log.d(TAG, "YES!");
        String message = intent.getExtras().getString("message");
        if(message.equals("region1")){
            enteredFirstRegion("TEST");
        }
        if(message.equals("region2")){
            enteredSecondRegion("TEST");

        }


        } catch (Exception e){
            Log.d("Exception", "e");
        }
    }
    };
}
