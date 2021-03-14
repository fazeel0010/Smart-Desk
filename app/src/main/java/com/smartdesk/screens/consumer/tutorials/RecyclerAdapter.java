package com.smartdesk.screens.consumer.tutorials;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.smartdesk.R;
import com.smartdesk.model.tutorials.TutorialsDTO;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.VideoInfoHolder> {

    Context ctx;
    List<TutorialsDTO> tutorialsDTO;

    public RecyclerAdapter(Context context, List<TutorialsDTO> tutorialsDTO) {
        this.ctx = context;
        this.tutorialsDTO = tutorialsDTO;
    }

    @Override
    public VideoInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_tutorial_new, parent, false);
        return new VideoInfoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VideoInfoHolder holder, final int position) {

        holder.title.setText(tutorialsDTO.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return tutorialsDTO.size();
    }

    public class VideoInfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        protected ImageView playButton;
        TextView title;

        public VideoInfoHolder(View itemView) {
            super(itemView);
            playButton =  itemView.findViewById(R.id.btnYoutube_player);
            title = itemView.findViewById(R.id.title);
            playButton.setOnClickListener(this);
            relativeLayoutOverYouTubeThumbnailView =  itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
        }

        @Override
        public void onClick(View v) {
        }
    }
}