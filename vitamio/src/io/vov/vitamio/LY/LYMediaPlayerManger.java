package io.vov.vitamio.LY;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;

import io.vov.vitamio.MediaPlayer;

/**
 * 控制 全局只有一个mediaplayer
 */
public class LYMediaPlayerManger implements MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnTimedTextListener {
   private static final String TAG="LYMediaPlayerManger";
    static LYMediaPlayerManger manger;
    private MediaPlayer mMediaPlayer;
    private int mVideoChroma = MediaPlayer.VIDEOCHROMA_RGBA;
    private final HandlerThread mMediaHandlerThread;
    private final Handler mainThreadHandler;

    /**
     * 必须通过这个调调初始化。
     * @return
     */
    public static LYMediaPlayerManger init(){
        if (manger==null){
            manger=new LYMediaPlayerManger();
        }
        return manger;
    }

    public LYMediaPlayerManger() {
        mMediaHandlerThread = new HandlerThread(TAG);
        mMediaHandlerThread.start();
        mainThreadHandler = new Handler();
    }

    /**
     * 初始化b
     * @param context
     * @param url
     */
    public void initMediaPlayer(Context context, String url){
        try {
            mMediaPlayer = new MediaPlayer(context,true);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnInfoListener(this);
            mMediaPlayer.setOnSeekCompleteListener(this);
            mMediaPlayer.setOnTimedTextListener(this);
            mMediaPlayer.setDataSource(context, Uri.parse(url));
            // mMediaPlayer.setBufferSize(mBufSize);//设置视频缓冲大小（默认1024KB）单位Byte。
            //设置色彩
            mMediaPlayer.setVideoChroma(mVideoChroma == MediaPlayer.VIDEOCHROMA_RGB565 ? MediaPlayer.VIDEOCHROMA_RGB565 : MediaPlayer.VIDEOCHROMA_RGBA);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
        }catch (Exception e){

        }
    }

    /**
     * 给播放器设置 显示界面。
     * @param surface
     */
    public void setSurface(Surface surface){
        if (mMediaPlayer!=null){
            mMediaPlayer.setSurface(surface);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onTimedText(String text) {

    }

    @Override
    public void onTimedTextUpdate(byte[] pixels, int width, int height) {

    }
}
