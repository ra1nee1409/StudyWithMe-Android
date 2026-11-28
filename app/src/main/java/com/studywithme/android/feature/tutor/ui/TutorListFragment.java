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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.studywithme.android.R;
import com.studywithme.android.core.ui.components.UiComponentHelper;
import com.studywithme.android.databinding.FragmentTutorListBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TutorListFragment extends Fragment {

    private FragmentTutorListBinding binding;
    private TutorSearchAdapter adapter;
    private final List<TutorSearchAdapter.TutorCardModel> allTutors = TutorSearchAdapter.sampleTutors();

    private String selectedSubject = "Tất cả";
    private String searchQuery = "";
    private boolean onlyUnder170k = false;
    private boolean onlyHighRating = false;
    private List<TextView> subjectChips;

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
        binding.etTutorSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s != null ? s.toString() : "";
                binding.btnTutorClearSearch.setVisibility(searchQuery.isEmpty() ? View.GONE : View.VISIBLE);
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
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

        binding.chipUnder200k.setOnClickListener(v -> {
            onlyUnder170k = !onlyUnder170k;
            renderQuickToggle(binding.chipUnder200k, onlyUnder170k, R.color.surface_container_low, R.color.on_surface);
            applyFilters();
        });

        binding.chipRating49.setOnClickListener(v -> {
            onlyHighRating = !onlyHighRating;
            renderQuickToggle(binding.chipRating49, onlyHighRating, R.color.accent_coral_subtle, R.color.tertiary);
            applyFilters();
        });

        binding.btnTutorResetFilters.setOnClickListener(v -> resetFilters());
        binding.btnTutorFilterToggle.setOnClickListener(v -> {
            if (hasActiveFilters()) {
                resetFilters();
                UiComponentHelper.showToastMessage(binding.getRoot(), "Đã đặt lại bộ lọc gia sư");
            } else {
                onlyHighRating = true;
                renderQuickToggle(binding.chipRating49, true, R.color.accent_coral_subtle, R.color.tertiary);
                applyFilters();
                UiComponentHelper.showToastMessage(binding.getRoot(), "Đang lọc gia sư đánh giá 4.95★+");
            }
        });
        binding.cardSmartSuggestion.setOnClickListener(v ->
                UiComponentHelper.navigateSafely(binding.getRoot(), R.id.action_tutor_list_to_booking, null)
        );
    }

    private boolean hasActiveFilters() {
        return !"Tất cả".equals(selectedSubject)
                || onlyUnder170k
                || onlyHighRating
                || !searchQuery.trim().isEmpty();
    }

    private void resetFilters() {
        selectedSubject = "Tất cả";
        onlyUnder170k = false;
        onlyHighRating = false;
        binding.etTutorSearch.setText("");
        renderSubjectChips();
        renderQuickToggle(binding.chipUnder200k, false, R.color.surface_container_low, R.color.on_surface);
        renderQuickToggle(binding.chipRating49, false, R.color.accent_coral_subtle, R.color.tertiary);
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

    private void renderQuickToggle(@NonNull TextView chip, boolean active, int defaultBgRes, int defaultFgRes) {
        Context ctx = requireContext();
        int bg = ContextCompat.getColor(ctx, active ? R.color.primary : defaultBgRes);
        int fg = ContextCompat.getColor(ctx, active ? R.color.on_primary : defaultFgRes);
        chip.setBackgroundTintList(ColorStateList.valueOf(bg));
        chip.setTextColor(fg);
    }

    private void applyFilters() {
        String q = searchQuery.trim().toLowerCase(Locale.ROOT);
        List<TutorSearchAdapter.TutorCardModel> filtered = new ArrayList<>();
        for (TutorSearchAdapter.TutorCardModel item : allTutors) {
            boolean matchesSubject = "Tất cả".equals(selectedSubject) || item.subjectKey.equals(selectedSubject);
            boolean matchesQuery = q.isEmpty() || item.searchBlob().contains(q);
            boolean matchesPrice = !onlyUnder170k || item.priceValue <= 170000;
            boolean matchesRating = !onlyHighRating || item.rating >= 4.95;
            if (matchesSubject && matchesQuery && matchesPrice && matchesRating) {
                filtered.add(item);
            }
        }
        adapter.submitList(filtered);
        binding.tvTutorCount.setText(String.format(Locale.US, "%d gia sư", filtered.size()));
        binding.viewFilterActiveDot.setVisibility(hasActiveFilters() ? View.VISIBLE : View.GONE);
        binding.layoutTutorSearchEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subjectChips = null;
        binding = null;
    }
}
