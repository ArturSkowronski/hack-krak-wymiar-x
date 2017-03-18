package demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.audio;



public abstract class AudioHandler implements AudioInterface {

    protected AudioCallback callback;

    public AudioHandler(AudioCallback callback){
            this.callback = callback;
    }

}
