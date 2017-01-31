package com.applefish.smartshop.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.applefish.smartshop.R;
import com.applefish.smartshop.classes.ConnectChecked;
import com.applefish.smartshop.classes.Offer;
import com.applefish.smartshop.classes.PagerAdapter;
import com.applefish.smartshop.classes.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String storesResult;
    String latestResult;
    String mostViewedResult;

    private String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";
    public static final String TAG_NAME = "storeName";
    private static final String TAG_ADD ="logoUrl";

    private static final String STORES_URL = "http://smartshop-uae.org/smartshop/retrivelogo.php";
    private static final String LATEST_URL = "http://smartshop-uae.org/smartshop/date.php";
    private static final String MOST_VIEWED_URL = "http://smartshop-uae.org/smartshop/view.php";

    public static ArrayList<Store> storesList;
    public static ArrayList<Offer> latestOffersList;
    public static ArrayList<Offer> mostViewedList;

    public static ArrayList<ImageButton> storesLogosList;
    public static ArrayList<ImageButton> latestOffersCoversList;
    public static ArrayList<ImageButton> mostViewedCoversList;

    public static ArrayList<Bitmap> storesBitmapsList;
    public static ArrayList<Bitmap> latestBitmapsList;
    public static ArrayList<Bitmap> mostViewedBitmapsList;

    private static JSONArray storesArray = null;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    static Thread getData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchagent = new Intent();
                searchagent.setClass(getBaseContext(), SearchAgent.class);
                startActivity(searchagent);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

//        // Associate searchable configuration with the SearchView
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        final SearchView searchView = (SearchView) findViewById(R.id.search);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                if ( ConnectChecked.isNetworkAvailable( getBaseContext() ) &&
//                        ConnectChecked.isOnline() ) {
//                Intent searchIntent = new Intent();
//                searchIntent.setClass( getBaseContext(), SearchActivity.class );
//                searchIntent.putExtra( SearchManager.QUERY , query);
//                searchIntent.setAction( Intent.ACTION_SEARCH );
//                startActivity(searchIntent);
//                return false;
//                } else {
//                    Snackbar.make(toolbar, "No Internet Connection", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }
//                return false;
//
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        storesList = new ArrayList<>();
        storesLogosList = new ArrayList<>();
        storesBitmapsList = new ArrayList<>();

        latestOffersList = new ArrayList<>();
        latestOffersCoversList = new ArrayList<>();
        latestBitmapsList = new ArrayList<>();

        mostViewedList  = new ArrayList<>();
        mostViewedCoversList = new ArrayList<>();
        mostViewedBitmapsList = new ArrayList<>();

        getData = new Thread(){
            @Override
            public void run() {
                getJSON( true );
            }
        };


        try {
            getData.start();
            getData.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // new pager Adapter and TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Latest"));
        tabLayout.addTab(tabLayout.newTab().setText("Most Viewed"));
        tabLayout.addTab(tabLayout.newTab().setText("Stores"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(2);

        runOnUiThread(new Runnable() {
            public void run() {
                displayViewPagerContent( 0 );
            }
        });

    }


    public  void displayViewPagerContent(final int position) {

             try
             {

                final Handler handler = new Handler(Looper.getMainLooper());

                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {


                           if( storesResult != null && latestResult != null && mostViewedResult != null ){
                                    final PagerAdapter adapter = new PagerAdapter
                                            (getSupportFragmentManager(), tabLayout.getTabCount());
                                    viewPager.setAdapter(adapter);
                                    viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                                    tabLayout.addOnTabSelectedListener(onTabSelectedListener(viewPager));

                               viewPager.setCurrentItem(position);
                               tabLayout.getTabAt(position).select();
                                }
                                else
                           {
                               displayViewPagerContent( position );
                           }
                            }
                        }, 2000);
                    }
                });



            } catch (Exception e) {
                Log.i("ddddddddddddddddddddddd", "onCreate: " +e);
            }

    }

    private TabLayout.OnTabSelectedListener onTabSelectedListener(final ViewPager Pager)
    {

        return  new TabLayout.OnTabSelectedListener(){


            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Pager.setCurrentItem(tab.getPosition());

                Log.i("OnTabSelectedListener","position="+tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
          //  super.onBackPressed();
                moveTaskToBack(true);
        }

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.refresh:
                actionRefresh();
                return true;
//            case R.id.searchbtn:
//                Intent searchagent = new Intent();
//                searchagent.setClass(getBaseContext(), SearchAgent.class);
//                startActivity(searchagent);
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favourites) {

            Intent favorite = new Intent();
            favorite.setClass(getBaseContext(), FavoriteActivity.class);
            startActivity(favorite);

        } else if (id == R.id.nav_share) {

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT,"DownLoad Smart Shopp UAE Android App To Know Before Shop -------URL for App in AppStore-----");
            shareIntent.setType("text/plain");
            startActivity(shareIntent);

        } else if (id == R.id.nav_help) {

            Intent help = new Intent();
            help.setClass(getBaseContext(), HelpActivity.class);
            startActivity(help);

        } else if (id == R.id.nav_settings) {

            Intent settings = new Intent();
            settings.setClass(getBaseContext(), SettingActivity.class);
            startActivity(settings);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getJSON ( boolean check ) {


        class GetStores extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String result;
                BufferedReader bufferedReader;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    int status = con.getResponseCode();


                    Log.i("GetStores", "doInBackground: " +status);

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }
                    result=sb.toString().trim();
                    return result;

                }catch(Exception e){
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String result) {
               super.onPostExecute(result);
                storesResult = result;
                buildStoresList();
            }
        }


        class GetLatest extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String result;
                BufferedReader bufferedReader;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    int status = con.getResponseCode();

                    Log.i("GetLatest", "doInBackground: " +status);

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }
                    result=sb.toString().trim();
                    return result;

                }catch(Exception e){
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                latestResult=result;
                buildOffersList(result,"latest");
            }
        }

        class GetMostViewed extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String result;
                BufferedReader bufferedReader;

                try {

                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    int status = con.getResponseCode();

                    Log.i("GetMost", "doInBackground: " +status);

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }
                    result=sb.toString().trim();
                    return result;

                }catch(Exception e){
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                mostViewedResult=result;
                buildOffersList(result,"mostViewed");
            }
        }


        GetStores getStores = new GetStores();
        GetMostViewed getMostViewed = new GetMostViewed( );
        GetLatest getLatest = new GetLatest();

            if (check) {
                getStores.execute( STORES_URL );
                getMostViewed.execute( MOST_VIEWED_URL );
                getLatest.execute( LATEST_URL );
            }
            else {

                if( !getStores.isCancelled() )
                    getStores.cancel( true );

                if( !getMostViewed.isCancelled() )
                    getMostViewed.cancel( true );

                if( !getLatest.isCancelled() )
                    getLatest.cancel( true );

            }


    }

    public void buildStoresList() {

        try {

            if ( storesResult != null) {

                JSONObject jsonObj = new JSONObject(storesResult);

                if( !storesResult.toString().equals("{\"result\":\"NoStores\"}") ) {
                storesArray = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < storesArray.length(); i++) {

                        JSONObject c = storesArray.getJSONObject(i);
                        int id = Integer.parseInt( c.getString(TAG_ID) );
                        String name = c.getString(TAG_NAME);
                        String logoUrl = c.getString(TAG_ADD);

                        Store store = new Store( id, name, logoUrl );
                        storesList.add(store);

                    }
                }
                else {
                    Toast.makeText(getBaseContext(), "NO Stores", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getBaseContext(), "NO thing in DB", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void buildOffersList( String latestResult, String list ) {

        try {

            if (latestResult != null) {

                JSONObject jsonObj = new JSONObject(latestResult);

                if (!latestResult.toString().equals("{\"result\":\"NoOffers\"}")) {
                    JSONArray offersArray = jsonObj.getJSONArray(TAG_RESULTS);


                    for (int i = 0; i < offersArray.length(); i++) {

                        JSONObject c = offersArray.getJSONObject(i);

                        int id = Integer.parseInt(c.getString(TAG_ID));
                        String title = c.getString(OffersActivity.TAG_TITLE);
                        String date = c.getString(OffersActivity.TAG_DATE);
                        int numberOfViews = Integer.parseInt(c.getString(OffersActivity.TAG_NUM_OF_VIEWS));
                        String PdfUrl = c.getString(OffersActivity.TAG_PDFURL);
                        String coverUrl = c.getString(OffersActivity.TAG_COVERURL);
                        int numberOfPages = Integer.parseInt(c.getString(OffersActivity.TAG_NUMOFPAGES));
                        String specification = c.getString(OffersActivity.TAG_SPECIFICATION);
                        int store_idstore = Integer.parseInt(c.getString(OffersActivity.TAG_STORE_IDSTORE));

                        Offer offer = new Offer(id, title, date, numberOfViews, PdfUrl, coverUrl, numberOfPages, specification, store_idstore);

                            if ( list.equals("latest"))
                                latestOffersList.add(offer);
                            else if ( list.equals("mostViewed"))
                                mostViewedList.add(offer);

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getStoresImages(){

        Thread retrieveLogo ;
        retrieveLogo = new Thread(){

            @Override
            public void run() {
                for( int i=0; i < storesList.size() ; i++ ) {
                    final Store store = storesList.get(i);
                    getStoreImage( i, store.getLogoUrl());
                }
            }
        };

        retrieveLogo.start();

    }

    public static void getOffersImages(final String list ){

        Thread retrieveImage ;
        retrieveImage = new Thread(){

            @Override
            public void run() {
                if ( list.equals("latest"))
                    for( int i=0; i < latestOffersList.size() ; i++ ) {
                        Offer offer = latestOffersList.get(i);
                        getLatestImage( i, offer.getCoverURL());
                    }
                else if ( list.equals("mostViewed") )
                    for( int i=0; i < mostViewedList.size() ; i++ ) {
                        Offer offer = mostViewedList.get(i);
                        getMostViewedImage( i, offer.getCoverURL());
                    }
            }
        };

        retrieveImage.start();

    }

    private static void getStoreImage(final int index, String urlToImage){

        class GetImage extends AsyncTask<String,Void,Bitmap> {


            @Override
            protected Bitmap doInBackground(String... params) {

                URL url;
                String urlToImage = params[0];
                Bitmap image = null;

                    if ( storesBitmapsList.size() == (index+1) )
                        image = storesBitmapsList.get( index );

                    if ( image == null ) {
                        try {

                            url = new URL(urlToImage);
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();

                            Log.i("getImage", "URL : " + urlToImage);

                            image = BitmapFactory.decodeStream(con.getInputStream());
                            storesBitmapsList.add(image);

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                return image;
            }


            @Override
            protected void onPostExecute(Bitmap bitmap) {

                super.onPostExecute(bitmap);
                if(storesList.size()>0)
                {  Store store = storesList.get( index );
                store.setLogo(bitmap);
                MainActivity.setStoreBitmap(index);}
                Log.i("post Execute", "Call # : "+index );

            }
        }

        GetImage gi = new GetImage();
        gi.execute(urlToImage);

    }

    private static void getLatestImage(final int index, String urlToImage){

        class GetImage extends AsyncTask<String,Void,Bitmap> {


            @Override
            protected Bitmap doInBackground(String... params) {

                URL url;
                String urlToImage = params[0];
                Bitmap image = null;

                if ( latestBitmapsList.size() == (index+1) )
                    image = latestBitmapsList.get( index );

                if ( image == null ) {
                    try {

                        url = new URL(urlToImage);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();

                        Log.i("getImage", "URL : " + urlToImage);

                        image = BitmapFactory.decodeStream(con.getInputStream());
                        latestBitmapsList.add(image);

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return image;
            }


            @Override
            protected void onPostExecute(Bitmap bitmap) {

                super.onPostExecute(bitmap);
            if(latestOffersList.size()>0)
            {Offer offer = latestOffersList.get( index );
                offer.setCover(bitmap);
                MainActivity.setOffersBitmap(index,"latest");}
                Log.i("post Execute", "Call # : "+index );

            }
        }

        GetImage gi = new GetImage();
        gi.execute(urlToImage);

    }

    private static void getMostViewedImage(final int index, String urlToImage){

        class GetImage extends AsyncTask<String,Void,Bitmap> {


            @Override
            protected Bitmap doInBackground(String... params) {

                URL url;
                String urlToImage = params[0];
                Bitmap image = null;

                if ( mostViewedBitmapsList.size() == (index+1) )
                    image = mostViewedBitmapsList.get( index );

                if ( image == null ) {
                    try {

                        url = new URL(urlToImage);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();

                        Log.i("getImage", "URL : " + urlToImage);

                        image = BitmapFactory.decodeStream(con.getInputStream());
                        mostViewedBitmapsList.add(image);

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return image;
            }


            @Override
            protected void onPostExecute(Bitmap bitmap) {

                super.onPostExecute(bitmap);
                if(mostViewedList.size()>0)
                {  Offer offer = mostViewedList.get( index );
                offer.setCover(bitmap);
                MainActivity.setOffersBitmap(index,"mostViewed");}
                Log.i("post Execute", "Call # : "+index );

            }
        }

        GetImage gi = new GetImage();
        gi.execute(urlToImage);

    }

    private static void setStoreBitmap( int index ){

        // RelativeLayout  Params  apply on child (imageButton ) when on click
        final RelativeLayout.LayoutParams rlp4 = new RelativeLayout.LayoutParams(
                300,
                300
        );
        rlp4.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rlp4.addRule(RelativeLayout.CENTER_VERTICAL);

        ImageButton storeLogo = storesLogosList.get(index);
        storeLogo.setBackgroundResource(0);
        storeLogo.setLayoutParams(rlp4);
        storeLogo.setImageBitmap(storesList.get(index).getLogo());

    }

    private static void setOffersBitmap( int index, String list ){

        // RelativeLayout  Params  apply on child (imageButton ) when on click
        final RelativeLayout.LayoutParams rlp4 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        rlp4.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rlp4.addRule(RelativeLayout.CENTER_VERTICAL);

        if ( list.equals("latest")) {
            ImageButton cover = latestOffersCoversList.get(index);
            cover.setBackgroundResource(0);
            cover.setLayoutParams(rlp4);
            cover.setBackgroundResource(R.drawable.customborder);
            cover.setImageBitmap(latestOffersList.get(index).getCover());

        } else
        if ( list.equals("mostViewed") ) {
            ImageButton cover = mostViewedCoversList.get(index);
          cover.setBackgroundResource(0);
            cover.setLayoutParams(rlp4);
            cover.setBackgroundResource(R.drawable.customborder);
            cover.setImageBitmap(mostViewedList.get(index).getCover());
        }

    }


    public void actionRefresh (){

        if ( ConnectChecked.isNetworkAvailable( getBaseContext() ) &&
                ConnectChecked.isOnline() ) {

            getJSON( false );

            runOnUiThread(new Runnable() {
                public void run() {
                    Log.i("onRefresh "," Refreshing ......");

                    int position = tabLayout.getSelectedTabPosition();

                    storesList.clear();
                    storesLogosList.clear();
                    storesBitmapsList.clear();

                    latestOffersList.clear();
                    latestOffersCoversList.clear();
                    latestBitmapsList.clear();

                    mostViewedList.clear();
                    mostViewedCoversList.clear();
                    mostViewedBitmapsList.clear();

                    storesResult = null;
                    latestResult = null;
                    mostViewedResult = null;

                    Thread refresh = new Thread(){
                        @Override
                        public void run() {
                            getJSON( true );
                        }
                    };

                    try {
                        refresh.start();
                        refresh.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    displayViewPagerContent( position );

                }
            });

        } else {
            Snackbar.make( toolbar , "No Internet Connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

}