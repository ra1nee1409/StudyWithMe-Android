package com.studywithme.android.feature.home;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.studywithme.android.core.network.ApiResponse;
import com.studywithme.android.core.ui.NetworkImageLoader;
import com.studywithme.android.feature.home.data.HomeModels;

import org.junit.Test;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HomeModelsTest {

    @Test
    public void formatVnd_formatsPositiveAndFallback() {
        assertEquals("160.000đ", HomeModels.formatVnd(new BigDecimal("160000")));
        assertEquals("Liên hệ", HomeModels.formatVnd(BigDecimal.ZERO));
        assertEquals("Liên hệ", HomeModels.formatVnd(null));
    }

    @Test
    public void mapTutorsFromApi_mapsFieldsAndPrices() {
        HomeModels.FeaturedTutorDto dto = new HomeModels.FeaturedTutorDto();
        dto.id = 10L;
        dto.hoTen = "ThS. Nguyễn An";
        dto.tieuDeChuyenMon = "Toán nâng cao - Luyện thi vào 10";
        dto.anhDaiDien = "/uploads/avatar.jpg";
        dto.rating = 4.9;

        List<HomeModels.HomeTutorItem> mapped = HomeModels.mapTutorsFromApi(
                Collections.singletonList(dto),
                Collections.singletonMap(10L, new BigDecimal("200000"))
        );

        assertEquals(1, mapped.size());
        HomeModels.HomeTutorItem item = mapped.get(0);
        assertEquals("ThS. Nguyễn An", item.name);
        assertEquals("Toán nâng cao", item.primaryTag);
        assertEquals("Luyện thi vào 10", item.secondaryTag);
        assertEquals("200.000đ", item.priceText);
        assertTrue(item.showUnitSuffix);
    }

    @Test
    public void filterTutors_filtersBySearchAndTag() {
        List<HomeModels.HomeTutorItem> all = HomeModels.defaultTutors();

        assertEquals(3, HomeModels.filterTutors(all, "", "Tất cả").size());
        assertEquals(1, HomeModels.filterTutors(all, "", "IELTS 6.5+").size());
        assertEquals("Thầy Trần Hoàng", HomeModels.filterTutors(all, "IELTS", "Tất cả").get(0).name);
        assertTrue(HomeModels.filterTutors(all, "không tồn tại", "Tất cả").isEmpty());
    }

    @Test
    public void mapCategoriesFromApi_padsToFourSlots() {
        HomeModels.CategoryDto dto = new HomeModels.CategoryDto();
        dto.id = 5L;
        dto.tenDanhMuc = "Toán THPT";
        dto.soKhoaHoc = 3;
        dto.soLopHoc = 2;

        List<HomeModels.HomeCategoryItem> items = HomeModels.mapCategoriesFromApi(
                Collections.singletonList(dto)
        );
        assertEquals(4, items.size());
        assertEquals("Toán THPT", items.get(0).title);
        assertEquals("5 khóa/lớp", items.get(0).subtitle);
    }

    @Test
    public void resolveUrl_handlesRelativeAndAbsoluteUrls() {
        assertEquals(
                "http://10.0.2.2:8080/uploads/a.png",
                NetworkImageLoader.resolveUrl("http://10.0.2.2:8080/", "/uploads/a.png")
        );
        assertEquals(
                "https://cdn.example.com/a.png",
                NetworkImageLoader.resolveUrl("http://10.0.2.2:8080/", "https://cdn.example.com/a.png")
        );
        assertNull(NetworkImageLoader.resolveUrl("http://10.0.2.2:8080/", "  "));
    }

    @Test
    public void apiResponse_deserializesVietnameseAndEnglishKeys() {
        Gson gson = new Gson();
        Type type = new TypeToken<ApiResponse<Map<String, Long>>>() {}.getType();

        ApiResponse<Map<String, Long>> vi = gson.fromJson(
                "{\"thanhCong\":true,\"thongBao\":\"OK\",\"duLieu\":{\"count\":3}}",
                type
        );
        assertTrue(vi.isSuccess());
        assertEquals(Long.valueOf(3L), vi.getData().get("count"));

        ApiResponse<Map<String, Long>> en = gson.fromJson(
                "{\"success\":false,\"message\":\"Err\"}",
                type
        );
        assertFalse(en.isSuccess());
        assertEquals("Err", en.getMessage());
    }
}
