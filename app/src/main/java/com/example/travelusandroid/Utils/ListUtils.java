package com.example.travelusandroid.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListUtils {
    public static List<String> getIntersection(List<List<String>> listOfLists) {
        if (listOfLists == null || listOfLists.isEmpty()) {
            List<String> result = new ArrayList<>();
            return result;
        }

        Set<String> intersection = new HashSet<>(listOfLists.get(0));
        for (int i = 1; i < listOfLists.size(); i++) {
            intersection.retainAll(listOfLists.get(i));
        }
        List<String> instersectionList = new ArrayList<>(intersection);
        return instersectionList;
    }
}
