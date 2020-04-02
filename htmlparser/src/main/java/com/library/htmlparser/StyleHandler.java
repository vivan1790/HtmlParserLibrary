package com.library.htmlparser;

import android.content.Context;

import org.jsoup.nodes.Element;

class StyleHandler {

    private Context context;
    private String styleToken;

    StyleHandler(Context context, String styleToken) {
        this.context = context;
        this.styleToken = styleToken;
    }

    int findStyleResourceId(Element element) {
        int id = 0;
        if (styleToken != null && !styleToken.isEmpty()) {
            id = findStyleResourceId(SearchType.TAG_ID, element.id(), true);
            if (id == 0) {
                id = findStyleResourceId(SearchType.TAG_CLASS, element.className(), true);
            }
            if (id == 0) {
                id = findStyleResourceId(SearchType.TAG_NAME, element.tagName(), true);
            }
        }
        if (id == 0) {
            id = findStyleResourceId(SearchType.TAG_ID, element.id(), false);
            if (id == 0) {
                id = findStyleResourceId(SearchType.TAG_CLASS, element.className(), false);
            }
            if (id == 0) {
                id = findStyleResourceId(SearchType.TAG_NAME, element.tagName(), false);
            }
        }
        return id;
    }

    int findStyleResourceId(SearchType searchType, String searchParam,
                                    boolean searchByStyleToken) {
        StringBuilder resName = new StringBuilder();
        if (searchByStyleToken) {
            resName.append(styleToken).append("__");
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

    int getDefaultTextStyleResId() {
        StringBuilder resName = new StringBuilder();
        if (styleToken != null && !styleToken.isEmpty()) {
            resName.append(styleToken).append("__");
        }
        resName.append("textStyleDefault");
        int resId = context.getResources().getIdentifier(resName.toString(), "style",
                context.getPackageName());
        if (resId != 0) return resId;
        else return context.getResources().getIdentifier("textStyleDefault", "style",
                context.getPackageName());
    }

}
