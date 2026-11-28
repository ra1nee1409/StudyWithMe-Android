package com.studywithme.android.feature.home.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.studywithme.android.R;
import com.studywithme.android.core.ui.NetworkImageLoader;
import com.studywithme.android.databinding.ItemHomeReviewBinding;
import com.studywithme.android.feature.home.data.HomeModels;

import java.util.Objects;

public class ParentReviewAdapter
        extends ListAdapter<HomeModels.ParentReviewItem, ParentReviewAdapter.ReviewViewHolder> {

    private static final DiffUtil.ItemCallback<HomeModels.ParentReviewItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<HomeModels.ParentReviewItem>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull HomeModels.ParentReviewItem oldItem,
                        @NonNull HomeModels.ParentReviewItem newItem
                ) {
                    return oldItem.authorName.equals(newItem.authorName);
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull HomeModels.ParentReviewItem oldItem,
                        @NonNull HomeModels.ParentReviewItem newItem
                ) {
                    return oldItem.quote.equals(newItem.quote)
                            && oldItem.authorRole.equals(newItem.authorRole)
                            && Objects.equals(oldItem.avatarUrl, newItem.avatarUrl);
                }
            };

    public ParentReviewAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHomeReviewBinding binding = ItemHomeReviewBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ReviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final ItemHomeReviewBinding binding;

        ReviewViewHolder(@NonNull ItemHomeReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull HomeModels.ParentReviewItem item) {
            Context ctx = binding.getRoot().getContext();
            binding.tvReviewQuote.setText(item.quote);
            binding.tvAuthorName.setText(item.authorName);
            binding.tvAuthorRole.setText(item.authorRole);

            int bgColor = ContextCompat.getColor(
                    ctx,
                    item.warmAvatarBg ? R.color.accent_coral_subtle : R.color.accent_cyan_subtle
            );
            int tintColor = ContextCompat.getColor(
                    ctx,
                    item.warmAvatarBg ? R.color.tertiary : R.color.primary
            );
            binding.reviewAvatarBg.setBackgroundTintList(ColorStateList.valueOf(bgColor));

            if (item.avatarUrl != null && !item.avatarUrl.trim().isEmpty()) {
                binding.ivReviewAvatar.setImageTintList(null);
                binding.ivReviewAvatar.setPadding(0, 0, 0, 0);
                NetworkImageLoader.load(binding.ivReviewAvatar, item.avatarUrl, R.drawable.ic_person);
            } else {
                binding.ivReviewAvatar.setImageResource(R.drawable.ic_person);
                binding.ivReviewAvatar.setImageTintList(ColorStateList.valueOf(tintColor));
            }
        }
    }
}
