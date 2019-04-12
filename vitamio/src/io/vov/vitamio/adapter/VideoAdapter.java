package io.vov.vitamio.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.vov.vitamio.Bean.VideoInfo;
import io.vov.vitamio.CallBack.InterceptCallBack;
import io.vov.vitamio.CallBack.LYVideoADCallBack;
import io.vov.vitamio.LY.LYVideoView;
import io.vov.vitamio.R;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    private final LayoutInflater inflater;

    public VideoAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VideoHolder(inflater.inflate(R.layout.item_video,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        VideoHolder videoHolder= (VideoHolder) viewHolder;
        VideoInfo info=new VideoInfo();
        info.playkey="标清";
        info.SCREEN_TYPE=LYVideoView.SCREEN_LIST;//标志是列表播放就没有返回键和 标题栏了
        info.resolutions.put("标清","http://192.168.0.121:2100/video/20190405/8f4DyBLz/hls/index.m3u8");
        info.resolutions.put("高清","http://192.168.0.121:2100/video/20190405/8f4DyBLz/hls/index.m3u8");
        info.setImgAD("http://192.168.0.121:2100/pic/3.jpg");
        info.imgCover="http://192.168.0.121:2100/pic/3.jpg";
        info.setVideo_AD("http://192.168.0.121:2100/short/4.mp4");
        videoHolder.ly_video.setVideoUri(info);
        videoHolder.ly_video.setAdCallBack(new LYVideoADCallBack() {
            @Override
            public void onADCallBack() {
                //你点击了广告
            }
        });
        //这个是点击播放按钮拦截事件
        videoHolder.ly_video.setInterceptCallBack(new InterceptCallBack() {
            @Override
            public void onStartClick() {
                //这个播放有广告的
               // videoHolder.ly_video.startADVideo();
                //这个是播放没有视频广告的。
                videoHolder.ly_video.startVideo();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }
    class VideoHolder extends RecyclerView.ViewHolder{

        public  LYVideoView ly_video;

        public VideoHolder(@NonNull View itemView) {
            super(itemView);
            ly_video = itemView.findViewById(R.id.ly_recy_video);
        }
    }
}
