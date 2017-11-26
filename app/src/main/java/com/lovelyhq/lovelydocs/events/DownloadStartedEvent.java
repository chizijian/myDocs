package com.lovelyhq.lovelydocs.events;

import com.lovelyhq.lovelydocs.models.DocsetVersion;

public class DownloadStartedEvent extends DownloadBaseEvent {
    public DownloadStartedEvent(DocsetVersion docsetVersion) {
        super(docsetVersion);
    }
}
