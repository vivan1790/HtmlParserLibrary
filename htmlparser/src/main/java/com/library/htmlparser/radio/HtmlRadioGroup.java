package com.library.htmlparser.radio;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import java.util.HashSet;
import java.util.Set;

public class HtmlRadioGroup extends RadioGroup {

    private Set<CompoundButton> buttons = new HashSet<>();

    public HtmlRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void registerCompoundButton(CompoundButton button) {
        buttons.add(button);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    for (CompoundButton cb : buttons) {
                        if (!cb.equals(compoundButton)) {
                            cb.setChecked(false);
                        }
                    }
                }
            }
        });
    }
}
