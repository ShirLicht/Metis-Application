package com.example.admin.metis;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    enum AdapterVersion {BAR_MENU, BAR_CHAT}

    private AdapterVersion adapterVersion;

    public SectionsPagerAdapter(FragmentManager fm, AdapterVersion ver) {
        super(fm);
        adapterVersion = ver;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                switch (adapterVersion) {
                    case BAR_MENU:
                        return new FoodMenuFragment();
                    case BAR_CHAT:
                        return new ChatBarFragment();
                }

            case 1:
                switch (adapterVersion) {
                    case BAR_MENU:
                        return new AlcoDrinksFragment();
                    case BAR_CHAT:
                        return new ChatPersonalFragment();
                }

            case 2:
                switch (adapterVersion) {
                    case BAR_MENU:
                        return new NonAlcoDrinksFragment();
                    case BAR_CHAT:
                        return new ChatRequestsFragment();
                }

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                switch (adapterVersion) {
                    case BAR_MENU:
                        return "FOOD";
                    case BAR_CHAT:
                        return "GENERAL CHAT";
                }

            case 1:
                switch (adapterVersion) {
                    case BAR_MENU:
                        return "ALCOHOLIC DRINKS";
                    case BAR_CHAT:
                        return "PERSONAL CHAT";
                }

            case 2:
                switch (adapterVersion) {
                    case BAR_MENU:
                        return "NON ALCOHOLIC DRINKS";
                    case BAR_CHAT:
                        return "CHAT REQUESTS";
                }

            default:
                return null;
        }
    }
}
