package com.example.marcqtan.inventorysystem;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by Marc Q. Tan on 23/02/2020.
 */
public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.example.MySuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
