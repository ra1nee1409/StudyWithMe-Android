package com.studywithme.android.core.navigation;

import com.studywithme.android.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Navigation destination configuration for top-level BottomNavigationView tabs.
 */
public final class NavigationHelper {

    private NavigationHelper() {
    }

    public static Set<Integer> getTopLevelDestinationIds() {
        return new HashSet<>(Arrays.asList(
                R.id.homeFragment,
                R.id.courseListFragment,
                R.id.tutorListFragment,
                R.id.conversationListFragment,
                R.id.profileFragment
        ));
    }

    public static boolean shouldShowBottomBar(int destinationId) {
        return getTopLevelDestinationIds().contains(destinationId);
    }
}
