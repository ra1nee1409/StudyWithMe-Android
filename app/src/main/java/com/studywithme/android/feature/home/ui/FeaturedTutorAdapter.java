package com.studywithme.android.feature.home.ui;

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
import com.studywithme.android.core.ui.NetworkImageLoader;
import com.studywithme.android.databinding.ItemHomeTutorBinding;
import com.studywithme.android.feature.home.data.HomeModels;

import java.util.Locale;
import java.util.Objects;

public class FeaturedTutorAdapter
        extends ListAdapter<HomeModels.HomeTutorItem, FeaturedTutorAdapter.TutorViewHolder> {

    public interface OnTutorActionListener {
        void onTutorClick(@NonNull HomeModels.HomeTutorItem tutor);
        void onBookTrialClick(@NonNull HomeModels.HomeTutorItem tutor);
    }

    private static final DiffUtil.ItemCallback<HomeModels.HomeTutorItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<HomeModels.HomeTutorItem>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull HomeModels.HomeTutorItem oldItem,
                        @NonNull HomeModels.HomeTutorItem newItem
                ) {
                    return Objects.equals(oldItem.id, newItem.id)
                            && oldItem.name.equals(newItem.name);
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull HomeModels.HomeTutorItem oldItem,
                        @NonNull HomeModels.HomeTutorItem newItem
                ) {
                    return oldItem.name.equals(newItem.name)
                            && oldItem.subtitle.equals(newItem.subtitle)
                            && oldItem.priceText.equals(newItem.priceText)
                            && Objects.equals(oldItem.avatarUrl, newItem.avatarUrl)
                            && Double.compare(oldItem.rating, newItem.rating) == 0;
                }
            };

    private final OnTutorActionListener listener;

    public FeaturedTutorAdapter(@NonNull OnTutorActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public TutorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHomeTutorBinding binding = ItemHomeTutorBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new TutorViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class TutorViewHolder extends RecyclerView.ViewHolder {
        private final ItemHomeTutorBinding binding;

        TutorViewHolder(@NonNull ItemHomeTutorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull HomeModels.HomeTutorItem item, @NonNull OnTutorActionListener listener) {
            Context ctx = binding.getRoot().getContext();
            binding.tvTutorName.setText(item.name);
            binding.tvTutorSubtitle.setText(item.subtitle);

            String ratingStr = item.rating == Math.floor(item.rating)
                    ? String.format(Locale.US, "%.1f", item.rating)
                    : String.format(Locale.US, "%.2f", item.rating);
            binding.tvTutorRating.setText(ratingStr);

            if (item.reviewCount > 0) {
                binding.tvTutorReviewCount.setVisibility(View.VISIBLE);
                binding.tvTutorReviewCount.setText(
                        String.format(Locale.US, "(%d)", item.reviewCount)
                );
            } else {
                binding.tvTutorReviewCount.setVisibility(View.GONE);
            }

            binding.tvTagPrimary.setText(item.primaryTag);
            int primaryBg = ContextCompat.getColor(
                    ctx,
                    item.warmTag ? R.color.accent_coral_subtle : R.color.accent_cyan_subtle
            );
            int primaryText = ContextCompat.getColor(
                    ctx,
                    item.warmTag ? R.color.tertiary : R.color.primary
            );
            binding.tvTagPrimary.setBackgroundTintList(ColorStateList.valueOf(primaryBg));
            binding.tvTagPrimary.setTextColor(primaryText);

            binding.tvTagSecondary.setText(item.secondaryTag);

            binding.tvTutorPrice.setText(item.priceText);
            binding.tvPriceUnit.setVisibility(item.showUnitSuffix ? View.VISIBLE : View.GONE);

            if (item.avatarUrl != null && !item.avatarUrl.trim().isEmpty()) {
                binding.ivTutorAvatar.setImageTintList(null);
                binding.ivTutorAvatar.setPadding(0, 0, 0, 0);
                NetworkImageLoader.load(binding.ivTutorAvatar, item.avatarUrl, R.drawable.ic_person);
            } else {
                int pad = Math.round(11 * ctx.getResources().getDisplayMetrics().density);
                int bgRes = item.warmTag ? R.color.accent_coral_subtle : R.color.accent_cyan_subtle;
                int tintRes = item.warmTag ? R.color.tertiary : R.color.primary;
                binding.ivTutorAvatar.setBackgroundColor(ContextCompat.getColor(ctx, bgRes));
                binding.ivTutorAvatar.setPadding(pad, pad, pad, pad);
                binding.ivTutorAvatar.setImageResource(R.drawable.ic_person);
                binding.ivTutorAvatar.setImageTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(ctx, tintRes)
                ));
            }

            binding.getRoot().setOnClickListener(v -> listener.onTutorClick(item));
            binding.btnBookTrial.setOnClickListener(v -> listener.onBookTrialClick(item));
        }
    }
}
