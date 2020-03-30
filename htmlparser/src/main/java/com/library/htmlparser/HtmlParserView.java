package com.library.htmlparser;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.library.htmlparser.codehighlight.CodeSyntaxHighlighter;
import com.library.htmlparser.codehighlight.CodeSyntaxTheme;
import com.library.htmlparser.iframe.HtmlIFrameLayout;
import com.library.htmlparser.image.HtmlImageView;
import com.library.htmlparser.radio.HtmlRadioGroup;
import com.library.htmlparser.radio.HtmlRadioLabelLayout;
import com.library.htmlparser.table.TableTagLayout;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HtmlParserView extends LinearLayout
        implements Observable<HtmlParserView.OnParsingListener>, SearchableByHtmlTags {

    public interface OnParsingListener {
        void onParsingStarted(HtmlParserView parserView);
        
        void onParsingSuccessful(HtmlParserView parserView);
        
        void onParsingFailed(HtmlParserView parserView, String errorMessage);
    }

    private enum SearchType {
        TAG_ID, TAG_CLASS, TAG_NAME
    }

    private HtmlContent currentHtmlContent;
    private String themeTokenName = null;

    private Set<OnParsingListener> listeners = new HashSet<>();

    private Set<String> imageUrls = new HashSet<>();
    private Set<String> radioGroupClasses = null;
    private List<ViewGroup> viewGroupList = new ArrayList<>();

    private CodeSyntaxTheme codeSyntaxTheme = CodeSyntaxTheme.DEFAULT;

    public HtmlParserView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public HtmlParserView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, @Nullable AttributeSet attrs) {
        this.setOrientation(LinearLayout.VERTICAL);
        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.HtmlParserView);
        themeTokenName = values.getString(R.styleable.HtmlParserView_themeToken);
        values.recycle();
    }

    public void clear() {
        if (imageUrls != null) imageUrls.clear();
        this.removeAllViewsInLayout();
        this.removeAllViews();
    }

    public void parseHTMLContent(HtmlContent htmlContent) {
        clear();
        if (listeners != null) {
            for (OnParsingListener listener : listeners) {
                listener.onParsingStarted(this);
            }
        }
        if (htmlContent == null || htmlContent.getHtmlContent() == null) {
            if (listeners != null) {
                for (OnParsingListener listener : listeners) {
                    listener.onParsingFailed(this,
                            getContext().getString(R.string.error_html_null_or_empty));
                }
            }
            return;
        }
        this.currentHtmlContent = htmlContent;
        try {
            Document document = Jsoup.parse(htmlContent.getHtmlContent());
            Element initialElement = (htmlContent.getInitialElementTagId() != null) ?
                    document.getElementById(htmlContent.getInitialElementTagId()) : document.body();
            parse(initialElement, this);
            if (listeners != null) {
                for (OnParsingListener listener : listeners) {
                    listener.onParsingSuccessful(this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listeners != null) {
                for (OnParsingListener listener : listeners) {
                    listener.onParsingFailed(this, e.getLocalizedMessage());
                }
            }
        }
    }

    private void parse(Element element, final ViewGroup parent) {
        if (element.childNodeSize() > 0) {
            for (Node node : element.childNodes()) {
                if (node instanceof Element) {
                    Element nodeElement = (Element) node;
                    if (isRadioGroupClass(nodeElement)) {
                        HtmlRadioGroup layout = new HtmlRadioGroup(
                                getThemeWrappedContext(nodeElement, parent), null);
                        parseRadioGroupElement((Element) node, layout);
                        parent.addView(layout);
                        layout.setTag(nodeElement);
                    } else if (nodeElement.isBlock()) {
                        parseBlockElement(nodeElement, parent);
                    } else {
                        parseNonBlockElement(nodeElement, parent);
                    }
                } else {
                    SpannableStringBuilder nodeText = new SpannableStringBuilder(node.outerHtml());
                    addTextInParent(nodeText, parent);
                }
            }
        }
    }

    private void addTextInParent(Spanned spannedText, final ViewGroup parent) {
        int childCount = parent.getChildCount();
        if (childCount == 0 ||
                !(parent.getChildAt(childCount - 1) instanceof TextView)) {
            if (!spannedText.toString().trim().isEmpty()) {
                TextView textView = new TextView(parent.getContext(), null, 0);
                textView.setBackgroundColor(Color.TRANSPARENT);
                parent.addView(textView);
                textView.setText(spannedText);
            }
        } else {
            TextView textView = (TextView) parent.getChildAt(childCount - 1);
            SpannableStringBuilder text =
                    new SpannableStringBuilder(textView.getText());
            text.append(spannedText);
            textView.setText(text);
        }
    }

    private void parseBlockElement(Element element, final ViewGroup parent) {
        Context context = getThemeWrappedContext(element, parent);
        ViewGroup layout;
        switch (element.tagName()) {
            case "li":
                StringBuilder indexSB = new StringBuilder();
                if ("ol".equals(element.parentNode().nodeName())) {
                    int liSiblingsCount = 0;
                    for (Node sibling : element.parentNode().childNodes()) {
                        if ("li".equals(sibling.nodeName())) {
                            liSiblingsCount++;
                        }
                        if (sibling.equals(element)) break;
                    }
                    indexSB.append(liSiblingsCount).append('.');
                } else {
                    indexSB.append('\u2022');
                }
                LinearLayout liLayout = new LinearLayout(context, null, 0);
                liLayout.setOrientation(LinearLayout.HORIZONTAL);
                TextView itemIndexView = new TextView(context, null, 0);
                itemIndexView.setText(indexSB);
                layout = new LinearLayout(context, null, 0);
                ((LinearLayout) layout).setOrientation(LinearLayout.VERTICAL);
                parse(element, layout);
                liLayout.addView(itemIndexView);
                liLayout.addView(layout);
                parent.addView(liLayout);
                layout.setTag(element);
                break;
            case "table":
                layout = new FrameLayout(context, null, 0);
                parse(element, layout);
                parent.addView(layout);
                layout.setTag(element);
                break;
            case "tbody":
                layout = new TableTagLayout(context, null);
                parse(element, layout);
                parent.addView(layout);
                layout.setTag(element);
                break;
            case "tr":
                layout = new TableRow(context, null);
                parse(element, layout);
                parent.addView(layout);
                layout.setTag(element);
                break;
            case "th":
            case "td":
                layout = new LinearLayout(context, null, 0);
                ((LinearLayout) layout).setOrientation(LinearLayout.VERTICAL);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                if (!element.attr("colspan").isEmpty()) {
                    layoutParams.span = Integer.parseInt(element.attr("colspan"));
                }
                layout.setLayoutParams(layoutParams);
                parse(element, layout);
                parent.addView(layout);
                layout.setTag(element);
                break;
            default:
                layout = new LinearLayout(context, null, 0);
                ((LinearLayout) layout).setOrientation(LinearLayout.VERTICAL);
                parse(element, layout);
                parent.addView(layout);
                layout.setTag(element);
        }
    }

    private void parseNonBlockElement(Element element, final ViewGroup parent) {
        Context context = getThemeWrappedContext(element, parent);
        SpannableStringBuilder nodeText = new SpannableStringBuilder();
        switch(element.tagName()) {
            case "br":
            case "hr":
                break;
            case "iframe":
                HtmlIFrameLayout iFrameLayout = new HtmlIFrameLayout(context, null, 0);
                parent.addView(iFrameLayout);
                iFrameLayout.setTag(element);
                iFrameLayout.loadData(element);
                break;
            case "img":
                ImageView imageView = new HtmlImageView(context, null, 0);
                parent.addView(imageView);
                String imageUrl = getAbsoluteUrl(element.attr("src"));
                Picasso.get().load(imageUrl).into(imageView);
                imageUrls.add(imageUrl);
                break;
            case "code":
                SpannableStringBuilder codeSpanned = new SpannableStringBuilder();
                if (element.className().isEmpty()) {
                    codeSpanned.append(element.text());
                    ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(
                            codeSyntaxTheme.getUnclassifiedTextColor());
                    codeSpanned.setSpan(foregroundSpan, 0, codeSpanned.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else if (element.className().contains("language")) {
                    addTextInParent(nodeText, parent);
                    ViewGroup codeLayout = new LinearLayout(context, null, 0);
                    new CodeSyntaxHighlighter(codeLayout,
                            element.className().replace("language-", ""),
                            codeSyntaxTheme).execute(element.text());
                    parent.addView(codeLayout);
                    codeLayout.setTag(element);
                    break;
                } else {
                    codeSpanned.append(element.text());
                }
                nodeText.append(codeSpanned);
            default:
                addTextInParent(nodeText, parent);
        }
    }

    private void parseRadioGroupElement(Element element, final HtmlRadioGroup parent) {
        if (element.childNodeSize() > 0) {
            Node inputNode = null, labelNode = null;
            for (Node node : element.childNodes()) {
                if (node.nodeName().equals("input")) {
                    inputNode = node;
                } else if (node.nodeName().equals("label")) {
                    labelNode = node;
                } else if (node instanceof Element && ((Element) node).isBlock()) {
                    parseRadioGroupElement((Element) node, parent);
                }
            }
            if (inputNode != null && labelNode != null
                    && labelNode.attr("for").equals(inputNode.attr("id"))) {
                final HtmlRadioLabelLayout labelLayout = new HtmlRadioLabelLayout(parent.getContext());
                parent.addView(labelLayout);
                parent.registerCompoundButton(labelLayout.getRadioButton());
                SpannableStringBuilder labelText = new SpannableStringBuilder(
                        Html.fromHtml(labelNode.outerHtml(), Html.FROM_HTML_MODE_LEGACY,
                                new Html.ImageGetter() {
                                    @Override
                                    public Drawable getDrawable(String str) {
                                        labelLayout.addImage(getAbsoluteUrl(str));
                                        return new ShapeDrawable();
                                    }
                                }, null));
                labelLayout.setLabel(labelText);
            }
        }
    }

    private boolean isRadioGroupClass(Element element) {
        return (radioGroupClasses != null && (radioGroupClasses.contains(element.className())));
    }

    private Context getThemeWrappedContext(Element element, final ViewGroup parent) {
        Context context = parent.getContext();
        int id = 0;
        if (themeTokenName != null && !themeTokenName.isEmpty()) {
            id = findStyleResId(context, SearchType.TAG_ID, element.id(), true);
            if (id == 0) {
                id = findStyleResId(context, SearchType.TAG_CLASS, element.className(), true);
            }
            if (id == 0) {
                id = findStyleResId(context, SearchType.TAG_NAME, element.tagName(), true);
            }
        }
        if (id == 0) {
            id = findStyleResId(context, SearchType.TAG_ID, element.id(), false);
            if (id == 0) {
                id = findStyleResId(context, SearchType.TAG_CLASS, element.className(), false);
            }
            if (id == 0) {
                id = findStyleResId(context, SearchType.TAG_NAME, element.tagName(), false);
            }
        }
        if (id != 0) {
            context = new ContextThemeWrapper(parent.getContext(), id);
        }
        return context;
    }

    private int findStyleResId(Context context, SearchType searchType,
                               String searchParam, boolean searchByThemToken) {
        StringBuilder resName = new StringBuilder();
        if (searchByThemToken) {
            resName.append(themeTokenName).append("__");
        }
        switch (searchType) {
            case TAG_ID:
                resName.append("id_");
                break;
            case TAG_CLASS:
                resName.append("class_");
                break;
            case TAG_NAME:
                resName.append("tag_");
        }
        resName.append(searchParam.replaceAll("-", "_"));
        return context.getResources().getIdentifier(resName.toString(), "style",
                context.getPackageName());
    }

    public Set<String> getImageUrls() {
        return imageUrls;
    }

    public Set<String> getRadioGroupClasses() {
        return radioGroupClasses;
    }

    public void setRadioGroupClasses(Set<String> radioGroupClasses) {
        this.radioGroupClasses = radioGroupClasses;
    }

    @Override
    public void registerOnParsingListener(OnParsingListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unRegisterOnParsingListener(OnParsingListener listener) {
        listeners.remove(listener);
    }

    @Override
    public List<ViewGroup> findViewGroupsByHtmlTagName(String tagName) {
        viewGroupList.clear();
        traverseAndAddToList(SearchType.TAG_NAME, tagName, this);
        return viewGroupList;
    }

    @Override
    public List<ViewGroup> findViewGroupsByHtmlTagId(String id) {
        viewGroupList.clear();
        traverseAndAddToList(SearchType.TAG_ID, id, this);
        return viewGroupList;
    }

    @Override
    public List<ViewGroup> findViewGroupsByHtmlTagClassName(String className) {
        viewGroupList.clear();
        traverseAndAddToList(SearchType.TAG_CLASS, className, this);
        return viewGroupList;
    }

    public void setCodeSyntaxTheme(CodeSyntaxTheme codeSyntaxTheme) {
        this.codeSyntaxTheme = codeSyntaxTheme;
    }

    private void traverseAndAddToList(SearchType searchType, String searchParam,
                                      ViewGroup viewGroup) {
        if (viewGroup.getTag() != null && viewGroup.getTag() instanceof Element) {
            Element element = (Element) viewGroup.getTag();
            switch (searchType) {
                case TAG_NAME:
                    if (searchParam.equals(element.tagName())) {
                        viewGroupList.add(viewGroup);
                    }
                    break;
                case TAG_ID:
                    if (searchParam.equals(element.id())) {
                        viewGroupList.add(viewGroup);
                    }
                    break;
                case TAG_CLASS:
                    if (searchParam.equals(element.className())) {
                        viewGroupList.add(viewGroup);
                    }
                    break;
            }
        }
        int childCount = viewGroup.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                if (child instanceof ViewGroup) {
                    traverseAndAddToList(searchType, searchParam, (ViewGroup) child);
                }
            }
        }
    }

    private String getAbsoluteUrl(String str) {
        String imageUrl;
        try {
            URL url = new URL(String.format("%s/%s", currentHtmlContent.getBaseUrl(),
                    currentHtmlContent.getEndPoint()));
            URI uri = url.toURI();
            imageUrl = uri.resolve(str).toString();
        } catch (Exception e) {
            e.printStackTrace();
            imageUrl = str;
            if (!imageUrl.contains("http")) {
                imageUrl = currentHtmlContent.getBaseUrl() + '/' + imageUrl;
            }
        }
        return imageUrl;
    }
}