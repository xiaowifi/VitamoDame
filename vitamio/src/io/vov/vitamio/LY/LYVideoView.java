package io.vov.vitamio.LY;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.vov.vitamio.Bean.VideoInfo;
import io.vov.vitamio.CallBack.InterceptCallBack;
import io.vov.vitamio.CallBack.LYVideoADCallBack;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.R;
import io.vov.vitamio.adapter.ResolutionAdapter;
import io.vov.vitamio.utils.ContextUtils;
import io.vov.vitamio.utils.StringUtils;

/**
 * 落叶 基于vitamio 播放器
 */
public class LYVideoView extends FrameLayout implements LYLeftlCycle, AdapterView.OnItemClickListener, View.OnClickListener, View.OnTouchListener, RadioGroup.OnCheckedChangeListener {
    static final String TAG="LYVideoView";
    //todo  网络显示放大器
    public static int NET_MAGNIFICATION=20;
    // TODO full screen 全屏屏幕状态常亮
    public static final int SCREEN_FULL=1;//全屏
    public static final int SCREEN_NORMAL=2;//正常屏幕。
    public static final int SCREEN_LIST=3;//列表屏幕。
    public static final int SCREEN_SMALL=4;//小屏幕播放。暂不处理、
    //todo  重要马甲分割线-----------------------------变量
    public int SCREEN_TYPE=SCREEN_NORMAL;//设置屏幕状态为默认状态。
    public int START_SCREEN_TYPE=SCREEN_NORMAL;//初始屏幕状态
    private RelativeLayout re_video;
    private FrameLayout frame_video;//用于存储播放器相关的。
    private ImageView imgCover;
    private TextView t_ad_time;
    private LinearLayout line_top;
    private ProgressBar pr_loading;
    private TextView t_percentage;
    private TextView t_network;
    private ImageView img_back;
    private TextView t_title;
    private LinearLayout l_controller;
    private TextView t_dissmiss;
    private GridView grid_sharpness;
    private RadioGroup radio_type;
    private SeekBar seek_sound;
    private SeekBar seek_brightness;
    private ImageView img_start;
    private LinearLayout lin_center_pro;
    private TextView t_progress;
    private ProgressBar progress_bar;
    private LinearLayout l_bottom_controller;
    private ImageView mediacontroller_play_pause;
    private TextView mediacontroller_time_current;
    private SeekBar mediacontroller_seekbar;
    private TextView mediacontroller_time_total;
    private ImageView img_move;
    private ImageView img_full;
    private TextView t_speek_1;
    private TextView t_speek_2;
    private TextView t_speek_3;
    private TextView t_speek_4;
    Activity activity;
    Handler handler=new Handler();//主线程hander
    private float rawX;
    private float rawY;
    private boolean left;
    int horizontal=-1;
    float event_x=-1;
    private TextView t_config;
    long modify_time=0;
    boolean ismove=false;
    private float event_y;
    int magnification=15;
    private int interval_move=50;
    private int boottom_move=50;
    Window window;
    private AudioManager mAudioManager;
    float nowVolunme=-1;
    InterceptCallBack interceptCallBack;
    VideoInfo info;//视频信息存储对象
    private ResolutionAdapter adapter;
    boolean isVideo=false;
    private LinearLayout l_laoding;

    public boolean isVideo() {
        return isVideo;
    }

    public void setInterceptCallBack(InterceptCallBack interceptCallBack) {
        this.interceptCallBack = interceptCallBack;
    }

    public LYVideoView(Context context) {
        super(context);
        initLayout();
    }

    public LYVideoView( Context context,  AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    public LYVideoView( Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout();
    }
    //todo 重要马甲分割线， 这是初始化区域。-----------------------------------------------------------------------------------------------
    private void initLayout() {
        //设置背景颜色。
        setBackgroundColor(Color.parseColor("#ff0099"));
        View.inflate(getContext(),getLayoutId(),this);
        initControllerViews();
    }

    /**
     * 初始化控制的所有view
     */
    private void initControllerViews() {
        //将view  从xml 中转成对象
        interval_move = ContextUtils.dp2px(getContext(), 35);
        boottom_move = ContextUtils.dp2px(getContext(), 55);
        re_video = findViewById(R.id.re_video);
        frame_video = findViewById(R.id.frame_video);
        imgCover = findViewById(R.id.imgCover);
        t_ad_time = findViewById(R.id.t_ad_time);
        line_top = findViewById(R.id.line_top);
        pr_loading = findViewById(R.id.pr_loading);
        t_percentage = findViewById(R.id.t_percentage);
        t_network = findViewById(R.id.t_network);
        img_back = findViewById(R.id.img_back);
        t_title = findViewById(R.id.t_title);
        l_controller = findViewById(R.id.l_controller);
        t_dissmiss = findViewById(R.id.t_dissmiss);
        grid_sharpness = findViewById(R.id.grid_sharpness);
        radio_type = findViewById(R.id.radio_type);//屏幕模式
        seek_sound = findViewById(R.id.seek_sound);
        seek_brightness = findViewById(R.id.seek_brightness);
        img_start = findViewById(R.id.img_start);
        lin_center_pro = findViewById(R.id.lin_center_pro);
        t_progress = findViewById(R.id.t_progress);
        progress_bar = findViewById(R.id.progress_bar);
        l_bottom_controller = findViewById(R.id.l_bottom_controller);
        mediacontroller_play_pause = findViewById(R.id.mediacontroller_play_pause);
        mediacontroller_time_current = findViewById(R.id.mediacontroller_time_current);
        mediacontroller_seekbar = findViewById(R.id.mediacontroller_seekbar);
        mediacontroller_time_total = findViewById(R.id.mediacontroller_time_total);
        l_laoding = findViewById(R.id.l_laoding);
        img_move = findViewById(R.id.img_move);
        img_full = findViewById(R.id.img_full);
        t_config = findViewById(R.id.t_config);
        t_speek_1 = findViewById(R.id.t_speek_1);
        t_speek_2 = findViewById(R.id.t_speek_2);
        t_speek_3 = findViewById(R.id.t_speek_3);
        t_speek_4 = findViewById(R.id.t_speek_4);
        //todo  在这个地方判断
        //设置view的点击事件和触摸事件
        re_video.setOnClickListener(this);
        img_back.setOnClickListener(this);
        t_dissmiss.setOnClickListener(this);
        //倍数播放的点击事件
        t_speek_1.setOnClickListener(this);
        t_speek_2.setOnClickListener(this);
        t_speek_3.setOnClickListener(this);
        t_speek_4.setOnClickListener(this);
        img_start.setOnClickListener(this);
        mediacontroller_play_pause.setOnClickListener(this);
        img_move.setOnClickListener(this);
        img_full.setOnClickListener(this);
        radio_type.setOnCheckedChangeListener(this);
        grid_sharpness.setOnItemClickListener(this);
        re_video.setOnTouchListener(this);
        mediacontroller_seekbar.setOnSeekBarChangeListener(mediacontrollerSeekbar);
        seek_brightness.setOnSeekBarChangeListener(seekBrightness);
        seek_sound.setOnSeekBarChangeListener(seekSound);
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        imgCover.setOnClickListener(this);
    }

    SeekBar.OnSeekBarChangeListener seekSound=new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateVolumeProgress(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    /**
     * 亮度拖动条相关事件
     */
    SeekBar.OnSeekBarChangeListener seekBrightness=new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateBrightProgress(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


    /**
     * 底部拖动条的拖动事件
     */
    SeekBar.OnSeekBarChangeListener mediacontrollerSeekbar=new SeekBar.OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            LYVPMannger.getInstance().pause();
            handler.removeCallbacks(updataController);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser)
                return;
            long newposition = (LYVPMannger.getInstance().getDuration() * progress) / 1000;
            String time = StringUtils.generateTime(newposition);
            LYVPMannger.getInstance().seekTo(newposition);
            if (mediacontroller_time_current != null)
                mediacontroller_time_current.setText(time);
        }

        public void onStopTrackingTouch(SeekBar bar) {
            pr_loading.setVisibility(View.VISIBLE);
            t_network.setVisibility(View.VISIBLE);
            t_percentage.setVisibility(View.VISIBLE);
            //videoView.seekTo((mDuration * bar.getProgress()) / 1000);
            LYVPMannger.getInstance().start();
            handler.postDelayed(updataController,UpdataController);
        }
    };

    /**
     * 全局点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_start) {
        //开始播放的点击事件
            if (interceptCallBack!=null){
                interceptCallBack.onStartClick();
            }else {
                startVideo();
            }

        }else if (i==R.id.re_video){
            //TODO 定时5秒 关闭控制层
           showAndDissMissController();
        }else if (i==R.id.img_full){
            //控制屏幕横竖屏切换。
            if (SCREEN_TYPE==SCREEN_FULL){
                Log.e(TAG, "现在是全屏改成小屏" );
                onBack();

            }else {
                Log.e(TAG, "现在是小屏改成全屏" );
                onFull();
            }
        }else if (i==R.id.img_move){
            //控制操作层显示
            if (info!=null&&info.VIDEO_AD==false&&info.IMAGE_AD==false){
                l_controller.setVisibility(VISIBLE);
            }

        }else if (i==R.id.t_dissmiss){
            l_controller.setVisibility(GONE);
        }else if (i==R.id.mediacontroller_play_pause){
            //暂停或者播放
            if (LYVPMannger.getInstance().isPlayer()){
                LYVPMannger.getInstance().pause();
                mediacontroller_play_pause.setImageResource(getResources().getIdentifier("mediacontroller_play", "drawable", getContext().getPackageName()));
            }else {
                LYVPMannger.getInstance().start();
                mediacontroller_play_pause.setImageResource(getResources().getIdentifier("mediacontroller_pause", "drawable", getContext().getPackageName()));

            }
        }else if (i==R.id.t_speek_1){
            t_speek_1.setTextColor(Color.parseColor("#ea8b3c"));
            t_speek_2.setTextColor(Color.parseColor("#ffffff"));
            t_speek_3.setTextColor(Color.parseColor("#ffffff"));
            t_speek_4.setTextColor(Color.parseColor("#ffffff"));
            LYVPMannger.getInstance().setPlaybackSpeed(1.0f);
        }else if (i==R.id.t_speek_2){
            t_speek_2.setTextColor(Color.parseColor("#ea8b3c"));
            t_speek_1.setTextColor(Color.parseColor("#ffffff"));
            t_speek_3.setTextColor(Color.parseColor("#ffffff"));
            t_speek_4.setTextColor(Color.parseColor("#ffffff"));
            LYVPMannger.getInstance().setPlaybackSpeed(1.25f);
        }else if (i==R.id.t_speek_3){
            t_speek_3.setTextColor(Color.parseColor("#ea8b3c"));
            t_speek_1.setTextColor(Color.parseColor("#ffffff"));
            t_speek_2.setTextColor(Color.parseColor("#ffffff"));
            t_speek_4.setTextColor(Color.parseColor("#ffffff"));
            LYVPMannger.getInstance().setPlaybackSpeed(1.5f);
        }else if (i==R.id.t_speek_4){
            t_speek_4.setTextColor(Color.parseColor("#ea8b3c"));
            t_speek_2.setTextColor(Color.parseColor("#ffffff"));
            t_speek_3.setTextColor(Color.parseColor("#ffffff"));
            t_speek_1.setTextColor(Color.parseColor("#ffffff"));
            LYVPMannger.getInstance().setPlaybackSpeed(2f);
        }else if (i==R.id.img_back){
            if (START_SCREEN_TYPE==SCREEN_FULL){
                LYVPMannger.getInstance().clearVideo();
                activity= (Activity) getContext();
                activity.finish();
                return;
            }
            if (SCREEN_TYPE==SCREEN_FULL){
                onBack();
            }else {
                LYVPMannger.getInstance().clearVideo();
                activity= (Activity) getContext();
                activity.finish();
            }
        }else if (i==R.id.imgCover){
            if (info!=null&&info.IMAGE_AD){
                if (adCallBack!=null){
                    adCallBack.onADCallBack();
                }
            }
        }
    }



    /**
     * gridview 的item的点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (info.resolutions.containsKey(info.getKeys().get(position))){
            //如果包含
            adapter.setSelect_key(position);
            info.playkey=info.getKeys().get(position);
            startVideo();
        }
    }

    /**
     * 全局触摸事件
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId()!=R.id.re_video){
            return false;
        }
        switch (event.getAction()){
            //单指
            case MotionEvent.ACTION_DOWN:
                rawX = event.getRawX();
                rawY = event.getRawY();
                //触碰区域。
                left = false;
                if (rawX <=getWidth()/2){
                    left =true;
                }
                LYVPMannger.getInstance().pause();
                handler.removeCallbacks(updataController);
                Log.e(TAG, "setFullProbar 手指按下"+ rawX +"--------------------"+ rawY);
                //手指 初次接触到屏幕 时触发
                if (info!=null&&info.IMAGE_AD==false&&info.VIDEO_AD==false&&isVideo){
                    l_bottom_controller.setVisibility(VISIBLE);
                    if (SCREEN_TYPE!=SCREEN_LIST){
                        line_top.setVisibility(VISIBLE);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "手指移动: " );
                setFullProbar(event);
                //手指 在屏幕上滑动 时触发，会多次触发。
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "onTouchEvent: 手指抬起" );
                t_config.setVisibility(GONE);
                t_progress.setVisibility(GONE);
                progress_bar.setVisibility(GONE);
                //2019年4月1日18:35:06  重写 滑动改变事件
                if (horizontal==0){
                    LYVPMannger.getInstance().seekTo(modify_time);
                }
                LYVPMannger.getInstance().start();
                ismove=false;
                handler.postDelayed(updataController,UpdataController);
                showAndDissMissController();
                //手指 离开屏幕 时触发。
                event_x=-1;
                event_y = -1;
                horizontal=-1;
                break;
            case MotionEvent.ACTION_CANCEL:
                //事件 被上层拦截 时触发。
                break;
            case MotionEvent.ACTION_OUTSIDE:
                //手指 不在控件区域 时触发。
                break;
            //多指
            case MotionEvent.ACTION_POINTER_DOWN:
                //有非主要的手指按下(即按下之前已经有手指在屏幕上)。
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //有非主要的手指抬起(即抬起之后仍然有手指在屏幕上)。
                break;
        }
        return true;
    }


    /**
     * 单选点击事件
     * todo  暂未实现
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId==R.id.radio_1){
            LYVPMannger.getInstance().setVideoLayout(0,0);
        }else if (checkedId==R.id.radio_2){
            LYVPMannger.getInstance().setVideoLayout(1,0);
        }else if (checkedId==R.id.radio_3){
            LYVPMannger.getInstance().setVideoLayout(2,0);
        }else {
            LYVPMannger.getInstance().setVideoLayout(3,0);
        }
    }

    /**
     *
     * @return
     */
    private int getLayoutId() {
        return R.layout.ly_video_floor;
    }
    //todo 重要马甲分割线， 这是生命周期 回调处理。-----------------------------------------------------------------------------------------------
    @Override
    public void onError(int what, int extra) {
        startVideo();
        showDialog("哎呀，播放发生错误了，要不刷新一下或者重新打开APP试试？");
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onInfo(int what, int extra) {
        Log.e(TAG, "onInfo: "+what );
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.e(TAG, "onInfo: 播放错误，未知错误");
                break;
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.e(TAG, "onInfo: 播放错误（一般视频播放比较慢或视频本身有问题会引发）。");
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                Log.e(TAG, "onInfo: 视频过于复杂，无法解码：不能快速解码帧。此时可能只能正常播放音频。参见MediaPlayer.OnInfoListener。");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                Log.e(TAG, "onInfo: MediaPlayer暂停播放等待缓冲更多数据。");
                imgCover.setVisibility(GONE);
                img_start.setVisibility(GONE);
                   // videoView.pause();
                l_laoding.setVisibility(VISIBLE);
                pr_loading.setVisibility(View.VISIBLE);
               // t_network.setText("正在缓冲中，请稍等···");
                t_network.setVisibility(View.VISIBLE);
                t_percentage.setText("");
                t_percentage.setVisibility(VISIBLE);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                Log.e(TAG, "onInfo: MediaPlayer在缓冲完后继续播放。");
                pr_loading.setVisibility(View.GONE);
                t_network.setVisibility(View.GONE);
                t_percentage.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                Log.e(TAG, "onInfo: 媒体不支持Seek，例如直播流。");
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                Log.e(TAG, "MEDIA_INFO_DOWNLOAD_RATE_CHANGED。 "+NET_MAGNIFICATION);
                t_network.setText("正在缓冲中，请稍等···\n" + StringUtils.netWorkForamt(extra,NET_MAGNIFICATION));
                break;
            case MediaPlayer.VIDEOQUALITY_LOW:
                Log.e(TAG, "onInfo: 视频质量——流畅。");
                break;
            case MediaPlayer.VIDEOQUALITY_HIGH:
                Log.e(TAG, "onInfo: 视频质量——高质。");
                break;
            case MediaPlayer.SUBTITLE_INTERNAL:
                Log.e(TAG, "onInfo: 字幕显示来自内置字幕。");
                break;
        }
    }

    @Override
    public void onBuffer(int percent) {
        //缓冲进度
      //  Log.e(TAG, "onBuffer: "+percent );
        t_percentage.setText(percent + "%");

    }

    @Override
    public void onCompletion() {
        //播放完成
        if (info.VIDEO_AD){
            info.VIDEO_AD=false;
        }
        startVideo();
    }

    @Override
    public void onPrepared() {
        //准备完成
       updataBottomInfo();
       isVideo=true;
       setTag(TAG);
        img_start.setVisibility(GONE);
    }

    @Override
    public void onSeekComplete() {
        //todo 拖动完成
        Log.e(TAG, "onSeekComplete: " );
    }


    @Override
    public void onRestore() {
        //todo 恢复
        isVideo=false;
        if (getLocalVisibleRect(new Rect())){
            imgCover.setVisibility(VISIBLE);
        }
        l_laoding.setVisibility(GONE);
        frame_video.removeAllViews();
        img_start.setVisibility(VISIBLE);
        l_bottom_controller.setVisibility(GONE);
        clearALlPost();
    }

    @Override
    public void onActivityBack() {
        if (SCREEN_TYPE==SCREEN_FULL){
            onBack();
        }else {
            handler.removeCallbacks(imgAd);
            LYVPMannger.getInstance().clearVideo();
            activity= (Activity) getContext();
            activity.finish();
        }
    }

    @Override
    public void onFragmentBack() {
        if (SCREEN_TYPE==SCREEN_FULL){
            onBack();
        }
    }


    //todo 重要马甲分割线， 这是其他方法区域。-----------------------------------------------------------------------------------------------

    /**
     * 开始播放
     */
    public void startVideo() {
        if (info==null){
            showDialog("播放地址无效，请刷新后重试");
            return;
        }
        Log.e(TAG, "startVideo: " );
        img_start.setVisibility(GONE);
        imgCover.setVisibility(GONE);
        t_ad_time.setVisibility(GONE);
        frame_video.removeAllViews();
        frame_video.addView(LYVPMannger.getInstance().getVideoPlayer(getContext(),this,info.resolutions.get(info.playkey)),new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 播放视频广告
     */
    private void startVideoADVideo(){
        if (info==null){
            showDialog("播放地址无效，请刷新后重试");
            return;
        }
        img_start.setVisibility(GONE);
        frame_video.removeAllViews();
        frame_video.addView(LYVPMannger.getInstance().getVideoPlayer(getContext(),this,info.video_AD),new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    }
    /**
     * 播放带有广告的视频、
     */
    public void startADVideo(){
        img_start.setVisibility(GONE);
        imgCover.setVisibility(VISIBLE);
        LYVPMannger.getInstance().clearVideo();
        if (info==null){
            showDialog("播放地址无效，请刷新后重试");
            return;
        }
        if (info.IMAGE_AD){
            showImageAd();
            //表示这个地方需要处理 图片广告
            return;
        }
        if (info.VIDEO_AD){
            //表示这个地方需要处理视频广告。
            startVideoADVideo();
            return;
        }
        //播放正文视频
        startVideo();
    }

    /**
     * 设置 视频信息
     * @param info
     */
    public void setVideoUri(VideoInfo info){
        this.info=info;
        bindInfo();
    }



    /**
     * 移除所有 hander  postion 相关的线程
     */
    private void clearALlPost() {
        handler.removeCallbacks(updataController);
    }
    /**
     * 更新底部的控制层。
     */
    private void updataBottomInfo() {
        upController();
        handler.removeCallbacks(updataController);//移除之前未完成的run。
        handler.post(updataController);
        showAndDissMissController();
    }
    //todo  重要马甲分割线。通过hander post  刷新UI.---------------------------------------------------------------------------------------------------------


    //TODO  5秒后关闭底部控制栏
    long DissMIssBootomController=5000;
    Runnable dissMIssBootomController=new Runnable() {
        @Override
        public void run() {
            line_top.setVisibility(GONE);
            if (l_bottom_controller.getVisibility()==VISIBLE){
                l_bottom_controller.setVisibility(GONE);
            }
        }
    };

    //todo  每隔 0.5秒刷新一次控制栏数据
    long UpdataController=500;
    Runnable updataController=new Runnable() {
        @Override
        public void run() {
            //逻辑，只有底部控制层处于显示状态的时候才会更新信息。同时处于播放 时候才更新数据。
          if (l_bottom_controller.getVisibility()==VISIBLE){
              if (LYVPMannger.getInstance().isPlayer()&&LYVPMannger.getInstance().isValid()){
                  upController();
              }
          }
          handler.postDelayed(updataController,UpdataController);
        }
    };

    //todo  UP 更新区域--------------------------------------------------------------------------------------------------------------------------

    /**
     * 绑定 info 传递过来的数据。
     */
    private void bindInfo() {
        Picasso.get().load(info.imgCover).into(imgCover);
        START_SCREEN_TYPE=info.SCREEN_TYPE;
        SCREEN_TYPE=info.SCREEN_TYPE;
        if (SCREEN_TYPE==SCREEN_LIST){
            line_top.setVisibility(GONE);
        }else {
            line_top.setVisibility(VISIBLE);
            img_back.setVisibility(VISIBLE);
            t_title.setText(info.title);
        }
        //设置清晰度
        adapter = new ResolutionAdapter(getContext(), info.getKeys(), info.playkey);
        grid_sharpness.setAdapter(adapter);
    }

    /**
     *更新UI
     */
    private void upController() {
        long position = LYVPMannger.getInstance().getCurrentPosition();
        long duration = LYVPMannger.getInstance().getDuration();
       // Log.e(TAG, "upController: "+position+"-------------------"+duration );
        if (mediacontroller_seekbar != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                mediacontroller_seekbar.setProgress((int) pos);
            }
        }

        if (mediacontroller_time_total != null){
                mediacontroller_time_total.setText(StringUtils.generateTime(duration));}
        if (mediacontroller_time_current != null){
            mediacontroller_time_current.setText(StringUtils.generateTime(position));}
        if (mediacontroller_play_pause != null) {
            if (LYVPMannger.getInstance().isPlayer()) {
                mediacontroller_play_pause.setImageResource(getResources().getIdentifier("mediacontroller_pause", "drawable", getContext().getPackageName()));
            } else {
                mediacontroller_play_pause.setImageResource(getResources().getIdentifier("mediacontroller_play", "drawable", getContext().getPackageName()));
            }

        }
    }
    /**
     * 先显示 后隐藏控制层
     */
    private void showAndDissMissController() {
        if (info!=null&&info.VIDEO_AD==false&&info.IMAGE_AD==false&&isVideo){
            handler.removeCallbacks(dissMIssBootomController);
            l_bottom_controller.setVisibility(VISIBLE);
            handler.postDelayed(dissMIssBootomController,DissMIssBootomController);
            if (SCREEN_TYPE!=SCREEN_LIST){
                line_top.setVisibility(VISIBLE);
            }
        }

    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {//点击的是返回键
            Log.e(TAG, "dispatchKeyEvent: "+SCREEN_TYPE );
            if (SCREEN_TYPE==SCREEN_FULL){
                onBack();
                return false;
            }else {
                LYVPMannger.getInstance().clearVideo();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 全屏改成小屏
     */
    private void onBack() {
        LYVPMannger.getInstance().setFull(false);
        Activity activity= (Activity) getContext();
        SCREEN_TYPE=START_SCREEN_TYPE;
        //ActivityUtlis.showNavigationBar(activity);
        img_full.setImageResource(R.drawable.ic_full);
        //videoView.pause();
        img_start.setVisibility(GONE);
        imgCover.setVisibility(GONE);
        l_controller.setVisibility(GONE);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (START_SCREEN_TYPE==SCREEN_LIST){
            line_top.setVisibility(GONE);

        }else {
            img_back.setVisibility(VISIBLE);
        }
        ViewGroup view = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        view.removeView(re_video);
        this.removeAllViews();
        this.addView(re_video,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        LYVPMannger.getInstance().setVideoLayout(3,0);
        LYVPMannger.getInstance().start();
        img_move.setVisibility(GONE);
    }

    /**
     * 小屏转大屏
     */
    private void onFull(){
        LYVPMannger.getInstance().setFull(true);
        Activity activity= (Activity) getContext();
      //  ActivityUtlis.hideSupportActionBar(activity);
        SCREEN_TYPE=SCREEN_FULL;
        img_back.setVisibility(VISIBLE);
        l_controller.setVisibility(GONE);
        line_top.setVisibility(VISIBLE);
        if (START_SCREEN_TYPE==SCREEN_LIST){
            t_title.setVisibility(GONE);
        }
        img_start.setVisibility(GONE);
        imgCover.setVisibility(GONE);
        img_full.setImageResource(R.drawable.ic_dissmiss_full);
        LYVPMannger.getInstance().setVideoLayout(3,0);
        img_move.setVisibility(VISIBLE);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ViewGroup view = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        this.removeAllViews();
        view.removeView(re_video);
        view.addView(re_video,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
       LYVPMannger.getInstance().start();
    }

    /**
     * 对全屏状态下 触摸事件进行分发
     * @param event
     */
    private void setFullProbar(MotionEvent event) {
        if (SCREEN_TYPE!=1){
            //非全屏状态
            return;
        }
        if (horizontal==-1){
            //todo  凼且仅当 未移动坐标的时候 赋值为-1.
            if (Math.abs(rawX-event.getRawX())==0&&Math.abs(rawY-event.getRawY())==0){
                Log.e(TAG, "setFullProbar: -1" );
                horizontal=-1;
            }else if (Math.abs(rawX-event.getRawX())>Math.abs(rawY-event.getRawY())){
                horizontal=0;
                Log.e(TAG, "setFullProbar: 0" );
                lin_center_pro.setVisibility(VISIBLE);
                t_progress.setVisibility(VISIBLE);
                progress_bar.setVisibility(VISIBLE);
                t_progress.setText(StringUtils.generateTime(LYVPMannger.getInstance().getCurrentPosition())+"");
                progress_bar.setProgress((int) (LYVPMannger.getInstance().getCurrentPosition()*100/LYVPMannger.getInstance().getDuration()));
            }else {
                Log.e(TAG, "setFullProbar: 1" );
                // Log.e(TAG, "setFullProbar: ==1" );
                horizontal=1;
            }
        }
        if (horizontal==0){
            //表示是左右滑动
                float n_x= event.getRawX()-rawX;
                float width = getWidth()*magnification;
                float v_p= (n_x/width)*LYVPMannger.getInstance().getDuration();
                  Log.e(TAG, "setFullProbar: "+n_x+"////////////////////////"+v_p );
                if (n_x<0.0){
                    ismove=true;
                    modify_time = (long) (LYVPMannger.getInstance().getCurrentPosition()+v_p);
                    if (modify_time<=0){
                        modify_time=0;
                    }
                    t_progress.setText(StringUtils.generateTime(modify_time)+"");
                    progress_bar.setProgress((int) (modify_time *100/LYVPMannger.getInstance().getDuration()));
                }else if (n_x>0.0){
                    ismove=true;
                    if (modify_time>=LYVPMannger.getInstance().getDuration()){
                        modify_time=LYVPMannger.getInstance().getDuration();
                    }
                    modify_time = (long) (LYVPMannger.getInstance().getCurrentPosition()+v_p);
                    t_progress.setText(StringUtils.generateTime(modify_time)+"");
                    progress_bar.setProgress((int) (modify_time *100/LYVPMannger.getInstance().getDuration()));
                }else {
                    ismove=false;
                }
        }else if (horizontal==1){
            if (event_y==-1){
                event_y=event.getRawY();
            }
            if (Math.abs(event_y-event.getRawY())<interval_move){
                return;
            }
            //表示是上下滑动
            t_config.setVisibility(VISIBLE);
            if (left){
                //TODO 调整亮度

                if (activity==null){
                    activity = (Activity) getContext();
                }

                if (window==null){
                    window = activity.getWindow();
                }
                WindowManager.LayoutParams params = window.getAttributes();
                if (event_y-event.getRawY()>0){
                    params.screenBrightness=params.screenBrightness+0.05f;
                }else {
                    params.screenBrightness=params.screenBrightness-0.05f;
                }

                if (params.screenBrightness > 1.0f) {
                    params.screenBrightness = 1.0f;
                }
                if (params.screenBrightness <= 0.01f) {
                    params.screenBrightness = 0.01f;
                }
                window.setAttributes(params);
                int pro= (int) (params.screenBrightness*100);
                t_config.setText("亮度："+pro+"%");
            }else {
                //todo 调整声音 如果移动区域小于 35就不那个啥了。
                if (Math.abs(event_y-event.getRawY())<interval_move){
                    return;
                }
                if (mAudioManager != null) {
                    float maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

                    if (nowVolunme==-1){
                        nowVolunme= mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    }
                    if (event_y-event.getRawY()>0){
                        nowVolunme=nowVolunme+0.5f;
                    }else {
                        nowVolunme=nowVolunme-0.5f;
                    }
                    if (nowVolunme>maxVolume){
                        nowVolunme=maxVolume;
                    }
                    if (nowVolunme<0){
                        nowVolunme=0;
                    }
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) nowVolunme, 0);
                    t_config.setText("声音："+nowVolunme);
                }
            }
        }
    }

    /**
     * 显示图片广告
     */
    private void showImageAd() {
        t_ad_time.setVisibility(VISIBLE);
        t_ad_time.setText(info.img_ad_time+" 秒");
        Picasso.get().load(info.imgAD).into(imgCover);
        handler.postDelayed(imgAd,ADIN);
    }
    int ADIN=1000;
    Runnable imgAd=new Runnable() {
        @Override
        public void run() {
            info.img_ad_time--;
            t_ad_time.setText(info.img_ad_time+" 秒");
            if (info.img_ad_time>0){
                handler.postDelayed(imgAd,ADIN);
            }else {
                t_ad_time.setVisibility(GONE);
                imgCover.setVisibility(GONE);
                info.IMAGE_AD=false;
                startADVideo();
            }
        }
    };

    /**
     * 调整屏幕亮度
     * @param progress
     */
    private void updateBrightProgress(int progress) {
        Activity activity = (Activity) getContext();
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.screenBrightness = progress * 1.0f / 100;
        if (params.screenBrightness > 1.0f) {
            params.screenBrightness = 1.0f;
        }
        if (params.screenBrightness <= 0.01f) {
            params.screenBrightness = 0.01f;
        }

        window.setAttributes(params);
        seek_brightness.setProgress(progress);
    }

    /**
     * 显示错误弹窗
     * @param errContent
     */
    private void showDialog(String errContent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setMessage(errContent).setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    /**
     * 调整声音
     * @param progress
     */
    private void updateVolumeProgress(int progress) {
        float percentage = (float) progress / seek_sound.getMax();

        if (percentage < 0 || percentage > 1)
            return;

        if (mAudioManager != null) {
            int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int newVolume = (int) (percentage * maxVolume);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
        }
    }
    LYVideoADCallBack adCallBack;

    public void setAdCallBack(LYVideoADCallBack adCallBack) {
        this.adCallBack = adCallBack;
    }
}
