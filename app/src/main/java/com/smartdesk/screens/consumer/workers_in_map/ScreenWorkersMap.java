package com.smartdesk.screens.consumer.workers_in_map;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.smartdesk.R;
import com.smartdesk.model.hire_worker.WorkerRequestDTO;
import com.smartdesk.model.signup.SignupMechanicDTO;
import com.smartdesk.screens.consumer.track_mechanic.ScreenTrackMechanic;
import com.smartdesk.utility.location.FusedLocation;
import com.smartdesk.utility.memory.MemoryCache;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;

import static com.smartdesk.constants.Constants.USER_DOCUMENT_ID;
import static com.smartdesk.constants.Constants.USER_MOBILE;
import static com.smartdesk.constants.Constants.USER_NAME;
import static com.smartdesk.constants.Constants.USER_PROFILE;
import static com.smartdesk.constants.Constants.const_WorkerRequestDTO;
import static com.smartdesk.constants.FirebaseConstants.firebaseFirestore;
import static com.smartdesk.constants.FirebaseConstants.usersCollection;
import static com.smartdesk.constants.PermisionCode.MY_LOCATION_PERMISSIONS_CODE;
import static com.smartdesk.utility.UtilityFunctions.alertNoteWithOkButton;
import static com.smartdesk.utility.UtilityFunctions.loadingAnim;
import static com.smartdesk.utility.UtilityFunctions.setupUI;

public class ScreenWorkersMap extends AppCompatActivity {

    private Activity context;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        new MemoryCache().clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_worker_map);
        context = this;
        const_WorkerRequestDTO = new WorkerRequestDTO();
        const_WorkerRequestDTO.setName(USER_NAME);
        const_WorkerRequestDTO.setNumber(USER_MOBILE);
        const_WorkerRequestDTO.setProfileImage(USER_PROFILE);
        const_WorkerRequestDTO.setConsumerDocumentID(USER_DOCUMENT_ID);
        actionBar("Workers List");
        initLoadingBarItems();
        initViewPager();
        setupUI(findViewById(R.id.parent), this);
    }

    FragmentMapView fragmentMapView;

    public void getLocation(FragmentMapView fragmentMapView) {
        startAnim();
        this.fragmentMapView = fragmentMapView;
        new FusedLocation(context, false, fragmentMapView).startLocationUpdates(false);
        if (askForLocationPermission()) {
            new FusedLocation(context, false, fragmentMapView).startLocationUpdates(false);
        }
        realTimeListner();
    }

    public void stopOnUiThread() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                stopAnim();
            } catch (Exception ex) {
            }
        }, 0);
    }

    private void initViewPager() {
        PagerAdapterWorkersMap sectionsPagerAdapter = new PagerAdapterWorkersMap(this, getSupportFragmentManager());
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
    private LinearLayout load, bg_main;
    private ObjectAnimator anim;
    private ImageView progressBar;
    private Boolean isLoad;

    private void initLoadingBarItems() {
        load = findViewById(R.id.loading_view);
        bg_main = findViewById(R.id.bg_main);
        progressBar = findViewById(R.id.loading_image);
    }

    public void startAnim() {
        isLoad = true;
        load.setVisibility(View.VISIBLE);
        bg_main.setAlpha((float) 0.2);
        anim = loadingAnim(this, progressBar);
        load.setOnTouchListener((v, event) -> isLoad);
    }

    public void stopAnim() {
        anim.end();
        load.setVisibility(View.GONE);
        bg_main.setAlpha((float) 1);
        isLoad = false;
    }
    //======================================== Show Loading bar ==============================================

    public boolean askForLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean allTrue = true;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                allTrue = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_PERMISSIONS_CODE);
                else
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_PERMISSIONS_CODE);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                allTrue = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_LOCATION_PERMISSIONS_CODE);
                else
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_LOCATION_PERMISSIONS_CODE);
            }
            return allTrue;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_LOCATION_PERMISSIONS_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (fragmentMapView != null)
                        getLocation(fragmentMapView);
                    else {
                        new FusedLocation(context, false).startLocationUpdates(false);
                        if (askForLocationPermission()) {
                            new FusedLocation(context, false).startLocationUpdates(false);
                        }
                    }
                } else {
                    boolean showRationale = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        try {
                            showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                            if (!showRationale)
                                dialogForNeverAskAgain("Prompt to the Application Permission Setting & Allow Location");
                            else {
                                locationDialog("You can't go further until you didn't allow Location Permission");
                            }
                        } catch (Exception ex) {
                        }
                    }
                }
                stopOnUiThread();
                break;
            }
        }
    }

    //Take you to the app setting where you allow permission
    public void dialogForNeverAskAgain(String msg) {
        try {
            stopOnUiThread();
            final AlertDialog confirmationAlert = new AlertDialog.Builder(this).create();
            final View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog_return, null);
            ((TextView) dialogView.findViewById(R.id.noteText)).setText(msg);
            ((TextView) dialogView.findViewById(R.id.noteTitle)).setText("Location Permission");
            ((Button) dialogView.findViewById(R.id.yesbtn)).setText("Go");
            ((Button) dialogView.findViewById(R.id.nobtn)).setText("Back");
            dialogView.findViewById(R.id.yesbtn).setOnClickListener(v -> {
                confirmationAlert.dismiss();
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, MY_LOCATION_PERMISSIONS_CODE);
                } else {
                    if (askForLocationPermission()) {
                        new FusedLocation(context, false).startLocationUpdates(false);
                    }
                }
            });
            dialogView.findViewById(R.id.nobtn).setOnClickListener(v -> {
                confirmationAlert.dismiss();
                finish();
            });
            confirmationAlert.setView(dialogView);
            confirmationAlert.setCancelable(false);
            confirmationAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmationAlert.show();
        } catch (Exception ex) {

        }
    }

    public void locationDialog(final String msg) {
        new Handler().postDelayed(() -> {
            try {
                stopOnUiThread();
                final AlertDialog confirmationAlert = new AlertDialog.Builder(context).create();
                final View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog_return, null);
                ((TextView) dialogView.findViewById(R.id.noteText)).setText(msg);
                ((TextView) dialogView.findViewById(R.id.noteTitle)).setText("Location Permission");
                ((Button) dialogView.findViewById(R.id.yesbtn)).setText("Allow");
                ((Button) dialogView.findViewById(R.id.nobtn)).setText("Back");
                dialogView.findViewById(R.id.yesbtn).setOnClickListener(v -> {
                    confirmationAlert.dismiss();
                    if (askForLocationPermission()) {
                        new FusedLocation(context, false).startLocationUpdates(false);
                    }
                });
                dialogView.findViewById(R.id.nobtn).setOnClickListener(v -> {
                    confirmationAlert.dismiss();
                    finish();
                });
                confirmationAlert.setView(dialogView);
                confirmationAlert.setCancelable(false);
                confirmationAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                confirmationAlert.show();
            } catch (Exception ex) {

            }
        }, 0);
    }

    public void realTimeListner() {
        try {
            final DocumentReference docRef = firebaseFirestore.collection(usersCollection).document(USER_DOCUMENT_ID);
            docRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    return;
                }
                try {
                    if (snapshot != null && snapshot.exists()) {
                        SignupMechanicDTO signupMechanicDTO = snapshot.toObject(SignupMechanicDTO.class);
                        if (signupMechanicDTO.getHired()) {
                            alertNoteWithOkButton(context, "Worker Accepted", "Worker accepted your request"
                                    , Gravity.CENTER, R.color.whatsapp_green_dark, R.color.white, false, false, new Intent(context, ScreenTrackMechanic.class));
                        } else {
                        }
                    } else {
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}