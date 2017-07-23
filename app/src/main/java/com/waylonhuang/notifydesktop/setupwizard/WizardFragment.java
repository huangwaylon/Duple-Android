package com.waylonhuang.notifydesktop.setupwizard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badoualy.stepperindicator.StepperIndicator;
import com.waylonhuang.notifydesktop.R;

import static com.waylonhuang.notifydesktop.setupwizard.SignInFragment.LIFE_CYCLE_INTENT;

public class WizardFragment extends Fragment {
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    public WizardFragment() {
        // Required empty public constructor
    }

    public static WizardFragment newInstance() {
        WizardFragment fragment = new WizardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.wtf("WIZARD", "onCreate");
        setRetainInstance(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wizard, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Setup");

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) view.findViewById(R.id.wizard_vp);
        mPagerAdapter = new ScreenSlidePagerAdapter(getActivity().getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        // Bind the step indicator to the adapter.
        StepperIndicator indicator = (StepperIndicator) view.findViewById(R.id.step_indicator);
        indicator.setViewPager(mPager);

        return view;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new EnableServiceFragment();
                case 1:
                    return new SignInFragment();
                case 2:
                    return new WebsiteFragment();
                default:
                    return new DoneSetupFragment();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.wtf("WIZARD", "onPause");
        Intent intent = new Intent(LIFE_CYCLE_INTENT);
        getActivity().sendBroadcast(intent);
    }
}
