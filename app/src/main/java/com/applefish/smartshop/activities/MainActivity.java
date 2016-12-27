package com.applefish.smartshop.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.applefish.smartshop.R;
import com.applefish.smartshop.classes.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.applefish.smartshop.R.id.bottom;
import static com.applefish.smartshop.R.id.container;
import static com.applefish.smartshop.R.id.textView;
import static com.applefish.smartshop.R.id.top;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private MainActivity.SectionsPagerAdapter sectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;

    String jsonResult;

    private static final String TAG_RESULTS="result";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "storeName";
    private static final String TAG_ADD ="logoUrl";
    private static final String STORES_URL = "http://192.168.1.2/smartshop/retrivelogo.php";

    private static ArrayList<Store> storesList;
    private static ArrayList<ImageButton> storesLogosList;
    private static ArrayList<Bitmap> bitmapsList;
    private static JSONArray storesArray = null;

    static Thread getData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        sectionsPagerAdapter = new MainActivity.SectionsPagerAdapter( getSupportFragmentManager() );

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(container);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        storesList = new ArrayList<>();
        storesLogosList = new ArrayList<>();
        bitmapsList = new ArrayList<>();

        getData = new Thread(){
            @Override
            public void run() {
                super.run();
                getJSON( STORES_URL );
            }
        };

        getData.start();

    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favourites) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_settings) {

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return MainActivity.PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Latest";
                case 1:
                    return "Most Viewed";
                case 2:
                    return "Stores";
            }
            return null;
        }


    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private View rootView;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MainActivity.PlaceholderFragment newInstance( int sectionNumber ) {

            MainActivity.PlaceholderFragment fragment = new MainActivity.PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            rootView = inflater.inflate(R.layout.fragment_tabbed, container, false);


                   if( getArguments().getInt(ARG_SECTION_NUMBER) == 3 ) {

                       if( storesList.size() > 0 )
                            rootView.findViewById(R.id.progressbarstore).setVisibility(View.GONE);

                       storesLogosList.clear();

                       SetupTabThread setupTab = new SetupTabThread( getData );

                       try {
                           setupTab.start();
                           setupTab.join();
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }

                       MainActivity.getAllImages();

                   }

            return rootView;

        }


        private class SetupTabThread extends Thread {

            private Thread predecessor;

            public SetupTabThread(Thread predecessor) {
                this.predecessor = predecessor;
            }

            public void run() {

                if (predecessor != null && predecessor.isAlive()) {
                    try {
                        predecessor.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                final TableLayout mTlayout = (TableLayout)rootView.findViewById(R.id.tablestore);
                final TableRow[] tr = {new TableRow(getContext())};

                //do your stuff
                for ( int i=0; i < storesList.size(); i++ ){

                    if (i % 2 == 0) {
                        tr[0] = new TableRow(getContext());
                        mTlayout.addView(tr[0]);
                    }

                    //create component
                    RelativeLayout linearLayout1 = new RelativeLayout(getContext());

                    TextView storeName = new TextView(getContext());
                    storeName.setText( storesList.get(i).getStoreName() );

                    final ImageButton storeLogo = new ImageButton(getContext());

                    // TableRow  Params  apply on child (RelativeLayout)
                    TableRow.LayoutParams rlp = new TableRow.LayoutParams( 225, 275,1f );
                    rlp.rightMargin=7;
                    rlp.leftMargin=7;
                    rlp.topMargin=7;
                    rlp.bottomMargin=7;

                    // RelativeLayout  Params  apply on child (imageButton )
                    RelativeLayout.LayoutParams rlp2 = new RelativeLayout.LayoutParams( 52, 52 );
                    rlp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    rlp2.addRule(RelativeLayout.CENTER_VERTICAL);
                    rlp2.rightMargin=10;
                    rlp2.leftMargin=10;

                    // RelativeLayout  Params  apply on child (textView )
                    final RelativeLayout.LayoutParams rlp3 = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    rlp3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    rlp3.addRule(RelativeLayout.CENTER_HORIZONTAL);

                    //set layout params
                    linearLayout1.setLayoutParams(rlp);
                    storeLogo.setLayoutParams(rlp2);
                    storeName.setLayoutParams(rlp3);


                    linearLayout1.setBackgroundResource(R.drawable.mybutton_background);
                    linearLayout1.setAddStatesFromChildren(true); // <<<<  this line is the best in the world

                    storeLogo.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    storeLogo.setBackgroundResource(R.drawable.spin_animation);


                    // Get the background, which has been compiled to an AnimationDrawable object.
                    AnimationDrawable frameAnimation = (AnimationDrawable) storeLogo.getBackground();

                    // Start the animation (looped playback by default).
                    frameAnimation.start();

                    //add id for imageButton  &  linearLayout1
                    storeLogo.setId( 1000+storesList.get(i).getId() );
                    linearLayout1.setId( 2000+storesList.get(i).getId()) ;

                    //add  View
                    linearLayout1.addView(storeLogo);
                    linearLayout1.addView(storeName);
                    tr[0].addView(linearLayout1);

                    storesLogosList.add(storeLogo);

                    storeLogo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent offers = new Intent( );
                            Bundle bundle = new Bundle();

                            offers.setClass( getContext(), OffersActivity.class );
                            bundle.putParcelable(TAG_NAME , storesList.get( storeLogo.getId()-1001 ));
                            offers.putExtras( bundle );
                            startActivity( offers );

                        }
                    });


                }
            }
        }

    }




    private void getJSON(String url) {


        class GetJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String result ="";
                        BufferedReader bufferedReader = null;
                try {

                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    int status = con.getResponseCode();


                    Log.i("getJSON", "doInBackground: " +status);

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
                jsonResult = result;
                buildStoresList();
            }
        }

        GetJSON gj = new GetJSON();
        gj.execute(url);

    }


    public void buildStoresList() {

        try {

            if ( jsonResult != null) {

                JSONObject jsonObj = new JSONObject(jsonResult);
                if(!jsonResult.toString().equals("{\"result\":\"NoStores\"}")) {
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

    public static void getAllImages(){

        Thread retrieveLogo ;
        retrieveLogo = new Thread("retrieveLogo"){

            @Override
            public void run() {
                for( int i=0; i < storesList.size() ; i++ ) {
                    final Store store = storesList.get(i);
                    getImage( i, store.getLogoUrl());
                }
            }
        };

        retrieveLogo.start();

    }

    private static void getImage(final int index, String urlToImage){

        class GetImage extends AsyncTask<String,Void,Bitmap> {


            @Override
            protected Bitmap doInBackground(String... params) {

                URL url;
                String urlToImage = params[0];
                Bitmap image = null;

                    if ( bitmapsList.size() == (index+1) )
                        image = bitmapsList.get( index );

                    if ( image == null ) {
                        try {

                            url = new URL(urlToImage);
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();

                            Log.i("getImage", "URL : " + urlToImage);

                            image = BitmapFactory.decodeStream(con.getInputStream());
                            bitmapsList.add(image);

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

                // id-1 because of id starts from 1
                Store store = storesList.get( index );
                store.setLogo(bitmap);
                MainActivity.setImageBitmap(index);
                Log.i("post Execute", "Call # : "+index );

            }
        }

        GetImage gi = new GetImage();
        gi.execute(urlToImage);

    }

    private static void setImageBitmap( int index ){

        // RelativeLayout  Params  apply on child (imageButton ) when on click
        final RelativeLayout.LayoutParams rlp4 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        rlp4.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rlp4.addRule(RelativeLayout.CENTER_VERTICAL);
        rlp4.rightMargin=20;
        rlp4.leftMargin=20;
        rlp4.bottomMargin=40;


        ImageButton storeLogo = storesLogosList.get(index);
        storeLogo.setBackgroundResource(0);
        storeLogo.setLayoutParams(rlp4);
        storeLogo.setImageBitmap(storesList.get(index).getLogo());

    }



}



