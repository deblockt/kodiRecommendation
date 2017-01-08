package com.deblockt.kodirecomendations.search.parser;

import com.deblockt.kodirecomendations.search.QueryData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by thomas on 15/11/16.
 */

public class EpisodeAndSeasonParser extends QueryParser {
    @Override
    protected QueryData internalParse(String query) {
        Pattern p = Pattern.compile("(.+)\\s*[E|é]pisode\\s*(\\d+)\\s*Saison\\s*(\\d+).*", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(query);

        boolean b = m.matches();
        QueryData data = null;
        if (b) {
            data = new QueryData(m.group(1).trim());
            data.episode = Integer.parseInt(m.group(2));
            data.season = Integer.parseInt(m.group(3));
        }

        return data;
    }
}
