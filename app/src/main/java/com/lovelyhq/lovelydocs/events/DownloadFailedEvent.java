package com.lovelyhq.lovelydocs.events;

import com.lovelyhq.lovelydocs.models.DocsetVersion;

public class DownloadFailedEvent extends DownloadBaseEvent {
    public DownloadFailedEvent(DocsetVersion docsetVersion) {
        super(docsetVersion);
    }
}
