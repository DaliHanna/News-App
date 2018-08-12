package com.example.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsArticle>>, SharedPreferences.OnSharedPreferenceChangeListener {
    private TextView mEmptyStateTextView;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    Button tryAgain;
     NewsAdapter newsAdapter;
    private static final int NEWS_LOADER_ID = 1;
    public static final String NEWS_REQUEST_URL = "https://content.guardianapis.com/search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycle_view);
        progressBar = findViewById(R.id.progressbar);
        mEmptyStateTextView = findViewById(R.id.empty_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        tryAgain = findViewById(R.id.btn_again);

        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {
                    LoaderManager loaderManager = getSupportLoaderManager();
                    loaderManager.initLoader(0, null, MainActivity.this).forceLoad(); // this means belong to LoaderCallback
                    progressBar.setVisibility(View.GONE);
                    mEmptyStateTextView.setVisibility(View.GONE);
                    tryAgain.setVisibility(View.GONE);

                } else {
                    mEmptyStateTextView.setText(R.string.no_internet);
                }
            }
        });

        if (isConnected()) {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(0, null, this).forceLoad(); // this means belong to LoaderCallback
            progressBar.setVisibility(View.GONE);
            tryAgain.setVisibility(View.GONE);

        } else {
            mEmptyStateTextView.setText(R.string.no_internet);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.settings_news_key))) {
            // Hide the empty state text view as the loading indicator will be displayed
            mEmptyStateTextView.setVisibility(View.GONE);

            // Show the loading indicator while new data is being fetched
            View loadingIndicator = findViewById(R.id.progressbar);
            loadingIndicator.setVisibility(View.VISIBLE);

            // Restart the loader to requery the USGS as the query settings have been updated
            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, (android.app.LoaderManager.LoaderCallbacks<Object>) MainActivity.this);
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else
            return false;
    }

    @NonNull
    @Override
    public Loader<List<NewsArticle>> onCreateLoader(int i, @Nullable Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sectionBy = sharedPrefs.getString(getString(R.string.settings_section_by_key), getString(R.string.settings_show_all_section));
        String orderBy = sharedPrefs.getString(getString(R.string.order_by_key), getString(R.string.settings_newest_default));

        Uri baseUri = Uri.parse(NEWS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter(getString(R.string.api_label), getString(R.string.api_value));
        uriBuilder.appendQueryParameter(getString(R.string.q_label), getString(R.string.q_value));
        uriBuilder.appendQueryParameter(getString(R.string.show_fields_label), getString(R.string.show_fields_value));
        uriBuilder.appendQueryParameter(getString(R.string.show_tags_label), getString(R.string.show_tags_value));
        if (!sectionBy.equals(getString(R.string.api_label))) {
            uriBuilder.appendQueryParameter(getString(R.string.section_label), sectionBy);
        }

        uriBuilder.appendQueryParameter(getString(R.string.order_by_label), orderBy);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<NewsArticle>> loader, List<NewsArticle> newsArticles) {
        if (newsArticles != null && !newsArticles.isEmpty()) {

            newsAdapter = new NewsAdapter(MainActivity.this, newsArticles);
            recyclerView.setAdapter(newsAdapter);
            progressBar.setVisibility(View.GONE);
        } else {

            mEmptyStateTextView.setText(R.string.empty_content);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<NewsArticle>> loader) {
        newsAdapter.clearApplications();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
