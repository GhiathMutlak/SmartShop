package com.applefish.smartshop.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.applefish.smartshop.R;
import com.applefish.smartshop.classes.Offer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
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
    //private static final String SEARCH_OFFERS_URL = "http://192.168.1.2/smartshop/search.php";
    private static final String SEARCH_OFFERS_URL ="http://smartshop-uae.org/smartshop/search.php";
    private static ArrayList<Offer> offersList;
    private static ArrayList<ImageButton> offersCoversList;
    private static JSONArray offersArray = null;

    final static String Key = "com.applefish.smartshop.PdfViewer";
    final static String Key2 = "com.applefish.smartshop.IDOffer";

    private ScrollView scrollView;
    private TextView textView;
    private Toolbar toolbar;
    TextView test;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);

        test = (TextView) findViewById(R.id.test);
        // Scroll_fav_offer
        scrollView=(ScrollView)findViewById(R.id.search_scroll) ;
        offersList = new ArrayList<>();
        textView=(TextView) findViewById(R.id.textView8);

        handleIntent( getIntent() );

        int permissionCheckWriteExternalStorage = ContextCompat.checkSelfPermission(
                SearchActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheckReadPhoneState = ContextCompat.checkSelfPermission(SearchActivity.this,
                Manifest.permission.READ_PHONE_STATE);

        if ( permissionCheckWriteExternalStorage != PackageManager.PERMISSION_GRANTED ||
                permissionCheckReadPhoneState != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(SearchActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {

        if ( Intent.ACTION_SEARCH.equals( intent.getAction() ) ) {
             query = intent.getStringExtra(SearchManager.QUERY);
            test.setText(query);
            //use the query to search your data somehow
//            Thread getOfferData = new Thread() {
//                @Override
//                public void run() {
//                    super.run();
                    getJSON(SEARCH_OFFERS_URL,true);
//                }
//            };
//
//            getOfferData.start();

        }
    }
    private void getJSON(String url,boolean check) {


        class GetJSON extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog=new ProgressDialog(SearchActivity.this);

            @Override
            protected void onPreExecute()
            {
                this.dialog.setMessage("Search.....");
                this.dialog.show();
            }
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
                    Log.i("getJSONOffers", "Exception: " +e);
                    return null;

                }

            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                jsonResult = result;
                buidlOffersList();
                if(dialog.isShowing())
                    dialog.dismiss();

            }
            @Override
            protected void onCancelled() {
                if(dialog.isShowing())
                    dialog.dismiss();

            }
        }
        GetJSON gj = new GetJSON();
        String []spilt=query.split(" ");
        String new_query="";
        for(int i=0;i<spilt.length;i++)
        {
         if(i != spilt.length-1)
             new_query=spilt[i]+","+new_query;
            else
             new_query=new_query+spilt[i];
        }

        String full_url=url+"/?search="+new_query;
        if(check)
            gj.execute(full_url);
        else
        {
            if(!gj.isCancelled())
                gj.cancel(true);
        }

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
                                    final TableLayout mTlayout = (TableLayout)findViewById(R.id.search_table);
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
                                      //  tr[0].setGravity(Gravity.CENTER);

                                        mTlayout.addView(tr[0]);

                                        //create component
                                        //  RelativeLayout relativeLayout = new RelativeLayout(getBaseContext());
                                        LinearLayout linearLayout = new LinearLayout(getBaseContext());
                                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                                       // linearLayout.setGravity(Gravity.CENTER);
                                      // linearLayout.setBackgroundColor(Color.GREEN);


                                        TextView title = new TextView(getBaseContext());
                                        title.setText( offersList.get(i).getTitle() );
                                        // title.setBackgroundResource(R.drawable.customborder3);
                                        title.setTextSize(15);
                                        title.setGravity(Gravity.CENTER);
                                        title.setTextColor(Color.rgb(24, 155, 226));
                                        title.setTypeface(null, Typeface.BOLD);

                                        TextView date = new TextView(getBaseContext());
                                        date.setText( "Added Date "+offersList.get(i).getDate() );
                                        date.setBackgroundResource(R.drawable.customborder4);
                                        date.setTextSize(15);
                                        date.setGravity(Gravity.CENTER);
                                        date.setTextColor(Color.WHITE);
                                        date.setTypeface(null, Typeface.BOLD);

                                        TextView numOfPages = new TextView(getBaseContext());
                                        numOfPages.setText( "    Pages="+offersList.get(i).getNumberOfPages() );
                                        numOfPages.setBackgroundResource(R.drawable.customborder7);
                                        numOfPages.setTextSize(14);
                                        numOfPages.setGravity(Gravity.CENTER);
                                        numOfPages.setTextColor(Color.WHITE);
                                        numOfPages.setTypeface(null, Typeface.BOLD);


                                        //  final ImageButton offerCover = new ImageButton(getBaseContext());

                                        LinearLayout.LayoutParams rlp2 = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.MATCH_PARENT ,65);
                                        // LinearLayout  Params  apply on child (textView Number of pages)
                                        final LinearLayout.LayoutParams rlp3 = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                LinearLayout.LayoutParams.MATCH_PARENT
                                                ,35
                                        );
                                        // LinearLayout  Params  apply on child (textView Date)
                                        final LinearLayout.LayoutParams rlp4= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                                                ,LinearLayout.LayoutParams.WRAP_CONTENT         );

                                        //  LinearLayout  Params  apply on  (textView Title)
                                        final LinearLayout.LayoutParams rlp5 = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT,1
                                        );


                                        final TableRow.LayoutParams rlp6 = new TableRow.LayoutParams(
                                                0,
                                                TableRow.LayoutParams.MATCH_PARENT
                                                ,1
                                        );
                                        rlp6.gravity= Gravity.CENTER;

                                        final LinearLayout.LayoutParams rlp7 = new TableRow.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                            ,1
                                        );

                                        //set layout params
                                        linearLayout.setLayoutParams(rlp6);
                                        // offerCover.setLayoutParams(rlp6);
                                        date.setLayoutParams(rlp2);
                                        numOfPages.setLayoutParams(rlp3);
                                        title.setLayoutParams(rlp5);

                                       tr[0].setBackgroundResource(R.drawable.mybutton_background);
                                        tr[0].setAddStatesFromChildren(true); // <<<<  this line is the best in the world


                                        tr[0].setId( 1100+i) ;

                                        LinearLayout l4=new LinearLayout(getBaseContext());
                                        l4.setOrientation(LinearLayout.HORIZONTAL);
                                        l4.setLayoutParams(rlp7);
                                         //l4.setBackgroundColor(Color.RED);

                                        LinearLayout l5=new LinearLayout(getBaseContext());
                                        l5.setOrientation(LinearLayout.HORIZONTAL);
                                        l5.setLayoutParams(rlp7);
                                       // l5.setBackgroundColor(Color.YELLOW);
                                        l4.setLayoutParams(rlp4);
                                        l4.addView(date);
                                        l4.addView(numOfPages);
                                        l5.addView(title);
                                        linearLayout.addView(l5);
                                        linearLayout.addView(l4);

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
                    //Toast.makeText(getBaseContext(), "NO offers", Toast.LENGTH_SHORT).show();

                    textView.setText("No matching results");
                }
            }
            else {
                Toast.makeText(getBaseContext(), "NO thing in DB", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onBackPressed() {
        //Stop Task
        getJSON( "" ,false);
        offersList = new ArrayList<>();
        offersCoversList = new ArrayList<>();
        super.onBackPressed();


    }
}
