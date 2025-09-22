package com.example.letmecook.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.letmecook.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Load fragment pertama (HomeFragment)
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment(), "Let Me Cook 🧑‍🍳");
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                replaceFragment(new HomeFragment(), "Let Me Cook 🧑‍🍳");
                return true;
            } else if (itemId == R.id.navigation_favorite) {
                replaceFragment(new WishlistFragment(), "My Wishlist 🔖");
                return true;
            }
            return false;
        });
    }

    private void replaceFragment(Fragment fragment, String title) {
        toolbar.setTitle(title);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        fragmentTransaction.commit();
    }
}