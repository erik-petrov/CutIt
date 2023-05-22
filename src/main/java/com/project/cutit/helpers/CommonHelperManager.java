package com.project.cutit.helpers;

import java.util.ArrayList;
import java.util.List;

public class CommonHelperManager {
    private static final List<CommonHelper> helperList = new ArrayList<>();

    public static void registerHelper(CommonHelper helper) {
        helperList.add(helper);
    }

    public static void triggerSetMediaItems() {
        for (CommonHelper helper : helperList) {
            helper.setMediaItems();
        }
    }
}