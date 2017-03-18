package demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.audio;


public interface AudioCallback {

     void onStart(long duration);
     void onProgress(long position);
     void onBufferChange(int percent);
     void onFinished();
}
