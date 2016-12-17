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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applefish.smartshop.R;
import com.applefish.smartshop.classes.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import android.app.ProgressDialog;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import android.widget.Button;

import static com.applefish.smartshop.R.id.container;
import static com.applefish.smartshop.R.id.imageView;


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
     String myJSON;

    private static final String TAG_RESULTS="result";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "storeName";
    private static final String TAG_ADD ="logoUrl";
    private static String store_url = "http://192.168.1.2/smartshop/retrivelogo.php";
    JSONArray store = null;
    ArrayList<HashMap<String, String>> storeList;
   static ArrayList<Store> storeImageList;

    //------------------
    static  Bitmap bitmapb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

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


        //get stores data---------------------
        storeList = new ArrayList<HashMap<String,String>>();
        storeImageList = new ArrayList<Store>();
        Toast.makeText(getApplicationContext(),"before get",Toast.LENGTH_LONG).show();
        //getData();
        getJSON("http://192.168.1.2/smartshop/retrivelogo.php");
        Toast.makeText(getApplicationContext(),"after get",Toast.LENGTH_LONG).show();
        //----------------------------------------
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favourites) {
            // Handle the camera action
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
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.layoutStore);
                   if(storeImageList.size()>=2)
                   {
                       for (int i=0;i<storeImageList.size();i++){
                           ImageButton imageButton=new ImageButton(getContext());

                           imageButton.setImageBitmap(storeImageList.get(i).getLogo());


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
        class GetJSON extends AsyncTask<String, Void, String>{
            ProgressDialog loading;

//           @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                loading = ProgressDialog.show(MainActivity.this, "Please Wait...",null,true,true);
//            }
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

                    if (status != HttpURLConnection.HTTP_OK)
                        Log.i("getJSON", "doInBackground: " +status);
                    else
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
//               loading.dismiss();
                myJSON=result;
                Toast.makeText(getApplicationContext(),"Json>>"+myJSON,Toast.LENGTH_LONG).show();
                showList();
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute(url);
    }

    /////////////
    public  void getData(){
        Toast.makeText(getApplicationContext(),"getData",Toast.LENGTH_LONG).show();
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost("http://192.168.1.2/smartshop/retrivelogo.php");

                // Depends on your web service
             //   httppost.setHeader("Content-type", "application/json");

                InputStream inputStream = null;
                String result = "{\"result\":[{\"id\":\"1\",\"storeName\":\"Mercedes company\",\"logoUrl\":\"http:\\/\\/localhost\\/smartshop\\/logo\\/Mercedeslogo.jpg\"},{\"id\":\"2\",\"storeName\":\"Lexus company\",\"logoUrl\":\"http:\\/\\/localhost\\/smartshop\\/logo\\/Lexuslogo.jpg\"}]}";
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                }
                finally {
                    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                Toast.makeText(getApplicationContext(),"Json>>"+myJSON,Toast.LENGTH_LONG).show();
            showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }
    //show

    public void showList(){
        Toast.makeText(getApplicationContext(),"showList",Toast.LENGTH_LONG).show();
        try {
            if(myJSON!=null) {
                JSONObject jsonObj = new JSONObject(myJSON);
                store = jsonObj.getJSONArray(TAG_RESULTS);

                for (int i = 0; i < store.length(); i++) {
                    JSONObject c = store.getJSONObject(i);
                    String id = c.getString(TAG_ID);
                    String name = c.getString(TAG_NAME);
                    String address = c.getString(TAG_ADD);

                    HashMap<String, String> persons = new HashMap<String, String>();

                    persons.put(TAG_ID, id);
                    persons.put(TAG_NAME, name);
                    persons.put(TAG_ADD, address);

                    storeList.add(persons);
                    Toast.makeText(getApplicationContext(), id + name + address, Toast.LENGTH_LONG).show();

                }

                GetAllImage();
            }
            else {
                Toast.makeText(getApplicationContext(), "لايوجد شيء بالقاعدة", Toast.LENGTH_LONG).show();

            }
/*
                ListAdapter adapter = new SimpleAdapter(
                        MainActivity.this, personList, R.layout.list_item,
                        new String[]{TAG_ID,TAG_NAME,TAG_ADD},
                        new int[]{R.id.id, R.id.name, R.id.address}
                );

                list.setAdapter(adapter);
*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //---------------------------------------------------------------------------image
    private void getImage(final int id, final String stroename, String urlToImage){
        class GetImage extends AsyncTask<String,Void,Bitmap>{
            ProgressDialog loading;
            @Override
            protected Bitmap doInBackground(String... params) {
                URL url = null;
                Bitmap image = null;

                String urlToImage = params[0];
                try {
                    url = new URL(urlToImage);

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    int status = con.getResponseCode();

                    if (status != HttpURLConnection.HTTP_OK)
                        Log.i("getJSON", "doInBackground: " +status);
                    else
                        Log.i("getJSON", "doInBackground: " +status);

                    image = BitmapFactory.decodeStream(con.getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bitmapb=image;
                return bitmapb;
            }

//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                loading = ProgressDialog.show(MainActivity.this,"Downloading Image...","Please wait...",true,true);
//
//            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
               // loading.dismiss();
                Store store=new Store(id,stroename,bitmap);
                storeImageList.add(store);

            }
        }
        GetImage gi = new GetImage();
        gi.execute(urlToImage);
    }
    //---------------------------------Get all images
    public void GetAllImage()
    {
        HashMap<String,String> store = new HashMap<String,String>();
        for(int i=0;i<storeList.size();i++)
        {

            store=(HashMap<String, String>) storeList.get(i);

            int id =Integer.parseInt(store.get(TAG_ID));
            String name=store.get(TAG_NAME);
           String address= store.get(TAG_ADD);
            getImage(id,name,address);



        }

    }
}



