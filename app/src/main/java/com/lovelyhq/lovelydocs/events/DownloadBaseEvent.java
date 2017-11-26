package com.lovelyhq.lovelydocs.events;

import com.lovelyhq.lovelydocs.models.DocsetVersion;

public class DownloadBaseEvent {
    private DocsetVersion docsetVersion;

    public DownloadBaseEvent(DocsetVersion docsetVersion) {
        this.docsetVersion = docsetVersion;
    }

    public DocsetVersion getDocsetVersion() {
        return this.docsetVersion;
    }

    public void setDocsetVersion(DocsetVersion docsetVersion) {
        this.docsetVersion = docsetVersion;
    }
}
