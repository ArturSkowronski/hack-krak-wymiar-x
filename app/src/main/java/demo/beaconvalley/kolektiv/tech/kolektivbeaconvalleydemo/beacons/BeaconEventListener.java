package demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.beacons;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ForceScanConfiguration;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.configuration.scan.EddystoneScanContext;
import com.kontakt.sdk.android.ble.configuration.scan.IBeaconScanContext;
import com.kontakt.sdk.android.ble.configuration.scan.ScanContext;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.discovery.BluetoothDeviceEvent;
import com.kontakt.sdk.android.ble.filter.eddystone.EddystoneFilters;
import com.kontakt.sdk.android.ble.filter.eddystone.UIDFilter;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerContract;
import com.kontakt.sdk.android.manager.KontaktProximityManager;

import java.util.Collections;
import java.util.List;

/**
 * Created by arturskowronski on 09/06/16.
 */
public class BeaconEventListener {

    String TAG = "EDDY_BEACON_LISTENER";

    ProximityManagerContract proximityManager;
    ScanContext scanContext;
    private String namespace;

    public BeaconEventListener(Context context, String namespace) {
        this.namespace = namespace;
        this.proximityManager = new KontaktProximityManager(context);
        List<UIDFilter> filterList = Collections.singletonList(EddystoneFilters.newNamespaceIdFilter(namespace));
        scanContext = createScanContext(new EddystoneScanContext.Builder()
                .setUIDFilters(filterList)
                .build());

    }

    public void onEvent(final EventListenerSInterface action) {
        proximityManager.initializeScan(scanContext,

                new OnServiceReadyListener() {
                    @Override
                    public void onServiceReady() {
                        Log.d(TAG, "Scan Initialized with permission");

                        proximityManager.attachListener(new ProximityManager.ProximityListener() {
                            @Override
                            public void onScanStart() {
                                Log.d(TAG + namespace, "scan started");

                            }

                            @Override
                            public void onScanStop() {
                                Log.d(TAG + namespace, "scan stopped");

                            }

                            @Override
                            public void onEvent(BluetoothDeviceEvent event) {
                                action.doAction(event, namespace);
                            }
                        });
                    }

                    @Override
                    public void onConnectionFailure() {

                    }
                });
    }

    @NonNull
    private ScanContext createScanContext(EddystoneScanContext eddystoneScanContext) {
        return new ScanContext.Builder()
                .setScanPeriod(ScanPeriod.RANGING) // or for monitoring for 15 seconds scan and 10 seconds waiting:
                .setScanMode(ProximityManager.SCAN_MODE_BALANCED)
                .setActivityCheckConfiguration(ActivityCheckConfiguration.MINIMAL)
                .setForceScanConfiguration(ForceScanConfiguration.MINIMAL)
                .setEddystoneScanContext(eddystoneScanContext)
                .setForceScanConfiguration(ForceScanConfiguration.MINIMAL)
                .build();
    }
}
