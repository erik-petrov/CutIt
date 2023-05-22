package com.project.cutit.helpers;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n_Helper {
    private static Locale projectLocale = Locale.forLanguageTag("et-ET");
    private static ResourceBundle translationBundle = ResourceBundle.getBundle("com.project.cutit.translation", projectLocale);

    public static String getTranslation(String key) {
        return translationBundle.getString(key);
    }

    public static void setProjectLocale(String newLocale) {
        projectLocale = Locale.forLanguageTag(newLocale);
        translationBundle = ResourceBundle.getBundle("com.project.cutit.translation", projectLocale);
    }

    public static ResourceBundle getTranslationBundle() { return translationBundle; }
}
