package com.applefish.smartshop.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.applefish.smartshop.R;
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

import static com.applefish.smartshop.R.id.container;


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
    private static JSONArray storesArray = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        sectionsPagerAdapter = new MainActivity.SectionsPagerAdapter(getSupportFragmentManager());

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

        getJSON( STORES_URL );

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
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MainActivity.PlaceholderFragment newInstance(int sectionNumber) {

            MainActivity.PlaceholderFragment fragment = new MainActivity.PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_tabbed, container, false);

            LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.layoutStore);

                   if( getArguments().getInt(ARG_SECTION_NUMBER) == 3 ) {

                       for ( int i=0; i < storesList.size(); i++ ){
                           ImageView imageButton = new ImageButton(getContext());
                           imageButton.setImageBitmap(storesList.get(i).getLogo());

                           layout.addView(imageButton);
                       }
                   }

            return rootView;
        }
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
                getAllImages();
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute(url);
    }


    public void getAllImages() {

        try {

            if ( jsonResult != null) {

                JSONObject jsonObj = new JSONObject(jsonResult);
                storesArray = jsonObj.getJSONArray(TAG_RESULTS);

                for (int i = 0; i < storesArray.length(); i++) {

                    JSONObject c = storesArray.getJSONObject(i);
                    int id = Integer.parseInt( c.getString(TAG_ID) );
                    String name = c.getString(TAG_NAME);
                    String logoUrl = c.getString(TAG_ADD);

                    Store store = new Store( id, name, logoUrl );
                    storesList.add(store);

                }

            } else {
                Toast.makeText(getApplicationContext(), "لايوجد شيء بالقاعدة", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



        for( int i=0; i < storesList.size() ; i++ ) {

            Store store = storesList.get(i);
            getImage( store.getId() , store.getStoreName(), store.getLogoUrl() );


        }

    }

    private void getImage(final int id, final String storeName, String urlToImage){

        class GetImage extends AsyncTask<String,Void,Bitmap> {


            @Override
            protected Bitmap doInBackground(String... params) {

                URL url;
                Bitmap image = null;

                String urlToImage = params[0];

                try {

                    url = new URL(urlToImage);

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    int status = con.getResponseCode();

                    Log.i("getJSON", "doInBackground: " +status);

                    image = BitmapFactory.decodeStream(con.getInputStream());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return image;
            }


            @Override
            protected void onPostExecute(Bitmap bitmap) {

                super.onPostExecute(bitmap);

                // id-1 because of id starts from 1
                Store store = storesList.get( id-1 );
                store.setLogo(bitmap);


            }
        }
        GetImage gi = new GetImage();
        gi.execute(urlToImage);
    }

}



