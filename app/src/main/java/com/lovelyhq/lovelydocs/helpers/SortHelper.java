package com.lovelyhq.lovelydocs.helpers;

import com.lovelyhq.lovelydocs.models.TypeItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SortHelper {
    private List<TypeItem> mItems;
    private String mSearchQuery;

    public SortHelper(List<TypeItem> items, String searchQuery) {
        this.mItems = items;
        this.mSearchQuery = searchQuery;
    }

    public static String toCamelCase(String s) {
        String camelCaseString = "";
        for (String part : s.split(" ")) {
            camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString;
    }

    public static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public List<TypeItem> sort() {
        List<TypeItem> perfectMatches = extractPerfectMatches();
        List<TypeItem> prefixes = extractPrefixes();
        List<TypeItem> suffixes = extractSuffixes();
        List<TypeItem> containsMatches = extractContainsMatches();
        List<TypeItem> camelCaseMatches = extractCamelCaseMatches();
        List<TypeItem> finalResult = new ArrayList<>();
        finalResult.addAll(perfectMatches);
        finalResult.addAll(prefixes);
        finalResult.addAll(suffixes);
        finalResult.addAll(containsMatches);
        finalResult.addAll(camelCaseMatches);
        Collections.sort(this.mItems, new Comparator<TypeItem>() {
            public int compare(TypeItem lhs, TypeItem rhs) {
                return SortHelper.this.levenshteinDistance(lhs.getName(), SortHelper.this.mSearchQuery) - SortHelper.this.levenshteinDistance(rhs.getName(), SortHelper.this.mSearchQuery);
            }
        });
        finalResult.addAll(this.mItems);
        return finalResult;
    }

    private void removeMatchesNotContainingAllLetters() {
        Iterator<TypeItem> iterator = this.mItems.iterator();
        while (iterator.hasNext()) {
            TypeItem item =  iterator.next();
            for (char c : this.mSearchQuery.toCharArray()) {
                if (!item.getName().contains(Character.toString(c))) {
                    iterator.remove();
                }
            }
        }
    }

    private List<TypeItem> extractPerfectMatches() {
        List<TypeItem> perfectMatches = new ArrayList();
        Iterator<TypeItem> iterator = this.mItems.iterator();
        while (iterator.hasNext()) {
            TypeItem item =  iterator.next();
            if (item.getName().equalsIgnoreCase(this.mSearchQuery)) {
                perfectMatches.add(item);
                iterator.remove();
            }
        }
        return perfectMatches;
    }

    private List<TypeItem> extractPrefixes() {
        List<TypeItem> prefixes = new ArrayList<>();
        Iterator<TypeItem> iterator = this.mItems.iterator();
        while (iterator.hasNext()) {
            TypeItem item =  iterator.next();
            if (item.getName().toLowerCase().startsWith(this.mSearchQuery.toLowerCase())) {
                prefixes.add(item);
                iterator.remove();
            }
        }
        return prefixes;
    }

    private List<TypeItem> extractSuffixes() {
        List<TypeItem> sufixes = new ArrayList<>();
        Iterator<TypeItem> iterator = this.mItems.iterator();
        while (iterator.hasNext()) {
            TypeItem item = iterator.next();
            if (item.getName().toLowerCase().endsWith(this.mSearchQuery.toLowerCase())) {
                sufixes.add(item);
                iterator.remove();
            }
        }
        return sufixes;
    }

    private List<TypeItem> extractContainsMatches() {
        List<TypeItem> contained = new ArrayList<>();
        Iterator<TypeItem> iterator = this.mItems.iterator();
        while (iterator.hasNext()) {
            TypeItem item = iterator.next();
            if (item.getName().toLowerCase().contains(this.mSearchQuery.toLowerCase())) {
                contained.add(item);
                iterator.remove();
            }
        }
        return contained;
    }

    private List<TypeItem> extractCamelCaseMatches() {
        List<TypeItem> contained = new ArrayList<>();
        Iterator<TypeItem> iterator = this.mItems.iterator();
        while (iterator.hasNext()) {
            TypeItem item =  iterator.next();
            if (item.getName().contains(toCamelCase(this.mSearchQuery))) {
                contained.add(item);
                iterator.remove();
            }
        }
        return contained;
    }

    public int levenshteinDistance(String s0, String s1) {
        int i;
        int len0 = s0.length() + 1;
        int len1 = s1.length() + 1;
        int[] cost = new int[len0];
        int[] newcost = new int[len0];
        for (i = 0; i < len0; i++) {
            cost[i] = i;
        }
        for (int j = 1; j < len1; j++) {
            newcost[0] = j;
            for (i = 1; i < len0; i++) {
                newcost[i] = Math.min(Math.min(cost[i] + 1, newcost[i - 1] + 1), cost[i - 1] + (s0.charAt(i + -1) == s1.charAt(j + -1) ? 0 : 1));
            }
            int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }
        return cost[len0 - 1];
    }
}
