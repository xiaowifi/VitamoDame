package io.vov.vitamio.LY;

/**
 * 播放器前添加播放器子项，然后处理之前的播放器
 */
public class LYVideoMannger {
   static LYVideoMannger mannger;

    private LYVideoMannger() {
    }
    public static LYVideoMannger init(){
        if (mannger==null){
            mannger=new LYVideoMannger();
        }
        return mannger;
    }

    /**
     * 停止所有播放器，同时播放器还原。
     */
    public void stopAll(){

    }
}
