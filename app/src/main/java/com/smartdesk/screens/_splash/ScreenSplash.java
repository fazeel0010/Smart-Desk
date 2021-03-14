package com.smartdesk.screens._splash;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartdesk.R;
import com.smartdesk.constants.Constants;
import com.smartdesk.constants.FirebaseConstants;
import com.smartdesk.databinding.ScreenSplashBinding;
import com.smartdesk.screens.admin._home.ScreenAdminHome;
import com.smartdesk.screens.desk_users_screens._home.ScreenDeskUserHome;
import com.smartdesk.screens.manager_screens._home.ScreenManagerHome;
import com.smartdesk.screens.user_management.login.ScreenLogin;
import com.smartdesk.utility.UtilityFunctions;
import com.smartdesk.utility.encryption.EncryptPassword;
import com.smartdesk.model.signup.SignupUserDTO;
import com.smartdesk.utility.memory.MemoryCache;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.smartdesk.constants.FirebaseConstants.firebaseFirestore;

public class ScreenSplash extends AppCompatActivity {

    ScreenSplashBinding binding;
    private Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ScreenSplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);

        UtilityFunctions.subscribeFCMTopic(FirebaseConstants.fcmTopic);
        initLoadingBarItems();
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        binding.welcometxt.startAnimation(animation);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                checkAccessToken();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1500);
    }

    private void checkAccessToken() {
        final SharedPreferences prefs = getSharedPreferences("info", Context.MODE_PRIVATE);
        final String documentId = prefs.getString(Constants.SP_DOCUMENT_ID, null);
        final String mobile = prefs.getString(Constants.SP_MOBILE, null);
        final String pass = prefs.getString(Constants.SP_PASSWORD, null);
        final Boolean isLogin = prefs.getBoolean(Constants.SP_ISLOGIN, false);
        Constants.USER_DOCUMENT_ID = documentId;
        if (documentId != null && isLogin) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startAnim();
                firebaseFirestore.collection(FirebaseConstants.usersCollection).whereEqualTo("workerPhone", mobile).get()
                        .addOnSuccessListener(task -> {
                    if (!task.isEmpty()) {
                        SignupUserDTO signupUserDTO = task.toObjects(SignupUserDTO.class).get(0);
                        new Thread(() -> {
                            String decryptPassword = "";
                            try {
                                decryptPassword = EncryptPassword.passwordDecryption(signupUserDTO.getWorkerPassword());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            stopAnimOnUIThread();
                            if (decryptPassword.equals(pass)) {
                                Constants.USER_NAME = signupUserDTO.getWorkerName();
                                Constants.USER_PROFILE = signupUserDTO.getProfilePicture();
                                Constants.USER_MOBILE = signupUserDTO.getWorkerPhone();
                                UtilityFunctions.scheduleJob(this, true);

                                if (signupUserDTO.getUserStatus().equals(Constants.activeStatus)) {
                                    UtilityFunctions.greenSnackBar(context, "Login Successfully!", Snackbar.LENGTH_SHORT);
                                    if (signupUserDTO.getRole().equals(Constants.adminRole)) {
                                        System.out.println("admin");
                                        Constants.USER_ROLE = Constants.adminRole;
                                        UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenAdminHome.class), true, Constants.changeIntentDelay);
                                    } else if (signupUserDTO.getRole().equals(Constants.deskUserRole)) {
                                        System.out.println("Mechanic");
                                        Constants.USER_ROLE = Constants.deskUserRole;
                                        UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenDeskUserHome.class), true, Constants.changeIntentDelay);
                                    } else if (signupUserDTO.getRole().equals(Constants.managerRole)) {
                                        System.out.println("Consumer");
                                        Constants.USER_ROLE = Constants.managerRole;
                                        UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenManagerHome.class), true, Constants.changeIntentDelay);
                                    }
                                } else {
                                    UtilityFunctions.setIsLoignSharedPreference(context, false);
                                    alertToMoveToLogin("Account Status", signupUserDTO.getUserStatus(), Gravity.CENTER);
                                }
                            } else {
                                UtilityFunctions.setIsLoignSharedPreference(context, false);
                                alertToMoveToLogin("Account Credentials", "Your Password has been changed!", Gravity.CENTER);
                            }
                        }).start();
                    } else {
                        stopAnimOnUIThread();
                        UtilityFunctions.setIsLoignSharedPreference(context, false);
                        UtilityFunctions.orangeSnackBar(context, "Phone Number is not Registered!", Snackbar.LENGTH_SHORT);
                        UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenLogin.class), true, Constants.changeIntentDelay);
                    }
                }).addOnFailureListener(e -> {
                    UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenLogin.class), true, 0);
                });
            }, 0);
        } else {
            UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenLogin.class), true, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        new MemoryCache().clear();
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
        anim = UtilityFunctions.loadingAnim(this, progressBar);
        load.setOnTouchListener((v, event) -> isLoad);
    }

    public void stopAnim() {
        anim.end();
        load.setVisibility(View.GONE);
        bg_main.setAlpha((float) 1);
        isLoad = false;
    }

    public void stopAnimOnUIThread() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> stopAnim(), 0);
    }
    //======================================== Show Loading bar ==============================================


    public void alertToMoveToLogin(String title, String textMsg, int gravity) {
        try {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                final AlertDialog confirmationAlert = new AlertDialog.Builder(context).create();
                final View dialogView = getLayoutInflater().inflate(R.layout.alert_note_dialog, null);
                ((TextView) dialogView.findViewById(R.id.noteTitle)).setText(title);
                ((TextView) dialogView.findViewById(R.id.noteText)).setText(textMsg);
                ((TextView) dialogView.findViewById(R.id.noteText)).setGravity(gravity);
                dialogView.findViewById(R.id.yesbtn).setOnClickListener(v -> {
                    confirmationAlert.dismiss();
                    UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenLogin.class), true, 0);
                });
                confirmationAlert.setView(dialogView);
                confirmationAlert.setCancelable(false);
                confirmationAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                confirmationAlert.show();
            }, 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}