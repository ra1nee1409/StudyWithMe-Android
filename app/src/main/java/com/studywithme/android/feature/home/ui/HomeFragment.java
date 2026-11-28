package com.studywithme.android.feature.home.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.studywithme.android.MainActivity;
import com.studywithme.android.R;
import com.studywithme.android.core.ui.components.UiComponentHelper;
import com.studywithme.android.databinding.FragmentHomeBinding;
import com.studywithme.android.feature.home.data.HomeModels;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private FeaturedTutorAdapter tutorAdapter;
    private List<ChipSpec> filterChips;

    private static class ChipSpec {
        final TextView view;
        final String label;
        @ColorRes
        final int defaultBgRes;
        @ColorRes
        final int defaultTextRes;

        ChipSpec(TextView view, String label, @ColorRes int defaultBgRes, @ColorRes int defaultTextRes) {
            this.view = view;
            this.label = label;
            this.defaultBgRes = defaultBgRes;
            this.defaultTextRes = defaultTextRes;
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupHeroHeadline();
        setupRecyclerViews();
        setupSearchAndFilters();
        setupCategoryActions();
        setupCtaActions();
        observeViewModel();
    }

    private void setupHeroHeadline() {
        String prefix = getString(R.string.hero_title_prefix);
        String highlight = getString(R.string.hero_title_highlight);
        String suffix = getString(R.string.hero_title_suffix);
        SpannableString span = new SpannableString(prefix + highlight + suffix);
        int highlightColor = ContextCompat.getColor(requireContext(), R.color.tertiary_fixed);
        span.setSpan(
                new ForegroundColorSpan(highlightColor),
                prefix.length(),
                prefix.length() + highlight.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        binding.tvHeroTitle.setText(span);
    }

    private void setupRecyclerViews() {
        tutorAdapter = new FeaturedTutorAdapter(new FeaturedTutorAdapter.OnTutorActionListener() {
            @Override
            public void onTutorClick(@NonNull HomeModels.HomeTutorItem tutor) {
                Bundle args = new Bundle();
                if (tutor.id != null) {
                    args.putLong("tutorId", tutor.id);
                }
                UiComponentHelper.navigateSafely(binding.getRoot(), R.id.action_home_to_tutor_detail, args);
            }

            @Override
            public void onBookTrialClick(@NonNull HomeModels.HomeTutorItem tutor) {
                Bundle args = new Bundle();
                if (tutor.id != null) {
                    args.putLong("tutorId", tutor.id);
                }
                UiComponentHelper.navigateSafely(binding.getRoot(), R.id.action_home_to_booking, args);
            }
        });
        binding.rvFeaturedTutors.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.rvFeaturedTutors.setAdapter(tutorAdapter);
    }

    private void setupSearchAndFilters() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s != null ? s.toString() : "";
                binding.btnClearSearch.setVisibility(text.isEmpty() ? View.GONE : View.VISIBLE);
                viewModel.setSearchQuery(text);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.btnClearSearch.setOnClickListener(v -> {
            binding.etSearch.setText("");
            UiComponentHelper.hideKeyboard(binding.etSearch);
        });

        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.setSearchQuery(v.getText() != null ? v.getText().toString() : "");
                UiComponentHelper.hideKeyboard(v);
                scrollToTutorsSection();
                return true;
            }
            return false;
        });

        binding.btnAdvancedFilter.setOnClickListener(v -> switchTab(R.id.tutorListFragment));

        binding.btnResetFilter.setOnClickListener(v -> {
            binding.etSearch.setText("");
            viewModel.selectTag(getString(R.string.filter_all));
        });

        filterChips = Arrays.asList(
                new ChipSpec(binding.chipAll, getString(R.string.filter_all), R.color.surface_container, R.color.on_surface_variant),
                new ChipSpec(binding.chipMath9, getString(R.string.filter_math9), R.color.accent_cyan_subtle, R.color.primary),
                new ChipSpec(binding.chipIelts, getString(R.string.filter_ielts), R.color.accent_coral_subtle, R.color.tertiary),
                new ChipSpec(binding.chipChemPhys, getString(R.string.filter_chem_phys), R.color.surface_container, R.color.on_surface_variant),
                new ChipSpec(binding.chipCambridge, getString(R.string.filter_cambridge), R.color.surface_container, R.color.on_surface_variant),
                new ChipSpec(binding.chipGrade15, getString(R.string.filter_grade_1_5), R.color.surface_container, R.color.on_surface_variant),
                new ChipSpec(binding.chipGrade69, getString(R.string.filter_grade_6_9), R.color.surface_container, R.color.on_surface_variant),
                new ChipSpec(binding.chipGrade1012, getString(R.string.filter_grade_10_12), R.color.surface_container, R.color.on_surface_variant)
        );

        for (ChipSpec chip : filterChips) {
            chip.view.setOnClickListener(v -> viewModel.selectTag(chip.label));
        }
    }

    private void setupCategoryActions() {
        binding.btnCategoriesSeeMore.setOnClickListener(v -> switchTab(R.id.courseListFragment));
        binding.cardCat1.setOnClickListener(v -> selectCategoryTag(getString(R.string.filter_grade_1_5)));
        binding.cardCat2.setOnClickListener(v -> selectCategoryTag(getString(R.string.filter_grade_6_9)));
        binding.cardCat3.setOnClickListener(v -> selectCategoryTag(getString(R.string.filter_grade_10_12)));
        binding.cardCat4.setOnClickListener(v -> selectCategoryTag(getString(R.string.filter_ielts)));
        binding.btnTutorsSeeAll.setOnClickListener(v -> switchTab(R.id.tutorListFragment));
    }

    private void selectCategoryTag(@NonNull String tag) {
        viewModel.selectTag(tag);
        scrollToTutorsSection();
    }

    private void scrollToTutorsSection() {
        if (binding == null) {
            return;
        }
        binding.homeScrollView.post(() -> {
            if (binding != null) {
                binding.homeScrollView.smoothScrollTo(0, binding.sectionTutorsHeader.getTop());
            }
        });
    }

    private void setupCtaActions() {
        binding.btnHeroCta.setOnClickListener(v ->
                UiComponentHelper.navigateSafely(binding.getRoot(), R.id.action_home_to_booking, null)
        );
        binding.btnBottomFreeTrial.setOnClickListener(v ->
                UiComponentHelper.navigateSafely(binding.getRoot(), R.id.action_home_to_booking, null)
        );
        binding.btnHotline.setOnClickListener(v ->
                UiComponentHelper.openDialer(requireContext(), getString(R.string.hotline_uri))
        );
    }

    private void observeViewModel() {
        viewModel.getHomeData().observe(getViewLifecycleOwner(), data -> {
            if (data == null || binding == null) {
                return;
            }
            if (data.bannerBadgeText != null && !data.bannerBadgeText.isEmpty()) {
                binding.tvHeroBadge.setText(data.bannerBadgeText);
            }
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setUnreadNotificationIndicator(data.unreadNotifications > 0);
            }
            bindCategories(data.categories);
        });

        viewModel.getVisibleTutors().observe(getViewLifecycleOwner(), tutors -> {
            if (binding == null) {
                return;
            }
            List<HomeModels.HomeTutorItem> safe = tutors != null ? tutors : HomeModels.defaultTutors();
            tutorAdapter.submitList(safe);
            binding.layoutTutorEmpty.setVisibility(safe.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getSelectedTag().observe(getViewLifecycleOwner(), this::renderSelectedChip);
    }

    private void bindCategories(@NonNull List<HomeModels.HomeCategoryItem> categories) {
        if (categories.size() < 4) {
            return;
        }
        binding.tvCat1Title.setText(categories.get(0).title);
        binding.tvCat1Sub.setText(categories.get(0).subtitle);
        binding.tvCat2Title.setText(categories.get(1).title);
        binding.tvCat2Sub.setText(categories.get(1).subtitle);
        binding.tvCat3Title.setText(categories.get(2).title);
        binding.tvCat3Sub.setText(categories.get(2).subtitle);
        binding.tvCat4Title.setText(categories.get(3).title);
        binding.tvCat4Sub.setText(categories.get(3).subtitle);
    }

    private void renderSelectedChip(@Nullable String activeLabel) {
        if (binding == null || filterChips == null) {
            return;
        }
        Context ctx = requireContext();
        int activeBg = ContextCompat.getColor(ctx, R.color.primary);
        int activeText = ContextCompat.getColor(ctx, R.color.on_primary);

        for (ChipSpec chip : filterChips) {
            boolean isSelected = chip.label.equals(activeLabel);
            int bg = isSelected ? activeBg : ContextCompat.getColor(ctx, chip.defaultBgRes);
            int fg = isSelected ? activeText : ContextCompat.getColor(ctx, chip.defaultTextRes);
            chip.view.setBackgroundTintList(ColorStateList.valueOf(bg));
            chip.view.setTextColor(fg);
        }
    }

    private void switchTab(int destinationId) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).selectBottomTab(destinationId);
        } else if (binding != null) {
            UiComponentHelper.navigateSafely(binding.getRoot(), destinationId, null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        filterChips = null;
        binding = null;
    }
}
