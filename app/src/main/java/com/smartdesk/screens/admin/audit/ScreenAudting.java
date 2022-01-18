package com.smartdesk.screens.admin.audit;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smartdesk.R;
import com.smartdesk.constants.FirebaseConstants;
import com.smartdesk.constants.PermisionCode;
import com.smartdesk.firebase.AuditLogModel;
import com.smartdesk.utility.UtilityFunctions;
import com.smartdesk.utility.library.CustomEditext;
import com.smartdesk.utility.memory.MemoryCache;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScreenAudting extends AppCompatActivity {

    private Activity context;
    //======================================== Show Loading bar ==============================================
    private LinearLayout load, bg_main;
    private ObjectAnimator anim;
    private ImageView progressBar;
    private Boolean isLoad;

    private DatePickerDialog startDateListener, endDateListener;
    private CustomEditext startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_auditing);
        context = this;
        askForPermission();
        actionBar("Auditing & Logs");
        initLoadingBarItems();
        setDateListneres();
        getData();
    }

    private void setDateListneres() {
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);

        startDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mmonth = c.get(Calendar.MONTH);
            int mdate = c.get(Calendar.DAY_OF_MONTH);
            int myear = c.get(Calendar.YEAR);

            startDateListener = new DatePickerDialog(context, R.style.DialogTheme, (view, year, month, dayOfMonth) -> {
                c.set(year, month, dayOfMonth);
                String date = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
                startDate.setText(date);
            }, myear, mmonth, mdate);
            startDateListener.getDatePicker().setMaxDate(System.currentTimeMillis());
            startDateListener.show();
        });

        endDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mmonth = c.get(Calendar.MONTH);
            int mdate = c.get(Calendar.DAY_OF_MONTH);
            int myear = c.get(Calendar.YEAR);

            endDateListener = new DatePickerDialog(context, R.style.DialogTheme, (view, year, month, dayOfMonth) -> {
                c.set(year, month, dayOfMonth);
                String date = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
                endDate.setText(date);
            }, myear, mmonth, mdate);
            endDateListener.getDatePicker().setMaxDate(System.currentTimeMillis());
            endDateListener.show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        new MemoryCache().clear();
    }

    @SuppressLint("SetTextI18n")
    public void getData() {
//        startAnim();
    }

    public void actionBar(String actionTitle) {
        Toolbar a = findViewById(R.id.actionbarInclude).findViewById(R.id.toolbar);
        setSupportActionBar(a);
        ((TextView) findViewById(R.id.actionbarInclude).findViewById(R.id.actionTitleBar)).setText(actionTitle);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(context, R.drawable.icon_chevron_left_blue));
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

    public void LoginLogoutLogs(View view) {
        String startDateStr = startDate.getText().toString().trim();
        String endDateStr = endDate.getText().toString().trim();
        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            UtilityFunctions.alertNoteWithOkButton(context, "Date Input", "Please provide both Start and End date to generate logs.\n\n Thank You!", Gravity.CENTER, R.color.SmartDesk_Orange, R.color.black_color, false, false, null);
        } else {
            try {
                Date startDateTime = new SimpleDateFormat("dd/MM/yyyy").parse(startDateStr);
                Date endDateTime = new SimpleDateFormat("dd/MM/yyyy").parse(endDateStr);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endDateTime);
                calendar.add(Calendar.DATE, 1);
                endDateTime = calendar.getTime();

                Query queryNumber = FirebaseConstants.firebaseFirestore.collection(FirebaseConstants.loginAuditCollection);
                queryNumber.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    String logContent = "";
                    for (DocumentSnapshot docs : queryDocumentSnapshots.getDocuments()) {
                        AuditLogModel logs = docs.toObject(AuditLogModel.class);
                        if (logs.date.getTime() >= startDateTime.getTime() &&
                                logs.date.getTime() <= calendar.getTime().getTime()) {
                            logContent += logs.getContent() + "\n";
                        }
                    }
                    Date currentTime = Calendar.getInstance().getTime();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
                    generateNoteOnSD("Log-File-" + dateFormat.format(currentTime)+".txt", logContent);
                });
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void deskCreationDeletionLogsBTN(View view) {
    }

    public void deskBookLogsBTN(View view) {
    }

    public void generateNoteOnSD(String sFileName, String sBody) {
        boolean isOkay = true;
        try {
            File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() +"/Logs");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
//            if (gpxfile.exists ()) gpxfile.delete ();
//            gpxfile.createNewFile();

            FileWriter writer = new FileWriter(gpxfile);
            try {
                writer.append(sBody);
                writer.flush();
                writer.close();
                UtilityFunctions.alertNoteWithOkButton(context, "Log", "log file has been generated.\n\nYou can see your file in download folder.\n\n Thank You!", Gravity.CENTER, R.color.whatsapp_green_dark, R.color.white, false, false, null);
            } catch (IOException e) {
                isOkay = false;
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
            UtilityFunctions.alertNoteWithOkButton(context, "Log", "log file has not been generated.\n\n" + e.getMessage(), Gravity.CENTER, R.color.SmartDesk_Orange, R.color.black_color, false, false, null);
        }
        if (!isOkay)
            UtilityFunctions.alertNoteWithOkButton(context, "Log", "log file has not been generated.\n\nSomething went wrong" , Gravity.CENTER, R.color.SmartDesk_Orange, R.color.black_color, false, false, null);

    }

    public boolean askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean allTrue = true;
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                allTrue = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE))
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PermisionCode.MY_STORAGE_PERMISSION_CODE);
                else
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PermisionCode.MY_STORAGE_PERMISSION_CODE);
            } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                allTrue = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermisionCode.MY_STORAGE_PERMISSION_CODE);
                else
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermisionCode.MY_STORAGE_PERMISSION_CODE);
            } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                allTrue = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA))
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PermisionCode.MY_CAMERA_PERMISSION_CODE);
                else
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PermisionCode.MY_CAMERA_PERMISSION_CODE);
            }
            return allTrue;
        } else {
            return true;
        }
    }

    //======================================== Show Loading bar ==============================================
}