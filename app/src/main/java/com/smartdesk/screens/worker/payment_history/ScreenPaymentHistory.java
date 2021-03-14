package com.smartdesk.screens.worker.payment_history;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartdesk.R;
import com.smartdesk.model.payment_history.PaymentHistoryDTO;
import com.smartdesk.utility.UtilityFunctions;
import com.smartdesk.utility.memory.MemoryCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.smartdesk.constants.Constants.USER_DOCUMENT_ID;
import static com.smartdesk.constants.FirebaseConstants.firebaseFirestore;
import static com.smartdesk.constants.FirebaseConstants.paymentCollection;
import static com.smartdesk.utility.UtilityFunctions.getDateFormat;
import static com.smartdesk.utility.UtilityFunctions.loadingAnim;

public class ScreenPaymentHistory extends AppCompatActivity {

    private Activity context;

    //RecyclerView Variables
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ScreenPaymentHistory.Adapter adapter;
    List<PaymentHistoryDTO> paymentHistoryDTOList = new ArrayList<>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        new MemoryCache().clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_payment_history);
        context=this;
        actionBar("Payment History");
        initLoadingBarItems();
        initIds();
        setRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDataOnList(false);
    }

    private void initIds() {
        swipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.SmartDesk_Editext_red), ContextCompat.getColor(context, R.color.SmartDesk_Blue));
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> showDataOnList(true), 0));
    }

    void onItemsLoadComplete() {
        swipeRefreshLayout.setRefreshing(false);
    }

    public void setRecyclerView() {
        new Handler().postDelayed(() -> {
            adapter = new ScreenPaymentHistory.Adapter(paymentHistoryDTOList);
            recyclerView = UtilityFunctions.setRecyclerView(findViewById(R.id.recycler_view), context);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }, 0);
    }

    public void showDataOnList(Boolean isSwipe) {
        new Handler().postDelayed(() -> {
            if (!isSwipe)
                startAnim();
            firebaseFirestore.collection(paymentCollection)
                    .whereEqualTo("documentID", USER_DOCUMENT_ID).get().
                    addOnSuccessListener(task -> {
                        onItemsLoadComplete();
                        paymentHistoryDTOList.clear();
                        if (!isSwipe)
                            stopAnim();
                        if (task.isEmpty()) {
                            findViewById(R.id.listEmptyText).setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        } else {
                            List<PaymentHistoryDTO> paymentHistoryDTOS = task.toObjects(PaymentHistoryDTO.class);
                            Collections.sort(paymentHistoryDTOS);

                            paymentHistoryDTOList.addAll(paymentHistoryDTOS);

                            if (paymentHistoryDTOS.size() > 0) {
                                findViewById(R.id.listEmptyText).setVisibility(View.GONE);
                            } else {
                                findViewById(R.id.listEmptyText).setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        adapter.notifyDataSetChanged();
                    }).addOnFailureListener(e -> {
                onItemsLoadComplete();
                if (!isSwipe)
                    stopAnim();
                paymentHistoryDTOList.clear();
                adapter.notifyDataSetChanged();
            });
        }, 0);
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

    public class Adapter extends RecyclerView.Adapter<ScreenPaymentHistory.Adapter.ViewHolder> {

        List<PaymentHistoryDTO> paymentDTOS;

        public Adapter(List<PaymentHistoryDTO> paymentDTOS) {
            this.paymentDTOS = paymentDTOS;
        }

        @NonNull
        @Override
        public ScreenPaymentHistory.Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.rv_item_payment, parent, false);
            return new ScreenPaymentHistory.Adapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ScreenPaymentHistory.Adapter.ViewHolder holder, final int position) {
            holder.date.setText(getDateFormat(paymentDTOS.get(position).getDateFomat()));
            holder.reward.setText("Rs. " +  paymentDTOS.get(position).getAmount());
            holder.company.setText(paymentDTOS.get(position).getCompany());
            holder.model.setText(paymentDTOS.get(position).getModel());
        }

        public int getItemCount() {
            return paymentDTOS.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView company,model, date, reward;

            public ViewHolder(@NonNull View view) {
                super(view);
                company = view.findViewById(R.id.company);
                model = view.findViewById(R.id.model);
                reward = view.findViewById(R.id.price);
                date = view.findViewById(R.id.date);
            }
        }
    }

    //======================================== Show Loading bar ==============================================
    private LinearLayout load;
    private CoordinatorLayout bg_main;
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
}