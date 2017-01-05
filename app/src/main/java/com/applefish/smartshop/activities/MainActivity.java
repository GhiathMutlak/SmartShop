package com.applefish.smartshop.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
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

    String storesResult;

    final static String Key="com.applefish.smartshop.PdfViewer";
    final static String Key2 = "com.applefish.smartshop.IDOffer";

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "storeName";
    private static final String TAG_ADD ="logoUrl";

    private static final String STORES_URL = "http://gherasbirr.org/smartshop/retrivelogo.php";
    private static final String LATEST_URL = "http://gherasbirr.org/smartshop/date.php";
    private static final String MOST_VIEWED_URL = "http://gherasbirr.org/smartshop/view.php";

    private static ArrayList<Store> storesList;
    private static ArrayList<Offer> latestOffersList;
    private static ArrayList<Offer> mostViewedList;

    private static ArrayList<ImageButton> storesLogosList;
    private static ArrayList<ImageButton> latestOffersCoversList;
    private static ArrayList<ImageButton> mostViewedCoversList;

    private static ArrayList<Bitmap> storesBitmapsList;
    private static ArrayList<Bitmap> latestBitmapsList;
    private static ArrayList<Bitmap> mostViewedBitmapsList;

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
                super.run();
                getJSON(  );
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


                   if( getArguments().getInt(ARG_SECTION_NUMBER) == 1 ) {

                       if ( latestOffersList.size() > 0 )
                           rootView.findViewById(R.id.progressbarstore).setVisibility(View.GONE);

                       latestOffersCoversList.clear();

                       if ( getData.isAlive() )
                           try {
                               getData.join();
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           }

                       Thread setupLatestTab = new Thread(){

                           @Override
                           public void run() {
                               new setupTab().setupLatest();
                           }
                       };


                       try {
                           setupLatestTab.start();
                           setupLatestTab.join();
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }

                       MainActivity.getOffersImages("latest");

                   }
                    else if( getArguments().getInt(ARG_SECTION_NUMBER) == 2 ) {

                    if ( mostViewedList.size() > 0 )
                        rootView.findViewById(R.id.progressbarstore).setVisibility(View.GONE);

                    mostViewedCoversList.clear();

                    if ( getData.isAlive() )
                        try {
                            getData.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    Thread setupMostViewedTab = new Thread(){

                        @Override
                        public void run() {
                            new setupTab().setupMostViewed();
                        }
                    };


                    try {
                        setupMostViewedTab.start();
                        setupMostViewedTab.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    MainActivity.getOffersImages("mostViewed");

                    }
                    else if ( getArguments().getInt(ARG_SECTION_NUMBER) == 3 ) {

                       if( storesList.size() > 0 )
                           rootView.findViewById(R.id.progressbarstore).setVisibility(View.GONE);

                       storesLogosList.clear();

                       if ( getData.isAlive() )
                           try {
                               getData.join();
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           }

                       Thread setupStoresTab = new Thread(){

                           @Override
                           public void run() {
                               new setupTab().setupStores();
                           }
                       };

                       try {
                           setupStoresTab.start();
                           setupStoresTab.join();
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }

                       MainActivity.getStoresImages();

                   }

            return rootView;

        }


        private class setupTab {

           public void setupLatest(){

               final TableLayout mTlayout = (TableLayout)rootView.findViewById(R.id.latest_table);
               final TableRow[] tr = {new TableRow(getContext())};

               Thread setupTab = new Thread() {

                   @Override
                   public void run() {

                       super.run();


                       for ( int i=0; i < latestOffersList.size(); i++ ){


                           TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                   TableLayout.LayoutParams.MATCH_PARENT );
                           params.rightMargin = 5;
                           params.leftMargin = 5;
                           params.topMargin = 5;
                           params.bottomMargin = 5;
                           tr[0] = new TableRow(getContext());
                           tr[0].setLayoutParams(params);
                           tr[0].setBackgroundColor(Color.BLACK);
                           mTlayout.addView(tr[0]);

                           //create component
                           RelativeLayout relativeLayout = new RelativeLayout(getContext());
                           LinearLayout linearLayout = new LinearLayout(getContext());
                           linearLayout.setOrientation(LinearLayout.VERTICAL);


                           TextView title = new TextView(getContext());
                           title.setText( latestOffersList.get(i).getTitle() );
                           //   title.setBackgroundResource(R.drawable.customborder3);
                           title.setTextSize(15);
                           title.setTextColor(Color.GRAY);

                           TextView date = new TextView(getContext());
                           date.setText( latestOffersList.get(i).getDate() );
                           //    date.setBackgroundResource(R.drawable.customborder3);
                           date.setTextSize(15);
                           date.setTextColor(Color.GRAY);
                           date.setTypeface(null, Typeface.BOLD);

                           TextView numOfPages = new TextView(getContext());
                           numOfPages.setText( " Pages="+latestOffersList.get(i).getNumberOfPages() );
                           numOfPages.setBackgroundResource(R.drawable.customborder3);
                           numOfPages.setTextSize(14);
                           numOfPages.setTextColor(Color.WHITE);
                           numOfPages.setTypeface(null, Typeface.BOLD);


                           final ImageButton offerCover = new ImageButton(getContext());

                           // TableRow  Params  apply on child (RelativeLayout)
                           TableRow.LayoutParams rlp = new TableRow.LayoutParams(200,
                                   260,40 );

                           TableRow.LayoutParams rlp2 = new TableRow.LayoutParams(400,
                                   260 ,60);
                           // LinearLayout  Params  apply on child (textView Number of pages)
                           final LinearLayout.LayoutParams rlp3 = new LinearLayout.LayoutParams(
                                   LinearLayout.LayoutParams.WRAP_CONTENT,
                                   LinearLayout.LayoutParams.WRAP_CONTENT
                           );
                           //rlp3.weight=1;
                           // rlp3.rightMargin=10;
                           rlp3.leftMargin=100;
                           // rlp3.bottomMargin=27;
                           rlp3.topMargin=3;
                           // LinearLayout  Params  apply on child (textView Date)
                           final LinearLayout.LayoutParams rlp4= new LinearLayout.LayoutParams(
                                   LinearLayout.LayoutParams.MATCH_PARENT,
                                   LinearLayout.LayoutParams.WRAP_CONTENT
                           );
                           // rlp4.weight=1;
                           rlp4.leftMargin=30;
                           rlp4.topMargin=10;
                           // LinearLayout  Params  apply on child (textView Title)
                           final LinearLayout.LayoutParams rlp5 = new LinearLayout.LayoutParams(
                                   LinearLayout.LayoutParams.MATCH_PARENT,
                                   LinearLayout.LayoutParams.WRAP_CONTENT
                           );
                           //    rlp5.weight=1;
                           rlp5.leftMargin=20;
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

                           offerCover.setScaleType(ImageView.ScaleType.FIT_CENTER);
                           offerCover.setBackgroundResource(R.drawable.spin_animation);


                           // Get the background, which has been compiled to an AnimationDrawable object.
                           AnimationDrawable frameAnimation = (AnimationDrawable) offerCover.getBackground();

                           // Start the animation (looped playback by default).
                           frameAnimation.start();


                           tr[0].setId( 1100+i) ;

                           LinearLayout l1=new LinearLayout(getContext());
                           l1.setOrientation(LinearLayout.HORIZONTAL);

                           View v1=new View(getContext());

                           l1.addView(numOfPages);
                           l1.addView(v1);

                           LinearLayout l2=new LinearLayout(getContext());
                           l2.setOrientation(LinearLayout.HORIZONTAL);
                           l2.addView(title);

                           LinearLayout l3=new LinearLayout(getContext());
                           l3.setOrientation(LinearLayout.HORIZONTAL);
                           View v3=new View(getContext());
                           l3.addView(date);
                           l3.addView(v3);

                           //add  View
                           relativeLayout.addView(offerCover);
                           linearLayout.addView(l1,270,75);
                           linearLayout.addView(l2,  LinearLayout.LayoutParams.WRAP_CONTENT,  100);
                           linearLayout.addView(l3,230,75);


                           tr[0].addView(relativeLayout);
                           tr[0].addView(linearLayout);

                           latestOffersCoversList.add(offerCover);

                           tr[0].setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {

                                   Intent pdfViewer = new Intent( );
                                   int tableRowId = v.getId();
                                   String pdfUrl = latestOffersList.get(tableRowId-1100).getPDF_URL();
                                   int idoffer=latestOffersList.get(tableRowId-1100).getId();
                                   Toast.makeText(getContext(),pdfUrl,Toast.LENGTH_SHORT).show();
                                   Log.i("getAllImages", "setOnClickListener: " +pdfUrl);
                                   pdfViewer.putExtra(Key,pdfUrl);
                                   pdfViewer.putExtra(Key2,idoffer);
                                   pdfViewer.setClass( getContext(), PdfViewerActivity.class );
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


           }

           public void setupMostViewed () {

               final TableLayout mTlayout = (TableLayout)rootView.findViewById(R.id.most_viewed_table);
               final TableRow[] tr = {new TableRow(getContext())};

               Thread setupTab = new Thread() {

                   @Override
                   public void run() {

                       super.run();


                       for ( int i=0; i < latestOffersList.size(); i++ ){


                           TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                   TableLayout.LayoutParams.MATCH_PARENT );
                           params.rightMargin = 5;
                           params.leftMargin = 5;
                           params.topMargin = 5;
                           params.bottomMargin = 5;
                           tr[0] = new TableRow(getContext());
                           tr[0].setLayoutParams(params);
                           tr[0].setBackgroundColor(Color.BLACK);
                           mTlayout.addView(tr[0]);

                           //create component
                           RelativeLayout relativeLayout = new RelativeLayout(getContext());
                           LinearLayout linearLayout = new LinearLayout(getContext());
                           linearLayout.setOrientation(LinearLayout.VERTICAL);


                           TextView title = new TextView(getContext());
                           title.setText( mostViewedList.get(i).getTitle() );
                           //   title.setBackgroundResource(R.drawable.customborder3);
                           title.setTextSize(15);
                           title.setTextColor(Color.GRAY);

                           TextView date = new TextView(getContext());
                           date.setText( mostViewedList.get(i).getDate() );
                           //    date.setBackgroundResource(R.drawable.customborder3);
                           date.setTextSize(15);
                           date.setTextColor(Color.GRAY);
                           date.setTypeface(null, Typeface.BOLD);

                           TextView numOfPages = new TextView(getContext());
                           numOfPages.setText( " Pages="+mostViewedList.get(i).getNumberOfPages() );
                           numOfPages.setBackgroundResource(R.drawable.customborder3);
                           numOfPages.setTextSize(14);
                           numOfPages.setTextColor(Color.WHITE);
                           numOfPages.setTypeface(null, Typeface.BOLD);


                           final ImageButton offerCover = new ImageButton(getContext());

                           // TableRow  Params  apply on child (RelativeLayout)
                           TableRow.LayoutParams rlp = new TableRow.LayoutParams(200,
                                   260,40 );

                           TableRow.LayoutParams rlp2 = new TableRow.LayoutParams(400,
                                   260 ,60);
                           // LinearLayout  Params  apply on child (textView Number of pages)
                           final LinearLayout.LayoutParams rlp3 = new LinearLayout.LayoutParams(
                                   LinearLayout.LayoutParams.WRAP_CONTENT,
                                   LinearLayout.LayoutParams.WRAP_CONTENT
                           );
                           //rlp3.weight=1;
                           // rlp3.rightMargin=10;
                           rlp3.leftMargin=100;
                           // rlp3.bottomMargin=27;
                           rlp3.topMargin=3;
                           // LinearLayout  Params  apply on child (textView Date)
                           final LinearLayout.LayoutParams rlp4= new LinearLayout.LayoutParams(
                                   LinearLayout.LayoutParams.MATCH_PARENT,
                                   LinearLayout.LayoutParams.WRAP_CONTENT
                           );
                           // rlp4.weight=1;
                           rlp4.leftMargin=30;
                           rlp4.topMargin=10;
                           // LinearLayout  Params  apply on child (textView Title)
                           final LinearLayout.LayoutParams rlp5 = new LinearLayout.LayoutParams(
                                   LinearLayout.LayoutParams.MATCH_PARENT,
                                   LinearLayout.LayoutParams.WRAP_CONTENT
                           );
                           //    rlp5.weight=1;
                           rlp5.leftMargin=20;
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

                           offerCover.setScaleType(ImageView.ScaleType.FIT_CENTER);
                           offerCover.setBackgroundResource(R.drawable.spin_animation);


                           // Get the background, which has been compiled to an AnimationDrawable object.
                           AnimationDrawable frameAnimation = (AnimationDrawable) offerCover.getBackground();

                           // Start the animation (looped playback by default).
                           frameAnimation.start();


                           tr[0].setId( 1100+i) ;

                           LinearLayout l1=new LinearLayout(getContext());
                           l1.setOrientation(LinearLayout.HORIZONTAL);

                           View v1=new View(getContext());

                           l1.addView(numOfPages);
                           l1.addView(v1);

                           LinearLayout l2=new LinearLayout(getContext());
                           l2.setOrientation(LinearLayout.HORIZONTAL);
                           l2.addView(title);

                           LinearLayout l3=new LinearLayout(getContext());
                           l3.setOrientation(LinearLayout.HORIZONTAL);
                           View v3=new View(getContext());
                           l3.addView(date);
                           l3.addView(v3);

                           //add  View
                           relativeLayout.addView(offerCover);
                           linearLayout.addView(l1,270,75);
                           linearLayout.addView(l2,  LinearLayout.LayoutParams.WRAP_CONTENT,  100);
                           linearLayout.addView(l3,230,75);


                           tr[0].addView(relativeLayout);
                           tr[0].addView(linearLayout);

                           mostViewedCoversList.add(offerCover);

                           tr[0].setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {

                                   Intent pdfViewer = new Intent( );
                                   int tableRowId = v.getId();
                                   String pdfUrl = mostViewedList.get(tableRowId-1100).getPDF_URL();
                                   int idoffer=mostViewedList.get(tableRowId-1100).getId();
                                   Toast.makeText(getContext(),pdfUrl,Toast.LENGTH_SHORT).show();
                                   Log.i("getAllImages", "setOnClickListener: " +pdfUrl);
                                   pdfViewer.putExtra(Key,pdfUrl);
                                   pdfViewer.putExtra(Key2,idoffer);
                                   pdfViewer.setClass( getContext(), PdfViewerActivity.class );
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


           }

           public void setupStores () {

               final TableLayout mTlayout = (TableLayout)rootView.findViewById(R.id.tablestore);
               final TableRow[] tr = {new TableRow(getContext())};

               //do your stuff
               for ( int i=0; i < storesList.size(); i++ ){

                   if (i % 2 == 0) {
                       tr[0] = new TableRow(getContext());
                       mTlayout.addView(tr[0]);
                   }

                   //create component
                   final RelativeLayout linearLayout1 = new RelativeLayout(getContext());

                   TextView storeName = new TextView(getContext());
                   storeName.setText( storesList.get(i).getStoreName() );

                   final ImageButton storeLogo = new ImageButton(getContext());

                   // TableRow  Params  apply on child (RelativeLayout)
                   TableRow.LayoutParams rlp = new TableRow.LayoutParams( 250, 250,1f );
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
                   storeLogo.setId( 1000+i);
                   linearLayout1.setId( 2000+i) ;

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
                           bundle.putParcelable(TAG_NAME , storesList.get( storeLogo.getId()-1000 ));
                           bundle.putString("ACTIVITY_NAME","MAIN");
                           offers.putExtras( bundle );
                           startActivity( offers );

                       }
                   });
                   linearLayout1.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {

                           Intent offers = new Intent( );
                           Bundle bundle = new Bundle();

                           offers.setClass( getContext(), OffersActivity.class );
                           bundle.putParcelable(TAG_NAME , storesList.get( linearLayout1.getId()-2000 ));
                           bundle.putString("ACTIVITY_NAME","MAIN");
                           offers.putExtras( bundle );
                           startActivity( offers );

                       }
                   });




               }
           }

           }

    }

    private void getJSON (  ) {


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
                buildOffersList(result,"mostViewed");
            }
        }

        GetStores getStores = new GetStores();
        getStores.execute( STORES_URL );

        GetLatest getLatest = new GetLatest();
        getLatest.execute( LATEST_URL );

        GetMostViewed getMostViewed = new GetMostViewed( );
        getMostViewed.execute( MOST_VIEWED_URL );

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

                Store store = storesList.get( index );
                store.setLogo(bitmap);
                MainActivity.setStoreBitmap(index);
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

                Offer offer = latestOffersList.get( index );
                offer.setCover(bitmap);
                MainActivity.setOffersBitmap(index,"latest");
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

                Offer offer = mostViewedList.get( index );
                offer.setCover(bitmap);
                MainActivity.setOffersBitmap(index,"mostViewed");
                Log.i("post Execute", "Call # : "+index );

            }
        }

        GetImage gi = new GetImage();
        gi.execute(urlToImage);

    }

    private static void setStoreBitmap( int index ){

        // RelativeLayout  Params  apply on child (imageButton ) when on click
        final RelativeLayout.LayoutParams rlp4 = new RelativeLayout.LayoutParams(
                200,
                200
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
                200,
                200
        );
        rlp4.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rlp4.addRule(RelativeLayout.CENTER_VERTICAL);

            if ( list.equals("latest")) {
                ImageButton cover = latestOffersCoversList.get(index);
                cover.setBackgroundResource(0);
                cover.setLayoutParams(rlp4);
                cover.setImageBitmap(latestOffersList.get(index).getCover());
            } else
                if ( list.equals("mostViewed") ) {
                    ImageButton cover = mostViewedCoversList.get(index);
                    cover.setBackgroundResource(0);
                    cover.setLayoutParams(rlp4);
                    cover.setImageBitmap(mostViewedList.get(index).getCover());
                }


    }


}