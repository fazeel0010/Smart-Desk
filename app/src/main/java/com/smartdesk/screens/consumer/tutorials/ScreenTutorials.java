package com.smartdesk.screens.consumer.tutorials;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartdesk.R;
import com.smartdesk.utility.UtilityFunctions;
import com.smartdesk.model.tutorials.TutorialsDTO;
import com.smartdesk.utility.memory.MemoryCache;

import java.util.ArrayList;
import java.util.List;

public class ScreenTutorials extends AppCompatActivity {

    private Activity context;

    //RecyclerView Variables
    RecyclerView carRecylerview;
    List<TutorialsDTO> carVideosList = new ArrayList<>();

    RecyclerView bikeRecylerview;
    List<TutorialsDTO> bikeVideosList = new ArrayList<>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        new MemoryCache().clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_tutorials);
        context = this;
        actionBar("Video Tutorials");
        initLoadingBarItems();

        carVideosList.add(new TutorialsDTO("How Mass Air Flow Sensor Works", "UAmJ7tr605s"));
        carVideosList.add(new TutorialsDTO("Vehicle Fuel Pumps", "UUUmEYU8WJk"));
        carVideosList.add(new TutorialsDTO("How Vehicle Water Pump Works", "n0C6jItRtHA"));
        carVideosList.add(new TutorialsDTO("How Car Exhaust System Works", "FqZ19DbvzmM"));
        carVideosList.add(new TutorialsDTO("Car Batteries", "jIyA3hvKP7I"));
        carVideosList.add(new TutorialsDTO("How Power Steering Works", "drvSY-enrBE"));
        carVideosList.add(new TutorialsDTO("How Airbags Work", "6yXT_Uw61ng"));
        carVideosList.add(new TutorialsDTO("Types of Wiper Blades", "ef4khtgdvRE"));
        carVideosList.add(new TutorialsDTO("How Spark Plugs Work", "jreH-Y-eP7s"));
        carVideosList.add(new TutorialsDTO("How the Timing Belt Works", "LMCixq_FPus"));
        carVideosList.add(new TutorialsDTO("Oil Changes", "8kTKAG0U08M"));
        carVideosList.add(new TutorialsDTO("Headlamp Maintenance Options", "Hg39V2TpQto"));
        carVideosList.add(new TutorialsDTO("How Car Suspension Works", "Ww2fygySJl4"));
        carVideosList.add(new TutorialsDTO("How Vehicle Alternators Work", "66usCFVCBWc"));

        carRecylerview = (RecyclerView) findViewById(R.id.car_video_recyvlerview);
        carRecylerview.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        carRecylerview.setLayoutManager(linearLayoutManager);
        RecyclerAdapter adapter = new RecyclerAdapter(context, carVideosList);
        carRecylerview.setAdapter(adapter);
        carRecylerview.setVisibility(View.GONE);


        bikeVideosList.add(new TutorialsDTO("Understanding Motorcycle Clutch", "JhTf7cBeGcs"));
        bikeVideosList.add(new TutorialsDTO("How a motorcycle transmission works", "g8xnIFf4id4"));
        bikeVideosList.add(new TutorialsDTO("How Motorcycles Work - The Basics", "mdOJ717PKRc"));
        bikeVideosList.add(new TutorialsDTO("Understanding your motorcycle's brake | Disc Brake", "6c4deRAhqcA"));

        bikeRecylerview = (RecyclerView) findViewById(R.id.bike_video_recyvlerview);
        bikeRecylerview.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        bikeRecylerview.setLayoutManager(linearLayoutManager);
        RecyclerAdapter adapter1 = new RecyclerAdapter(context, bikeVideosList);
        bikeRecylerview.setAdapter(adapter1);
        bikeRecylerview.setVisibility(View.GONE);
    }


    @Override
    protected void onResume() {
        super.onResume();
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

    Boolean carExpand = false;
    Boolean bikeExpand = false;

    public void carDropDown(View view) {
        if (!carExpand) {
            carExpand = true;
            ((ImageView) findViewById(R.id.carExpand)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_arrow_up));
            carRecylerview.setVisibility(View.VISIBLE);
        } else {
            carExpand = false;
            ((ImageView) findViewById(R.id.carExpand)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_arrow_down));
            carRecylerview.setVisibility(View.GONE);
        }
    }

    public void bikeDropDown(View view) {
        if (!bikeExpand) {
            bikeExpand = true;
            ((ImageView) findViewById(R.id.bikeExpand)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_arrow_up));
            bikeRecylerview.setVisibility(View.VISIBLE);
        } else {
            bikeExpand = false;
            ((ImageView) findViewById(R.id.bikeExpand)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_arrow_down));
            bikeRecylerview.setVisibility(View.GONE);
        }
    }

    //======================================== Show Loading bar ==============================================
    private LinearLayout load;
    private LinearLayout bg_main;
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
    //======================================== Show Loading bar ==============================================
}
