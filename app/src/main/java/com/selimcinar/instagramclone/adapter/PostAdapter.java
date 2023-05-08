package com.selimcinar.instagramclone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.selimcinar.instagramclone.databinding.RecyclerRowBinding;
import com.selimcinar.instagramclone.model.Post;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    private ArrayList<Post> postArrayList;

    public  PostAdapter(ArrayList <Post> postArrayList){
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return  new PostHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull PostHolder holder, int position) {
        holder.recyclerRowBinding.RecyclerViewInputText.setText(postArrayList.get(position).email);
        holder.recyclerRowBinding.RecyclerViewCommentText.setText(postArrayList.get(position).comment);
        Picasso.get().load(postArrayList.get(position).downloadUrl).into(holder.recyclerRowBinding.RecyclerViewImageView);
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    class  PostHolder extends  RecyclerView.ViewHolder{
        RecyclerRowBinding recyclerRowBinding;
        public  PostHolder(RecyclerRowBinding recyclerRowBinding){
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding=recyclerRowBinding;
        }
    }



}
