package com.blogspot.techtibet.tempapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import java.util.List;

class VideosRecyclerAdapter extends RecyclerView.Adapter<VideosRecyclerAdapter.ViewHolder>{
    Context context;
    List<Videos> videosList;
    VideosRecyclerAdapter(Context context,List<Videos> videosList){
        this.context=context;
        this.videosList=videosList;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_list,parent,false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String videourl=videosList.get(position).getVideo_url();
        final String vname=videosList.get(position).getVideo_name();
        final String vuploader=videosList.get(position).getUser();
        final String vdate=videosList.get(position).getTime();
        String thumburl=videosList.get(position).getThumb_url();
        final long viewCount=videosList.get(position).getView_count();
        Resources res=context.getResources();
        holder.mViewCount.setText(viewCount+" "+res.getText(R.string.view_count_text));
        final String videoId=videosList.get(position).videosId;
        holder.setImg(thumburl);
        holder.mVideoName.setText(vname);
        holder.mVideoUploader.setText(vuploader);
        holder.mVideoDate.setText(vdate);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,PlayActivity.class);
                intent.putExtra("videourl",videourl);
                intent.putExtra("videoId",videoId);
                intent.putExtra("vname",vname);
                intent.putExtra("vcount",viewCount);
                intent.putExtra("vdate",vdate);
                intent.putExtra("vuploader",vuploader);

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageView mVideoImg;
        TextView mVideoName;
        TextView mVideoUploader;
        TextView mVideoDate;
        TextView mViewCount;


        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            mVideoImg=mView.findViewById(R.id.video_img);
            mVideoName=mView.findViewById(R.id.video_name);
            mVideoUploader=mView.findViewById(R.id.video_uploader);
            mVideoDate=mView.findViewById(R.id.video_date);
            mViewCount=mView.findViewById(R.id.view_count);


        }

        public void setImg(String thumburl) {

            RequestOptions options=new RequestOptions();
            options.placeholder(R.drawable.vinelogo);
            Glide.with(context).setDefaultRequestOptions(options).load(thumburl).into(mVideoImg);
        }
    }
}
