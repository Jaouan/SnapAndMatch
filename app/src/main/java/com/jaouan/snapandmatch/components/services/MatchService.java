package com.jaouan.snapandmatch.components.services;

import android.content.Context;

import com.google.gson.Gson;
import com.jaouan.snapandmatch.components.models.exceptions.ServiceTechnicalException;
import com.jaouan.snapandmatch.components.models.googlebean.GoogleSearchResponse;
import com.jaouan.snapandmatch.components.models.matches.Match;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Service that searches for matches.
 *
 * @author Maxence Jaouan
 */
public class MatchService {

    /**
     * SINGLETON.
     */
    private static final MatchService SINGLETON = new MatchService();

    public static MatchService getInstance() {
        return SINGLETON;
    }

    /**
     * Gson.
     */
    private static final Gson GSON = new Gson();

    /**
     * Search for matches.
     *
     * @param context Context.
     * @param texts   Texts to search.
     * @return Matches result.
     * @throws ServiceTechnicalException Thrown when a technical exception occured.
     */
    public Set<Match> search(final Context context, final String[] texts) throws ServiceTechnicalException {
        // - Matches maps for relevance.
        final Map<String, Match> urls = new HashMap<String, Match>();
        final Map<String, Match> books = new HashMap<String, Match>();

        // - For each text, search websites and books.

        for (final String text : texts) {
            try {
                searchWebsites(urls, text);
                searchBooks(books, text);
            } catch (final IOException ioException) {
                throw new ServiceTechnicalException("Unable to search text : " + text, ioException);
            }
        }

        // - Aggregate all values.
        final Set<Match> matches = new HashSet<Match>();
        matches.addAll(urls.values());
        matches.addAll(books.values());

        // - Increase relevance for specifics websites.
        for (final Match result : matches) {
            if (result.getUrl().contains("wikimedia") || result.getUrl().contains("wikipedia") || result.getUrl().contains("amazon") || result.getUrl().contains("fnac")) {
                result.setRelevance(result.getRelevance() + 2);
            }
        }

        return matches;
    }

    /**
     * Search for books containing text.
     *
     * @param books Books map for relevance.
     * @param text  Text to search.
     * @throws IOException
     */
    private void searchBooks(Map<String, Match> books, String text) throws IOException {
        // - Call REST books search service.
        final String jsonResponse = httpGet("https://ajax.googleapis.com/ajax/services/search/books?v=1.0&q=" + URLEncoder.encode(text, Charset.defaultCharset().name()));

        // - Parse JSon response.
        final GoogleSearchResponse searchResponse = GSON.fromJson(jsonResponse, GoogleSearchResponse.class);

        // - For each result.
        for (final Match match : searchResponse.getResponseData().getResults()) {
            match.setType(Match.TYPE.BOOK);

            // - If book title is null (REST response...), continue iteration.
            final String bookTitle = match.getTitleNoFormatting();
            if (match.getTitleNoFormatting().toLowerCase().contains("null")) {
                continue;
            }

            // - If books map does not contain the book, add it.
            Match existingMatch = books.get(bookTitle);
            if (existingMatch == null) {
                books.put(bookTitle, match);
                match.setParsedUrl(new URL(match.getUnescapedUrl()));
            }
            // - Else, increment it relevance.
            else {
                existingMatch.setRelevance(existingMatch.getRelevance() + 1);
            }
        }
    }

    /**
     * Search for matches in websites.
     *
     * @param urls URLs map for relevance.
     * @param text Text to search.
     * @throws IOException
     */
    private void searchWebsites(Map<String, Match> urls, String text) throws IOException {
        // - Call REST web search service.
        final String jsonResponse = httpGet("https://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=" + URLEncoder.encode(text, Charset.defaultCharset().name()));

        // - Parse JSon response.
        final GoogleSearchResponse searchResponse = GSON.fromJson(jsonResponse, GoogleSearchResponse.class);

        // - For each result.
        for (Match match : searchResponse.getResponseData().getResults()) {
            match.setType(Match.TYPE.WEB);

            // - If web title is null (REST response...), continue iteration.
            String url = match.getUnescapedUrl();
            if (match.getTitleNoFormatting().toLowerCase().contains("null")) {
                continue;
            }

            // - If web map does not contain the book, add it.
            Match existingMatch = urls.get(url);
            if (existingMatch == null) {
                urls.put(url, match);
                match.setParsedUrl(new URL(url));
            }
            // - Else, increment it relevance.
            else {
                existingMatch.setRelevance(existingMatch.getRelevance() + 1);
            }
        }
    }

    /**
     * Do HTTP GET.
     *
     * @param uri Target URI.
     * @return HTTP response's body
     */
    private String httpGet(String uri) throws IOException {
        final StringBuilder bodyStringBuilder = new StringBuilder();

        HttpURLConnection urlConnection = null;
        try {
            final URL url = new URL(uri);
            urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                bodyStringBuilder.append(inputLine);
            }
            in.close();
        } catch (IOException exception) {
            throw exception;
        } finally {
            urlConnection.disconnect();
        }

        return bodyStringBuilder.toString();
    }

}
