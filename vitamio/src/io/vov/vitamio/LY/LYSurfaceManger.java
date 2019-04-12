package io.vov.vitamio.LY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

/**
 * surface  单例管理器。
 */
@SuppressLint("NewApi")
public class LYSurfaceManger implements TextureView.SurfaceTextureListener {
    String TAG="LYSurfaceManger";
    static LYSurfaceManger manger;
    public LYTextureView textureView;
    public static SurfaceTexture savedSurfaceTexture;
    public static Surface surface;
    Context context;
    String url;
    private LYSurfaceManger() {
    }

    public static LYSurfaceManger init(){
        if (manger==null){
            manger=new LYSurfaceManger();
        }
        return manger;
    }
    public void setTextureView(LYTextureView textureView,String url) {
        this.textureView = textureView;
        context=textureView.getContext();
        this.url=url;
        this.textureView.setSurfaceTextureListener(this);
    }
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureAvailable: " );
        if (savedSurfaceTexture == null) {
            savedSurfaceTexture = surface;
            prepare();
        } else {
            textureView.setSurfaceTexture(savedSurfaceTexture);
        }
    }

    /**
     *
     */
    private void prepare() {
        LYMediaPlayerManger.init().initMediaPlayer(context,url);
        if (surface==null){
            surface = new Surface(savedSurfaceTexture);
        }
        LYMediaPlayerManger.init().setSurface(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
