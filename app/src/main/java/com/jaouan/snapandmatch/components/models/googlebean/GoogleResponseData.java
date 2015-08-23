package com.jaouan.snapandmatch.components.models.googlebean;


import com.jaouan.snapandmatch.components.models.matches.Match;

/**
 * Google search API response data, containing all results.
 */
public class GoogleResponseData {

    private Match[] results;

    public Match[] getResults() {
        return results;
    }

    public void setResults(Match[] results) {
        this.results = results;
    }
}
