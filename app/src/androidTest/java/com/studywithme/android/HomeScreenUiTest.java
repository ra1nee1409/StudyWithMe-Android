package com.studywithme.android;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented UI/UX verification across all 5 top-level bottom navigation tabs (API 37 safe).
 */
@RunWith(AndroidJUnit4.class)
public class HomeScreenUiTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void homeScreen_rendersBrandLogoHeaderHeroTutorsAndBottomNav() {
        waitForAdapterAtLeast(R.id.rvFeaturedTutors, 3);
        activityRule.getScenario().onActivity(activity -> {
            View header = activity.findViewById(R.id.homeHeaderBar);
            View toolbar = activity.findViewById(R.id.toolbar);
            TextView heroTitle = activity.findViewById(R.id.tvHeroTitle);
            BottomNavigationView bottomNav = activity.findViewById(R.id.bottom_navigation);

            assertNotNull(header);
            assertEquals(View.VISIBLE, header.getVisibility());
            assertEquals(View.VISIBLE, activity.findViewById(R.id.ivBrandMark).getVisibility());
            assertEquals(View.VISIBLE, activity.findViewById(R.id.ivBrandWordmark).getVisibility());
            assertEquals(View.GONE, toolbar.getVisibility());
            assertTrue(heroTitle.getText().toString().contains("Gia sư 1-Kèm-1 chất lượng"));
            assertEquals(5, bottomNav.getMenu().size());
        });
    }

    @Test
    public void searchAndFilter_updatesTutorListAndResetsCleanly() {
        activityRule.getScenario().onActivity(activity ->
                activity.findViewById(R.id.chipIelts).performClick()
        );
        waitForAdapterCount(R.id.rvFeaturedTutors, 1);

        activityRule.getScenario().onActivity(activity -> {
            EditText searchInput = activity.findViewById(R.id.etSearch);
            searchInput.setText("khong_ton_tai_xyz");
            assertEquals(View.VISIBLE, activity.findViewById(R.id.btnClearSearch).getVisibility());
            assertEquals(View.VISIBLE, activity.findViewById(R.id.layoutTutorEmpty).getVisibility());
        });
        waitForAdapterCount(R.id.rvFeaturedTutors, 0);

        activityRule.getScenario().onActivity(activity ->
                activity.findViewById(R.id.btnResetFilter).performClick()
        );
        waitForAdapterAtLeast(R.id.rvFeaturedTutors, 3);

        activityRule.getScenario().onActivity(activity -> {
            EditText searchInput = activity.findViewById(R.id.etSearch);
            assertEquals("", searchInput.getText().toString());
            assertEquals(View.GONE, activity.findViewById(R.id.btnClearSearch).getVisibility());
            assertEquals(View.GONE, activity.findViewById(R.id.layoutTutorEmpty).getVisibility());
        });
    }

    @Test
    public void allFiveTabs_renderSharedHeaderAndCoreUiStates() {
        // Tab 2: Tìm gia sư
        selectTabAndWait(R.id.tutorListFragment);
        waitForAdapterCount(R.id.rvTutors, 4);
        activityRule.getScenario().onActivity(activity -> {
            assertEquals(View.VISIBLE, activity.findViewById(R.id.brandHeaderContainer).getVisibility());
            assertEquals(View.GONE, activity.findViewById(R.id.toolbar).getVisibility());
        });

        // Tab 3: Lớp của tôi (active + empty toggle)
        selectTabAndWait(R.id.courseListFragment);
        activityRule.getScenario().onActivity(activity -> {
            assertEquals(View.VISIBLE, activity.findViewById(R.id.layoutActiveClasses).getVisibility());
            assertEquals(View.GONE, activity.findViewById(R.id.layoutEmptyClasses).getVisibility());
            activity.findViewById(R.id.tabUpcomingClasses).performClick();
            assertEquals(View.GONE, activity.findViewById(R.id.layoutActiveClasses).getVisibility());
            assertEquals(View.VISIBLE, activity.findViewById(R.id.layoutEmptyClasses).getVisibility());
        });

        // Tab 4: Tin nhắn (compact list + simple empty filter)
        selectTabAndWait(R.id.conversationListFragment);
        waitForAdapterCount(R.id.rvConversations, 4);
        activityRule.getScenario().onActivity(activity ->
                activity.findViewById(R.id.chipChatClass).performClick()
        );
        waitForAdapterCount(R.id.rvConversations, 0);
        activityRule.getScenario().onActivity(activity ->
                assertEquals(View.VISIBLE, activity.findViewById(R.id.tvChatEmpty).getVisibility())
        );

        // Tab 5: Tài khoản
        selectTabAndWait(R.id.profileFragment);
        activityRule.getScenario().onActivity(activity -> {
            assertNotNull(activity.findViewById(R.id.btnTopUpSessions));
            assertEquals(View.VISIBLE, activity.findViewById(R.id.btnLogout).getVisibility());
        });

        // Back to Home
        selectTabAndWait(R.id.homeFragment);
        activityRule.getScenario().onActivity(activity ->
                assertEquals(View.VISIBLE, activity.findViewById(R.id.brandHeaderContainer).getVisibility())
        );
    }

    private void selectTabAndWait(int tabId) {
        activityRule.getScenario().onActivity(activity -> {
            BottomNavigationView bottomNav = activity.findViewById(R.id.bottom_navigation);
            bottomNav.setSelectedItemId(tabId);
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    private void waitForAdapterCount(int recyclerViewId, int expected) {
        int[] actual = new int[]{-1};
        long deadline = System.currentTimeMillis() + 2000L;
        while (System.currentTimeMillis() < deadline) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            activityRule.getScenario().onActivity(activity -> {
                RecyclerView rv = activity.findViewById(recyclerViewId);
                actual[0] = (rv != null && rv.getAdapter() != null) ? rv.getAdapter().getItemCount() : -1;
            });
            if (actual[0] == expected) {
                return;
            }
            try {
                Thread.sleep(50L);
            } catch (InterruptedException ignored) {
            }
        }
        assertEquals(expected, actual[0]);
    }

    private void waitForAdapterAtLeast(int recyclerViewId, int minCount) {
        int[] actual = new int[]{-1};
        long deadline = System.currentTimeMillis() + 2000L;
        while (System.currentTimeMillis() < deadline) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            activityRule.getScenario().onActivity(activity -> {
                RecyclerView rv = activity.findViewById(recyclerViewId);
                actual[0] = (rv != null && rv.getAdapter() != null) ? rv.getAdapter().getItemCount() : -1;
            });
            if (actual[0] >= minCount) {
                return;
            }
            try {
                Thread.sleep(50L);
            } catch (InterruptedException ignored) {
            }
        }
        assertTrue("Expected at least " + minCount + " items, got " + actual[0], actual[0] >= minCount);
    }
}
