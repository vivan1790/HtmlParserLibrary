package com.library.htmlparser.image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

public class ImagePreviewActivity extends AppCompatActivity {

    private static Bitmap sCurrentImageBitmap = null;

    public static void setImageBitmapForPreview(Bitmap bitmap) {
        sCurrentImageBitmap = bitmap;
    }

    public static Intent getCallIntent(Context context) {
        return new Intent(context, ImagePreviewActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.BLACK);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setPadding(10, 10, 10, 10);
        linearLayout.setBackgroundColor(Color.BLACK);
        AppCompatImageView imagePreview = new TouchImageView(this);
        imagePreview.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imagePreview.setBackgroundColor(Color.TRANSPARENT);
        imagePreview.setImageBitmap(sCurrentImageBitmap);
        linearLayout.addView(imagePreview);
        getWindow().setNavigationBarColor(Color.BLACK);
        setContentView(linearLayout);
    }
}
