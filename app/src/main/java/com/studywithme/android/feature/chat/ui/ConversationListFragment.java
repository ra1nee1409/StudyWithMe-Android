package com.studywithme.android.feature.chat.ui;

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

import com.studywithme.android.MainActivity;
import com.studywithme.android.R;
import com.studywithme.android.core.ui.components.UiComponentHelper;
import com.studywithme.android.databinding.FragmentConversationListBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ConversationListFragment extends Fragment {

    private FragmentConversationListBinding binding;
    private ConversationAdapter adapter;
    private final List<ConversationAdapter.ConversationItem> allConversations =
            ConversationAdapter.sampleConversations();

    private String activeFilter = "Tất cả";
    private String query = "";
    private List<TextView> filterChips;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentConversationListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ConversationAdapter(item -> {
            markConversationRead(item.id);
            applyFilters();
            openChatRoom(item.id);
        });
        binding.rvConversations.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvConversations.setAdapter(adapter);

        setupInteractions();
        applyFilters();
    }

    private void setupInteractions() {
        binding.etChatSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                query = s != null ? s.toString() : "";
                binding.btnChatClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.etChatSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                UiComponentHelper.hideKeyboard(v);
                return true;
            }
            return false;
        });
        binding.btnChatClearSearch.setOnClickListener(v -> {
            binding.etChatSearch.setText("");
            UiComponentHelper.hideKeyboard(binding.etChatSearch);
        });

        filterChips = Arrays.asList(
                binding.chipChatAll,
                binding.chipChatTutor,
                binding.chipChatSupport,
                binding.chipChatClass
        );
        binding.chipChatAll.setOnClickListener(v -> selectFilter("Tất cả"));
        binding.chipChatTutor.setOnClickListener(v -> selectFilter("Gia sư"));
        binding.chipChatSupport.setOnClickListener(v -> selectFilter("Cố vấn 24/7"));
        binding.chipChatClass.setOnClickListener(v -> selectFilter("Lớp học"));

        binding.btnMarkAllRead.setOnClickListener(v -> {
            for (int i = 0; i < allConversations.size(); i++) {
                allConversations.set(i, allConversations.get(i).withUnreadCount(0));
            }
            applyFilters();
            UiComponentHelper.showToastMessage(binding.getRoot(), "Đã đánh dấu tất cả tin nhắn là đã đọc");
        });

        binding.quickOnline1.setOnClickListener(v -> openChatRoom(1L));
        binding.quickOnline2.setOnClickListener(v -> openChatRoom(2L));
        binding.quickOnline3.setOnClickListener(v -> openChatRoom(4L));
        binding.quickOnline4.setOnClickListener(v -> openChatRoom(3L));
        binding.btnAskStudyBot.setOnClickListener(v -> openChatRoom(0L));
    }

    private void selectFilter(@NonNull String filter) {
        activeFilter = filter;
        renderFilterChips();
        applyFilters();
    }

    private void markConversationRead(long id) {
        for (int i = 0; i < allConversations.size(); i++) {
            ConversationAdapter.ConversationItem item = allConversations.get(i);
            if (item.id == id && item.unreadCount > 0) {
                allConversations.set(i, item.withUnreadCount(0));
                break;
            }
        }
    }

    private void renderFilterChips() {
        Context ctx = requireContext();
        int activeBg = ContextCompat.getColor(ctx, R.color.primary);
        int activeFg = ContextCompat.getColor(ctx, R.color.on_primary);
        int idleBg = ContextCompat.getColor(ctx, R.color.surface_container);
        int idleFg = ContextCompat.getColor(ctx, R.color.on_surface_variant);

        String[] keys = {"Tất cả", "Gia sư", "Cố vấn 24/7", "Lớp học"};
        for (int i = 0; i < filterChips.size(); i++) {
            boolean selected = keys[i].equals(activeFilter);
            TextView chip = filterChips.get(i);
            chip.setBackgroundTintList(ColorStateList.valueOf(selected ? activeBg : idleBg));
            chip.setTextColor(selected ? activeFg : idleFg);
        }
    }

    private void applyFilters() {
        int totalUnread = 0;
        for (ConversationAdapter.ConversationItem item : allConversations) {
            totalUnread += item.unreadCount;
        }
        binding.chipChatAll.setText(
                totalUnread > 0 ? String.format(Locale.US, "Tất cả (%d)", totalUnread) : "Tất cả"
        );
        binding.btnMarkAllRead.setVisibility(totalUnread > 0 ? View.VISIBLE : View.GONE);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setUnreadNotificationIndicator(totalUnread > 0);
        }

        String q = query.trim().toLowerCase(Locale.ROOT);
        List<ConversationAdapter.ConversationItem> filtered = new ArrayList<>();
        for (ConversationAdapter.ConversationItem item : allConversations) {
            boolean matchesFilter = "Tất cả".equals(activeFilter) || item.category.equals(activeFilter);
            boolean matchesQuery = q.isEmpty() || item.searchBlob().contains(q);
            if (matchesFilter && matchesQuery) {
                filtered.add(item);
            }
        }
        adapter.submitList(filtered);
        binding.tvChatEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void openChatRoom(long conversationId) {
        if (binding == null) {
            return;
        }
        Bundle args = new Bundle();
        args.putLong("conversationId", conversationId);
        UiComponentHelper.navigateSafely(binding.getRoot(), R.id.action_conversations_to_chat_room, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        filterChips = null;
        binding = null;
    }
}
