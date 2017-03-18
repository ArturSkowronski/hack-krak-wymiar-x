package demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.os.Handler;

public class MediaPlayerAudioHandler extends AudioHandler implements OnCompletionListener, OnPreparedListener, OnErrorListener {
    private MediaRecorder recorder;
    private boolean isRecording = false;
    MediaPlayer mPlayer;
    private boolean isPlaying = false;
    private boolean isReliced = true;
    private boolean isPausedByDevice = false;

    private final Handler handler = new Handler();

    public MediaPlayerAudioHandler(AudioCallback iAudio){
        super(iAudio);
    }

    public void start(Context context, int id) {
        if(isPlaying && mPlayer != null){
            mPlayer.stop();
            mPlayer.release();
            isPlaying= false;
            isReliced = true;
        }

            try {
                mPlayer = new MediaPlayer();
                AssetFileDescriptor afd = context.getResources().openRawResourceFd(id);
                if (afd == null) return;
                mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                isPlaying=true;
                mPlayer.prepare();
                mPlayer.setOnPreparedListener(this);

            } catch (Exception e)
            {
                e.printStackTrace();
            }


    }

    @Override
    public void play() {
        if(! isPlaying &&   this.mPlayer!= null && !isReliced  ){
            mPlayer.seekTo(0);
            this.mPlayer.start();
            isPlaying = true;
            isPausedByDevice= false;
            startPlayProgressUpdater();
            callback.onStart(mPlayer.getDuration());
        }

    }
    @Override
    public void resume() {
        if(isPausedByDevice){
            play();
        }

    }

    @Override
    public void pause() {
        if(isPlaying ){
         this.mPlayer.pause();
            isPlaying= false;
        }

    }

  @Override
    public void pauseByDevice() {
        if(isPlaying ){
            isPausedByDevice= true;
            this.pause();
        }
    }

    public void stop() {
        if (isPlaying) {
            mPlayer.stop();
            mPlayer.release();
            isPlaying=false;
            isReliced= true;
            isPausedByDevice= false;
        }
    }

    public void onCompletion(MediaPlayer mPlayer) {
        callback.onFinished();
        isPlaying=false;
    }

    public long getCurrentPosition() {
        if (isPlaying)
        {   try {
                return (mPlayer.getCurrentPosition());
            }catch (Exception e ){
                return (-1);
            }
        } else { return(-1); }
    }

    private boolean isStreaming(String file)
    {
        if (file.contains("http://") || file.contains("https://")) {
            return true;
        } else {
            return false;
        }
    }

    public long getDuration(String file) {
        long duration = -2;
        if (!isPlaying & !isStreaming(file)) {
            try {
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource("/sdcard/" + file);
                mPlayer.prepare();
                duration = mPlayer.getDuration();
                mPlayer.release();
            } catch (Exception e) { e.printStackTrace(); return(-3); }
        } else
        if (isPlaying & !isStreaming(file)) {
            duration = mPlayer.getDuration();
        } else
        if (isPlaying & isStreaming(file)) {
            try {
                duration = mPlayer.getDuration();
            } catch (Exception e) { e.printStackTrace(); return(-4); }
        }else { return -1; }
        return duration;
    }

    public void onPrepared(MediaPlayer mPlayer) {
        if (isPlaying) {
            isReliced= false;
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
                public void onBufferingUpdate(MediaPlayer mPlayer, int percent) {
                    callback.onBufferChange(percent);
                }
            });
            callback.onStart(mPlayer.getDuration());
            if(!isPausedByDevice) {
                mPlayer.start();
                startPlayProgressUpdater();
            }
        }
    }

    public void startPlayProgressUpdater() {
        if (isPlaying) {
            Runnable notification = new Runnable() {
                public void run() {
                    if(isPlaying) {
                        callback.onProgress((getCurrentPosition()*100)/mPlayer.getDuration());
                        startPlayProgressUpdater();
                    }
                }
            };
            handler.postDelayed(notification, 100);
        }
    }

    public boolean onError(MediaPlayer mPlayer, int arg1, int arg2) {
        return false;
    }

    public void seekTo(int mill){

        mPlayer.seekTo(mill);
    }



}
