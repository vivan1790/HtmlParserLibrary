package com.library.htmlparser.radio;

import android.content.Context;
import android.text.Spanned;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.library.htmlparser.image.HtmlImageView;
import com.squareup.picasso.Picasso;

public class HtmlRadioLabelLayout extends LinearLayout implements Checkable {

    private RadioButton radioButton;

    public HtmlRadioLabelLayout(Context context) {
        super(context);
        this.setOrientation(LinearLayout.HORIZONTAL);
        radioButton = new RadioButton(context);
        addView(radioButton);
    }

    public RadioButton getRadioButton() {
        return radioButton;
    }

    public void setLabel(Spanned labelText) {
        radioButton.setText(labelText);
    }

    public void addImage(String url) {
        ImageView imageView = new HtmlImageView(getContext(), null, 0);
        Picasso.get().load(url).into(imageView);
        addView(imageView);
    }

    @Override
    public void setChecked(boolean checked) {
        radioButton.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return radioButton.isChecked();
    }

    @Override
    public void toggle() {
        radioButton.setChecked(!isChecked());
    }
}
