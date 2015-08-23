package com.jaouan.snapandmatch.components.models.matches;

import java.io.Serializable;
import java.net.URL;

/**
 * Match model.
 *
 * @author Maxence Jaouan
 */
public class Match implements Serializable {

    /**
     * Match type.
     */
    public enum TYPE {
        WEB, BOOK
    }

    /**
     * Type.
     */
    private TYPE type;

    /**
     * Unescaped URL.
     */
    private String unescapedUrl;

    /**
     * Title.
     */
    private String titleNoFormatting;

    /**
     * Parsed URL.
     */
    private URL parsedUrl;

    /**
     * Relevance.
     */
    private int relevance;

    /**
     * Original URL.
     */
    private String url;

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRelevance() {
        return relevance;
    }

    public void setRelevance(int relevance) {
        this.relevance = relevance;
    }

    public String getUnescapedUrl() {
        return unescapedUrl;
    }

    public void setUnescapedUrl(String unescapedUrl) {
        this.unescapedUrl = unescapedUrl;
    }

    public String getTitleNoFormatting() {
        return titleNoFormatting;
    }

    public void setTitleNoFormatting(String titleNoFormatting) {
        this.titleNoFormatting = titleNoFormatting;
    }

    public URL getParsedUrl() {
        return parsedUrl;
    }

    public void setParsedUrl(URL parsedUrl) {
        this.parsedUrl = parsedUrl;
    }
}
