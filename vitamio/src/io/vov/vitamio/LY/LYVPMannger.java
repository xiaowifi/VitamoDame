package io.vov.vitamio.LY;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import io.vov.vitamio.widget.TexttureVideoView;

/**
 * luoye video  player  管理器
 * 2019年4月4日15:12:36
 */
public class LYVPMannger  {
    String TAG="LYVPMannger";
    private static LYVPMannger mannger;
    TexttureVideoView videoView;
    //用于存储数据对象。
    private Object object;
    LYLeftlCycle leftlCycle;
    boolean full=false;
    int postion=-1;

    public boolean isFull() {
        return full;
    }
    public void setPostion(int postion) {
        this.postion = postion;
        Log.e(TAG, "setPostion: "+postion );
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    /**
     * 必须从这个地方初始化
     * @return
     */
    public static LYVPMannger getInstance(){
        if (mannger==null){
            mannger=new LYVPMannger();
        }
        return mannger;
    }

    private LYVPMannger() {
    }

    //todo  重要马甲分割线-------------------------------下面是主体方法了。

    public TexttureVideoView getVideoPlayer(Context context, LYLeftlCycle leftlCycle, String path){
        clearVideo();
       this.leftlCycle=leftlCycle;
        videoView = new TexttureVideoView(context);
       // videoView.setTag(object);
        videoView.setVideoURI(Uri.parse(path.trim()));
        videoView.setLeftlCycle(leftlCycle);
        return videoView;
    }
    /**
     * 清空播放器状态等相关信息。
     */
    public void clearVideo() {
        postion=-1;
        if (videoView!=null){
            videoView.stopPlayback();
            videoView=null;
        }
        //移除生命周期监听。
       if (leftlCycle!=null){
           leftlCycle.onRestore();
           leftlCycle=null;
       }
    }

    /**
     * 是否处于播放状态
     * @return
     */
    public boolean isPlayer(){
        if (videoView!=null){
            return videoView.isPlaying();
        }
        return false;
    }

    /**
     * 缓冲中
     * @return
     */
    public boolean isBuffer(){
        if (videoView!=null){
            return videoView.isBuffering();
        }
        return false;
    }

    /**
     * Surface是否有效
     * @return
     */
    public boolean isValid(){
        if (videoView!=null){
            return videoView.isValid();
        }
        return false;
    }

    /**
     * 获取当前播放的时间进度
     * @return
     */
    public long getCurrentPosition(){
        if (videoView!=null){
           return videoView.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 获取视频的总时长
     * @return
     */
    public long getDuration(){
        if (videoView!=null){
            return videoView.getDuration();
        }
        return 0;
    }

    /**
     * 暂停
     */
    public void pause(){
        if (videoView!=null){
            videoView.pause();
        }
    }

    /**
     * 播放
     */
    public void start(){
        if (videoView!=null){
            videoView.start();
        }
    }

    /**
     * 设置 拖动条 强制 位移
     * @param seek
     */
    public void seekTo(long seek){
        if (videoView!=null){
            videoView.seekTo(seek);
        }
    }

    /**
     * 恢复播放
     */
    public void resume(){
        if (videoView!=null){
            videoView.resume();
        }
    }
    public void setPlaybackSpeed(float speed){
        if (videoView!=null){
            videoView.setPlaybackSpeed(speed);
        }
    }

    /**
     * 设置播放状态
     * @param layout 0.1，2,3,4
     * @param aspectRatio
     */
    public void setVideoLayout(int layout,int aspectRatio){
        if (videoView!=null){
          //  videoView.setVideoLayout(layout,aspectRatio);
        }
    }

    /**
     * activity 的返回键监听
     */
    public boolean onBack(){
        if (videoView!=null&&leftlCycle!=null){
            leftlCycle.onActivityBack();
            return true;
        }
        return false;
    }

    /***
     * 这个用于 activity  中嵌套了fragment 的情况
     * @return
     */
    public boolean onFragmentBack(){
        if (videoView!=null&&leftlCycle!=null){
            leftlCycle.onFragmentBack();
            return true;
        }
        return false;
    }


    public void setTag(Object object){
        this.object=object;
    }
    public void setRecyleView(RecyclerView recyleView){
        LinearLayoutManager manager= (LinearLayoutManager) recyleView.getLayoutManager();
        recyleView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                int first_postion = manager.findFirstVisibleItemPosition();
                int last_postion = manager.findLastVisibleItemPosition();

                if (full){

                }else {
                    if (postion<first_postion||postion>last_postion){
                        Log.e(TAG, "onChildViewDetachedFromWindow: "+first_postion+"------------------"+last_postion );
                        clearVideo();
                    }
                }
            }
        });
    }
}
