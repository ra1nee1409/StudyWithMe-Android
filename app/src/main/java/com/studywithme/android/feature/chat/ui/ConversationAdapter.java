package com.studywithme.android.feature.chat.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.studywithme.android.R;
import com.studywithme.android.databinding.ItemConversationRowBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ConversationAdapter
        extends ListAdapter<ConversationAdapter.ConversationItem, ConversationAdapter.ViewHolder> {

    public static class ConversationItem {
        public final long id;
        public final String name;
        public final String roleBadge;
        public final String category;
        public final String timestamp;
        public final String lastMessage;
        public final boolean online;
        public final boolean isSupport;
        public final int unreadCount;

        public ConversationItem(
                long id, String name, String roleBadge, String category,
                String timestamp, String lastMessage, boolean online, boolean isSupport, int unreadCount
        ) {
            this.id = id;
            this.name = name;
            this.roleBadge = roleBadge;
            this.category = category;
            this.timestamp = timestamp;
            this.lastMessage = lastMessage;
            this.online = online;
            this.isSupport = isSupport;
            this.unreadCount = unreadCount;
        }

        public ConversationItem withUnreadCount(int newCount) {
            return new ConversationItem(
                    id, name, roleBadge, category, timestamp, lastMessage, online, isSupport, newCount
            );
        }

        public String searchBlob() {
            return (name + " " + roleBadge + " " + lastMessage).toLowerCase(Locale.ROOT);
        }
    }

    public interface OnConversationClickListener {
        void onConversationClick(@NonNull ConversationItem item);
    }

    private static final DiffUtil.ItemCallback<ConversationItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ConversationItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull ConversationItem oldItem, @NonNull ConversationItem newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull ConversationItem oldItem, @NonNull ConversationItem newItem) {
                    return oldItem.unreadCount == newItem.unreadCount
                            && oldItem.online == newItem.online
                            && oldItem.lastMessage.equals(newItem.lastMessage)
                            && oldItem.timestamp.equals(newItem.timestamp);
                }
            };

    private final OnConversationClickListener listener;

    public ConversationAdapter(@NonNull OnConversationClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConversationRowBinding binding = ItemConversationRowBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemConversationRowBinding binding;

        ViewHolder(@NonNull ItemConversationRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull ConversationItem item, @NonNull OnConversationClickListener listener) {
            Context ctx = binding.getRoot().getContext();
            binding.tvSenderName.setText(item.name);
            binding.tvRoleBadge.setText(item.roleBadge);
            binding.tvTimestamp.setText(item.timestamp);
            binding.tvLastMessage.setText(item.lastMessage);
            binding.viewOnlineDot.setVisibility(item.online ? View.VISIBLE : View.GONE);

            if (item.isSupport) {
                binding.avatarCircle.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.primary))
                );
                binding.ivAvatarIcon.setImageResource(R.drawable.ic_support);
                binding.ivAvatarIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.white))
                );
                binding.tvRoleBadge.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.primary))
                );
                binding.tvRoleBadge.setTextColor(ContextCompat.getColor(ctx, R.color.white));
            } else {
                binding.avatarCircle.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.accent_cyan_subtle))
                );
                binding.ivAvatarIcon.setImageResource(R.drawable.ic_person);
                binding.ivAvatarIcon.setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.primary))
                );
                binding.tvRoleBadge.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.accent_cyan_subtle))
                );
                binding.tvRoleBadge.setTextColor(ContextCompat.getColor(ctx, R.color.primary));
            }

            if (item.unreadCount > 0) {
                binding.tvUnreadBadge.setVisibility(View.VISIBLE);
                binding.tvUnreadBadge.setText(String.valueOf(item.unreadCount));
                binding.tvLastMessage.setTextColor(ContextCompat.getColor(ctx, R.color.on_surface));
            } else {
                binding.tvUnreadBadge.setVisibility(View.GONE);
                binding.tvLastMessage.setTextColor(ContextCompat.getColor(ctx, R.color.on_surface_variant));
            }

            binding.getRoot().setOnClickListener(v -> listener.onConversationClick(item));
        }
    }

    @NonNull
    public static List<ConversationItem> sampleConversations() {
        return new ArrayList<>(Arrays.asList(
                new ConversationItem(
                        1L, "ThS. Lê Hoàng Yến", "Toán 9", "Gia sư",
                        "10:25", "Cô đã gửi bài tập hình học buổi 7, em xem trước nhé!",
                        true, false, 2
                ),
                new ConversationItem(
                        2L, "Thầy Trần Hoàng Nam", "IELTS 8.5", "Gia sư",
                        "09:10", "Buổi tối nay mình luyện Speaking Part 2 chủ đề Travel.",
                        true, false, 1
                ),
                new ConversationItem(
                        3L, "Cố vấn StudyWithMe", "ADMIN", "Cố vấn 24/7",
                        "Hôm qua", "Chào bạn, lịch học thử tối nay lúc 19:30 đã được xác nhận.",
                        true, true, 0
                ),
                new ConversationItem(
                        4L, "Cô Lê Thị Thu Hà", "Hóa 11", "Gia sư",
                        "T2", "Em làm rất tốt phần cân bằng phản ứng oxi hóa khử!",
                        false, false, 0
                )
        ));
    }
}
