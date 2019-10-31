package com.example.assignment1;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assignment1.model.JobModel;

import java.util.List;
import java.util.Locale;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {

    private List<JobModel> mJobModel;
    private Context mContext;
    private OnJobListener mOnJobListener;
    private OnJobLongListener mOnJobLongListener;
    private OnJobFavoriteListener mOnJobFavoriteListener;
    private boolean favorited;
    private ViewHolder viewHolderTemp;

    public JobAdapter(Context context, List<JobModel> JobModel, OnJobListener onJobListener, OnJobLongListener onJobLongListener, OnJobFavoriteListener OnJobFavoriteListener) {
        mJobModel = JobModel;
        mContext = context;
        this.mOnJobListener = onJobListener;
        this.mOnJobLongListener = onJobLongListener;
        this.mOnJobFavoriteListener = OnJobFavoriteListener;

    }



    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public JobAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_demo, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView,mOnJobListener,mOnJobLongListener, mOnJobFavoriteListener);
        return viewHolder;
    }


    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final JobModel jobs = mJobModel.get(position);
        viewHolderTemp = viewHolder;
        // Sætter tækst i hver viewholder
        viewHolder.txtCompany.setText(jobs.getCompany());
        viewHolder.txtTitle.setText(jobs.getTitle());


        // if score is 10, remove decimal as it cant fit
        if (mJobModel.get(position).getScore()<9.9){
            viewHolder.txtScore.setText(String.format(Locale.US,"%.1f", mJobModel.get(position).getScore()));}
        else {
            viewHolder.txtScore.setText(String.format(Locale.US,"%.0f", mJobModel.get(position).getScore()));}


        // Applied ændrer farve efter om der er søgt eller ej

        if (mJobModel.get(position).getApplied()){
            viewHolder.txtStatus.setText("APPLIED");
            viewHolder.txtStatus.setTextColor(ContextCompat.getColor(mContext , R.color.applied));// should be resource
        }
        else{
            viewHolder.txtStatus.setText("NOT APP");
            viewHolder.txtStatus.setTextColor(ContextCompat.getColor(mContext , R.color.not_applied));
        }
        Log.d("heeeej", mJobModel.get(position).getStatusColor());
        PorterDuffColorFilter colorfilter = new PorterDuffColorFilter(Color.parseColor(mJobModel.get(position).getStatusColor()), PorterDuff.Mode.MULTIPLY);
        viewHolder.txtScore.getBackground().setColorFilter(colorfilter);


        // Billede i hver viewholder
        final ImageView imgLogo = viewHolder.imgLogo;
        Glide.with(viewHolder.imgLogo.getContext())
                .load(mJobModel.get(position).getCompanyLogo()).into(viewHolder.imgLogo);
        //final String nameOfImage = "img_"+position;
        //int resId = mContext.getResources().getIdentifier(nameOfImage, "drawable", mContext.getPackageName());
        //Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);
        //imgLogo.setImageBitmap(bitmap);

        favorited = mJobModel.get(position).getFavorited();
        setFavorite(viewHolder, favorited); // sets star in recyclerview according to fav status



    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mJobModel.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView txtCompany;
        public TextView txtTitle;
        public TextView txtStatus;
        public TextView txtScore;
        public ConstraintLayout layout;
        public ImageView imgLogo;
        public ImageView imgFavorite;
        OnJobListener onJobListener;
        OnJobLongListener onJobLongListener;
        OnJobFavoriteListener onJobFavoriteListener;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, OnJobListener onJobListener, OnJobLongListener onJobLongListener, OnJobFavoriteListener OnJobFavoriteListener) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            itemView.setOnClickListener(this); // this refererer til interfacet
            itemView.setOnLongClickListener(this);
            this.onJobListener = onJobListener;
            this.onJobLongListener = onJobLongListener;
            this.onJobFavoriteListener = OnJobFavoriteListener;
            txtCompany =    itemView.findViewById(R.id.txtCompany);
            txtTitle =      itemView.findViewById(R.id.txtJobtitle);
            txtStatus =     itemView.findViewById(R.id.txtStatusStatic);
            txtScore =      itemView.findViewById(R.id.txtScore);
            layout =        itemView.findViewById(R.id.LayoutJobEntry);
            imgLogo =       itemView.findViewById(R.id.imgLogo);
            imgFavorite =   itemView.findViewById(R.id.imgFavorite);


            imgFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favorited = favorited ? false : true;
                    onJobFavoriteListener.favoriteItem(getAdapterPosition());
                    if (favorited){
                        mJobModel.get(getAdapterPosition()).setFavorited(favorited);
                        imgFavorite.setImageResource(R.drawable.ic_star_24dp);
                    }
                    else{
                        mJobModel.get(getAdapterPosition()).setFavorited(favorited);
                        imgFavorite.setImageResource(R.drawable.ic_star_border_24dp);
                    }
                }
            });
        }

        @Override
        public void onClick (View view){
            onJobListener.onJobClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick (View view){
            return onJobLongListener.onJobLongClick(getAdapterPosition());
        }
    }
    public interface OnJobListener{
        void onJobClick(int position);
    }

    public interface OnJobLongListener{
        boolean onJobLongClick(int position);
    }

    void setFavorite(ViewHolder viewHolder, boolean fav){
            if (fav){
                viewHolder.imgFavorite.setImageResource(R.drawable.ic_star_24dp);
            }
            else{
                viewHolder.imgFavorite.setImageResource(R.drawable.ic_star_border_24dp);
            }
    }

    public interface OnJobFavoriteListener{
        void favoriteItem(int position);
    }
}