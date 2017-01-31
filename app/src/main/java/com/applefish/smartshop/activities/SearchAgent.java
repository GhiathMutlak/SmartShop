package com.applefish.smartshop.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.applefish.smartshop.R;
import com.applefish.smartshop.classes.ConnectChecked;

public class SearchAgent extends AppCompatActivity {

    private RadioButton wordsearch;
    private RadioButton catsearch;
    private SearchView searchView;
    private Spinner cat_spinner;
    private Spinner item_spinner;
    private Button button;
    Toolbar toolbar;
    private ArrayAdapter<CharSequence> All_cat_adapter;
    private ArrayAdapter<CharSequence> cat1_adapter;
    private ArrayAdapter<CharSequence> cat2_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_agent);

         toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        wordsearch= (RadioButton) findViewById(R.id.radioButton);
        catsearch= (RadioButton) findViewById(R.id.radioButton2);
        searchView= (android.support.v7.widget.SearchView) findViewById(R.id.search);
        cat_spinner= (Spinner) findViewById(R.id.spinner);
        item_spinner= (Spinner) findViewById(R.id.spinner2);
        button= (Button) findViewById(R.id.button2);
         //spiner and adapter
        All_cat_adapter=ArrayAdapter.createFromResource(this,R.array.Category,android.R.layout.preference_category);
        All_cat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cat_spinner.setAdapter(All_cat_adapter);

        cat1_adapter=ArrayAdapter.createFromResource(this,R.array.item1,android.R.layout.simple_spinner_item);
        cat1_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        cat2_adapter=ArrayAdapter.createFromResource(this,R.array.item2,android.R.layout.simple_spinner_item);
        cat2_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        cat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(i==0)
                    item_spinner.setAdapter(cat1_adapter);
                if(i==1)
                    item_spinner.setAdapter(cat2_adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if ( ConnectChecked.isNetworkAvailable( getBaseContext() ) &&
                        ConnectChecked.isOnline() ) {
                    Intent searchIntent = new Intent();
                    searchIntent.setClass( getBaseContext(), SearchActivity.class );
                    searchIntent.putExtra( SearchManager.QUERY , query);
                    searchIntent.setAction( Intent.ACTION_SEARCH );
                    startActivity(searchIntent);
                    return false;
                } else {
                    Snackbar.make(toolbar, "No Internet Connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( ConnectChecked.isNetworkAvailable( getBaseContext() ) &&
                        ConnectChecked.isOnline() ) {
                    Intent searchIntent = new Intent();
                    searchIntent.setClass( getBaseContext(), SearchActivity.class );
                    String item=item_spinner.getSelectedItem().toString();
                    String query;
                    if(item.toLowerCase().equals("all"))
                     query=cat_spinner.getSelectedItem().toString();
                    else
                     query=item;
                    searchIntent.putExtra( SearchManager.QUERY , query);
                    searchIntent.setAction( Intent.ACTION_SEARCH );
                    startActivity(searchIntent);

                } else {
                    Snackbar.make(toolbar, "No Internet Connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

    }

    public void SearchWay(View view)
    {
        int id=view.getId();
        if(id==R.id.radioButton)
        {
            wordsearch.setChecked(true);
            searchView.setVisibility(View.VISIBLE);

            catsearch.setChecked(false);
            cat_spinner.setVisibility(View.GONE);
            item_spinner.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        }
        else
        {

            wordsearch.setChecked(false);
            searchView.setVisibility(View.GONE);

            catsearch.setChecked(true);
            cat_spinner.setVisibility(View.VISIBLE);
            item_spinner.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        }

    }

}
