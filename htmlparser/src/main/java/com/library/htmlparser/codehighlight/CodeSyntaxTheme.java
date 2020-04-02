package com.library.htmlparser.codehighlight;

public enum CodeSyntaxTheme {
    DEFAULT(0x586E75, 0x859900, 0x268BD2, 0x269186, 0x93A1A1,
            0x269186, 0x586E75, 0x859900, 0x268BD2, 0x268BD2,
            0x269186, 0xE9EDF4, 0x586E75),
    DARK(0xF8F8F2, 0xA7E22E, 0xF92672, 0xA078ED, 0x75715E,
            0xE6DB74, 0xF8F8F2, 0xA078ED, 0xDB9E97, 0xA6E22E,
            0xE6DB74, 0x1E2A37, 0xF8F8F2);

    int normal;
    int typeName;
    int keyword;
    int literal;
    int comment;
    int string;
    int punctuation;
    int tag;
    int declaration;
    int attrName;
    int attrValue;
    int background;
    int unclassified;

    public int getUnclassifiedTextColor() {
        return unclassified;
    }

    public int getBackgroundColor() {
        return background;
    }

    CodeSyntaxTheme(int normal, int typeName, int keyword, int literal, int comment,
                    int string, int punctuation, int tag, int declaration, int attrName,
                    int attrValue, int background, int unclassified) {
        this.normal = normal;
        this.typeName = typeName;
        this.keyword = keyword;
        this.literal = literal;
        this.comment = comment;
        this.string = string;
        this.punctuation = punctuation;
        this.tag = tag;
        this.declaration = declaration;
        this.attrName = attrName;
        this.attrValue = attrValue;
        this.background = background;
        this.unclassified = unclassified;
    }

    public static class CodeSyntaxThemeBuilder {
        CodeSyntaxTheme codeSyntaxTheme;

        public CodeSyntaxThemeBuilder() {
            this.codeSyntaxTheme = CodeSyntaxTheme.DEFAULT;
        }

        public CodeSyntaxThemeBuilder(CodeSyntaxTheme codeSyntaxTheme) {
            this.codeSyntaxTheme = codeSyntaxTheme;
        }

        public CodeSyntaxThemeBuilder withNormalTextColor(int normal) {
            codeSyntaxTheme.normal = normal;
            return this;
        }

        public CodeSyntaxThemeBuilder withTypeNameColor(int typeName) {
            codeSyntaxTheme.typeName = typeName;
            return this;
        }

        public CodeSyntaxThemeBuilder withKeywordColor(int keyword) {
            codeSyntaxTheme.keyword = keyword;
            return this;
        }

        public CodeSyntaxThemeBuilder withLiteralColor(int literal) {
            codeSyntaxTheme.literal = literal;
            return this;
        }

        public CodeSyntaxThemeBuilder withCommentColor(int comment) {
            codeSyntaxTheme.comment = comment;
            return this;
        }

        public CodeSyntaxThemeBuilder withStringColor(int string) {
            codeSyntaxTheme.string = string;
            return this;
        }

        public CodeSyntaxThemeBuilder withPunctuationColor(int punctuation) {
            codeSyntaxTheme.punctuation = punctuation;
            return this;
        }

        public CodeSyntaxThemeBuilder withTagColor(int tag) {
            codeSyntaxTheme.tag = tag;
            return this;
        }

        public CodeSyntaxThemeBuilder withDeclarationColor(int declaration) {
            codeSyntaxTheme.declaration = declaration;
            return this;
        }

        public CodeSyntaxThemeBuilder withAttrNameColor(int attrName) {
            codeSyntaxTheme.attrName = attrName;
            return this;
        }

        public CodeSyntaxThemeBuilder withAttrValueColor(int attrValue) {
            codeSyntaxTheme.attrValue = attrValue;
            return this;
        }

        public CodeSyntaxThemeBuilder withBackgroundColor(int background) {
            codeSyntaxTheme.background = background;
            return this;
        }

        public CodeSyntaxThemeBuilder withUnclassifiedColor(int unclassified) {
            codeSyntaxTheme.unclassified = unclassified;
            return this;
        }

        public CodeSyntaxTheme build() {
            return codeSyntaxTheme;
        }
    }

}
