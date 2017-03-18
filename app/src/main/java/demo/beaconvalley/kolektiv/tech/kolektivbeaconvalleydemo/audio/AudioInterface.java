package demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.audio;

import android.content.Context;

public interface AudioInterface {


    public void stop();

    public void seekTo(int to);

    public void start(Context aud, int id);

    public void play();

    public void resume();

    public void pause();

    public void pauseByDevice();

    public long getCurrentPosition();

    public long getDuration(String file);

}
