package com.studywithme.android.feature.profile.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.studywithme.android.MainActivity;
import com.studywithme.android.R;
import com.studywithme.android.core.ui.components.UiComponentHelper;
import com.studywithme.android.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View.OnClickListener openEdit = v ->
                UiComponentHelper.navigateSafely(binding.getRoot(), R.id.action_profile_to_edit, null);
        binding.btnEditAvatar.setOnClickListener(openEdit);
        binding.rowEditProfile.setOnClickListener(openEdit);

        binding.rowTutorReports.setOnClickListener(v -> switchTab(R.id.courseListFragment));
        binding.rowFavoriteTutors.setOnClickListener(v -> switchTab(R.id.tutorListFragment));
        binding.btnTopUpSessions.setOnClickListener(v -> switchTab(R.id.tutorListFragment));

        binding.btnWalletPromos.setOnClickListener(v ->
                UiComponentHelper.showToastMessage(binding.getRoot(), "Bạn có 250.000đ xu ưu đãi khả dụng")
        );
        binding.rowPaymentHistory.setOnClickListener(v ->
                UiComponentHelper.showToastMessage(binding.getRoot(), "Lịch sử học phí sẽ đồng bộ khi nối API")
        );
        binding.rowPolicy.setOnClickListener(v ->
                UiComponentHelper.showToastMessage(binding.getRoot(), "Cam kết hoàn tiền 100% trong 30 ngày đầu")
        );
        binding.switchReminders.setOnCheckedChangeListener((buttonView, isChecked) ->
                UiComponentHelper.showToastMessage(
                        binding.getRoot(),
                        isChecked ? "Đã bật nhắc lịch học trước 30 phút" : "Đã tắt nhắc lịch học"
                )
        );

        binding.rowChangePassword.setOnClickListener(v ->
                UiComponentHelper.navigateSafely(binding.getRoot(), R.id.action_profile_to_change_password, null)
        );
        binding.btnLogout.setOnClickListener(v ->
                UiComponentHelper.navigateSafely(binding.getRoot(), R.id.action_profile_to_login, null)
        );
        binding.rowSupportHotline.setOnClickListener(v ->
                UiComponentHelper.openDialer(requireContext(), getString(R.string.hotline_uri))
        );
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
