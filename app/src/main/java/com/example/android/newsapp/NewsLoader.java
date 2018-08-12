package com.example.android.newsapp;

import android.content.Context;
import android.graphics.Movie;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<NewsArticle>> {
    public static String url_string;
    public final String LOG_TAG = MainActivity.class.getSimpleName();

    public NewsLoader(@NonNull Context context, String url) {
        super(context);
        url_string = url;
    }

    @Nullable
    @Override
    public List<NewsArticle> loadInBackground() {
        StringBuilder jsonResponse = new StringBuilder();
        HttpURLConnection urlConnection;
        InputStream inputStream;
        List<NewsArticle> newsArrayList = null;

        try {
            URL url = new URL(url_string);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    jsonResponse.append(line);
                    line = reader.readLine();
                }
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }


            JSONObject root = new JSONObject(jsonResponse.toString());
            JSONObject response = root.getJSONObject("response");
            JSONArray news = response.getJSONArray("results");

            newsArrayList = new ArrayList<>();
            String title;
            String imageUrl = null;
            String webUrl;
            String date;
            String firstName;
            String lastName;
            String author = "";
            String section;
            JSONArray tags;
            JSONObject fields;

            for (int i = 0; i < news.length(); i++) {
                JSONObject element = news.getJSONObject(i);
                tags = element.getJSONArray("tags");
                fields = element.getJSONObject("fields");
                title = element.getString("webTitle");
                section = element.getString("sectionName");

                if (fields.has("thumbnail")) {
                    imageUrl = fields.getString("thumbnail");
                }
                for (int j = 0; j < tags.length(); j++) {
                    JSONObject tagsElement = tags.getJSONObject(j);
                    firstName = tagsElement.getString("firstName");
                    lastName = tagsElement.getString("lastName");
                    author = firstName + " " + lastName;
                }
                date = element.getString("webPublicationDate");
                webUrl = element.getString("webUrl");
                newsArrayList.add(new NewsArticle(title, author, imageUrl, date, webUrl, section));
            }

        } catch (MalformedInputException e) {
            Log.e(LOG_TAG, "Error with creating URL", e);

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsArrayList;
    }
}
