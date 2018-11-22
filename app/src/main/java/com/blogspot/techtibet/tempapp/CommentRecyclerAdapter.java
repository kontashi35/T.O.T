package com.blogspot.techtibet.tempapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder> {
    List<Comments> commentsList;
    Context context;
    CommentRecyclerAdapter(List<Comments> commentsList, Context context){
        this.commentsList=commentsList;
        this.context=context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_comment_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    String name=commentsList.get(position).getName();
    holder.mName.setText(name);
        String date=commentsList.get(position).getDate();
        holder.mDate.setText(date);
        String comment=commentsList.get(position).getComment();
        holder.mComment.setText(comment);
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView mName;
        TextView mDate;
        TextView mComment;

        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            mName=mView.findViewById(R.id.comment_name);
            mDate=mView.findViewById(R.id.comment_date);
            mComment=mView.findViewById(R.id.comment_text);
        }
    }

}
