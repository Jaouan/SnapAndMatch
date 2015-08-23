package com.jaouan.snapandmatch.results;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jaouan.android.kerandroid.KerSupportFragment;
import com.jaouan.android.kerandroid.annotation.field.argument.Argument;
import com.jaouan.android.kerandroid.annotation.field.viewbyid.FindViewById;
import com.jaouan.snapandmatch.R;
import com.jaouan.snapandmatch.components.models.matches.Match;
import com.jaouan.snapandmatch.results.adapters.MatchAdapter;

import java.util.Arrays;
import java.util.Set;

/**
 * Matches fragment that displays all search matches.
 *
 * @author Maxence Jaouan
 */
public class MatchesFragment extends KerSupportFragment {

    /**
     * Create a new instance of {@link MatchesFragment}.
     *
     * @return Instance of {@link MatchesFragment}.
     */
    public static Fragment newInstance(Set<Match> matches) {
        MatchesFragment fragment = new MatchesFragment();
        Bundle args = new Bundle();
        args.putSerializable("matches", matches.toArray(new Match[0]));
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Argument - Matches.
     */
    @Argument
    private Match[] matches;

    @FindViewById(R.id.matchesListView)
    private ListView mMatchesListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // - If no match, then display no match layout.
        if (matches == null || matches.length == 0) {
            return inflater.inflate(R.layout.fragment_nomatch, container, false);
        }
        // - Else (matches), then display matches layout.
        else {
            return inflater.inflate(R.layout.fragment_matches, container, false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.result_title));

        // - If matches.
        if (mMatchesListView != null) {
            // - Sort matches by title and by relevance.
            Arrays.sort(matches, (match1, match2) -> match1.getTitleNoFormatting().compareTo(match2.getTitleNoFormatting()));
            Arrays.sort(matches, (match1, match2) -> new Integer(match2.getRelevance()).compareTo(new Integer(match1.getRelevance())));

            // - Display matches.
            mMatchesListView.setAdapter(new MatchAdapter(getActivity(), matches));
        }

    }

}
