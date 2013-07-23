package com.bionx.res.wallpaper.koushikdutta.urlimageviewhelper;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public interface UrlImageViewCallback {
    void onLoaded(ImageView imageView, Drawable loadedDrawable, String url, boolean loadedFromCache);
}
