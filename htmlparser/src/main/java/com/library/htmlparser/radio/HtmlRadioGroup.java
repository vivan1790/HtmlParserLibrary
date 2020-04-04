package com.library.htmlparser.radio;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

public class HtmlRadioGroup extends RadioGroup {

    private List<CompoundButton> buttons = new ArrayList<>();

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

    public int getIndexOfSelection() {
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).isChecked()) return i;
        }
        return -1;
    }
}
