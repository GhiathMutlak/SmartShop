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
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
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
import com.applefish.smartshop.classes.ConnectChecked;
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
    public static final String TAG_TITLE = "title";
    public static final String TAG_DATE = "date";
    public static final String TAG_NUM_OF_VIEWS = "numberOfViews";
    public static final String TAG_PDFURL = "PdfUrl";
    public static final String TAG_COVERURL= "coverUrl";
    public static final String TAG_NUMOFPAGES ="numberOfPages";
    public static final String TAG_SPECIFICATION ="specification";
    public static final String TAG_STORE_IDSTORE="store_idstore";
    //    private static final String OFFERS_URL = "http://192.168.1.2/smartshop/idid.php";
    private static final String OFFERS_URL ="http://smartshop-uae.org/smartshop/idid.php";
    private static ArrayList<Offer> offersList;
    private static ArrayList<ImageButton> offersCoversList;
    private static JSONArray offersArray = null;

    final static String Key = "com.applefish.smartshop.PdfViewer";
    final static String Key2 = "com.applefish.smartshop.IDOffer";

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

        if (ConnectChecked.isNetworkAvailable(getBaseContext()) &&
                ConnectChecked.isOnline()) {
        Thread getData = new Thread(){
            @Override
            public void run() {
                super.run();
                getJSON( OFFERS_URL ,true);            }
        };

        getData.start();
        } else {
            Snackbar.make(toolbar, "No Internet Connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

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
            //Stop Task
            getJSON( OFFERS_URL ,false);
            getImage(0,"",false);
            offersList = new ArrayList<>();
            offersCoversList = new ArrayList<>();
            super.onBackPressed();
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favourites)
        {
            Intent favorite2 = new Intent();
            favorite2.setClass(getBaseContext(), FavoriteActivity.class);
            startActivity(favorite2);
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


    private void getJSON(String url,boolean ckeck) {

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
                buildOffersList();

            }

            public void canceltask(GetJSON getJSON)
            {getJSON.cancel(true);}
        }
        GetJSON gj = new GetJSON();
        if(ckeck)
        {
            gj.execute(url+"/?store_idstore="+storeID);
        }
        else
        {
            if(!gj.isCancelled())
                gj.cancel(true);
        }

    }



    public void buildOffersList() {

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
                                    //========================================


                                    TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                            TableLayout.LayoutParams.MATCH_PARENT );
                                    params.rightMargin = 25;
                                    params.leftMargin = 25;
                                    params.topMargin = 5;
                                    params.bottomMargin = 5;
                                    params.gravity= Gravity.CENTER;
                                    tr[0] = new TableRow(getBaseContext());
                                    tr[0].setGravity(Gravity.CENTER);
                                    tr[0].setLayoutParams(params);
                                    //   tr[0].setBackgroundColor(Color.BLACK);
                                    mTlayout.addView(tr[0]);

                                    //create component
                                    RelativeLayout relativeLayout = new RelativeLayout(getBaseContext());
                                    LinearLayout linearLayout = new LinearLayout(getBaseContext());
                                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                                    linearLayout.setGravity(Gravity.CENTER);

                                    TextView title = new TextView(getBaseContext());
                                    title.setText( offersList.get(i).getTitle() );
                                    // title.setBackgroundResource(R.drawable.customborder4);
                                    title.setTextSize(16);
                                    title.setTextColor(Color.rgb(24, 155, 226));
                                    title.setTypeface(null, Typeface.BOLD);
                                    title.setGravity(Gravity.CENTER);
                                    TextView date = new TextView(getBaseContext());
                                    date.setText( "Added Date "+offersList.get(i).getDate() );
                                    //    date.setBackgroundResource(R.drawable.customborder3);
                                    date.setTextSize(15);
                                    date.setTextColor(Color.RED);
                                    date.setGravity(Gravity.CENTER);
                                    date.setTypeface(null, Typeface.BOLD);

                                    TextView numOfPages = new TextView(getBaseContext());
                                    numOfPages.setText( offersList.get(i).getNumberOfPages()+"Pages");
                                    numOfPages.setBackgroundResource(R.drawable.customborder3);
                                    numOfPages.setTextSize(14);
                                    numOfPages.setTextColor(Color.WHITE);
                                    numOfPages.setGravity(Gravity.CENTER);
                                    numOfPages.setTypeface(null, Typeface.BOLD);


                                    final ImageButton offerCover = new ImageButton(getBaseContext());
                                    offerCover.setAdjustViewBounds(true);
                                    offerCover.setPadding(20,20,20,20);
                                    // TableRow  Params  apply on child (RelativeLayout)
                                    TableRow.LayoutParams rlp = new TableRow.LayoutParams(0,
                                            350
                                            ,40 );

                                    TableRow.LayoutParams rlp2 = new TableRow.LayoutParams(0,
                                            350
                                            ,60);
                                    rlp2.gravity=Gravity.CENTER;


                                    // LinearLayout  Params  appl
                                    // y on child (textView Number of pages)
                                    final LinearLayout.LayoutParams rlp3 = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            0,
                                            23
                                    );
                                    rlp3.gravity= Gravity.CENTER;

                                    // rlp3.rightMargin=10;
                                    // rlp3.leftMargin=100;
                                    // rlp3.bottomMargin=27;
                                    //rlp3.topMargin=3;
                                    // LinearLayout  Params  apply on child (textView Date)
                                    final LinearLayout.LayoutParams rlp4= new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            0,
                                            23
                                    );
                                    rlp4.gravity= Gravity.CENTER;
                                    rlp4.leftMargin=2;
                                    //rlp4.topMargin=10;
                                    // LinearLayout  Params  apply on child (textView Title)
                                    final LinearLayout.LayoutParams rlp5 = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            0,
                                            56
                                    );
                                    rlp5.gravity=Gravity.CENTER;
                                    // rlp5.gravity=Gravity.CENTER_HORIZONTAL;

                                    // rlp5.leftMargin=20;
                                    // rlp.gravity= Gravity.CENTER;
                                    //  rlp5.bottomMargin=20;

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
                                    relativeLayout.setAddStatesFromChildren(true);
                                    //  offerCover.setScaleType(ImageView.ScaleType.FIT_CENTER);



                                    final Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            offerCover.setBackgroundResource(R.drawable.spin_animation);
                                            // Get the background, which has been compiled to an AnimationDrawable object.
                                            AnimationDrawable frameAnimation = (AnimationDrawable) offerCover.getBackground();

                                            // Start the animation (looped playback by default).
                                            frameAnimation.start();
                                        }
                                    }, 200);



                                    tr[0].setId( 1200+i) ;
                                    offerCover.setId( 2200+i) ;


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
                                            String pdfUrl = offersList.get(tableRowId-1200).getPDF_URL();
                                            int idoffer = offersList.get(tableRowId-1200).getId();
                                           // Toast.makeText(getBaseContext(),pdfUrl,Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getBaseContext(),"Please,wait.....",Toast.LENGTH_SHORT).show();
                                            Log.i("getAllImages", "setOnClickListener: " +pdfUrl);
                                            pdfViewer.putExtra(Key,pdfUrl);
                                            pdfViewer.putExtra(Key2,idoffer);
                                            pdfViewer.setClass( getBaseContext(), PdfViewerActivity.class );
                                            startActivity( pdfViewer);

                                        }
                                    });

                                    offerCover.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Intent pdfViewer = new Intent( );
                                            int Cover = v.getId();
                                            String pdfUrl = offersList.get(Cover-2200).getPDF_URL();
                                            int idoffer = offersList.get(Cover-2200).getId();
                                         //   Toast.makeText(getBaseContext(),pdfUrl,Toast.LENGTH_SHORT).show();
                                            Toast.makeText(getBaseContext(),"Please,wait.....",Toast.LENGTH_SHORT).show();
                                            Log.i("getAllImages", "setOnClickListener: " +pdfUrl);
                                            pdfViewer.putExtra(Key,pdfUrl);
                                            pdfViewer.putExtra(Key2,idoffer);
                                            pdfViewer.setClass( getBaseContext(), PdfViewerActivity.class );
                                            startActivity( pdfViewer);

                                        }
                                    });

                                    //=====================================

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
                    getImage(i, offer.getCoverURL(),true);
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

    private static void getImage(final int id, String urlToImage,boolean ckeck){

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
                if(offersList.size()!=0 && offersList.size() > id){
                    Offer offer = offersList.get( id );
                    Log.i("getImage onPostExecute", "id : " +id);
                    offer.setCover(bitmap);
                    OffersActivity.setImageBitmap(id);}
            }
        }
        GetImage gi = new GetImage();
        if(ckeck)
            gi.execute(urlToImage);
        else
        if (gi.isCancelled())
            gi.cancel(true);
    }

    private static void setImageBitmap( int index ){

        // RelativeLayout  Params  apply on child (imageButton ) when on click
        final RelativeLayout.LayoutParams rlp4 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        rlp4.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rlp4.addRule(RelativeLayout.CENTER_VERTICAL);


        ImageButton offerCover = offersCoversList.get(index);
        offerCover.setLayoutParams(rlp4);
        offerCover.setBackgroundResource(R.drawable.customborder);
        offerCover.setImageBitmap(offersList.get(index).getCover());



    }
}
