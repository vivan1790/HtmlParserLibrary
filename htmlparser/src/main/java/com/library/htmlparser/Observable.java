package com.library.htmlparser;

public interface Observable<T> {

    void registerOnParsingListener(T observer);

    void unRegisterOnParsingListener(T observer);
}
