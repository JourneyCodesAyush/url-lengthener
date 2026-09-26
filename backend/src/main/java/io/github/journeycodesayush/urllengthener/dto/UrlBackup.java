package io.github.journeycodesayush.urllengthener.dto;

import java.util.List;

public class UrlBackup {
    private int version;
    private List<Url> urls;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<Url> getUrls() {
        return urls;
    }

    public void setUrls(List<Url> urls) {
        this.urls = urls;
    }
}
