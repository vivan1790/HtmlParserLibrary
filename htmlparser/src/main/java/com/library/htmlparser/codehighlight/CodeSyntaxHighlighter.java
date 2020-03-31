package com.library.htmlparser.codehighlight;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.kbiakov.codeview.highlight.CodeHighlighter;
import io.github.kbiakov.codeview.highlight.ColorThemeData;
import io.github.kbiakov.codeview.highlight.SyntaxColors;

public class CodeSyntaxHighlighter extends AsyncTask<String, Void, Spanned> {

    @SuppressLint("StaticFieldLeak")
    private ViewGroup codeLayout;
    private String language;
    private CodeSyntaxTheme codeSyntaxTheme;

    public CodeSyntaxHighlighter(ViewGroup codeLayout, String language,
                                 CodeSyntaxTheme codeSyntaxTheme) {
        this.codeLayout = codeLayout;
        this.language = language;
        this.codeSyntaxTheme = codeSyntaxTheme;
    }

    @Override
    protected Spanned doInBackground(String... strings) {
        SyntaxColors syntaxColors = new SyntaxColors(
                codeSyntaxTheme.typeName, codeSyntaxTheme.keyword, codeSyntaxTheme.literal,
                codeSyntaxTheme.comment, codeSyntaxTheme.string, codeSyntaxTheme.punctuation,
                codeSyntaxTheme.normal, codeSyntaxTheme.tag, codeSyntaxTheme.declaration,
                codeSyntaxTheme.attrName, codeSyntaxTheme.attrValue);
        ColorThemeData colorThemeData = new ColorThemeData(
                syntaxColors, codeSyntaxTheme.literal, codeSyntaxTheme.background,
                codeSyntaxTheme.background, codeSyntaxTheme.normal);
        SpannableStringBuilder codeText = new SpannableStringBuilder();
        String codeHtml = CodeHighlighter.INSTANCE.highlight(language, strings[0], colorThemeData);
        for (String line : codeHtml.split(System.lineSeparator())) {
            codeText.append(Html.fromHtml(
                    line.replaceAll(" ", "\uFEFF "), Html.FROM_HTML_MODE_LEGACY))
                    .append(System.lineSeparator());
        }
        return codeText;
    }

    @Override
    protected void onPostExecute(Spanned spanned) {
        super.onPostExecute(spanned);
        TextView codeTextView = new TextView(codeLayout.getContext());
        codeTextView.setText(spanned);
        codeLayout.addView(codeTextView);
        codeLayout.setBackgroundColor(getApplicableColor(codeSyntaxTheme.background));
    }

    private int getApplicableColor(int value) {
        String hexValue = Integer.toHexString(value);
        return Color.parseColor("#" + hexValue);
    }
}
