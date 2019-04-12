/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2013 YIXIA.COM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vov.vitamio.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.vov.vitamio.LY.LYLeftlCycle;
import io.vov.vitamio.MediaFormat;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnSeekCompleteListener;
import io.vov.vitamio.MediaPlayer.OnTimedTextListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
import io.vov.vitamio.MediaPlayer.TrackInfo;
import io.vov.vitamio.utils.Log;
import io.vov.vitamio.utils.ScreenResolution;

/**
 * Displays a video file. The VideoView class can load images from various
 * sources (such as resources or content providers), takes care of computing its
 * measurement from the video so that it can be used in any layout manager, and
 * provides various display options such as scaling and tinting.
 * <p/>
 * VideoView also provide many wrapper methods for
 * {@link MediaPlayer}, such as {@link #getVideoWidth()},
 * {@link #setTimedTextShown(boolean)}
 */
@SuppressLint("NewApi")
public class TexttureVideoView extends LYTextureView implements MediaController.MediaPlayerControl, TextureView.SurfaceTextureListener {
    String TAG="VideoView";
	public static final int VIDEO_LAYOUT_ORIGIN = 0;
	public static final int VIDEO_LAYOUT_SCALE = 1;
	public static final int VIDEO_LAYOUT_STRETCH = 2;
	public static final int VIDEO_LAYOUT_ZOOM = 3;
	public static final int VIDEO_LAYOUT_FIT_PARENT = 4;
	private static final int STATE_ERROR = -1;
	private static final int STATE_IDLE = 0;
	private static final int STATE_PREPARING = 1;
	private static final int STATE_PREPARED = 2;
	private static final int STATE_PLAYING = 3;
	private static final int STATE_PAUSED = 4;
	private static final int STATE_PLAYBACK_COMPLETED = 5;
	private static final int STATE_SUSPEND = 6;
	private static final int STATE_RESUME = 7;
	private static final int STATE_SUSPEND_UNSUPPORTED = 8;
    private SurfaceTexture mSurfaceTexture;
    private Surface surface;

    Object object;

    /**
     * 通过生命周期出来下面，1当界面隐藏的时候 回收所有资源，当界面打开的时候重新加载资源。
     * todo 2019年4月11日10:40:57   不设置生命周期监听了。
     */
    public void setTag(Object object){
        this.object=object;
       /* if (object instanceof AppCompatActivity){
            AppCompatActivity activity= (AppCompatActivity) object;
            activity.getLifecycle().addObserver(observer);
        }else if (object instanceof Fragment){
            Fragment fragment= (Fragment) object;
            fragment.getLifecycle().addObserver(observer);
        }*/

    }

    LifecycleObserver observer=new LifecycleObserver() {
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        void onPause() {
            android.util.Log.e(TAG, "onPause: " );
            pause();
            if (object instanceof Fragment){
                if (leftlCycle!=null){
                    leftlCycle.onRestore();
                }
            }
        }
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        void onResume(){
            android.util.Log.e(TAG, "onResume: " );
            if (mUri!=null&&object instanceof AppCompatActivity){
                setVideoURI(mUri);//恢复播放
            }else if (mUri!=null&&object instanceof Fragment){
                start();
            }
        }
        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        void onStop(){
            android.util.Log.e(TAG, "onStop: " );
           stopPlayback();
           if (object instanceof Fragment){
               if (leftlCycle!=null){
                   leftlCycle.onRestore();
               }
           }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        void onDestroy() {
            android.util.Log.e(TAG, "onPause: " );
            stopPlayback();
            if (leftlCycle!=null){
                leftlCycle.onRestore();
            }
        }
    };
    OnVideoSizeChangedListener mSizeChangedListener = new OnVideoSizeChangedListener() {
		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
      Log.d("onVideoSizeChanged: (%dx%d)", width, height);
      mVideoWidth = mp.getVideoWidth();
      mVideoHeight = mp.getVideoHeight();
      mVideoAspectRatio = mp.getVideoAspectRatio();
      if (mVideoWidth != 0 && mVideoHeight != 0)
        setVideoLayout();
    }
  };


  OnPreparedListener mPreparedListener = new OnPreparedListener() {
    public void onPrepared(MediaPlayer mp) {
			mCurrentState = STATE_PREPARED;
			// mTargetState = STATE_PLAYING;

			// Get the capabilities of the player for this stream
			//TODO mCanPause
        if (leftlCycle!=null){
            leftlCycle.onPrepared();
        }
      if (mOnPreparedListener != null)
        mOnPreparedListener.onPrepared(mMediaPlayer);
      if (mMediaController != null)
        mMediaController.setEnabled(true);
      mVideoWidth = mp.getVideoWidth();
      mVideoHeight = mp.getVideoHeight();
      mVideoAspectRatio = mp.getVideoAspectRatio();

      long seekToPosition = mSeekWhenPrepared;
      if (seekToPosition != 0)
        seekTo(seekToPosition);

      if (mVideoWidth != 0 && mVideoHeight != 0) {
        setVideoLayout();
        if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
          if (mTargetState == STATE_PLAYING) {
            start();
            if (mMediaController != null)
              mMediaController.show();
          } else if (!isPlaying() && (seekToPosition != 0 || getCurrentPosition() > 0)) {
            if (mMediaController != null)
              mMediaController.show(0);
          }
        }
      } else if (mTargetState == STATE_PLAYING) {
        start();
      }
    }
  };
	private Uri mUri;
	private long mDuration;
	private int mCurrentState = STATE_IDLE;
	private int mTargetState = STATE_IDLE;
	private float mAspectRatio = 0;
	private int mVideoLayout = VIDEO_LAYOUT_SCALE;
	private MediaPlayer mMediaPlayer = null;
	private int mVideoWidth;
	private int mVideoHeight;
	private float mVideoAspectRatio;
	private int mVideoChroma = MediaPlayer.VIDEOCHROMA_RGBA;
	private boolean mHardwareDecoder = false;
	private int mSurfaceWidth;
	private int mSurfaceHeight;
	private MediaController mMediaController;
	private View mMediaBufferingIndicator;
	private OnCompletionListener mOnCompletionListener;
	private OnPreparedListener mOnPreparedListener;
	private OnErrorListener mOnErrorListener;
	private OnSeekCompleteListener mOnSeekCompleteListener;
	private OnTimedTextListener mOnTimedTextListener;
	private OnInfoListener mOnInfoListener;
	private OnBufferingUpdateListener mOnBufferingUpdateListener;
	private int mCurrentBufferPercentage;
	private long mSeekWhenPrepared; // recording the seek position while preparing
	private Context mContext;
	private Map<String, String> mHeaders;
	private int mBufSize;
    LYLeftlCycle leftlCycle;

    /**
     * 设置生命周期回调
     * @param leftlCycle
     */
    public void setLeftlCycle(LYLeftlCycle leftlCycle) {
        this.leftlCycle = leftlCycle;
    }

    private OnCompletionListener mCompletionListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
      Log.d("onCompletion");
      mCurrentState = STATE_PLAYBACK_COMPLETED;
      mTargetState = STATE_PLAYBACK_COMPLETED;
      if (mMediaController != null)
        mMediaController.hide();
      if (mOnCompletionListener != null)
        mOnCompletionListener.onCompletion(mMediaPlayer);
      if (leftlCycle!=null){
          leftlCycle.onCompletion();
      }
    }
  };
  private OnErrorListener mErrorListener = new OnErrorListener() {
    public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
      Log.d("Error: %d, %d", framework_err, impl_err);
      mCurrentState = STATE_ERROR;
      mTargetState = STATE_ERROR;
        if (leftlCycle!=null){
            leftlCycle.onError(framework_err,impl_err);
        }
      if (mMediaController != null)
        mMediaController.hide();

      if (mOnErrorListener != null) {
        if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err))
          return true;
      }

      if (getWindowToken() != null) {
        int message = framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ? getResources().getIdentifier("VideoView_error_text_invalid_progressive_playback", "string", mContext.getPackageName()): getResources().getIdentifier("VideoView_error_text_unknown", "string", mContext.getPackageName());

        new AlertDialog.Builder(mContext).setTitle(getResources().getIdentifier("VideoView_error_title", "string", mContext.getPackageName())).setMessage(message).setPositiveButton(getResources().getIdentifier("VideoView_error_button", "string", mContext.getPackageName()), new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            if (mOnCompletionListener != null)
              mOnCompletionListener.onCompletion(mMediaPlayer);
          }
        }).setCancelable(false).show();
      }
      return true;
    }
  };
  private OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
      mCurrentBufferPercentage = percent;
        if (leftlCycle!=null){
            leftlCycle.onBuffer(percent);
        }
      if (mOnBufferingUpdateListener != null)
        mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
    }
  };
  private OnInfoListener mInfoListener = new OnInfoListener() {
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
      Log.d("onInfo: (%d, %d)", what, extra);
        if (leftlCycle!=null){
            leftlCycle.onInfo(what,extra);
        }
      	if(MediaPlayer.MEDIA_INFO_UNKNOW_TYPE == what){
    		Log.e(" VITAMIO--TYPE_CHECK  stype  not include  onInfo mediaplayer unknow type ");
    	}

    	if(MediaPlayer.MEDIA_INFO_FILE_OPEN_OK == what){
    		long buffersize=mMediaPlayer.audioTrackInit();
    		mMediaPlayer.audioInitedOk(buffersize);
    	}

      Log.d("onInfo: (%d, %d)", what, extra);

      if (mOnInfoListener != null) {
        mOnInfoListener.onInfo(mp, what, extra);
      } else if (mMediaPlayer != null) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
          mMediaPlayer.pause();
          if (mMediaBufferingIndicator != null)
            mMediaBufferingIndicator.setVisibility(View.VISIBLE);
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
          mMediaPlayer.start();
          if (mMediaBufferingIndicator != null)
            mMediaBufferingIndicator.setVisibility(View.GONE);
        }
      }
      return true;
    }
  };
  private OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {
    @Override
    public void onSeekComplete(MediaPlayer mp) {
      Log.d("onSeekComplete");
      if (mOnSeekCompleteListener != null)
        mOnSeekCompleteListener.onSeekComplete(mp);
        if (leftlCycle!=null){
            leftlCycle.onSeekComplete();
        }
    }
  };
  private OnTimedTextListener mTimedTextListener = new OnTimedTextListener() {
    @Override
    public void onTimedTextUpdate(byte[] pixels, int width, int height) {
      Log.i("onSubtitleUpdate: bitmap subtitle, %dx%d", width, height);
      if (mOnTimedTextListener != null)
        mOnTimedTextListener.onTimedTextUpdate(pixels, width, height);
    }

    @Override
    public void onTimedText(String text) {
      Log.i("onSubtitleUpdate: %s", text);
      if (mOnTimedTextListener != null)
        mOnTimedTextListener.onTimedText(text);
    }
  };

  public TexttureVideoView(Context context) {
    super(context);
    initVideoView(context);
  }

  public TexttureVideoView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
    initVideoView(context);
  }

  public TexttureVideoView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initVideoView(context);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
    int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
    setMeasuredDimension(width, height);
  }

	/**
	 * Set the display options
	 */
	public void setVideoLayout() {
		Pair<Integer, Integer> res = ScreenResolution.getResolution(mContext);
		int windowWidth = res.first.intValue(),
                windowHeight = res.second.intValue();
        float x=(float) mVideoWidth/(float) mVideoHeight;
        //todo  以高为基准，绘制屏幕画布
        if (windowWidth<windowHeight){
            //竖屏
            //android.util.Log.e(TAG, "setVideoLayout: 竖屏"+windowWidth+"-------"+windowHeight+"------" );
            FrameLayout.LayoutParams params=new FrameLayout.LayoutParams((int) (mSurfaceHeight*x), mSurfaceHeight);
            params.gravity= Gravity.CENTER;
            setLayoutParams(params);
        }else {
            //横屏
          //  android.util.Log.e(TAG, "setVideoLayout: 横屏"+windowWidth+"-------"+windowHeight+"-------");
            FrameLayout.LayoutParams params=new FrameLayout.LayoutParams((int) (windowHeight*x),windowHeight);
            params.gravity= Gravity.CENTER;
            setLayoutParams(params);

        }
  }


  @SuppressWarnings("deprecation")
  private void initVideoView(Context ctx) {
      Activity activity= (Activity) getContext();
      activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mContext = ctx;
        mVideoWidth = 0;
        mVideoHeight = 0;
   setKeepScreenOn(true);
   setLayerType(TextureView.LAYER_TYPE_SOFTWARE, null);
   setSurfaceTextureListener(this);
    setFocusable(true);
    setFocusableInTouchMode(true);
    requestFocus();
    mCurrentState = STATE_IDLE;
    mTargetState = STATE_IDLE;
    if (ctx instanceof Activity)
      ((Activity) ctx).setVolumeControlStream(AudioManager.STREAM_MUSIC);
  }
  public boolean isValid() {
    return (mSurfaceTexture != null &&surface.isValid());
  }
 
  public void setVideoPath(String path) {
    setVideoURI(Uri.parse(path));
  }

  public void setVideoURI(Uri uri) {
    setVideoURI(uri, null);
  }
  
  public void setVideoURI(Uri uri, Map<String, String> headers) {
    mUri = uri;
    mHeaders = headers;
    mSeekWhenPrepared = 0;
    openVideo();
    requestLayout();
    invalidate();
  }

  public void stopPlayback() {
    if (mMediaPlayer != null) {
      mMediaPlayer.stop();
      mMediaPlayer.release();
      mMediaPlayer = null;
      mCurrentState = STATE_IDLE;
      mTargetState = STATE_IDLE;
    }
  }
  
  private void openVideo() {
      android.util.Log.e(TAG, "openVideo: " );
//    if (mUri == null || mSurfaceHolder == null || !Vitamio.isInitialized(mContext))
//      return;
    if (mUri == null || mSurfaceTexture == null )
    return;
    Intent i = new Intent("com.android.music.musicservicecommand");
    i.putExtra("command", "pause");
    mContext.sendBroadcast(i);

		release(false);
		try {
			mDuration = -1;
			mCurrentBufferPercentage = 0;
			mMediaPlayer = new MediaPlayer(mContext, mHardwareDecoder);
			mMediaPlayer.setOnPreparedListener(mPreparedListener);
			mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
			mMediaPlayer.setOnCompletionListener(mCompletionListener);
			mMediaPlayer.setOnErrorListener(mErrorListener);
			mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			mMediaPlayer.setOnInfoListener(mInfoListener);
			//不可设置 循环播放，特喵的。设置循环播放 会内存泄漏：Channel is unrecoverably broken and will be disposed!
			mMediaPlayer.setLooping(false);
			mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
			mMediaPlayer.setOnTimedTextListener(mTimedTextListener);
            mMediaPlayer.setDataSource(mContext, mUri, mHeaders);
            if (surface==null){
                surface = new Surface(mSurfaceTexture);
            }
            mMediaPlayer.setSurface(surface);
			mMediaPlayer.setBufferSize(mBufSize);
			mMediaPlayer.setVideoChroma(mVideoChroma == MediaPlayer.VIDEOCHROMA_RGB565 ? MediaPlayer.VIDEOCHROMA_RGB565 : MediaPlayer.VIDEOCHROMA_RGBA);
			mMediaPlayer.setScreenOnWhilePlaying(true);
			mMediaPlayer.prepareAsync();
			mCurrentState = STATE_PREPARING;
			attachMediaController();
		} catch (IOException ex) {
			Log.e("Unable to open content: " + mUri, ex);
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
			return;
		} catch (IllegalArgumentException ex) {
			Log.e("Unable to open content: " + mUri, ex);
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
			return;
		}
	}

  public void setMediaController(MediaController controller) {
    if (mMediaController != null)
      mMediaController.hide();
    mMediaController = controller;
    attachMediaController();
  }
  
  public void setMediaBufferingIndicator(View mediaBufferingIndicator) {
    if (mMediaBufferingIndicator != null)
      mMediaBufferingIndicator.setVisibility(View.GONE);
    mMediaBufferingIndicator = mediaBufferingIndicator;
  }

  private void attachMediaController() {
    if (mMediaPlayer != null && mMediaController != null) {
      mMediaController.setMediaPlayer(this);
      View anchorView = this.getParent() instanceof View ? (View) this.getParent() : this;
      mMediaController.setAnchorView(anchorView);
      mMediaController.setEnabled(isInPlaybackState());

      if (mUri != null) {
        List<String> paths = mUri.getPathSegments();
        String name = paths == null || paths.isEmpty() ? "null" : paths.get(paths.size() - 1);
        mMediaController.setFileName(name);
      }
    }
  }

  public void setOnPreparedListener(OnPreparedListener l) {
    mOnPreparedListener = l;
  }

  public void setOnCompletionListener(OnCompletionListener l) {
    mOnCompletionListener = l;
  }

  public void setOnErrorListener(OnErrorListener l) {
    mOnErrorListener = l;
  }

  public void setOnBufferingUpdateListener(OnBufferingUpdateListener l) {
    mOnBufferingUpdateListener = l;
  }

  public void setOnSeekCompleteListener(OnSeekCompleteListener l) {
    mOnSeekCompleteListener = l;
  }

  public void setOnTimedTextListener(OnTimedTextListener l) {
    mOnTimedTextListener = l;
  }

  public void setOnInfoListener(OnInfoListener l) {
    mOnInfoListener = l;
  }

  private void release(boolean cleartargetstate) {
    if (mMediaPlayer != null) {
      mMediaPlayer.reset();
      mMediaPlayer.release();
      mMediaPlayer = null;
      mCurrentState = STATE_IDLE;
      if (cleartargetstate)
        mTargetState = STATE_IDLE;
    }
  }



  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    if (isInPlaybackState() && mMediaController != null)
      toggleMediaControlsVisiblity();
    return false;
  }

  @Override
  public boolean onTrackballEvent(MotionEvent ev) {
    if (isInPlaybackState() && mMediaController != null)
      toggleMediaControlsVisiblity();
    return false;
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_MENU && keyCode != KeyEvent.KEYCODE_CALL && keyCode != KeyEvent.KEYCODE_ENDCALL;
    if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
      if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE) {
        if (mMediaPlayer.isPlaying()) {
          pause();
          mMediaController.show();
        } else {
          start();
          mMediaController.hide();
        }
        return true;
      } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
        if (!mMediaPlayer.isPlaying()) {
            start();
            mMediaController.hide();
        }
        return true;
      } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
        if (mMediaPlayer.isPlaying()) {
          pause();
          mMediaController.show();
        }
        return true;
      } else {
        toggleMediaControlsVisiblity();
      }
    }

    return super.onKeyDown(keyCode, event);
  }

  private void toggleMediaControlsVisiblity() {
    if (mMediaController.isShowing()) {
      mMediaController.hide();
    } else {
      mMediaController.show();
    }
  }

  public void start() {
    if (isInPlaybackState()) {
      mMediaPlayer.start();
      mCurrentState = STATE_PLAYING;
    }
    mTargetState = STATE_PLAYING;
  }

  public void pause() {
    if (isInPlaybackState()) {
      if (mMediaPlayer.isPlaying()) {
        mMediaPlayer.pause();
        mCurrentState = STATE_PAUSED;
      }
    }
    mTargetState = STATE_PAUSED;
  }

  public void suspend() {
    if (isInPlaybackState()) {
      release(false);
      mCurrentState = STATE_SUSPEND_UNSUPPORTED;
      Log.d("Unable to suspend video. Release MediaPlayer.");
    }
  }

    /**
     * 恢复播放。
     */
  public void resume() {
    if (mSurfaceTexture == null && mCurrentState == STATE_SUSPEND) {
      mTargetState = STATE_RESUME;
    } else if (mCurrentState == STATE_SUSPEND_UNSUPPORTED) {
      openVideo();
    }
  }

  public long getDuration() {
    if (isInPlaybackState()) {
      if (mDuration > 0)
        return mDuration;
      mDuration = mMediaPlayer.getDuration();
      return mDuration;
    }
    mDuration = -1;
    return mDuration;
  }

  public long getCurrentPosition() {
    if (isInPlaybackState())
      return mMediaPlayer.getCurrentPosition();
    return 0;
  }

  public void seekTo(long msec) {
    if (isInPlaybackState()) {
      mMediaPlayer.seekTo(msec);
      mSeekWhenPrepared = 0;
    } else {
      mSeekWhenPrepared = msec;
    }
  }

  public boolean isPlaying() {
    return isInPlaybackState() && mMediaPlayer.isPlaying();
  }

  public int getBufferPercentage() {
    if (mMediaPlayer != null)
      return mCurrentBufferPercentage;
    return 0;
  }

  public void setVolume(float leftVolume, float rightVolume) {
    if (mMediaPlayer != null)
      mMediaPlayer.setVolume(leftVolume, rightVolume);
  }

  public int getVideoWidth() {
    return mVideoWidth;
  }

  public int getVideoHeight() {
    return mVideoHeight;
  }

  public float getVideoAspectRatio() {
    return mVideoAspectRatio;
  }
  
  /**
   * Must set before {@link #setVideoURI}
   * @param chroma
   */
  public void setVideoChroma(int chroma) {

    //getHolder().setFormat(chroma == MediaPlayer.VIDEOCHROMA_RGB565 ? PixelFormat.RGB_565 : PixelFormat.RGBA_8888); // PixelFormat.RGB_565
    mVideoChroma = chroma;
  }
  
  public void setHardwareDecoder(boolean hardware) {
    mHardwareDecoder= hardware;
  }
  
  public void setVideoQuality(int quality) {
    if (mMediaPlayer != null)
      mMediaPlayer.setVideoQuality(quality);
  }
  
  public void setBufferSize(int bufSize) {
    mBufSize = bufSize;
  }

  public boolean isBuffering() {
    if (mMediaPlayer != null)
      return mMediaPlayer.isBuffering();
    return false;
  }

  public String getMetaEncoding() {
    if (mMediaPlayer != null)
      return mMediaPlayer.getMetaEncoding();
    return null;
  }

  public void setMetaEncoding(String encoding) {
    if (mMediaPlayer != null)
      mMediaPlayer.setMetaEncoding(encoding);
  }

  public SparseArray<MediaFormat> getAudioTrackMap(String encoding) {
    if (mMediaPlayer != null)
      return mMediaPlayer.findTrackFromTrackInfo(TrackInfo.MEDIA_TRACK_TYPE_AUDIO, mMediaPlayer.getTrackInfo(encoding));
    return null;
  }

  public int getAudioTrack() {
    if (mMediaPlayer != null)
      return mMediaPlayer.getAudioTrack();
    return -1;
  }

  public void setAudioTrack(int audioIndex) {
    if (mMediaPlayer != null)
      mMediaPlayer.selectTrack(audioIndex);
  }

  public void setTimedTextShown(boolean shown) {
    if (mMediaPlayer != null)
      mMediaPlayer.setTimedTextShown(shown);
  }

  public void setTimedTextEncoding(String encoding) {
    if (mMediaPlayer != null)
      mMediaPlayer.setTimedTextEncoding(encoding);
  }

  public int getTimedTextLocation() {
    if (mMediaPlayer != null)
      return mMediaPlayer.getTimedTextLocation();
    return -1;
  }

  public void addTimedTextSource(String subPath) {
    if (mMediaPlayer != null)
      mMediaPlayer.addTimedTextSource(subPath);
  }

  public String getTimedTextPath() {
    if (mMediaPlayer != null)
      return mMediaPlayer.getTimedTextPath();
    return null;
  }

  public void setSubTrack(int trackId) {
    if (mMediaPlayer != null)
      mMediaPlayer.selectTrack(trackId);
  }

  public int getTimedTextTrack() {
    if (mMediaPlayer != null)
      return mMediaPlayer.getTimedTextTrack();
    return -1;
  }

  public SparseArray<MediaFormat> getSubTrackMap(String encoding) {
    if (mMediaPlayer != null)
      return mMediaPlayer.findTrackFromTrackInfo(TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, mMediaPlayer.getTrackInfo(encoding));
    return null;
  }

  protected boolean isInPlaybackState() {
    return (mMediaPlayer != null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
  }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        android.util.Log.e(TAG, "onSurfaceTextureAvailable: "+width+"----------------"+height );
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        if (mSurfaceTexture==null){
            mSurfaceTexture = surface;
            openVideo();
        }else {
            setSurfaceTexture(mSurfaceTexture);
            setVideoLayout();
            //setVideoLayout(0);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        android.util.Log.e(TAG, "onSurfaceTextureSizeChanged: "+width+"-----------------------"+height );
        boolean isValidState = (mTargetState == STATE_PLAYING);
        boolean hasValidSize = (mVideoWidth == width && mVideoHeight == height);
        if (mMediaPlayer != null && isValidState && hasValidSize) {
            if (mSeekWhenPrepared != 0)
                seekTo(mSeekWhenPrepared);
            start();
            if (mMediaController != null) {
                if (mMediaController.isShowing())
                    mMediaController.hide();
                mMediaController.show();
            }
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return mSurfaceTexture==null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        //android.util.Log.e(TAG, "onSurfaceTextureUpdated: " );
    }

    /**
     * 设置播放速率
     * @param speed
     */
    public void setPlaybackSpeed(float speed){
      if (mMediaPlayer!=null){
          mMediaPlayer.setPlaybackSpeed(speed);
      }
    }

}