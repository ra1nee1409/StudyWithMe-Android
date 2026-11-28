package com.studywithme.android;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.studywithme.android.core.navigation.NavigationHelper;
import com.studywithme.android.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            appBarConfiguration = new AppBarConfiguration.Builder(
                    NavigationHelper.getTopLevelDestinationIds()
            ).build();

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

            setupBrandHeaderActions();

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                boolean isTopLevel = NavigationHelper.shouldShowBottomBar(destination.getId());
                // All 5 top-level tabs share the StudyWithMe brand header; sub-screens use MaterialToolbar with back arrow.
                binding.brandHeaderContainer.setVisibility(isTopLevel ? View.VISIBLE : View.GONE);
                binding.toolbar.setVisibility(isTopLevel ? View.GONE : View.VISIBLE);

                int barVis = isTopLevel ? View.VISIBLE : View.GONE;
                binding.bottomNavigation.setVisibility(barVis);
                binding.bottomNavDivider.setVisibility(barVis);
            });
        }
    }

    private void setupBrandHeaderActions() {
        binding.btnHeaderSearch.setOnClickListener(v -> {
            if (navController == null) {
                return;
            }
            if (navController.getCurrentDestination() != null
                    && navController.getCurrentDestination().getId() != R.id.tutorListFragment) {
                binding.bottomNavigation.setSelectedItemId(R.id.tutorListFragment);
            }
        });

        binding.btnHeaderNotifications.setOnClickListener(v -> {
            if (navController == null) {
                return;
            }
            try {
                navController.navigate(R.id.notificationFragment);
            } catch (IllegalArgumentException | IllegalStateException ignored) {
            }
        });

        binding.btnHeaderProfile.setOnClickListener(v ->
                binding.bottomNavigation.setSelectedItemId(R.id.profileFragment)
        );
    }

    public void setUnreadNotificationIndicator(boolean hasUnread) {
        if (binding != null) {
            binding.viewUnreadDot.setVisibility(hasUnread ? View.VISIBLE : View.GONE);
        }
    }

    public void selectBottomTab(int destinationId) {
        if (binding != null) {
            binding.bottomNavigation.setSelectedItemId(destinationId);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (navController != null && appBarConfiguration != null) {
            return NavigationUI.navigateUp(navController, appBarConfiguration)
                    || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}
