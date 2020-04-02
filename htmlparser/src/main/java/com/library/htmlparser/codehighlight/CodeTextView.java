package com.library.htmlparser.codehighlight;

import android.content.Context;
import android.text.Spanned;

public class CodeTextView extends androidx.appcompat.widget.AppCompatTextView
        implements CodeHighlightListener {

    public CodeTextView(Context context) {
        super(context);
    }

    @Override
    public void onCodeTextAdded(Spanned codeSpanned) {
        setText(codeSpanned);
    }
}
