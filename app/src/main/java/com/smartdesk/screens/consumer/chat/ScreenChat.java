package com.smartdesk.screens.consumer.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smartdesk.R;
import com.smartdesk.model.chat.ChatListDTO;
import com.smartdesk.model.chat.MessageChatDTO;
import com.smartdesk.utility.UtilityFunctions;
import com.smartdesk.utility.library.CustomEditext;
import com.smartdesk.utility.memory.MemoryCache;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.smartdesk.constants.Constants.USER_DOCUMENT_ID;
import static com.smartdesk.constants.Constants.USER_MOBILE;
import static com.smartdesk.constants.Constants.USER_NAME;
import static com.smartdesk.constants.Constants.USER_PROFILE;
import static com.smartdesk.constants.Constants.USER_ROLE;
import static com.smartdesk.constants.Constants.workerRole;
import static com.smartdesk.constants.FirebaseConstants.chatCollection;
import static com.smartdesk.constants.FirebaseConstants.chatListCollection;
import static com.smartdesk.constants.FirebaseConstants.firebaseFirestore;
import static com.smartdesk.utility.UtilityFunctions.loadingAnim;

public class ScreenChat extends AppCompatActivity {

    Activity context;

    public static String Chat_DOCUMENTID_OTHER;
    public static String CHAT_NAME;
    public static String CHAT_MOBILE;
    public static String CHAT_PROFILE;

    //RecyclerView Variables
    RecyclerView recyclerView;
    ScreenChat.Adapter adapter;
    List<MessageChatDTO> messageChatDTOS = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_chat);
        context = this;
        actionBar("Chat");
        initLoadingBarItems();
        setRecyclerView();
        showDataOnList();
        realTimeListner();
    }

    public void actionBar(String actionTitle) {
        Toolbar a = findViewById(R.id.actionbarInclude).findViewById(R.id.toolbar);
        setSupportActionBar(a);
        ((TextView) findViewById(R.id.actionbarInclude).findViewById(R.id.actionTitleBar)).setText(CHAT_NAME);
        ((TextView) findViewById(R.id.actionbarInclude).findViewById(R.id.actionBarPhoneNumber)).setText(CHAT_MOBILE);
        UtilityFunctions.picassoGetCircleImage(context, CHAT_PROFILE, ((CircleImageView) findViewById(R.id.actionbarInclude).findViewById(R.id.actionBarProfile)), null, R.drawable.side_profile_icon);

        ((ImageView) findViewById(R.id.actionbarInclude).findViewById(R.id.callImage)).setOnClickListener((View.OnClickListener) v -> {
            UtilityFunctions.openCallingScreen(context, CHAT_MOBILE);
        });

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        new MemoryCache().clear();
    }

    public void showDataOnList() {
        new Handler().postDelayed(() -> {
            Query query;
            if (USER_ROLE.equals(workerRole)) {
                query = firebaseFirestore.collection(chatCollection).document(USER_DOCUMENT_ID + Chat_DOCUMENTID_OTHER).collection(chatCollection);
            } else {
                query = firebaseFirestore.collection(chatCollection).document(Chat_DOCUMENTID_OTHER + USER_DOCUMENT_ID)
                        .collection(chatCollection);
            }
            query.get().
                    addOnSuccessListener(task -> {
                        messageChatDTOS.clear();

                        if (task.isEmpty()) {
                            adapter.notifyDataSetChanged();
                        } else {
                            List<MessageChatDTO> messageChatDTOS = task.toObjects(MessageChatDTO.class);
                            Collections.sort(messageChatDTOS, Collections.reverseOrder());

                            this.messageChatDTOS.addAll(messageChatDTOS);
                            try {
                                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                            } catch (Exception ex) {

                            }
                            adapter.notifyDataSetChanged();
                        }
                        adapter.notifyDataSetChanged();
                    }).addOnFailureListener(e -> {

                messageChatDTOS.clear();
                adapter.notifyDataSetChanged();
            });
        }, 0);
    }

    public void setRecyclerView() {
        new Handler().postDelayed(() -> {
            adapter = new ScreenChat.Adapter(messageChatDTOS);
            recyclerView = UtilityFunctions.setRecyclerView(findViewById(R.id.recycler_view), context);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }, 0);
    }

    public class Adapter extends RecyclerView.Adapter<ScreenChat.Adapter.ViewHolder> {

        List<MessageChatDTO> messageChatDTOS1;

        public Adapter(List<MessageChatDTO> chatListDTOS) {
            this.messageChatDTOS1 = chatListDTOS;
        }

        @NonNull
        @Override
        public ScreenChat.Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.rv_item_chat_message, parent, false);
            return new ScreenChat.Adapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ScreenChat.Adapter.ViewHolder holder, final int position) {
            final Context innerContext = holder.itemView.getContext();

            holder.msg.setText(messageChatDTOS1.get(position).getMessage());
            try {
                if (messageChatDTOS1.get(position).getSenderDocumentID().equals(USER_DOCUMENT_ID)) {

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(100, 3, 3, 3);
                    holder.msg.setLayoutParams(params);
                    holder.msg.setGravity(Gravity.END);
                    holder.cardView.setGravity(Gravity.END);
                    holder.msg.setPadding(10, 10, 10, 10);
                } else {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(3, 3, 100, 3);
                    holder.msg.setLayoutParams(params);
                    holder.msg.setGravity(Gravity.START);
                    holder.cardView.setGravity(Gravity.START);
                    holder.msg.setPadding(10, 10, 10, 10);

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onViewDetachedFromWindow(ScreenChat.Adapter.ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            holder.itemView.clearAnimation();
        }

        public int getItemCount() {
            return messageChatDTOS1.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView msg;
            LinearLayout cardView;

            public ViewHolder(@NonNull View view) {
                super(view);
                cardView = view.findViewById(R.id.cardView);
                msg = view.findViewById(R.id.msg);
            }
        }
    }

    //======================================== Show Loading bar ==============================================
    private LinearLayout load;
    CoordinatorLayout bg_main;
    private ObjectAnimator anim;
    private ImageView progressBar;
    private Boolean isLoad;

    private void initLoadingBarItems() {
        try {
            load = findViewById(R.id.loading_view);
            bg_main = findViewById(R.id.bg_main);
            progressBar = findViewById(R.id.loading_image);
        } catch (Exception ex) {

        }
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

    public void sendBtn(View view) {
        CustomEditext customEditext = findViewById(R.id.message);
        String msg = customEditext.getText().toString();
        if (msg.equals("")) {
            Toast.makeText(context, "Message cannot be null", Toast.LENGTH_SHORT).show();
        } else {
            customEditext.setText("");

            ChatListDTO myData = new ChatListDTO(new Timestamp(new Date().getTime()), USER_NAME, USER_PROFILE, USER_MOBILE, USER_DOCUMENT_ID);
            ChatListDTO receiverData = new ChatListDTO(new Timestamp(new Date().getTime()), CHAT_NAME, CHAT_PROFILE, CHAT_MOBILE, Chat_DOCUMENTID_OTHER);


            firebaseFirestore.collection(chatListCollection).document(USER_DOCUMENT_ID)
                    .collection(chatListCollection).document(Chat_DOCUMENTID_OTHER).set(receiverData);

            firebaseFirestore.collection(chatListCollection).document(Chat_DOCUMENTID_OTHER)
                    .collection(chatListCollection).document(USER_DOCUMENT_ID).set(myData);

            MessageChatDTO msgData = new MessageChatDTO(new Timestamp(new Date().getTime()), msg, USER_DOCUMENT_ID, Chat_DOCUMENTID_OTHER);
            if (USER_ROLE.equals(workerRole)) {
                firebaseFirestore.collection(chatCollection).document(USER_DOCUMENT_ID + Chat_DOCUMENTID_OTHER)
                        .collection(chatCollection).add(msgData);
            } else {
                firebaseFirestore.collection(chatCollection).document(Chat_DOCUMENTID_OTHER + USER_DOCUMENT_ID)
                        .collection(chatCollection).add(msgData);
            }
        }
    }


    public void realTimeListner() {
        String docID;
        if (USER_ROLE.equals(workerRole)) {
            docID = USER_DOCUMENT_ID + Chat_DOCUMENTID_OTHER;
        } else {
            docID = Chat_DOCUMENTID_OTHER + USER_DOCUMENT_ID;
        }
        firebaseFirestore.collection(chatCollection)
                .document(docID)
                .collection(chatCollection)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    showDataOnList();
                                    break;
                                case MODIFIED:
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }

                    }
                });
    }
    //======================================== Show Loading bar ==============================================
}