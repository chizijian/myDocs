package com.lovelyhq.lovelydocs.events;

import com.lovelyhq.lovelydocs.models.DocsetVersion;

public class DownloadCompletedEvent extends DownloadBaseEvent {
    public DownloadCompletedEvent(DocsetVersion docsetVersion) {
        super(docsetVersion);
    }
}
