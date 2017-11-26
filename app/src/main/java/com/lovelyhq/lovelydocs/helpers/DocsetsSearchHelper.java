package com.lovelyhq.lovelydocs.helpers;

import com.lovelyhq.lovelydocs.models.Docset;
import com.lovelyhq.lovelydocs.models.DocsetVersion;
import java.util.ArrayList;
import java.util.List;

public class DocsetsSearchHelper {
    public static List<Docset> performSearch(List<Docset> docsets, String query) {
        String[] queryByWords = query.toLowerCase().split("\\s+");
        List<Docset> docsetsFiltered = new ArrayList<>();
        for (Docset docset : docsets) {
            String content = docset.getName().toLowerCase();
            for (String word : queryByWords) {
                int numberOfMatches = queryByWords.length;
                if (!content.contains(word)) {
                    break;
                }
                if (numberOfMatches - 1 == 0) {
                    docsetsFiltered.add(docset);
                }
            }
        }
        return docsetsFiltered;
    }

    public static List<DocsetVersion> performVersionsSearch(List<DocsetVersion> docsetsVersions, String query) {
        String[] queryByWords = query.toLowerCase().split("\\s+");
        List<DocsetVersion> docsetsFiltered = new ArrayList<>();
        for (DocsetVersion docset : docsetsVersions) {
            if (docset != null && docset.getDocset() != null && docset.getDocset().getName() != null) {
                String content = docset.getDocset().getName().toLowerCase() + " " + docset.getVersion();
                for (String word : queryByWords) {
                    int numberOfMatches = queryByWords.length;
                    if (!content.contains(word)) {
                        break;
                    }
                    if (numberOfMatches - 1 == 0) {
                        docsetsFiltered.add(docset);
                    }
                }
            }
        }
        return docsetsFiltered;
    }
}
