package com.deblockt.kodirecomendations.search.parser;


import com.deblockt.kodirecomendations.search.QueryData;

/**
 * Created by thomas on 15/11/16.
 */

public abstract class QueryParser {
    public final static QueryParser DEFAULT_PARSER = new EpisodeAndSeasonParser()
            .addParser(new SeasonAndEpisodeParser())
            .addParser(new SeasonParser())
            .addParser(new MovieParser());

    private QueryParser nextParser = null;

    protected QueryParser addParser(QueryParser parser) {
        if (this.nextParser != null) {
            this.nextParser.addParser(parser);
        } else {
            this.nextParser = parser;
        }
        return this;
    }

    protected abstract QueryData internalParse(String query);

    public QueryData parse(String query) {
        QueryData result = this.internalParse(query);
        if (result != null || this.nextParser == null) {
            return result;
        }
        return this.nextParser.parse(query);
    }

}
