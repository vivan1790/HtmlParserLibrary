package com.library.htmlparser.codehighlight;

import android.os.AsyncTask;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.library.htmlparser.Observable;

import java.util.HashSet;
import java.util.Set;

import io.github.kbiakov.codeview.highlight.CodeHighlighter;
import io.github.kbiakov.codeview.highlight.ColorThemeData;
import io.github.kbiakov.codeview.highlight.SyntaxColors;

public class CodeSyntaxHighlighter extends AsyncTask<String, Void, Spanned>
        implements Observable<CodeHighlightListener> {

    private String language;
    private CodeSyntaxTheme codeSyntaxTheme;
    private Set<CodeHighlightListener> listeners = new HashSet<>();

    public CodeSyntaxHighlighter(String language, CodeSyntaxTheme codeSyntaxTheme) {
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
        for (CodeHighlightListener listener : listeners) {
            listener.onCodeTextAdded(spanned);
        }
    }

    @Override
    public void registerOnParsingListener(CodeHighlightListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unRegisterOnParsingListener(CodeHighlightListener listener) {
        listeners.remove(listener);
    }
}