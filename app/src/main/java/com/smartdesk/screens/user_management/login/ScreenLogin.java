package com.smartdesk.screens.user_management.login;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.smartdesk.R;
import com.smartdesk.constants.Constants;
import com.smartdesk.constants.FirebaseConstants;
import com.smartdesk.databinding.ScreenLoginBinding;
import com.smartdesk.screens.worker.track_consumer.ScreenTrackConsumerHired;
import com.smartdesk.screens.user_management.forget_password.ScreenForgetPasswordStep1;
import com.smartdesk.utility.UtilityFunctions;
import com.smartdesk.utility.encryption.EncryptPassword;
import com.smartdesk.model.signup.SignupMechanicDTO;
import com.smartdesk.screens.admin._home.ScreenAdminHome;
import com.smartdesk.screens.consumer._home.ScreenConsumerHome;
import com.smartdesk.screens.consumer.track_mechanic.ScreenTrackMechanic;
import com.smartdesk.screens.worker._home.ScreenWorkerHome;
import com.smartdesk.screens.worker.sign_up.ScreenWorkerSignup;
import com.smartdesk.utility.memory.MemoryCache;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class ScreenLogin extends AppCompatActivity {

    private Activity context;
    private ScreenLoginBinding binding;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        new MemoryCache().clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initLoadingBarItems();
        UtilityFunctions.setupUI(findViewById(R.id.bg_main), this);
    }


    private void init() {
        binding = ScreenLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
    }

    public void forgotPassword(View view) {
        UtilityFunctions.removeFocusFromEditexts(findViewById(R.id.bg_main), context);
        UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenForgetPasswordStep1.class), false, 0);
    }

    public void signUP(View view) {
        try {
            UtilityFunctions.removeFocusFromEditexts(findViewById(R.id.bg_main), context);
            Constants.const_MechanicSignupDTO = null;
            Constants.const_ConsumerSignupDTO = null;
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.bottomDialogTheme);
            View bottomView = LayoutInflater.from(this).inflate(R.layout.alert_bottom_registration, findViewById(R.id.bottom_view));

            bottomView.findViewById(R.id.WorkerRegistration).setOnClickListener(v -> UtilityFunctions.sendIntentNormal(context, new Intent(ScreenLogin.this, ScreenWorkerSignup.class), false, 0));
            bottomView.findViewById(R.id.MangerRegistration).setOnClickListener(v -> Toast.makeText(context,"Not Implemented Yet",Toast.LENGTH_SHORT).show());
            bottomSheetDialog.setContentView(bottomView);
            bottomSheetDialog.show();
        }catch (Exception ex){

        }
    }


    public void login(View view) {
        UtilityFunctions.removeFocusFromEditexts(findViewById(R.id.bg_main), context);
        Boolean isOkay = true;
        String mobile = "", pass = "";
        try {
            mobile = UtilityFunctions.getStringFromMaskWithLengthLimit(binding.phone, 12);
            if (!UtilityFunctions.isValidPhone(mobile)) {
                binding.phone.setError("invalid number");
                isOkay = false;
            }

            if (!mobile.startsWith("07")) {
                binding.phone.setError("Number should start from 07");
                isOkay = false;
            }
        } catch (NullPointerException ex) {
            binding.phone.setError("invalid number");
            isOkay = false;
        }

        try {
            pass = UtilityFunctions.getStringFromEditTextWithLengthLimit(binding.password, 6);
        } catch (NullPointerException ex) {
            binding.password.setError("Invalid Password");
            isOkay = false;
        }

        if (pass.length() < 6) {
            binding.password.setError("Min Length should be 6");
            isOkay = false;
        } else {
            System.out.println("Valid");
        }

        if (isOkay) {
            try {
                mobile = mobile.replaceAll("-", "");
                System.out.println(mobile);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            final String finalMobile = mobile;
            final String finalPass = pass;
            new Handler(Looper.getMainLooper()).postDelayed(() -> new Handler().postDelayed((Runnable) () -> {
                startAnim();
                Query queryNumber = FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.usersCollection).whereEqualTo("workerPhone", finalMobile);
                queryNumber.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    new Thread(() -> {
                        List<SignupMechanicDTO> signupMechanicDTO = queryDocumentSnapshots.toObjects(SignupMechanicDTO.class);
                        if (signupMechanicDTO.isEmpty()) {
                            stopAnimOnUIThread();
                            UtilityFunctions.orangeSnackBar(context, "Phone Number is not Registered!", Snackbar.LENGTH_LONG);
                        } else {
                            String decryptPassword = "";
                            try {
                                decryptPassword = EncryptPassword.passwordDecryption(signupMechanicDTO.get(0).getWorkerPassword());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            stopAnimOnUIThread();
                            if (decryptPassword.equals(finalPass)) {
                                UtilityFunctions.scheduleJob(this, true);
                                if (signupMechanicDTO.get(0).getUserStatus().equals(Constants.activeStatus)) {
                                    String documentID = "";
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        documentID = document.getId();
                                        break;
                                    }
                                    Constants.USER_DOCUMENT_ID = documentID;
                                    Constants.USER_NAME = signupMechanicDTO.get(0).getWorkerName();
                                    Constants.USER_PROFILE = signupMechanicDTO.get(0).getProfilePicture();
                                    Constants.USER_MOBILE = signupMechanicDTO.get(0).getWorkerPhone();
                                    UtilityFunctions.saveLoginCredentialsInSharedPreference(context, finalMobile, finalPass, documentID, true);
                                    UtilityFunctions.greenSnackBar(context, "Login Successfully!", Snackbar.LENGTH_SHORT);

                                    if (signupMechanicDTO.get(0).getRole() == Constants.adminRole) {
                                        System.out.println("admin");
                                        Constants.USER_ROLE = Constants.adminRole;
                                        UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenAdminHome.class), true, Constants.changeIntentDelay);
                                    } else if (signupMechanicDTO.get(0).getRole() == Constants.workerRole) {
                                        System.out.println("Mechanic");
                                        Constants.USER_ROLE = Constants.workerRole;
                                        Constants.USER_MECHANIC_ONLINE = signupMechanicDTO.get(0).getOnline();
                                        if (!signupMechanicDTO.get(0).getHired())
                                            UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenWorkerHome.class), true, Constants.changeIntentDelay);
                                        else
                                            UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenTrackConsumerHired.class), true, Constants.changeIntentDelay);
                                    } else if (signupMechanicDTO.get(0).getRole() == Constants.consumerRole) {
                                        System.out.println("Consumer");
                                        Constants.USER_ROLE = Constants.consumerRole;
                                        if (!signupMechanicDTO.get(0).getHired())
                                            UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenConsumerHome.class), true, Constants.changeIntentDelay);
                                        else
                                            UtilityFunctions.sendIntentNormal(context, new Intent(context, ScreenTrackMechanic.class), true, Constants.changeIntentDelay);
                                    }
                                } else {
                                    UtilityFunctions.alertNoteWithOkButton(context, "Account Status", signupMechanicDTO.get(0).getUserStatus(), Gravity.CENTER, R.color.SmartDesk_Orange, R.color.black_color, false, false, null);
                                }

                            } else {
                                UtilityFunctions.orangeSnackBar((Activity) context, "Incorrect Password!", Snackbar.LENGTH_SHORT);
                            }
                        }
                    }).start();
                }).addOnFailureListener(e -> {
                    stopAnim();
                    UtilityFunctions.redSnackBar(context, "No Internet!", Snackbar.LENGTH_SHORT);
                });
            }, 0), 0);

        } else

            UtilityFunctions.orangeSnackBar(this, "Please Enter Credentials Properly!", Snackbar.LENGTH_SHORT);

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

    public void contactUs(View view) {
        UtilityFunctions.alertNoteWithOkButton(context, "Contact Us", "Please Contact us on " + Constants.emailID + " with your contact details and the issue you are facing", Gravity.CENTER, R.color.SmartDesk_Orange, R.color.black_color, false, false, null);
    }
    //======================================== Show Loading bar ==============================================

    public void stopAnimOnUIThread() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> stopAnim(), 0);
    }
}