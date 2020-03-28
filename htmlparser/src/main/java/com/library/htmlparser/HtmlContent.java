package com.library.htmlparser;

public class HtmlContent {
    private String htmlContent;
    private String baseUrl;
    private String endPoint;
    private String initialElementTagId;

    public String getHtmlContent() {
        return htmlContent;
    }

    private void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    private void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getEndPoint() {
        return endPoint;
    }

    private void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getInitialElementTagId() {
        return initialElementTagId;
    }

    private void setInitialElementTagId(String initialElementTagId) {
        this.initialElementTagId = initialElementTagId;
    }

    private HtmlContent() {
        // empty private constructor
    }

    public static class Builder {
        private String htmlContent = null;
        private String baseUrl = null;
        private String endPoint = null;
        private String initialElementTagId = null;

        public Builder(String content) {
            this.htmlContent = content;
        }

        public Builder withBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder withEndPoint(String endPoint) {
            this.endPoint = endPoint;
            return this;
        }

        public Builder withInitialElementTagId(String initialElementTagId) {
            this.initialElementTagId = initialElementTagId;
            return this;
        }

        public HtmlContent build() {
            HtmlContent htmlContent = new HtmlContent();
            htmlContent.setHtmlContent(this.htmlContent);
            htmlContent.setBaseUrl(this.baseUrl);
            htmlContent.setEndPoint(this.endPoint);
            htmlContent.setInitialElementTagId(this.initialElementTagId);
            return htmlContent;
        }
    }
}