package com.library.htmlparser;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.library.htmlparser.codehighlight.CodeSyntaxHighlighter;
import com.library.htmlparser.codehighlight.CodeTextView;
import com.library.htmlparser.common.Observable;
import com.library.htmlparser.common.StyleHandler;
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

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HtmlParser implements Observable<HtmlParser.OnParsingListener>, SearchableByHtmlTags {

    public interface OnParsingListener {

        void onParsingStarted(HtmlParser htmlParser);

        void onParsingSuccessful(HtmlParser htmlParser);

        void onParsingFailed(HtmlParser htmlParser, String errorMessage);
    }

    private LinearLayout mainLayout;
    private HtmlContent currentHtmlContent;
    private StyleHandler styleHandler;
    private Set<OnParsingListener> listeners = new HashSet<>();

    private Set<String> imageUrls = new HashSet<>();
    private List<ViewGroup> viewGroupList = new ArrayList<>();

    public HtmlParser(LinearLayout layout) {
        this.mainLayout = layout;
        mainLayout.setOrientation(LinearLayout.VERTICAL);
    }

    public void parseHTMLContent(HtmlContent htmlContent) {
        clear();
        if (listeners != null) {
            for (OnParsingListener listener : listeners) {
                listener.onParsingStarted(this);
            }
        }
        if (htmlContent == null || htmlContent.getHtmlText() == null) {
            if (listeners != null) {
                for (OnParsingListener listener : listeners) {
                    listener.onParsingFailed(this,
                            getContext().getString(R.string.error_html_null_or_empty));
                }
            }
            return;
        }
        this.currentHtmlContent = htmlContent;
        styleHandler = new StyleHandler(getContext(), currentHtmlContent.getStyleToken());
        try {
            Document document = Jsoup.parse(htmlContent.getHtmlText());
            Element initialElement = (htmlContent.getInitialElementTagId() != null) ?
                    document.getElementById(htmlContent.getInitialElementTagId()) : document.body();
            parse(initialElement, mainLayout);
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

    private void scaleTextSize(TextView textView, int selfStyleResId, int parentStyleResId) {
        if (currentHtmlContent.getTextScaleFactor() != 1.0f) {
            float currentSize = styleHandler.getTextSizeFromStyle(selfStyleResId);
            float parentSize = styleHandler.getTextSizeFromStyle(parentStyleResId);
            if (parentSize != 0) {
                currentSize = parentSize;
            }
            float scaleDensity = getContext().getResources().getDisplayMetrics().scaledDensity;
            float newSize = currentHtmlContent.getTextScaleFactor() * currentSize / scaleDensity;
            textView.setTextSize(newSize);
        }
    }

    private void clear() {
        imageUrls.clear();
        mainLayout.removeAllViewsInLayout();
        mainLayout.removeAllViews();
    }

    private void addTextInParent(Spanned spannedText, final ViewGroup parent,
                                 int parentStyleResId) {
        int childCount = parent.getChildCount();
        if (childCount == 0 ||
                !(parent.getChildAt(childCount - 1) instanceof TextView)) {
            if (!spannedText.toString().trim().isEmpty()) {
                TextView textView = new TextView(getThemeContext(
                        getContext(), styleHandler.getDefaultTextStyleResId()), null, 0);
                textView.setBackgroundColor(Color.TRANSPARENT);
                parent.addView(textView);
                textView.setText(spannedText);
                if (parentStyleResId != 0) {
                    textView.setTextAppearance(parentStyleResId);
                }
                scaleTextSize(textView, styleHandler.getDefaultTextStyleResId(), parentStyleResId);
            }
        } else {
            TextView textView = (TextView) parent.getChildAt(childCount - 1);
            SpannableStringBuilder text =
                    new SpannableStringBuilder(textView.getText());
            text.append(spannedText);
            textView.setText(text);
        }
    }

    private void parse(Element element, final ViewGroup parent) {
        if (element.childNodeSize() > 0) {
            for (Node node : element.childNodes()) {
                if (node instanceof Element) {
                    Element nodeElement = (Element) node;
                    if (isRadioGroupClass(nodeElement)) {
                        int elementStyleResId = styleHandler.findStyleResourceId(nodeElement);
                        HtmlRadioGroup layout = new HtmlRadioGroup(
                                getThemeContext(getContext(), elementStyleResId), null);
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
                    addTextInParent(nodeText, parent, styleHandler.findStyleResourceId(element));
                }
            }
        }
    }

    private void parseBlockElement(Element element, final ViewGroup parent) {
        int elementStyleResId = styleHandler.findStyleResourceId(element);
        Context context = getThemeContext(getContext(), elementStyleResId);
        ViewGroup layout;
        switch (element.tagName()) {
            case "li":
                SpannableStringBuilder indexSB = new SpannableStringBuilder();
                if ("ol".equals(element.parentNode().nodeName())) {
                    int liSiblingsCount = 0;
                    for (Node sibling : element.parentNode().childNodes()) {
                        if ("li".equals(sibling.nodeName())) {
                            liSiblingsCount++;
                        }
                        if (sibling.equals(element)) break;
                    }
                    indexSB.append(String.valueOf(liSiblingsCount)).append('.');
                } else {
                    indexSB.append('\u2022');
                }
                LinearLayout liLayout = new LinearLayout(context, null, 0);
                liLayout.setOrientation(LinearLayout.HORIZONTAL);
                addTextInParent(indexSB, liLayout, elementStyleResId);
                /*TextView itemIndexView = new TextView(context, null, 0);
                itemIndexView.setText(indexSB);
                liLayout.addView(itemIndexView);*/
                layout = new LinearLayout(context, null, 0);
                ((LinearLayout) layout).setOrientation(LinearLayout.VERTICAL);
                parse(element, layout);
                liLayout.addView(layout);
                parent.addView(liLayout);
                layout.setTag(element);
                break;
            case "table":
                layout = new TableTagLayout(context, null);
                parse(element, layout);
                parent.addView(layout);
                layout.setTag(element);
                break;
            case "thead":
            case "tbody":
//                layout = new TableTagLayout(context, null);
//                parse(element, layout);
//                parent.addView(layout);
//                layout.setTag(element);
                parse(element, parent);
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
        int elementStyleResId = styleHandler.findStyleResourceId(element);
        Context context = getThemeContext(getContext(), elementStyleResId);
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
                if (currentHtmlContent.isContentOffline()) {
                    iFrameLayout.setVisibility(View.GONE);
                }
                break;
            case "img":
                ImageView imageView = new HtmlImageView(context, null, 0);
                parent.addView(imageView);
                String imageUrl = getAbsoluteUrl(element.attr("src"));
                if (currentHtmlContent.isContentOffline()) {
                    Picasso.get().load(new File(imageUrl)).into(imageView);
                } else {
                    Picasso.get().load(imageUrl).into(imageView);
                }
                imageUrls.add(imageUrl);
                break;
            case "code":
                SpannableStringBuilder codeSpanned = new SpannableStringBuilder();
                if (element.className().isEmpty()) {
                    codeSpanned.append(element.text());
                    ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(
                            currentHtmlContent.getCodeSyntaxTheme().getUnclassifiedTextColor());
                    codeSpanned.setSpan(foregroundSpan, 0, codeSpanned.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else if (element.className().contains("language")) {
                    addTextInParent(nodeText, parent, elementStyleResId);
                    ViewGroup codeLayout = new LinearLayout(context, null, 0);
                    codeLayout.setBackgroundColor(getApplicableColor(
                            currentHtmlContent.getCodeSyntaxTheme().getBackgroundColor()));
                    CodeSyntaxHighlighter codeSyntaxHighlighter = new CodeSyntaxHighlighter(
                            element.className().replace("language-", ""),
                            currentHtmlContent.getCodeSyntaxTheme());
                    codeSyntaxHighlighter.execute(element.text());
                    CodeTextView codeTextView = new CodeTextView(context);
                    codeSyntaxHighlighter.registerOnParsingListener(codeTextView);
                    codeLayout.addView(codeTextView);
                    scaleTextSize(codeTextView, styleHandler.getDefaultTextStyleResId(),
                            elementStyleResId);
                    parent.addView(codeLayout);
                    codeLayout.setTag(element);
                    break;
                } else {
                    codeSpanned.append(element.text());
                }
                nodeText.append(codeSpanned);
                addTextInParent(nodeText, parent, 0); // elementStyleResId not required
                break;
            default:
                if (elementStyleResId == 0 || styleHandler.getVisibleAttributeFromStyle(
                        elementStyleResId) == StyleHandler.Visibility.VISIBLE) {
                    nodeText.append(Html.fromHtml(element.toString(), Html.FROM_HTML_MODE_LEGACY));
                }
                addTextInParent(nodeText, parent, elementStyleResId);
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
                final HtmlRadioLabelLayout labelLayout = new HtmlRadioLabelLayout(
                        getContext(), styleHandler.getDefaultTextStyleResId(),
                        currentHtmlContent.getStyleToken());
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
                scaleTextSize(labelLayout.getRadioButton(),
                        styleHandler.getDefaultTextStyleResId(), 0);
            }
        }
    }

    private Context getContext() {
        return mainLayout.getContext();
    }

    private Context getThemeContext(Context context, int resourceId) {
        return new ContextThemeWrapper(context, resourceId);
    }

    private boolean isRadioGroupClass(Element element) {
        return (currentHtmlContent.getRadioGroupClasses() != null
                && (currentHtmlContent.getRadioGroupClasses().contains(element.className())));
    }

    public Set<String> getImageUrls() {
        return imageUrls;
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
        traverseAndAddToList(SearchType.TAG_NAME, tagName, mainLayout);
        return viewGroupList;
    }

    @Override
    public List<ViewGroup> findViewGroupsByHtmlTagId(String id) {
        viewGroupList.clear();
        traverseAndAddToList(SearchType.TAG_ID, id, mainLayout);
        return viewGroupList;
    }

    @Override
    public List<ViewGroup> findViewGroupsByHtmlTagClassName(String className) {
        viewGroupList.clear();
        traverseAndAddToList(SearchType.TAG_CLASS, className, mainLayout);
        return viewGroupList;
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
                    if (searchParam.equals(element.className())
                            || element.className().contains(searchParam)) {
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
                    currentHtmlContent.getPath()));
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

    private int getApplicableColor(int value) {
        String hexValue = Integer.toHexString(value);
        return Color.parseColor("#" + hexValue);
    }
}