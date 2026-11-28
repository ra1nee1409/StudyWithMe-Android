package com.studywithme.android.feature.tutor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.studywithme.android.feature.tutor.ui.TutorSearchAdapter;

import org.junit.Test;

import java.util.List;

public class TutorSearchFilterTest {

    private final List<TutorSearchAdapter.TutorCardModel> tutors = TutorSearchAdapter.sampleTutors();

    @Test
    public void sortByRatingDescAndAsc_ordersCorrectly() {
        List<TutorSearchAdapter.TutorCardModel> highFirst = TutorSearchAdapter.filterAndSort(
                tutors, "Tất cả", "", null, null, TutorSearchAdapter.SortOption.RATING_DESC
        );
        assertEquals(5.0, highFirst.get(0).rating, 0.001);
        assertEquals(4.92, highFirst.get(highFirst.size() - 1).rating, 0.001);

        List<TutorSearchAdapter.TutorCardModel> lowFirst = TutorSearchAdapter.filterAndSort(
                tutors, "Tất cả", "", null, null, TutorSearchAdapter.SortOption.RATING_ASC
        );
        assertEquals(4.92, lowFirst.get(0).rating, 0.001);
        assertEquals(5.0, lowFirst.get(lowFirst.size() - 1).rating, 0.001);
    }

    @Test
    public void sortByPriceAscAndDesc_ordersCorrectly() {
        List<TutorSearchAdapter.TutorCardModel> cheapFirst = TutorSearchAdapter.filterAndSort(
                tutors, "Tất cả", "", null, null, TutorSearchAdapter.SortOption.PRICE_ASC
        );
        assertEquals(150000, cheapFirst.get(0).priceValue);
        assertEquals(180000, cheapFirst.get(cheapFirst.size() - 1).priceValue);

        List<TutorSearchAdapter.TutorCardModel> expensiveFirst = TutorSearchAdapter.filterAndSort(
                tutors, "Tất cả", "", null, null, TutorSearchAdapter.SortOption.PRICE_DESC
        );
        assertEquals(180000, expensiveFirst.get(0).priceValue);
        assertEquals(150000, expensiveFirst.get(expensiveFirst.size() - 1).priceValue);
    }

    @Test
    public void customPriceRange_filtersWithinUserBounds() {
        List<TutorSearchAdapter.TutorCardModel> range = TutorSearchAdapter.filterAndSort(
                tutors, "Tất cả", "", 160000, 170000, TutorSearchAdapter.SortOption.PRICE_ASC
        );
        assertEquals(2, range.size());
        assertEquals(160000, range.get(0).priceValue);
        assertEquals(170000, range.get(1).priceValue);

        List<TutorSearchAdapter.TutorCardModel> inverted = TutorSearchAdapter.filterAndSort(
                tutors, "Tất cả", "", 170000, 160000, TutorSearchAdapter.SortOption.PRICE_ASC
        );
        assertEquals(2, inverted.size());
        assertEquals(160000, inverted.get(0).priceValue);
        assertEquals(170000, inverted.get(1).priceValue);

        List<TutorSearchAdapter.TutorCardModel> none = TutorSearchAdapter.filterAndSort(
                tutors, "Tất cả", "", 300000, null, TutorSearchAdapter.SortOption.DEFAULT
        );
        assertTrue(none.isEmpty());
    }
}
