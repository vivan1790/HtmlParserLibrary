package com.library.htmlparser.radio;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Spanned;
import android.view.ContextThemeWrapper;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.library.htmlparser.R;
import com.library.htmlparser.common.StyleHandler;
import com.library.htmlparser.image.HtmlImageView;
import com.squareup.picasso.Picasso;

public class HtmlRadioLabelLayout extends LinearLayout implements Checkable {

    private RadioButton radioButton;
    private StyleHandler styleHandler;

    public HtmlRadioLabelLayout(Context context) {
        super(context);
        this.setOrientation(LinearLayout.HORIZONTAL);
        radioButton = new RadioButton(new ContextThemeWrapper(
                context, android.R.style.Widget_DeviceDefault_Light_CompoundButton_RadioButton),
                null, 0);
        int buttonTint = styleHandler.getButtonTintFromStyle(R.style.radioLabelStyleDefault);
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, //unchecked
                        new int[]{ android.R.attr.state_checked}  //checked
                },
                new int[] { buttonTint, buttonTint }
        );
        radioButton.setButtonTintList(colorStateList);
        radioButton.setTextAppearance(R.style.radioLabelStyleDefault);
        addView(radioButton);
    }

    public HtmlRadioLabelLayout(Context context, int textStyleResourceId, String tokenName) {
        super(context);
        this.setOrientation(LinearLayout.HORIZONTAL);
        styleHandler = new StyleHandler(context, tokenName);
        radioButton = new RadioButton(new ContextThemeWrapper(
                context, android.R.style.Widget_DeviceDefault_Light_CompoundButton_RadioButton),
                null, 0);
        int buttonTint = styleHandler.getButtonTintFromStyle(
                styleHandler.getDefaultRadioLabelStyleResId());
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, //unchecked
                        new int[]{ android.R.attr.state_checked}  //checked
                },
                new int[] { buttonTint, buttonTint }
        );
        radioButton.setButtonTintList(colorStateList);
        radioButton.setTextAppearance(textStyleResourceId);
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