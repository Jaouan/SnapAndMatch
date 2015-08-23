package com.jaouan.snapandmatch.results.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaouan.snapandmatch.R;
import com.jaouan.snapandmatch.components.models.matches.Match;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Adapter for {@link Match}.
 *
 * @author Maxence Jaouan
 */
public class MatchAdapter extends BaseAdapter {

    /**
     * Activity.
     */
    private final Activity mActivity;

    /**
     * Matches to display.
     */
    private Match[] mMatches;

    /**
     * Layout inflater.
     */
    private LayoutInflater mLayoutInflater;

    /**
     * Constructor.
     *  @param activity Activity.
     * @param matches  Matches to display.
     */
    public MatchAdapter(Activity activity, Match[] matches) {
        this.mActivity = activity;
        this.mMatches = matches;
        this.mLayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mMatches.length;
    }

    @Override
    public Object getItem(int position) {
        return mMatches[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // - Create view if necessary.
        View view = convertView;
        ViewHolder viewHolder;
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_result, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.setTitleTextView((TextView) view.findViewById(R.id.titre));
            viewHolder.setDescriptionTextView((TextView) view.findViewById(R.id.description));
            viewHolder.setIconImageView((ImageView) view.findViewById(R.id.icon));
            viewHolder.setIconLayout(view.findViewById(R.id.iconLayout));
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // - Bind view with model.
        final Match match = mMatches[position];
        bindView(view, viewHolder, match);

        return view;
    }

    /**
     * Bind view with model.
     *
     * @param view       View to bind.
     * @param viewHolder View holder.
     * @param match     Model.
     */
    private void bindView(View view, ViewHolder viewHolder, final Match match) {
        // - Display title.
        viewHolder.getTitleTextView().setText(StringEscapeUtils.unescapeHtml4(match.getTitleNoFormatting()));
        if (match.getRelevance() > 0) {
            viewHolder.getTitleTextView().setTypeface(null, Typeface.BOLD);
        } else {
            viewHolder.getTitleTextView().setTypeface(null, Typeface.NORMAL);
        }

        // - Display cleaned description.
        viewHolder.getDescriptionTextView().setText(match.getParsedUrl().getHost().replace("www.", ""));

        // - Display icon.
        if (match.getType() == Match.TYPE.BOOK) {
            viewHolder.getIconImageView().setImageResource(R.drawable.ic_book_white_48dp);
            viewHolder.getIconLayout().setBackgroundResource(R.drawable.circle_book);
        } else if (match.getUrl().contains("wikipedia") || match.getUrl().contains("wikimedia")) {
            viewHolder.getIconImageView().setImageResource(R.drawable.wiki);
            viewHolder.getIconLayout().setBackgroundResource(R.drawable.circle_wiki);
        } else {
            viewHolder.getIconImageView().setImageResource(R.drawable.ic_language_white_48dp);
            viewHolder.getIconLayout().setBackgroundResource(R.drawable.circle_web);
        }


        // - Handle on click event.
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(match.getUnescapedUrl()));
                mActivity.startActivity(browserIntent);
            }
        });
    }


    /**
     * View holder.
     */
    private class ViewHolder {
        /**
         * Title.
         */
        private TextView titleTextView;

        /**
         * Description.
         */
        private TextView descriptionTextView;

        /**
         * Icon container.
         */
        private View iconLayout;

        /**
         * Icon.
         */
        private ImageView iconImageView;

        public TextView getTitleTextView() {
            return titleTextView;
        }

        public void setTitleTextView(TextView titleTextView) {
            this.titleTextView = titleTextView;
        }

        public TextView getDescriptionTextView() {
            return descriptionTextView;
        }

        public void setDescriptionTextView(TextView descriptionTextView) {
            this.descriptionTextView = descriptionTextView;
        }

        public View getIconLayout() {
            return iconLayout;
        }

        public void setIconLayout(View iconLayout) {
            this.iconLayout = iconLayout;
        }

        public ImageView getIconImageView() {
            return iconImageView;
        }

        public void setIconImageView(ImageView iconImageView) {
            this.iconImageView = iconImageView;
        }
    }
}
