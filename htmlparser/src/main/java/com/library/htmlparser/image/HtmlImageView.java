package com.library.htmlparser.image;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class HtmlImageView extends androidx.appcompat.widget.AppCompatImageView {
    public HtmlImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public HtmlImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getDrawable() == null) {
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.VISIBLE);
                    ImagePreviewActivity.setImageBitmapForPreview(
                            ((BitmapDrawable) getDrawable()).getBitmap());
                    getContext().startActivity(ImagePreviewActivity.getCallIntent(getContext()));
                }
            }
        });
    }
}
