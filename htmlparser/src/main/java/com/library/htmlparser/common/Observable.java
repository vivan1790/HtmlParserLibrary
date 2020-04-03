package com.library.htmlparser.common;

public interface Observable<T> {

    void registerOnParsingListener(T observer);

    void unRegisterOnParsingListener(T observer);
}
