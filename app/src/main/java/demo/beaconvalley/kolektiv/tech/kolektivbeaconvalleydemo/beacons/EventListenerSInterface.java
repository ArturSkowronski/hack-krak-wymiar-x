package demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.beacons;

import com.kontakt.sdk.android.ble.discovery.BluetoothDeviceEvent;

/**
 * Created by arturskowronski on 09/06/16.
 */
public interface EventListenerSInterface {
    void doAction(BluetoothDeviceEvent event, String namespace);
}
