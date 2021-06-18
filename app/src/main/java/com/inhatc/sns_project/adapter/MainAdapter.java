package com.inhatc.sns_project.adapter;


import android.app.Activity;
import android.media.Image;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inhatc.sns_project.PostInfo;
import com.inhatc.sns_project.R;
import com.inhatc.sns_project.listener.OnPostListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private FirebaseFirestore firebaseFirestore;
    private OnPostListener onPostListener;

    static class MainViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        MainViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public MainAdapter(Activity activity, ArrayList<PostInfo> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void setOnPostListener(OnPostListener onPostListener){
        this.onPostListener = onPostListener;
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    @NonNull
    @Override
    public MainAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final MainViewHolder mainViewHolder = new MainViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cardView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, mainViewHolder.getAdapterPosition());
            }
        });

        return mainViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final MainViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView titleTextView = cardView.findViewById(R.id.titleTextView);
        titleTextView.setText(mDataset.get(position).getTitle());

        TextView createdAtTextView = cardView.findViewById(R.id.createAtTextView);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).getCreatedAt()));

        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentsList = mDataset.get(position).getContents();

        if(contentsLayout.getTag() == null || !contentsLayout.getTag().equals(contentsList)){
            Log.e("로그: ", "태그");
            contentsLayout.setTag(contentsList);
            contentsLayout.removeAllViews();
            if(contentsList.size() > 0){
                for (int i = 0; i < contentsList.size(); i++) {
                    String contents = contentsList.get(i);
                    if (Patterns.WEB_URL.matcher(contents).matches()) {
                        ImageView imageView = new ImageView(activity);
                        imageView.setLayoutParams(layoutParams);
                        imageView.setAdjustViewBounds(true);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        contentsLayout.addView(imageView);
                        Glide.with(activity).load(contents).override(1000).thumbnail(0.1f).into(imageView);
                    } else {
                        TextView textView = new TextView(activity);
                        textView.setLayoutParams(layoutParams);
                        textView.setText(contents);
                        contentsLayout.addView(textView);
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

        private void showPopup(View v, final int position) {
            PopupMenu popup = new PopupMenu(activity, v);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    String id = mDataset.get(position).getId();
                    switch (menuItem.getItemId()) {
                        case R.id.modify:
                            onPostListener.onModify(id);
                            return true;
                        case R.id.delete:
                            onPostListener.onDelete(id);
                            return true;
                        default:
                            return false;
                    }
                }
            });

            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.post, popup.getMenu());
            popup.show();
        }
    }

