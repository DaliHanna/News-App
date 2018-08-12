package com.example.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private List<NewsArticle> newsDatabase;
    private Context context;

    public NewsAdapter(@NonNull Context context, List<NewsArticle> newsDatabase) {
        this.layoutInflater = LayoutInflater.from(context);
        this.newsDatabase = newsDatabase;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = layoutInflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final NewsArticle currentNews = newsDatabase.get(position);
        holder.title.setText(currentNews.getmTitle());
        String date = currentNews.getmDate();
        date = date.substring(0, 10);
        holder.releaseDate.setText(date);
        holder.author.setText(currentNews.getmAuthor());
        holder.section.setText(currentNews.getSeciton());
        Glide.with(context).load(currentNews.getmImage()).into(holder.imageView);
        holder.listItemClicked.setOnClickListener(new View.OnClickListener() { //or you can use holder.itemView.setOnClickListener
            @Override
            public void onClick(View view) {
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsArticleUri = Uri.parse(currentNews.getmUrl());
                // Create a new intent to view the News URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsArticleUri);

                // Send the intent to launch a new activity
                context.startActivity(websiteIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return newsDatabase == null ? 0 : newsDatabase.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView imageView;
        private TextView releaseDate;
        private TextView author;
        private LinearLayout listItemClicked;
        private TextView section;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            imageView = itemView.findViewById(R.id.image);
            releaseDate = itemView.findViewById(R.id.release_date);
            author = itemView.findViewById(R.id.author);
            listItemClicked = itemView.findViewById(R.id.linear_layout);
            section = itemView.findViewById(R.id.section);

        }
    }
}
