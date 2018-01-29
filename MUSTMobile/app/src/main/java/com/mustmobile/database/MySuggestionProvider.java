package com.mustmobile.database;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by Tosh on 10/10/2015.
 */
public class MySuggestionProvider extends SearchRecentSuggestionsProvider {

    public static final String AUTHORITY = "com.mustmobile.database.MySuggestionProvider";
    public static final int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;

    public MySuggestionProvider(){
        setupSuggestions(AUTHORITY, MODE);
    }
}
