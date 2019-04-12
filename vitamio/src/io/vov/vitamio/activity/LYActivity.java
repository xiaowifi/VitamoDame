package io.vov.vitamio.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import io.vov.vitamio.Bean.VideoInfo;
import io.vov.vitamio.LY.LYVPMannger;
import io.vov.vitamio.LY.LYVideoView;
import io.vov.vitamio.R;

public class LYActivity extends AppCompatActivity {


    private LYVideoView ly_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jz);
        LYVPMannger.getInstance().setTag(this);
        ly_video = findViewById(R.id.ly_video);
        VideoInfo info=new VideoInfo();
        info.playkey="标清";
        info.resolutions.put("标清","http://192.168.0.121:2100/video/20190405/8f4DyBLz/hls/index.m3u8");
        info.resolutions.put("高清","http://192.168.0.121:2100/video/20190405/8f4DyBLz/hls/index.m3u8");
        info.setImgAD("http://192.168.0.121:2100/pic/3.jpg");
        info.imgCover="http://192.168.0.121:2100/pic/3.jpg";
        info.setVideo_AD("http://192.168.0.121:2100/short/4.mp4");
        ly_video.setVideoUri(info);
        ly_video.startADVideo();
        findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWechatString(v.getContext(),"这个地方是分享到微信的文本内容");
            }
        });
    }
    /**
     * 直接分享文本到微信好友
     *
     * @param context 上下文
     */
    public static void shareWechatString(Context context, String content) {
        if (true) {
            Intent intent = new Intent();
            ComponentName cop = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(cop);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra("android.intent.extra.TEXT", content);
//            intent.putExtra("sms_body", content);
            intent.putExtra("Kdescription", !TextUtils.isEmpty(content) ? content : "");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "您需要安装微信客户端", Toast.LENGTH_LONG).show();
        }
    }
}
