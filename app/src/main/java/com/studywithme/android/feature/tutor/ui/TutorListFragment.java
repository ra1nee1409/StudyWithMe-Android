package com.studywithme.android.feature.tutor.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.studywithme.android.R;
import com.studywithme.android.core.ui.components.UiComponentHelper;
import com.studywithme.android.databinding.FragmentTutorListBinding;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TutorListFragment extends Fragment {

    private FragmentTutorListBinding binding;
    private TutorSearchAdapter adapter;
    private final List<TutorSearchAdapter.TutorCardModel> allTutors = TutorSearchAdapter.sampleTutors();

    private String selectedSubject = "Tất cả";
    private String searchQuery = "";
    private TutorSearchAdapter.SortOption sortOption = TutorSearchAdapter.SortOption.DEFAULT;
    @Nullable
    private Integer minPrice = null;
    @Nullable
    private Integer maxPrice = null;

    private List<TextView> subjectChips;
    private List<SortChipSpec> sortChips;

    private static class SortChipSpec {
        final TextView view;
        final TutorSearchAdapter.SortOption option;
        @ColorRes
        final int defaultBgRes;
        @ColorRes
        final int defaultFgRes;

        SortChipSpec(
                TextView view,
                TutorSearchAdapter.SortOption option,
                @ColorRes int defaultBgRes,
                @ColorRes int defaultFgRes
        ) {
            this.view = view;
            this.option = option;
            this.defaultBgRes = defaultBgRes;
            this.defaultFgRes = defaultFgRes;
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentTutorListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new TutorSearchAdapter(new TutorSearchAdapter.OnTutorCardListener() {
            @Override
            public void onOpenProfile(@NonNull TutorSearchAdapter.TutorCardModel model) {
                Bundle args = new Bundle();
                args.putLong("tutorId", model.id);
                UiComponentHelper.navigateSafely(binding.getRoot(), R.id.action_tutor_list_to_detail, args);
            }

            @Override
            public void onBookTrial(@NonNull TutorSearchAdapter.TutorCardModel model) {
                Bundle args = new Bundle();
                args.putLong("tutorId", model.id);
                UiComponentHelper.navigateSafely(binding.getRoot(), R.id.action_tutor_list_to_booking, args);
            }
        });
        binding.rvTutors.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTutors.setAdapter(adapter);

        setupSearchAndFilters();
        applyFilters();
    }

    private void setupSearchAndFilters() {
        binding.etTutorSearch.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s != null ? s.toString() : "";
                binding.btnTutorClearSearch.setVisibility(searchQuery.isEmpty() ? View.GONE : View.VISIBLE);
                applyFilters();
            }
        });

        binding.etTutorSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                UiComponentHelper.hideKeyboard(v);
                return true;
            }
            return false;
        });

        binding.btnTutorClearSearch.setOnClickListener(v -> {
            binding.etTutorSearch.setText("");
            UiComponentHelper.hideKeyboard(binding.etTutorSearch);
        });

        subjectChips = Arrays.asList(
                binding.chipSubAll,
                binding.chipSubMath,
                binding.chipSubEnglish,
                binding.chipSubChem,
                binding.chipSubPhysics
        );
        for (TextView chip : subjectChips) {
            chip.setOnClickListener(v -> {
                selectedSubject = chip.getText().toString();
                renderSubjectChips();
                applyFilters();
            });
        }

        sortChips = Arrays.asList(
                new SortChipSpec(
                        binding.chipSortRatingDesc,
                        TutorSearchAdapter.SortOption.RATING_DESC,
                        R.color.accent_coral_subtle,
                        R.color.tertiary
                ),
                new SortChipSpec(
                        binding.chipSortRatingAsc,
                        TutorSearchAdapter.SortOption.RATING_ASC,
                        R.color.surface_container_low,
                        R.color.on_surface_variant
                ),
                new SortChipSpec(
                        binding.chipSortPriceAsc,
                        TutorSearchAdapter.SortOption.PRICE_ASC,
                        R.color.surface_container_low,
                        R.color.on_surface_variant
                ),
                new SortChipSpec(
                        binding.chipSortPriceDesc,
                        TutorSearchAdapter.SortOption.PRICE_DESC,
                        R.color.surface_container_low,
                        R.color.on_surface_variant
                )
        );
        for (SortChipSpec spec : sortChips) {
            spec.view.setOnClickListener(v -> {
                sortOption = (sortOption == spec.option)
                        ? TutorSearchAdapter.SortOption.DEFAULT
                        : spec.option;
                renderSortChips();
                applyFilters();
            });
        }

        binding.btnTutorFilterToggle.setOnClickListener(v -> {
            boolean show = binding.cardPriceRange.getVisibility() != View.VISIBLE;
            binding.cardPriceRange.setVisibility(show ? View.VISIBLE : View.GONE);
            if (!show) {
                UiComponentHelper.hideKeyboard(binding.etPriceMin);
            }
        });

        binding.etPriceMin.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                minPrice = parsePriceOrNull(s);
                applyFilters();
            }
        });

        binding.etPriceMax.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                maxPrice = parsePriceOrNull(s);
                applyFilters();
            }
        });

        binding.etPriceMax.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                UiComponentHelper.hideKeyboard(v);
                return true;
            }
            return false;
        });

        binding.btnTutorResetFilters.setOnClickListener(v -> resetFilters());
        binding.cardSmartSuggestion.setOnClickListener(v ->
                UiComponentHelper.navigateSafely(binding.getRoot(), R.id.action_tutor_list_to_booking, null)
        );
    }

    @Nullable
    private static Integer parsePriceOrNull(@Nullable CharSequence text) {
        if (text == null) {
            return null;
        }
        String digits = text.toString().trim();
        if (digits.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean hasActiveFilters() {
        return !"Tất cả".equals(selectedSubject)
                || sortOption != TutorSearchAdapter.SortOption.DEFAULT
                || minPrice != null
                || maxPrice != null
                || !searchQuery.trim().isEmpty();
    }

    private void resetFilters() {
        selectedSubject = "Tất cả";
        sortOption = TutorSearchAdapter.SortOption.DEFAULT;
        minPrice = null;
        maxPrice = null;
        binding.etTutorSearch.setText("");
        binding.etPriceMin.setText("");
        binding.etPriceMax.setText("");
        renderSubjectChips();
        renderSortChips();
        applyFilters();
    }

    private void renderSubjectChips() {
        Context ctx = requireContext();
        int activeBg = ContextCompat.getColor(ctx, R.color.primary);
        int activeFg = ContextCompat.getColor(ctx, R.color.on_primary);
        int idleBg = ContextCompat.getColor(ctx, R.color.surface_container);
        int idleFg = ContextCompat.getColor(ctx, R.color.on_surface_variant);

        for (TextView chip : subjectChips) {
            boolean active = chip.getText().toString().equals(selectedSubject);
            chip.setBackgroundTintList(ColorStateList.valueOf(active ? activeBg : idleBg));
            chip.setTextColor(active ? activeFg : idleFg);
        }
    }

    private void renderSortChips() {
        Context ctx = requireContext();
        int activeBg = ContextCompat.getColor(ctx, R.color.primary);
        int activeFg = ContextCompat.getColor(ctx, R.color.on_primary);

        for (SortChipSpec spec : sortChips) {
            boolean active = spec.option == sortOption;
            int bg = active ? activeBg : ContextCompat.getColor(ctx, spec.defaultBgRes);
            int fg = active ? activeFg : ContextCompat.getColor(ctx, spec.defaultFgRes);
            spec.view.setBackgroundTintList(ColorStateList.valueOf(bg));
            spec.view.setTextColor(fg);
        }
    }

    private void applyFilters() {
        List<TutorSearchAdapter.TutorCardModel> filtered = TutorSearchAdapter.filterAndSort(
                allTutors, selectedSubject, searchQuery, minPrice, maxPrice, sortOption
        );
        adapter.submitList(filtered);
        binding.tvTutorCount.setText(String.format(Locale.US, "%d gia sư", filtered.size()));
        binding.viewFilterActiveDot.setVisibility(hasActiveFilters() ? View.VISIBLE : View.GONE);
        binding.layoutTutorSearchEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subjectChips = null;
        sortChips = null;
        binding = null;
    }
}
