package com.studywithme.android.feature.course.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.studywithme.android.MainActivity;
import com.studywithme.android.R;
import com.studywithme.android.core.ui.components.UiComponentHelper;
import com.studywithme.android.databinding.FragmentCourseListBinding;

public class CourseListFragment extends Fragment {

    private FragmentCourseListBinding binding;
    private int selectedTabIndex = 0;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentCourseListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tabActiveClasses.setOnClickListener(v -> selectTab(0));
        binding.tabUpcomingClasses.setOnClickListener(v -> selectTab(1));
        binding.tabCompletedClasses.setOnClickListener(v -> selectTab(2));

        binding.btnReschedule.setOnClickListener(v ->
                UiComponentHelper.navigateSafely(binding.getRoot(), R.id.action_course_list_to_booking, null)
        );
        View.OnClickListener openDetail = v ->
                UiComponentHelper.navigateSafely(binding.getRoot(), R.id.action_course_list_to_detail, null);
        binding.btnJoinStudyRoom.setOnClickListener(openDetail);
        binding.btnClassMaterials.setOnClickListener(openDetail);
        binding.btnViewRoadmap1.setOnClickListener(openDetail);
        binding.btnSubmitHomework.setOnClickListener(openDetail);
        binding.btnReportDetailHeader.setOnClickListener(openDetail);
        binding.btnViewScoreSheet.setOnClickListener(openDetail);

        View.OnClickListener openChat = v -> switchTab(R.id.conversationListFragment);
        binding.btnClassChatTutor.setOnClickListener(openChat);
        binding.btnClassChat1.setOnClickListener(openChat);
        binding.btnClassChat2.setOnClickListener(openChat);

        binding.btnExploreMoreSubjects.setOnClickListener(v -> switchTab(R.id.tutorListFragment));
        binding.btnEmptyExploreTutors.setOnClickListener(v -> switchTab(R.id.tutorListFragment));
        binding.btnEmptyBookTrial.setOnClickListener(v ->
                UiComponentHelper.navigateSafely(binding.getRoot(), R.id.action_course_list_to_booking, null)
        );

        selectTab(selectedTabIndex);
    }

    private void selectTab(int index) {
        selectedTabIndex = index;
        styleTab(binding.tabActiveClasses, index == 0);
        styleTab(binding.tabUpcomingClasses, index == 1);
        styleTab(binding.tabCompletedClasses, index == 2);

        boolean showActive = index == 0;
        binding.layoutActiveClasses.setVisibility(showActive ? View.VISIBLE : View.GONE);
        binding.layoutEmptyClasses.setVisibility(showActive ? View.GONE : View.VISIBLE);

        if (index == 1) {
            binding.tvEmptyClassTitle.setText("Bạn chưa có buổi học nào\nsắp diễn ra");
        } else if (index == 2) {
            binding.tvEmptyClassTitle.setText("Chưa có lớp học nào\nđã hoàn thành");
        }
    }

    private void styleTab(@NonNull TextView tab, boolean active) {
        Context ctx = requireContext();
        if (active) {
            tab.setBackgroundResource(R.drawable.bg_rounded_12);
            tab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.surface_card)));
            tab.setTextColor(ContextCompat.getColor(ctx, R.color.primary));
            tab.setTypeface(null, Typeface.BOLD);
            tab.setElevation(2f);
        } else {
            tab.setBackground(null);
            tab.setTextColor(ContextCompat.getColor(ctx, R.color.on_surface_variant));
            tab.setTypeface(null, Typeface.NORMAL);
            tab.setElevation(0f);
        }
    }

    private void switchTab(int destinationId) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).selectBottomTab(destinationId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
