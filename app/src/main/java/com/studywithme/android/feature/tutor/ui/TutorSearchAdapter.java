package com.studywithme.android.feature.tutor.ui;

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
import com.studywithme.android.databinding.ItemTutorSearchCardBinding;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TutorSearchAdapter
        extends ListAdapter<TutorSearchAdapter.TutorCardModel, TutorSearchAdapter.ViewHolder> {

    public static class TutorCardModel {
        public final long id;
        public final String cornerBadge;
        public final boolean warmBadge;
        public final String name;
        public final String university;
        public final double rating;
        public final String ratingMeta;
        public final String tag1;
        public final String tag2;
        public final String tag3;
        public final String studentsCount;
        public final String outcomeLabel;
        public final String outcomeValue;
        public final String nextSlot;
        public final String priceLabel;
        public final String priceText;
        public final int priceValue;
        public final String subjectKey;

        public TutorCardModel(
                long id, String cornerBadge, boolean warmBadge, String name, String university,
                double rating, String ratingMeta, String tag1, String tag2, String tag3,
                String studentsCount, String outcomeLabel, String outcomeValue, String nextSlot,
                String priceLabel, String priceText, int priceValue, String subjectKey
        ) {
            this.id = id;
            this.cornerBadge = cornerBadge;
            this.warmBadge = warmBadge;
            this.name = name;
            this.university = university;
            this.rating = rating;
            this.ratingMeta = ratingMeta;
            this.tag1 = tag1;
            this.tag2 = tag2;
            this.tag3 = tag3;
            this.studentsCount = studentsCount;
            this.outcomeLabel = outcomeLabel;
            this.outcomeValue = outcomeValue;
            this.nextSlot = nextSlot;
            this.priceLabel = priceLabel;
            this.priceText = priceText;
            this.priceValue = priceValue;
            this.subjectKey = subjectKey;
        }

        public String searchBlob() {
            return (name + " " + university + " " + tag1 + " " + tag2 + " " + tag3 + " " + subjectKey)
                    .toLowerCase(Locale.ROOT);
        }
    }

    public interface OnTutorCardListener {
        void onOpenProfile(@NonNull TutorCardModel model);
        void onBookTrial(@NonNull TutorCardModel model);
    }

    private static final DiffUtil.ItemCallback<TutorCardModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<TutorCardModel>() {
                @Override
                public boolean areItemsTheSame(@NonNull TutorCardModel oldItem, @NonNull TutorCardModel newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull TutorCardModel oldItem, @NonNull TutorCardModel newItem) {
                    return oldItem.name.equals(newItem.name)
                            && oldItem.priceValue == newItem.priceValue
                            && oldItem.nextSlot.equals(newItem.nextSlot);
                }
            };

    private final OnTutorCardListener listener;

    public TutorSearchAdapter(@NonNull OnTutorCardListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTutorSearchCardBinding binding = ItemTutorSearchCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTutorSearchCardBinding binding;

        ViewHolder(@NonNull ItemTutorSearchCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull TutorCardModel item, @NonNull OnTutorCardListener listener) {
            Context ctx = binding.getRoot().getContext();
            binding.tvCornerBadge.setText(item.cornerBadge);
            binding.tvCornerBadge.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(
                    ctx, item.warmBadge ? R.color.accent_coral_subtle : R.color.accent_cyan_subtle
            )));
            binding.tvCornerBadge.setTextColor(ContextCompat.getColor(
                    ctx, item.warmBadge ? R.color.tertiary : R.color.primary
            ));

            binding.ivAvatar.setBackgroundColor(ContextCompat.getColor(
                    ctx, item.warmBadge ? R.color.accent_coral_subtle : R.color.accent_cyan_subtle
            ));
            binding.ivAvatar.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(
                    ctx, item.warmBadge ? R.color.tertiary : R.color.primary
            )));

            binding.tvName.setText(item.name);
            binding.tvUniversity.setText(item.university);
            binding.tvRatingMeta.setText(item.ratingMeta);
            binding.tvTag1.setText(item.tag1);
            binding.tvTag2.setText(item.tag2);
            binding.tvTag3.setText(item.tag3);
            binding.tvMetricStudents.setText(item.studentsCount);
            binding.tvMetricOutcomeLabel.setText(item.outcomeLabel);
            binding.tvMetricOutcomeValue.setText(item.outcomeValue);
            binding.tvMetricSlot.setText(item.nextSlot);
            binding.tvPriceLabel.setText(item.priceLabel);
            binding.tvPrice.setText(item.priceText);

            binding.getRoot().setOnClickListener(v -> listener.onOpenProfile(item));
            binding.btnViewProfile.setOnClickListener(v -> listener.onOpenProfile(item));
            binding.btnBookTrial.setOnClickListener(v -> listener.onBookTrial(item));
        }
    }

    @NonNull
    public static List<TutorCardModel> sampleTutors() {
        return Arrays.asList(
                new TutorCardModel(
                        1L, "Hẹn lịch nhiều", true,
                        "ThS. Lê Hoàng Yến", "Thủ khoa ĐH Sư Phạm Hà Nội",
                        4.98, "4.98 • 142 đánh giá • 5 năm KN",
                        "Toán 9 ôn thi vào 10", "Toán nâng cao THCS", "Luyện đề chuyên Hà Nội",
                        "185 HS", "Đạt điểm >8.5", "94.2%", "Tối nay 19:30",
                        "Học phí trực tuyến", "160.000đ", 160000, "Môn Toán"
                ),
                new TutorCardModel(
                        2L, "IELTS 8.5 Master", false,
                        "Trần Hoàng Nam", "Cựu du học sinh Anh (Univ of Bristol)",
                        5.0, "5.0 • 210 đánh giá • Online ngay",
                        "IELTS Speaking & Writing", "Giao tiếp phản xạ chuẩn Anh", "Chữa bài 1-1",
                        "320+ HS", "Tăng band điểm", "98.5%", "Sáng mai 09:00",
                        "Học phí 1 kèm 1", "180.000đ", 180000, "Tiếng Anh / IELTS"
                ),
                new TutorCardModel(
                        3L, "Chuyên lấy gốc 12", true,
                        "ThS. Lê Thị Thu Hà", "Khoa Hóa - ĐH Khoa học Tự nhiên HN",
                        4.95, "4.95 • 210 đánh giá • 6 năm KN",
                        "Hóa 10 - 12", "Ôn thi ĐGNL ĐHQGHN", "Lấy gốc mất căn bản",
                        "245 HS", "Đỗ trường top", "91.8%", "Chiều nay 15:00",
                        "Học phí linh hoạt", "150.000đ", 150000, "Hóa Học"
                ),
                new TutorCardModel(
                        4L, "Giải Nhì QG Toán", false,
                        "Nguyễn Minh Quân", "ĐH Bách Khoa HN • Khoa CNTT",
                        4.92, "4.92 • 98 đánh giá • 3 năm KN",
                        "Vật Lý & Toán 12", "Tư duy giải nhanh trắc nghiệm", "Mục tiêu 9+ ĐH",
                        "110 HS", "Điểm 9+ QG", "89.5%", "Thứ 7 tuần này",
                        "Học phí tiêu chuẩn", "170.000đ", 170000, "Vật Lý"
                )
        );
    }
}
