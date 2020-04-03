package com.library.htmlparser;

import android.view.ViewGroup;

import java.util.List;

interface SearchableByHtmlTags {

    List<ViewGroup> findViewGroupsByHtmlTagName(String tagName);

    List<ViewGroup> findViewGroupsByHtmlTagId(String id);

    List<ViewGroup> findViewGroupsByHtmlTagClassName(String className);

}
