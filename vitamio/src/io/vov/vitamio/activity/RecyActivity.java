package io.vov.vitamio.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import io.vov.vitamio.LY.LYVPMannger;
import io.vov.vitamio.LY.LYVideoView;
import io.vov.vitamio.R;
import io.vov.vitamio.adapter.VideoAdapter;

public class RecyActivity extends AppCompatActivity {

    private RecyclerView recy_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recy);
        recy_video = findViewById(R.id.recy_video);
        recy_video.setLayoutManager(new LinearLayoutManager(this));
        VideoAdapter adapter=new VideoAdapter(this);
        recy_video.setAdapter(adapter);
        //这个用于管理播放器
        LYVPMannger.getInstance().setRecyleView(recy_video);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LYVPMannger.getInstance().onBack();
    }
}
