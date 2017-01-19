package com.applefish.smartshop.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.applefish.smartshop.R;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);

        test = (TextView) findViewById(R.id.test);

        handleIntent( getIntent() );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {

        if ( Intent.ACTION_SEARCH.equals( intent.getAction() ) ) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            test.setText(query);
            //use the query to search your data somehow
        }
    }
}
