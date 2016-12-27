package com.applefish.smartshop.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.applefish.smartshop.R;
import com.applefish.smartshop.classes.Offer;
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

public class OffersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG_NAME = "storeName";
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 0;
    private static final int MY_PERMISSIONS_READ_PHONE_STATE = 0;

    private Store selectedStore;

    private String jsonResult;
    private static final String TAG_RESULTS="result";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DATE = "date";
    private static final String TAG_NUM_OF_VIEWS = "numberOfViews";
    private static final String TAG_PDFURL = "PdfUrl";
    private static final String TAG_COVERURL= "coverUrl";
    private static final String TAG_NUMOFPAGES ="numberOfPages";
    private static final String TAG_SPECIFICATION ="specification";
    private static final String TAG_STORE_IDSTORE="store_idstore";
    private static final String OFFERS_URL = "http://192.168.1.2/smartshop/idid.php";

    private static ArrayList<Offer> offersList;
    private static ArrayList<ImageButton> offersCoversList;
    private static JSONArray offersArray = null;

    final static String Key = "com.applefish.smartshop.PdfViewer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle bundle = getIntent().getExtras();
        String activityName = bundle.getString("ACTIVITY_NAME");

            if(activityName.equalsIgnoreCase("MAIN"))
                selectedStore = (Store) bundle.get(TAG_NAME);
//-----------------------------------------------------------
        ImageView logo = (ImageView)findViewById(R.id.storelogo);
        logo.setImageBitmap(selectedStore.getLogo());

        TextView storeName = (TextView)findViewById(R.id.storeName);
        storeName.setText(selectedStore.getStoreName());

        offersList = new ArrayList<>();
        offersCoversList = new ArrayList<>();

        Thread getData = new Thread(){
            @Override
            public void run() {
                super.run();
                getJSON( OFFERS_URL );            }
        };

        getData.start();

        int permissionCheckWriteExternalStorage = ContextCompat.checkSelfPermission(
                OffersActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheckReadPhoneState = ContextCompat.checkSelfPermission(OffersActivity.this,
                Manifest.permission.READ_PHONE_STATE);

        if ( permissionCheckWriteExternalStorage != PackageManager.PERMISSION_GRANTED ||
                permissionCheckReadPhoneState != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(OffersActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

        }

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


    private void getJSON(String url) {

        int storeID = selectedStore.getId();

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

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }
                    result=sb.toString().trim();
                    Log.i("getJSONOffers", "doInBackground: " +result);
                    return result;

                }catch(Exception e){
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                jsonResult = result;
                buidlOffersList();

            }
        }
        GetJSON gj = new GetJSON();
        gj.execute(url+"/?store_idstore="+storeID);
    }



    public void buidlOffersList() {

        try {

            if ( jsonResult != null) {

                JSONObject jsonObj = new JSONObject(jsonResult);

                if( !jsonResult.toString().equals("{\"result\":\"NoOffers\"}")) {
                offersArray = jsonObj.getJSONArray(TAG_RESULTS);


                for (int i = 0; i < offersArray.length(); i++) {

                    JSONObject c = offersArray.getJSONObject(i);

                    int id = Integer.parseInt(c.getString(TAG_ID));
                    String title = c.getString(TAG_TITLE);
                    String date = c.getString(TAG_DATE);
                    int numberOfViews = Integer.parseInt(c.getString(TAG_NUM_OF_VIEWS));
                    String PdfUrl = c.getString(TAG_PDFURL);
                    String coverUrl = c.getString(TAG_COVERURL);
                    int numberOfPages = Integer.parseInt(c.getString(TAG_NUMOFPAGES));
                    String specification = c.getString(TAG_SPECIFICATION);
                    int store_idstore = Integer.parseInt(c.getString(TAG_STORE_IDSTORE));

                    Offer offer = new Offer(id, title, date, numberOfViews, PdfUrl, coverUrl, numberOfPages, specification, store_idstore);
                    offersList.add(offer);

                }


                    getAllImages();

                    {

                        final TableLayout mTlayout = (TableLayout)findViewById(R.id.tableoffer);
                        final TableRow[] tr = {new TableRow(this)};
                        if(offersList.size()>0)
                        this.findViewById(R.id.progressbaroffer).setVisibility(View.GONE);

                       offersCoversList.clear();

                        Thread setupTab = new Thread() {

                            @Override
                            public void run() {

                                super.run();


                                for ( int i=0; i < offersList.size(); i++ ){


                                 TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                           TableLayout.LayoutParams.MATCH_PARENT );
                                        params.rightMargin = 5;
                                        params.leftMargin = 5;
                                        params.topMargin = 5;
                                        params.bottomMargin = 5;
                                        tr[0] = new TableRow(getBaseContext());
                                        tr[0].setLayoutParams(params);
                                        tr[0].setBackgroundColor(Color.BLACK);
                                         mTlayout.addView(tr[0]);

                                       //create component
                                    RelativeLayout relativeLayout = new RelativeLayout(getBaseContext());
                                    LinearLayout linearLayout = new LinearLayout(getBaseContext());
                                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                                    TextView title = new TextView(getBaseContext());
                                    title.setText( offersList.get(i).getTitle() );
                                 //   title.setBackgroundResource(R.drawable.customborder3);
                                    title.setTextSize(15);
                                    title.setTextColor(Color.GRAY);

                                    TextView date = new TextView(getBaseContext());
                                    date.setText( offersList.get(i).getDate() );
                                //    date.setBackgroundResource(R.drawable.customborder3);
                                    date.setTextSize(15);
                                    date.setTextColor(Color.GRAY);
                                    date.setTypeface(null, Typeface.BOLD);

                                    TextView numOfPages = new TextView(getBaseContext());
                                    numOfPages.setText( " Pages="+offersList.get(i).getNumberOfPages() );
                                    numOfPages.setBackgroundResource(R.drawable.customborder3);
                                    numOfPages.setTextSize(14);
                                    numOfPages.setTextColor(Color.WHITE);
                                    numOfPages.setTypeface(null, Typeface.BOLD);

                                    final ImageButton offerCover = new ImageButton(getBaseContext());

                                    // TableRow  Params  apply on child (RelativeLayout)
                                    TableRow.LayoutParams rlp = new TableRow.LayoutParams(200,
                                            300,1 );

                                    TableRow.LayoutParams rlp2 = new TableRow.LayoutParams(450,
                                            300 ,1);
                                    // LinearLayout  Params  apply on child (textView Number of pages)
                                    final LinearLayout.LayoutParams rlp3 = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    rlp3.weight=1;
                                    rlp3.rightMargin=10;
                                    rlp3.leftMargin=300;
                                    rlp3.bottomMargin=27;
                                    rlp3.topMargin=10;
                                    // LinearLayout  Params  apply on child (textView Title)
                                    final LinearLayout.LayoutParams rlp4= new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    rlp4.weight=1;
                                    rlp4.leftMargin=300;
                                    rlp4.bottomMargin=20;
                                    // LinearLayout  Params  apply on child (textView Date)
                                    final LinearLayout.LayoutParams rlp5 = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    rlp5.weight=1;
                                    rlp5.leftMargin=25;
                                    rlp5.bottomMargin=20;

                                    // RelativeLayout  Params  apply on child (imageButton )
                                    RelativeLayout.LayoutParams rlp6 = new RelativeLayout.LayoutParams( 52, 52);
                                    rlp6.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                    rlp6.addRule(RelativeLayout.CENTER_VERTICAL);
                                    rlp6.leftMargin=10;
                                    rlp6.rightMargin=10;

                                    //set layout params
                                    relativeLayout.setLayoutParams(rlp);
                                    linearLayout.setLayoutParams(rlp2);

                                    offerCover.setLayoutParams(rlp6);
                                    numOfPages.setLayoutParams(rlp3);
                                    date.setLayoutParams(rlp4);
                                    title.setLayoutParams(rlp5);

                                   tr[0].setBackgroundResource(R.drawable.mybutton_background);
                                   tr[0].setAddStatesFromChildren(true); // <<<<  this line is the best in the world

                                    offerCover.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    offerCover.setBackgroundResource(R.drawable.spin_animation);


                                    // Get the background, which has been compiled to an AnimationDrawable object.
                                    AnimationDrawable frameAnimation = (AnimationDrawable) offerCover.getBackground();

                                    // Start the animation (looped playback by default).
                                    frameAnimation.start();

                               tr[0].setId( 1100+offersList.get(i).getId()) ;

                                    //add  View
                                  relativeLayout.addView(offerCover);
                                    linearLayout.addView(numOfPages);
                                    linearLayout.addView(title);
                                    linearLayout.addView(date);


                                    tr[0].addView(relativeLayout);
                                    tr[0].addView(linearLayout);

                                    offersCoversList.add(offerCover);

                                    tr[0].setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {


                                          Intent pdfViewer = new Intent( );
                                          int tableRowId = v.getId();
                                          String pdfUrl = offersList.get(tableRowId-1101).getPDF_URL();
                                          Toast.makeText(getBaseContext(),pdfUrl,Toast.LENGTH_SHORT).show();
                                          Log.i("getAllImages", "setOnClickListener: " +pdfUrl);
                                          pdfViewer.putExtra(Key,pdfUrl);


                                          pdfViewer.setClass( getBaseContext(), PdfViewerActivity.class );

                                          startActivity( pdfViewer);

                                        }
                                    });


                                }

                            }
                        };


                        try {
                            setupTab.start();
                            setupTab.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

//                     OffersActivity.setImageBitmap();

                    }

            }
            else {
                Toast.makeText(getBaseContext(), "NO offers", Toast.LENGTH_SHORT).show();
            }
            }
            else {
                Toast.makeText(getBaseContext(), "NO thing in DB", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void getAllImages(){

        Thread retrieveCover;


        retrieveCover = new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < offersList.size(); i++) {
                    final Offer offer = offersList.get(i);
                    getImage(i, offer.getCoverURL());
                }
            }
        };

        try {
            retrieveCover.start();
            retrieveCover.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static void getImage(final int id, String urlToImage){

        class GetImage extends AsyncTask<String,Void,Bitmap> {


            @Override
            protected Bitmap doInBackground(String... params) {

                URL url;
                Bitmap image = null;

                String urlToImage = params[0];

                try {

                    url = new URL(urlToImage);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

//                    int status = con.getResponseCode();

                    Log.i("getImage", "URL : " +urlToImage);

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
                Offer offer = offersList.get( id );
                offer.setCover(bitmap);
                OffersActivity.setImageBitmap(id);
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


            ImageButton offerCover = offersCoversList.get(index);
            offerCover.setBackgroundResource(0);
            offerCover.setLayoutParams(rlp4);
            offerCover.setImageBitmap(offersList.get(index).getCover());



    }
}
