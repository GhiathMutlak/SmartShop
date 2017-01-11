package com.applefish.smartshop.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {
    private static final String TAG_NAME = "storeName";
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 0;
    private static final int MY_PERMISSIONS_READ_PHONE_STATE = 0;


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
    //private static final String FAV_OFFERS_URL = "http://192.168.1.2/smartshop/favorite.php";
    private static final String FAV_OFFERS_URL ="http://samrtshop-uae.org/smartshop/favorite.php";
    private static ArrayList<Offer> offersList;
    private static ArrayList<ImageButton> offersCoversList;
    private static JSONArray offersArray = null;

    final static String Key = "com.applefish.smartshop.PdfViewer";
    final static String Key2 = "com.applefish.smartshop.IDOffer";

    private ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

          //delete all offer id in  SharedPreference
                writeSharedPreference("");
                setContentView(R.layout.activity_favorite);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                scrollView=(ScrollView)findViewById(R.id.Scroll_fav_offer) ;
                scrollView.setBackgroundResource(R.drawable.cry_star);

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Snackbar.make(scrollView, "Delete All Favorite", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }, 100);

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scrollView=(ScrollView)findViewById(R.id.Scroll_fav_offer) ;
        offersList = new ArrayList<>();
        String favoriteOffers=readSharedPreference();
        String [] offersId=favoriteOffers.split(",");
        if( offersId.length ==1 &&  offersId[0].equals(""))
        {
           scrollView.setBackgroundResource(R.drawable.cry_star);
            Log.i("scrollView", "scrollView" );
        }
        else
        {
            Thread getOfferData = new Thread(){
                @Override
                public void run() {
                    super.run();
                    getJSON( FAV_OFFERS_URL );
                }
            };

            getOfferData.start();
        }


        int permissionCheckWriteExternalStorage = ContextCompat.checkSelfPermission(
                FavoriteActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheckReadPhoneState = ContextCompat.checkSelfPermission(FavoriteActivity.this,
                Manifest.permission.READ_PHONE_STATE);

        if ( permissionCheckWriteExternalStorage != PackageManager.PERMISSION_GRANTED ||
                permissionCheckReadPhoneState != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(FavoriteActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

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
        gj.execute(url+"/?Fav_offers="+readSharedPreference());
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


                        Thread setupTable = new Thread() {

                            @Override
                            public void run() {

                                super.run();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final TableLayout mTlayout = (TableLayout)findViewById(R.id.table_fav_offer);
                                        final TableRow[] tr = {new TableRow(getBaseContext())};
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
                                          //  RelativeLayout relativeLayout = new RelativeLayout(getBaseContext());
                                            LinearLayout linearLayout = new LinearLayout(getBaseContext());
                                            linearLayout.setOrientation(LinearLayout.VERTICAL);

//                                    relativeLayout.setBackgroundColor(Color.CYAN);
//                                    linearLayout.setBackgroundColor(Color.GREEN);


                                            TextView title = new TextView(getBaseContext());
                                            title.setText( offersList.get(i).getTitle() );
                                            //   title.setBackgroundResource(R.drawable.customborder3);
                                            title.setTextSize(15);
                                            title.setTextColor(Color.GRAY);

                                            TextView date = new TextView(getBaseContext());
                                            date.setText( "Added Date "+offersList.get(i).getDate() );
                                            date.setBackgroundResource(R.drawable.customborder4);
                                            date.setTextSize(15);
                                            date.setTextColor(Color.WHITE);
                                            date.setTypeface(null, Typeface.BOLD);

                                            TextView numOfPages = new TextView(getBaseContext());
                                            numOfPages.setText( "    Pages="+offersList.get(i).getNumberOfPages() );
                                            numOfPages.setBackgroundResource(R.drawable.customborder3);
                                            numOfPages.setTextSize(14);
                                            numOfPages.setTextColor(Color.WHITE);
                                            numOfPages.setTypeface(null, Typeface.BOLD);


                                          //  final ImageButton offerCover = new ImageButton(getBaseContext());

//                                            // TableRow  Params  apply on child (RelativeLayout)
//                                            TableRow.LayoutParams rlp = new TableRow.LayoutParams(200,
//                                                    260,40 );

                                            TableRow.LayoutParams rlp2 = new TableRow.LayoutParams(0,
                                                    TableLayout.LayoutParams.MATCH_PARENT ,1);
                                            // LinearLayout  Params  apply on child (textView Number of pages)
                                            final LinearLayout.LayoutParams rlp3 = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.MATCH_PARENT
                                            );
                                         rlp3.weight=1;
                                            rlp3.gravity=Gravity.CENTER_HORIZONTAL;
                                            // rlp3.rightMargin=10;
                                          //  rlp3.leftMargin=100;
                                            // rlp3.bottomMargin=27;
                                            //rlp3.topMargin=3;
                                            // LinearLayout  Params  apply on child (textView Date)
                                            final LinearLayout.LayoutParams rlp4= new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                    100
                                            );
                                            rlp4.weight=1;
                                            rlp4.gravity=Gravity.CENTER;
                                          //  rlp4.leftMargin=30;
                                          //  rlp4.topMargin=10;
                                            // LinearLayout  Params  apply on child (textView Title)
                                            final LinearLayout.LayoutParams rlp5 = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                    75
                                            );
                                               rlp5.weight=1;
                                            rlp5.gravity=Gravity.CENTER;


                                            //set layout params
                                           // relativeLayout.setLayoutParams(rlp);
                                         linearLayout.setLayoutParams(rlp2);
                                           // offerCover.setLayoutParams(rlp6);
                                            numOfPages.setLayoutParams(rlp3);
                                           date.setLayoutParams(rlp3);
                                           title.setLayoutParams(rlp3);

                                            tr[0].setBackgroundResource(R.drawable.mybutton_background);
                                            tr[0].setAddStatesFromChildren(true); // <<<<  this line is the best in the world


                                            tr[0].setId( 1100+i) ;

                                            LinearLayout l1=new LinearLayout(getBaseContext());
                                            l1.setGravity(Gravity.CENTER_HORIZONTAL);
                                            l1.setOrientation(LinearLayout.HORIZONTAL);
                                            l1.setLayoutParams(rlp5);
                                            //View v1=new View(getBaseContext());

                                            l1.addView(title);
                                            //l1.addView(v1);

                                            LinearLayout l4=new LinearLayout(getBaseContext());
                                            l4.setOrientation(LinearLayout.HORIZONTAL);
                                           // View v4=new View(getBaseContext());
                                            l4.setLayoutParams(rlp5);
                                            l4.addView(date);
                                            l4.addView(numOfPages);
                                            linearLayout.addView(l1);
                                            linearLayout.addView(l4);



                                           // tr[0].addView(relativeLayout);
                                            tr[0].addView(linearLayout);

                                            //  offersCoversList.add(offerCover);

                                            tr[0].setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    Intent pdfViewer = new Intent( );
                                                    int tableRowId = ((TableRow)v).getId();
                                                    String pdfUrl = offersList.get(tableRowId-1100).getPDF_URL();
                                                    int idoffer=offersList.get(tableRowId-1100).getId();
                                                    Toast.makeText(getBaseContext(),pdfUrl,Toast.LENGTH_SHORT).show();
                                                    Log.i("getAllImages", "setOnClickListener: " +pdfUrl);
                                                    pdfViewer.putExtra(Key,pdfUrl);
                                                    pdfViewer.putExtra(Key2,idoffer);
                                                    pdfViewer.setClass( getBaseContext(), PdfViewerActivity.class );
                                                    startActivity( pdfViewer);

                                                }
                                            });


                                        }
                                    }
                                });


                            }
                        };


                        try {
                            setupTable.start();
                            setupTable.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
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


    public String readSharedPreference() {
        SharedPreferences sharedPref = getBaseContext().getSharedPreferences("com.applefish.smartshop.FAVORITE_KEY",MODE_PRIVATE);
        //0 is default_value if no vaule
        String savedFavoriteOffer = sharedPref.getString(getString(R.string.saved_favorite), "");

        return savedFavoriteOffer;
    }

    public  void  writeSharedPreference(String savedFavoriteOffer) {

        SharedPreferences sharedPref = getBaseContext().getSharedPreferences("com.applefish.smartshop.FAVORITE_KEY",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_favorite), savedFavoriteOffer);
        editor.commit();

    }

}
