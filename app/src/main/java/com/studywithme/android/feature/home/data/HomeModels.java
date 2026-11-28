package com.studywithme.android.feature.home.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * DTOs matching StudyWithMe Spring Boot PublicApiController responses and Home UI models.
 */
public final class HomeModels {

    private HomeModels() {
    }

    public static class BannerDto {
        @SerializedName("id")
        public Long id;
        @SerializedName("tieuDe")
        public String tieuDe;
        @SerializedName("linkAnhBanner")
        public String linkAnhBanner;
        @SerializedName("linkChuyenHuong")
        public String linkChuyenHuong;
    }

    public static class CategoryDto {
        @SerializedName("id")
        public Long id;
        @SerializedName("tenDanhMuc")
        public String tenDanhMuc;
        @SerializedName("duongDanSlug")
        public String duongDanSlug;
        @SerializedName("loaiDanhMucTen")
        public String loaiDanhMucTen;
        @SerializedName("soKhoaHoc")
        public long soKhoaHoc;
        @SerializedName("soLopHoc")
        public long soLopHoc;
    }

    public static class FeaturedTutorDto {
        @SerializedName("id")
        public Long id;
        @SerializedName("hoTen")
        public String hoTen;
        @SerializedName("anhDaiDien")
        public String anhDaiDien;
        @SerializedName("tieuDeChuyenMon")
        public String tieuDeChuyenMon;
        @SerializedName("gioiThieuBanThan")
        public String gioiThieuBanThan;
        @SerializedName("rating")
        public Double rating;
    }

    public static class TutorClassDto {
        @SerializedName("id")
        public Long id;
        @SerializedName("tieuDeLop")
        public String tieuDeLop;
        @SerializedName("hocPhiMoiHocVien")
        public BigDecimal hocPhiMoiHocVien;
    }

    public static class HomeCategoryItem {
        @Nullable
        public final Long id;
        @NonNull
        public final String title;
        @NonNull
        public final String subtitle;

        public HomeCategoryItem(@Nullable Long id, @NonNull String title, @NonNull String subtitle) {
            this.id = id;
            this.title = title;
            this.subtitle = subtitle;
        }
    }

    public static class HomeTutorItem {
        @Nullable
        public final Long id;
        @NonNull
        public final String name;
        @NonNull
        public final String subtitle;
        @Nullable
        public final String avatarUrl;
        public final double rating;
        public final int reviewCount;
        @NonNull
        public final String primaryTag;
        @NonNull
        public final String secondaryTag;
        public final boolean warmTag;
        @NonNull
        public final String priceText;
        public final boolean showUnitSuffix;
        @NonNull
        public final String searchBlob;

        public HomeTutorItem(
                @Nullable Long id,
                @NonNull String name,
                @NonNull String subtitle,
                @Nullable String avatarUrl,
                double rating,
                int reviewCount,
                @NonNull String primaryTag,
                @NonNull String secondaryTag,
                boolean warmTag,
                @NonNull String priceText,
                boolean showUnitSuffix,
                @NonNull String searchBlob
        ) {
            this.id = id;
            this.name = name;
            this.subtitle = subtitle;
            this.avatarUrl = avatarUrl;
            this.rating = rating;
            this.reviewCount = reviewCount;
            this.primaryTag = primaryTag;
            this.secondaryTag = secondaryTag;
            this.warmTag = warmTag;
            this.priceText = priceText;
            this.showUnitSuffix = showUnitSuffix;
            this.searchBlob = searchBlob;
        }
    }

    public static class HomeUiData {
        @Nullable
        public final String bannerBadgeText;
        @NonNull
        public final List<HomeCategoryItem> categories;
        @NonNull
        public final List<HomeTutorItem> tutors;
        public final long unreadNotifications;
        public final boolean syncedFromApi;

        public HomeUiData(
                @Nullable String bannerBadgeText,
                @NonNull List<HomeCategoryItem> categories,
                @NonNull List<HomeTutorItem> tutors,
                long unreadNotifications,
                boolean syncedFromApi
        ) {
            this.bannerBadgeText = bannerBadgeText;
            this.categories = categories;
            this.tutors = tutors;
            this.unreadNotifications = unreadNotifications;
            this.syncedFromApi = syncedFromApi;
        }
    }

    @NonNull
    public static String formatVnd(@Nullable BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "Liên hệ";
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("vi-VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        return formatter.format(amount.longValue()) + "đ";
    }

    @NonNull
    public static List<HomeCategoryItem> mapCategoriesFromApi(@Nullable List<CategoryDto> dtos) {
        List<HomeCategoryItem> defaults = defaultCategories();
        if (dtos == null || dtos.isEmpty()) {
            return defaults;
        }
        List<HomeCategoryItem> mapped = new ArrayList<>();
        for (int i = 0; i < Math.min(4, dtos.size()); i++) {
            CategoryDto dto = dtos.get(i);
            String title = safeText(dto.tenDanhMuc, defaults.get(i).title);
            long total = dto.soKhoaHoc + dto.soLopHoc;
            String subtitle = total > 0
                    ? total + " khóa/lớp"
                    : safeText(dto.loaiDanhMucTen, defaults.get(i).subtitle);
            mapped.add(new HomeCategoryItem(dto.id, title, subtitle));
        }
        while (mapped.size() < 4) {
            mapped.add(defaults.get(mapped.size()));
        }
        return mapped;
    }

    @NonNull
    public static List<HomeTutorItem> mapTutorsFromApi(
            @Nullable List<FeaturedTutorDto> dtos,
            @NonNull Map<Long, BigDecimal> lowestFeeByTutorId
    ) {
        if (dtos == null || dtos.isEmpty()) {
            return defaultTutors();
        }
        List<HomeTutorItem> result = new ArrayList<>();
        for (int i = 0; i < dtos.size(); i++) {
            FeaturedTutorDto dto = dtos.get(i);
            String name = safeText(dto.hoTen, "Gia sư StudyWithMe");
            String specialty = safeText(dto.tieuDeChuyenMon, "Giảng viên kiểm duyệt");
            String bio = dto.gioiThieuBanThan != null ? dto.gioiThieuBanThan : "";
            double rating = dto.rating != null && dto.rating > 0 ? dto.rating : 5.0;

            String[] tags = extractTags(specialty, bio);
            boolean warmTag = tags[0].toLowerCase(Locale.ROOT).contains("ielts")
                    || tags[0].toLowerCase(Locale.ROOT).contains("anh")
                    || i % 2 == 1;

            BigDecimal fee = dto.id != null ? lowestFeeByTutorId.get(dto.id) : null;
            boolean hasPrice = fee != null && fee.compareTo(BigDecimal.ZERO) > 0;
            String priceText = hasPrice ? formatVnd(fee) : "Liên hệ";

            String blob = (name + " " + specialty + " " + bio + " " + tags[0] + " " + tags[1])
                    .toLowerCase(Locale.ROOT);
            result.add(new HomeTutorItem(
                    dto.id,
                    name,
                    specialty,
                    dto.anhDaiDien,
                    rating,
                    0,
                    tags[0],
                    tags[1],
                    warmTag,
                    priceText,
                    hasPrice,
                    blob
            ));
        }
        return result;
    }

    @NonNull
    public static List<HomeTutorItem> filterTutors(
            @NonNull List<HomeTutorItem> all,
            @Nullable String query,
            @Nullable String tagFilter
    ) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        String tag = tagFilter == null ? "" : tagFilter.trim().toLowerCase(Locale.ROOT);
        boolean hasTag = !tag.isEmpty() && !tag.equals("tất cả");
        if (q.isEmpty() && !hasTag) {
            return all;
        }
        List<HomeTutorItem> filtered = new ArrayList<>();
        for (HomeTutorItem item : all) {
            boolean matchesQuery = q.isEmpty() || item.searchBlob.contains(q);
            boolean matchesTag = !hasTag || matchesTagToken(item.searchBlob, tag);
            if (matchesQuery && matchesTag) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    private static boolean matchesTagToken(@NonNull String blob, @NonNull String tag) {
        if (blob.contains(tag)) {
            return true;
        }
        if (tag.contains("toán")) {
            return blob.contains("toán");
        }
        if (tag.contains("ielts")) {
            return blob.contains("ielts") || blob.contains("anh") || blob.contains("speaking");
        }
        if (tag.contains("hóa") || tag.contains("lý")) {
            return blob.contains("hóa") || blob.contains("lý");
        }
        if (tag.contains("cambridge") || tag.contains("1-5")) {
            return blob.contains("tiểu học") || blob.contains("cambridge") || blob.contains("anh");
        }
        if (tag.contains("6-9")) {
            return blob.contains("9") || blob.contains("thcs") || blob.contains("10");
        }
        if (tag.contains("10-12")) {
            return blob.contains("10") || blob.contains("12") || blob.contains("thpt") || blob.contains("hóa");
        }
        return false;
    }

    @NonNull
    private static String[] extractTags(@NonNull String specialty, @NonNull String bio) {
        String[] parts = specialty.split("[|•,/\\-–]+");
        String first = parts.length > 0 && !parts[0].trim().isEmpty()
                ? truncateTag(parts[0].trim())
                : "Gia sư 1-1";
        String second;
        if (parts.length > 1 && !parts[1].trim().isEmpty()) {
            second = truncateTag(parts[1].trim());
        } else if (!bio.trim().isEmpty()) {
            second = "Kèm cặp tận tâm";
        } else {
            second = "Lịch học linh hoạt";
        }
        return new String[]{first, second};
    }

    @NonNull
    private static String truncateTag(@NonNull String raw) {
        return raw.length() > 22 ? raw.substring(0, 21).trim() + "…" : raw;
    }

    @NonNull
    private static String safeText(@Nullable String value, @NonNull String fallback) {
        return (value != null && !value.trim().isEmpty()) ? value.trim() : fallback;
    }

    @NonNull
    public static List<HomeCategoryItem> defaultCategories() {
        return Arrays.asList(
                new HomeCategoryItem(null, "Tiểu học", "Lớp 1 - 5"),
                new HomeCategoryItem(null, "THCS", "Lớp 6 - 9"),
                new HomeCategoryItem(null, "THPT", "Lớp 10 - 12"),
                new HomeCategoryItem(null, "IELTS/Tin", "Chứng chỉ")
        );
    }

    @NonNull
    public static List<HomeTutorItem> defaultTutors() {
        return Arrays.asList(
                new HomeTutorItem(
                        1L,
                        "ThS. Lê Hoàng Yến",
                        "ĐH Sư Phạm Hà Nội",
                        null,
                        4.98,
                        142,
                        "Toán nâng cao",
                        "Luyện thi 9 lên 10",
                        false,
                        "160.000đ",
                        true,
                        "ths. lê hoàng yến đh sư phạm hà nội toán nâng cao luyện thi 9 lên 10 thcs lớp 6-9"
                ),
                new HomeTutorItem(
                        2L,
                        "Thầy Trần Hoàng",
                        "IELTS 8.5 (Univ of Bristol)",
                        null,
                        5.0,
                        98,
                        "IELTS Speaking",
                        "Giao tiếp cấp tốc",
                        true,
                        "180.000đ",
                        true,
                        "thầy trần hoàng ielts 8.5 anh giao tiếp speaking cambridge chứng chỉ"
                ),
                new HomeTutorItem(
                        3L,
                        "Cô Lê Thị Thu Hà",
                        "ThS. ĐH KHTN Hà Nội",
                        null,
                        4.95,
                        210,
                        "Hóa 10-12",
                        "Lấy gốc Hóa cấp tốc",
                        false,
                        "150.000đ",
                        true,
                        "cô lê thị thu hà đh khtn hóa 10-12 lấy gốc hóa cấp tốc thpt lớp 10-12"
                )
        );
    }

    @NonNull
    public static HomeUiData defaultUiData() {
        return new HomeUiData(
                null,
                defaultCategories(),
                defaultTutors(),
                1L,
                false
        );
    }
}
