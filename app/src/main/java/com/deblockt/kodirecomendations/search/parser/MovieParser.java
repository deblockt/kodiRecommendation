package com.deblockt.kodirecomendations.search.parser;

import com.deblockt.kodirecomendations.search.QueryData;

/**
 * Created by thomas on 15/11/16.
 */

public class MovieParser extends QueryParser {
    @Override
    public QueryData internalParse(String query) {
        return new QueryData(query);
    }
}
