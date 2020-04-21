package com.library.htmlparser;

import com.library.htmlparser.codehighlight.CodeSyntaxTheme;

import java.util.Set;

public class HtmlContent {
    private String htmlText;
    private String baseUrl;
    private String path;
    private String initialElementTagId;
    private CodeSyntaxTheme codeSyntaxTheme;
    private Set<String> radioGroupClasses;
    private float textScaleFactor;
    private boolean offline;
    private String styleToken;

    public String getHtmlText() {
        return htmlText;
    }

    private void setHtmlText(String htmlContent) {
        this.htmlText = htmlContent;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    private void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPath() {
        return path;
    }

    private void setPath(String path) {
        this.path = path;
    }

    public String getInitialElementTagId() {
        return initialElementTagId;
    }

    private void setInitialElementTagId(String initialElementTagId) {
        this.initialElementTagId = initialElementTagId;
    }

    public String getStyleToken() {
        return styleToken;
    }

    private void setStyleToken(String styleToken) {
        this.styleToken = styleToken;
    }

    public CodeSyntaxTheme getCodeSyntaxTheme() {
        return codeSyntaxTheme;
    }

    private void setCodeSyntaxTheme(CodeSyntaxTheme codeSyntaxTheme) {
        this.codeSyntaxTheme = codeSyntaxTheme;
    }

    public Set<String> getRadioGroupClasses() {
        return radioGroupClasses;
    }

    private void setRadioGroupClasses(Set<String> radioGroupClasses) {
        this.radioGroupClasses = radioGroupClasses;
    }

    public float getTextScaleFactor() {
        return textScaleFactor;
    }

    private void setTextScaleFactor(float textScaleFactor) {
        this.textScaleFactor = textScaleFactor;
    }

    public boolean isContentOffline() {
        return offline;
    }

    private void setContentOffline(boolean offline) {
        this.offline = offline;
    }

    private HtmlContent() {
        // empty private constructor
    }

    public static class Builder {
        private String htmlText;
        private String baseUrl = null;
        private String path = null;
        private String initialElementTagId = null;
        private CodeSyntaxTheme codeSyntaxTheme = CodeSyntaxTheme.DEFAULT;
        private Set<String> radioGroupClasses = null;
        private float textScaleFactor = 1.0f;
        private boolean offlineContent = false;
        private String styleToken = null;

        public Builder(String htmlText) {
            this.htmlText = htmlText;
        }

        public Builder withBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder withPath(String endPoint) {
            this.path = endPoint;
            return this;
        }

        public Builder withInitialElementTagId(String initialElementTagId) {
            this.initialElementTagId = initialElementTagId;
            return this;
        }

        public Builder withStyleToken(String styleToken) {
            this.styleToken = styleToken;
            return this;
        }

        public Builder withCodeSyntaxTheme(CodeSyntaxTheme codeSyntaxTheme) {
            this.codeSyntaxTheme = codeSyntaxTheme;
            return this;
        }

        public Builder withRadioGroupClasses(Set<String> radioGroupClasses) {
            this.radioGroupClasses = radioGroupClasses;
            return this;
        }

        public Builder withTextScaleFactor(float textScaleFactor) {
            this.textScaleFactor = textScaleFactor;
            return this;
        }

        public Builder withContentOffline(boolean offlineContent) {
            this.offlineContent = offlineContent;
            return this;
        }

        public HtmlContent build() {
            HtmlContent htmlContent = new HtmlContent();
            htmlContent.setHtmlText(this.htmlText);
            htmlContent.setBaseUrl(baseUrl);
            htmlContent.setPath(path);
            htmlContent.setInitialElementTagId(initialElementTagId);
            htmlContent.setStyleToken(styleToken);
            htmlContent.setCodeSyntaxTheme(codeSyntaxTheme);
            htmlContent.setRadioGroupClasses(radioGroupClasses);
            htmlContent.setTextScaleFactor(textScaleFactor);
            htmlContent.setContentOffline(offlineContent);
            return htmlContent;
        }
    }
}