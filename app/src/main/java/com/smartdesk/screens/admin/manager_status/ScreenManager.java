package com.smartdesk.screens.admin.manager_status;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.smartdesk.R;
import com.smartdesk.constants.Constants;
import com.smartdesk.constants.FirebaseConstants;
import com.smartdesk.databinding.ScreenAdminHomeBinding;
import com.smartdesk.databinding.ScreenManagerBinding;
import com.smartdesk.screens.admin._home.desk_user.PagerAdapterDeskUser;
import com.smartdesk.screens.admin.desk_user_status.ScreenBlockedDeskUser;
import com.smartdesk.screens.admin.information.ScreenInformation;
import com.smartdesk.screens.user_management.login.ScreenLogin;
import com.smartdesk.screens.user_management.notification.ScreenNotification;
import com.smartdesk.screens.user_management.setting.ScreenAdminSetting;
import com.smartdesk.utility.UtilityFunctions;
import com.smartdesk.utility.memory.MemoryCache;

public class ScreenManager extends AppCompatActivity {

    ScreenManagerBinding binding;
    Activity context;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        new MemoryCache().clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ScreenManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        actionBar("Manager Requests");
        initViewPager();
        UtilityFunctions.setupUI(binding.parent, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initViewPager() {
        PagerAdapterManager sectionsPagerAdapter = new PagerAdapterManager(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    public void actionBar(String actionTitle) {
        Toolbar a = findViewById(R.id.actionbarInclude).findViewById(R.id.toolbar);
        setSupportActionBar(a);
        ((TextView) findViewById(R.id.actionbarInclude).findViewById(R.id.actionTitleBar)).setText(actionTitle);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.icon_chevron_left_blue));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    //======================================== Show Loading bar ==============================================
    private ObjectAnimator anim;
    private Boolean isLoad;

    public void startAnim() {
        isLoad = true;
        binding.loadingView.setVisibility(View.VISIBLE);
        binding.loadingImage.setAlpha((float) 0.2);
        anim = UtilityFunctions.loadingAnim(context, binding.loadingImage);
        binding.loadingView.setOnTouchListener((v, event) -> isLoad);
    }

    public void stopAnim() {
        anim.end();
        binding.loadingView.setVisibility(View.GONE);
        binding.bgMain.setAlpha((float) 1);
        isLoad = false;
    }
    //======================================== Show Loading bar ==============================================
}