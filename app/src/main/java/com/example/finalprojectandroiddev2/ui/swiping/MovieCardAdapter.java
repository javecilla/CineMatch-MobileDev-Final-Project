package com.example.finalprojectandroiddev2.ui.swiping;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalprojectandroiddev2.R;
import com.example.finalprojectandroiddev2.data.model.Movie;
import com.example.finalprojectandroiddev2.utils.Constants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * ViewPager2 adapter that displays full-screen swipeable movie cards.
 *
 * Step 7.2 — Swipe Gesture Detection:
 *   • Touch listener on each card detects horizontal drag (left = No, right = Yes)
 *   • Card translates + rotates proportionally during drag
 *   • YES (green) / NO (red) overlay labels fade in as the user drags
 *   • Releasing past the threshold fires SwipeCallback and flies the card off-screen
 *   • Releasing short of the threshold snaps the card back with a spring animation
 *
 * Each card also has two info states:
 *   EXPANDED (default) — title, date, overview, genres
 *   COLLAPSED (on tap)  — title and date only
 */
public class MovieCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // ── TMDB Genre ID → Name ──────────────────────────────────────────────────
    private static final Map<Integer, String> GENRE_MAP = new HashMap<>();
    static {
        GENRE_MAP.put(28,    "Action");
        GENRE_MAP.put(12,    "Adventure");
        GENRE_MAP.put(16,    "Animation");
        GENRE_MAP.put(35,    "Comedy");
        GENRE_MAP.put(80,    "Crime");
        GENRE_MAP.put(99,    "Documentary");
        GENRE_MAP.put(18,    "Drama");
        GENRE_MAP.put(10751, "Family");
        GENRE_MAP.put(14,    "Fantasy");
        GENRE_MAP.put(36,    "History");
        GENRE_MAP.put(27,    "Horror");
        GENRE_MAP.put(10402, "Music");
        GENRE_MAP.put(9648,  "Mystery");
        GENRE_MAP.put(10749, "Romance");
        GENRE_MAP.put(878,   "Sci-Fi");
        GENRE_MAP.put(10770, "TV Movie");
        GENRE_MAP.put(53,    "Thriller");
        GENRE_MAP.put(10752, "War");
        GENRE_MAP.put(37,    "Western");
    }

    // ── Swipe constants ───────────────────────────────────────────────────────

    /** Horizontal drag distance (dp) required to trigger a swipe. */
    private static final float SWIPE_THRESHOLD_DP = 120f;
    /** Max rotation angle (degrees) at full swipe threshold. */
    private static final float MAX_ROTATION_DEG   = 20f;
    /** Duration (ms) for fly-off and snap-back animations. */
    private static final int   FLY_DURATION_MS    = 280;
    private static final int   SNAP_DURATION_MS   = 250;

    // ── View types ──────────────────────────────────────────────────────────────

    private static final int TYPE_MOVIE       = 0;
    private static final int TYPE_END_OF_DECK = 1;

    // ── Callbacks ─────────────────────────────────────────────────────────────

    /** Notified when a card is fully swiped left (No) or right (Yes). */
    public interface SwipeCallback {
        void onSwipedYes();
        void onSwipedNo();
    }

    /** Notified when the host taps "Load More Movies" on the end-of-deck card. */
    public interface EndOfDeckCallback {
        void onLoadMoreClicked();
    }

    private SwipeCallback    swipeCallback;
    private EndOfDeckCallback endOfDeckCallback;
    private boolean           isHost = false;
    private int               usersDoneCount = 0;
    private int               totalUsersCount = 0;

    public void setSwipeCallback(SwipeCallback cb)         { this.swipeCallback     = cb; }
    public void setEndOfDeckCallback(EndOfDeckCallback cb) { this.endOfDeckCallback = cb; }
    public void setIsHost(boolean host)                    { this.isHost           = host; }
    
    public void setEndOfDeckProgress(int done, int total) {
        this.usersDoneCount  = done;
        this.totalUsersCount = total;
        // The last item is the end-of-deck card, so notify it specifically if movies are loaded
        if (!movies.isEmpty()) {
            notifyItemChanged(movies.size());
        }
    }

    // ── Data ──────────────────────────────────────────────────────────────────

    private final List<Movie> movies = new ArrayList<>();

    public void setMovies(List<Movie> newMovies) {
        movies.clear();
        if (newMovies != null) movies.addAll(newMovies);
        notifyDataSetChanged();
    }

    /**
     * Appends additional movies to the existing deck, skipping any that are already
     * present (deduplication by TMDB movie ID). Use this for "Load More" pages.
     *
     * @return the number of movies actually added after dedup
     */
    public int appendMovies(List<Movie> newMovies) {
        if (newMovies == null || newMovies.isEmpty()) return 0;
        java.util.Set<Integer> existingIds = new java.util.HashSet<>();
        for (Movie m : movies) existingIds.add(m.getId());

        List<Movie> toAdd = new ArrayList<>();
        for (Movie m : newMovies) {
            if (!existingIds.contains(m.getId())) toAdd.add(m);
        }
        if (!toAdd.isEmpty()) {
            int insertStart = movies.size(); // insert before the end-of-deck slot
            movies.addAll(toAdd);
            // insertStart+1 because slot insertStart was the end-of-deck (TYPE_END_OF_DECK)
            // and it shifts forward; new movie slots are insertStart…insertStart+toAdd.size()-1
            notifyItemRangeInserted(insertStart, toAdd.size());
        }
        return toAdd.size();
    }


    // ── Adapter ───────────────────────────────────────────────────────────────

    @Override
    public int getItemViewType(int position) {
        // Last slot is always the end-of-deck card (only visible when movies loaded)
        return (position == movies.size()) ? TYPE_END_OF_DECK : TYPE_MOVIE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_END_OF_DECK) {
            View v = li.inflate(R.layout.item_end_of_deck, parent, false);
            return new EndOfDeckViewHolder(v);
        }
        View v = li.inflate(R.layout.item_movie_card, parent, false);
        return new MovieCardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EndOfDeckViewHolder) {
            ((EndOfDeckViewHolder) holder).bind(isHost, endOfDeckCallback, usersDoneCount, totalUsersCount);
        } else {
            ((MovieCardViewHolder) holder).bind(movies.get(position));
        }
    }

    /** movies.size() real cards + 1 virtual end-of-deck card at the tail. */
    @Override
    public int getItemCount() { return movies.isEmpty() ? 0 : movies.size() + 1; }

    // ── EndOfDeckViewHolder ───────────────────────────────────────────────────

    static class EndOfDeckViewHolder extends RecyclerView.ViewHolder {
        private final View btnLoadMore;
        private final TextView textWaitingHost;
        private final TextView textProgressCount;
        private final com.google.android.material.button.MaterialButton mBtnLoadMore;

        EndOfDeckViewHolder(@NonNull View itemView) {
            super(itemView);
            btnLoadMore       = itemView.findViewById(R.id.btn_load_more);
            textWaitingHost   = itemView.findViewById(R.id.text_waiting_host);
            textProgressCount = itemView.findViewById(R.id.text_end_of_deck_count);
            mBtnLoadMore      = itemView.findViewById(R.id.btn_load_more);
        }

        void bind(boolean isHost, EndOfDeckCallback callback, int doneCount, int totalCount) {
            Context ctx = itemView.getContext();
            
            // Format "2 users already done out of 3"
            String progressText = ctx.getString(R.string.label_end_of_deck_count, doneCount, totalCount);
            if (totalCount > 0) {
                textProgressCount.setVisibility(View.VISIBLE);
                textProgressCount.setText(progressText);
            } else {
                textProgressCount.setVisibility(View.GONE);
            }

            boolean allDone = (totalCount > 0 && doneCount >= totalCount);

            if (isHost) {
                btnLoadMore.setVisibility(View.VISIBLE);
                textWaitingHost.setVisibility(View.GONE);
                
                if (allDone) {
                    btnLoadMore.setEnabled(true);
                    mBtnLoadMore.setText(R.string.btn_load_more);
                } else {
                    btnLoadMore.setEnabled(false);
                    mBtnLoadMore.setText(R.string.btn_waiting_others_vote);
                }
                
                btnLoadMore.setOnClickListener(v -> {
                    if (allDone && callback != null) {
                        callback.onLoadMoreClicked();
                    }
                });
            } else {
                btnLoadMore.setVisibility(View.GONE);
                textWaitingHost.setVisibility(View.VISIBLE);
            }
        }
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    class MovieCardViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivPoster;
        private final TextView  tvTitle;
        private final TextView  tvReleaseDate;
        private final TextView  tvRating;
        private final TextView  tvOverview;
        private final ChipGroup chipGroupGenres;
        private final TextView  overlayYes;
        private final TextView  overlayNo;

        /** Tracks whether the card is in its expanded state. */
        private boolean isExpanded = true;

        /** Pixel threshold derived from SWIPE_THRESHOLD_DP at bind time. */
        private float swipeThresholdPx;

        MovieCardViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster        = itemView.findViewById(R.id.image_poster);
            tvTitle         = itemView.findViewById(R.id.text_movie_title);
            tvReleaseDate   = itemView.findViewById(R.id.text_movie_release_date);
            tvRating        = itemView.findViewById(R.id.text_movie_rating);
            tvOverview      = itemView.findViewById(R.id.text_movie_overview);
            chipGroupGenres = itemView.findViewById(R.id.chip_group_card_genres);
            overlayYes      = itemView.findViewById(R.id.overlay_swipe_yes);
            overlayNo       = itemView.findViewById(R.id.overlay_swipe_no);

            // Convert threshold dp → px once
            float density      = itemView.getContext().getResources().getDisplayMetrics().density;
            swipeThresholdPx   = SWIPE_THRESHOLD_DP * density;
        }

        void bind(Movie movie) {
            // Reset transform/overlays whenever card is (re)bound
            resetCard();

            // Default to expanded state
            isExpanded = true;
            setExpandedState(true);

            // Populate text fields
            tvTitle.setText(movie.getTitle() != null ? movie.getTitle() : "");
            tvRating.setText(String.format(Locale.getDefault(), "⭐ %.1f", movie.getVoteAverage()));
            tvReleaseDate.setText(formatReleaseDate(movie.getReleaseDate()));
            tvOverview.setText(movie.getOverview() != null ? movie.getOverview() : "");

            buildGenreChips(itemView.getContext(), movie);

            // Load image
            String imagePath;
            if (movie.getBackdropPath() != null && !movie.getBackdropPath().isEmpty()) {
                imagePath = Constants.TMDB_IMAGE_BASE_URL + movie.getBackdropPath();
            } else if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) {
                imagePath = Constants.TMDB_IMAGE_BASE_URL + movie.getPosterPath();
            } else {
                imagePath = null;
            }
            Glide.with(itemView.getContext())
                    .load(imagePath)
                    .centerCrop()
                    .placeholder(R.color.color_surface)
                    .error(R.color.color_surface)
                    .into(ivPoster);

            // Attach swipe + tap gesture
            attachTouchListener();
        }

        // ── Swipe gesture ─────────────────────────────────────────────────────

        /**
         * Handles the complete swipe lifecycle:
         *   ACTION_DOWN  — record touch start position
         *   ACTION_MOVE  — translate/rotate card, fade YES/NO overlays
         *   ACTION_UP    — commit if past threshold, else snap back
         *   ACTION_CANCEL— always snap back
         */
        private void attachTouchListener() {
            final float[] startX  = {0f};
            final float[] startY  = {0f};
            final boolean[] isDragging = {false};

            itemView.setOnTouchListener((v, event) -> {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        startX[0]     = event.getRawX();
                        startY[0]     = event.getRawY();
                        isDragging[0] = false;
                        return true;

                    case MotionEvent.ACTION_MOVE: {
                        float dx = event.getRawX() - startX[0];
                        float dy = event.getRawY() - startY[0];

                        // Start drag only after meaningful horizontal movement
                        if (!isDragging[0] && Math.abs(dx) > 10f) isDragging[0] = true;
                        if (!isDragging[0]) return true;

                        // Translate card
                        itemView.setTranslationX(dx);
                        itemView.setTranslationY(dy * 0.15f); // tiny vertical drift

                        // Rotate proportionally to horizontal drag
                        float fraction = Math.min(Math.abs(dx) / swipeThresholdPx, 1f);
                        float rotation = MAX_ROTATION_DEG * fraction * Math.signum(dx);
                        itemView.setRotation(rotation);

                        // Fade YES / NO labels proportionally
                        if (dx > 0) {
                            overlayYes.setVisibility(View.VISIBLE);
                            overlayYes.setAlpha(fraction);
                            overlayNo.setVisibility(View.INVISIBLE);
                        } else {
                            overlayNo.setVisibility(View.VISIBLE);
                            overlayNo.setAlpha(fraction);
                            overlayYes.setVisibility(View.INVISIBLE);
                        }
                        return true;
                    }

                    case MotionEvent.ACTION_UP: {
                        float dx = event.getRawX() - startX[0];

                        if (!isDragging[0]) {
                            // Short tap — toggle expand/collapse
                            v.performClick();
                            return true;
                        }

                        if (Math.abs(dx) >= swipeThresholdPx) {
                            flyOff(dx > 0); // commit swipe
                        } else {
                            snapBack();     // not far enough — return card
                        }
                        return true;
                    }

                    case MotionEvent.ACTION_CANCEL:
                        snapBack();
                        return true;
                }
                return false;
            });

            // Keep tap-to-expand working (called by performClick above)
            itemView.setOnClickListener(v -> {
                isExpanded = !isExpanded;
                setExpandedState(isExpanded);
            });
        }

        /**
         * Flies the card off-screen in the swipe direction, then fires the callback.
         * @param isYes true = swiped right (Yes), false = swiped left (No)
         */
        private void flyOff(boolean isYes) {
            float targetX = itemView.getWidth() * (isYes ? 2f : -2f);
            itemView.animate()
                    .translationX(targetX)
                    .translationY(itemView.getTranslationY())
                    .rotation(isYes ? MAX_ROTATION_DEG * 2 : -MAX_ROTATION_DEG * 2)
                    .alpha(0f)
                    .setDuration(FLY_DURATION_MS)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            resetCard();
                            if (swipeCallback != null) {
                                if (isYes) swipeCallback.onSwipedYes();
                                else       swipeCallback.onSwipedNo();
                            }
                        }
                    })
                    .start();
        }

        /** Snaps the card back to its resting position with a spring-like animation. */
        private void snapBack() {
            itemView.animate()
                    .translationX(0f)
                    .translationY(0f)
                    .rotation(0f)
                    .alpha(1f)
                    .setDuration(SNAP_DURATION_MS)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            overlayYes.setVisibility(View.INVISIBLE);
                            overlayNo.setVisibility(View.INVISIBLE);
                        }
                    })
                    .start();
        }

        /** Resets all transform/overlay state (called on bind and after fly-off). */
        private void resetCard() {
            itemView.setTranslationX(0f);
            itemView.setTranslationY(0f);
            itemView.setRotation(0f);
            itemView.setAlpha(1f);
            overlayYes.setVisibility(View.INVISIBLE);
            overlayNo.setVisibility(View.INVISIBLE);
        }

        // ── Info panel state ──────────────────────────────────────────────────

        private void setExpandedState(boolean expanded) {
            int vis = expanded ? View.VISIBLE : View.GONE;
            tvOverview.setVisibility(vis);
            chipGroupGenres.setVisibility(vis);
        }

        // ── Genre chips ───────────────────────────────────────────────────────

        private void buildGenreChips(Context ctx, Movie movie) {
            chipGroupGenres.removeAllViews();
            if (movie.getGenreIds() == null) return;
            for (int id : movie.getGenreIds()) {
                String name = GENRE_MAP.get(id);
                if (name == null) continue;
                Chip chip = new Chip(ctx);
                chip.setText(name);
                chip.setClickable(false);
                chip.setCheckable(false);
                chip.setChipBackgroundColorResource(R.color.color_surface);
                chip.setTextColor(ctx.getColor(R.color.color_text_primary));
                chip.setTextSize(11f);
                chipGroupGenres.addView(chip);
            }
        }

        // ── Date formatting ───────────────────────────────────────────────────

        private String formatReleaseDate(String rawDate) {
            if (rawDate == null || rawDate.isEmpty()) return "—";
            try {
                java.text.SimpleDateFormat inputFmt  = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                java.text.SimpleDateFormat outputFmt = new java.text.SimpleDateFormat("MMM yyyy",   Locale.getDefault());
                java.util.Date date = inputFmt.parse(rawDate);
                return date != null ? outputFmt.format(date) : rawDate;
            } catch (java.text.ParseException e) {
                return rawDate;
            }
        }
    }
}
