package com.applefish.smartshop.classes;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.applefish.smartshop.activities.MainActivity;
import com.applefish.smartshop.activities.PdfViewerActivity;


public class LatestFragment extends Fragment {

    final static String Key = "com.applefish.smartshop.PdfViewer";
    final static String Key2 = "com.applefish.smartshop.IDOffer";

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate( R.layout.tab_latest, container, false );

        if ( MainActivity.latestOffersList.size() > 0 )
            rootView.findViewById(R.id.progressbar_latest).setVisibility(View.GONE);

        MainActivity.latestOffersCoversList.clear();


        Thread setupLatestTab = new Thread(){

            @Override
            public void run() {
                setupLatest();
            }
        };


        try {
            setupLatestTab.start();
            setupLatestTab.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MainActivity.getOffersImages("latest");

        return rootView;
    }

    private void setupLatest() {

        final TableLayout mTlayout = (TableLayout)rootView.findViewById(R.id.latest_table);
        final TableRow[] tr = {new TableRow(getContext())};

//        final Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            public void run() {

        Thread setupTab2 = new Thread() {

            @Override
            public void run() {

                super.run();


                for ( int i=0; i < MainActivity.latestOffersList.size(); i++ ){


                    TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.MATCH_PARENT );
                    params.rightMargin = 25;
                    params.leftMargin = 25;
                    params.topMargin = 5;
                    params.bottomMargin = 5;
                    params.gravity=Gravity.CENTER;
                    tr[0] = new TableRow(getContext());
                    tr[0].setGravity(Gravity.CENTER);
                    tr[0].setLayoutParams(params);
                    //   tr[0].setBackgroundColor(Color.BLACK);
                    mTlayout.addView(tr[0]);

                    //create component
                    RelativeLayout relativeLayout = new RelativeLayout(getContext());
                    relativeLayout.setGravity(Gravity.CENTER);
                    LinearLayout linearLayout = new LinearLayout(getContext());
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setGravity(Gravity.CENTER);

                    TextView title = new TextView(getContext());
                    title.setText( MainActivity.latestOffersList.get(i).getTitle() );
                    // title.setBackgroundResource(R.drawable.customborder4);
                    title.setTextSize(16);
                    title.setTextColor(Color.rgb(24, 155, 226));
                    title.setTypeface(null, Typeface.BOLD);
                    title.setGravity(Gravity.CENTER);
                    TextView date = new TextView(getContext());
                    date.setText( "Added Date "+MainActivity.latestOffersList.get(i).getDate() );
                    //    date.setBackgroundResource(R.drawable.customborder3);
                    date.setTextSize(15);
                    date.setTextColor(Color.RED);
                    date.setGravity(Gravity.CENTER);
                    date.setTypeface(null, Typeface.BOLD);

                    TextView numOfPages = new TextView(getContext());
                    numOfPages.setText( MainActivity.latestOffersList.get(i).getNumberOfPages()+"Pages" );
                    numOfPages.setBackgroundResource(R.drawable.customborder3);
                    numOfPages.setTextSize(14);
                    numOfPages.setTextColor(Color.WHITE);
                    numOfPages.setGravity(Gravity.CENTER);
                    numOfPages.setTypeface(null, Typeface.BOLD);


                    final ImageButton offerCover = new ImageButton(getContext());
                    offerCover.setAdjustViewBounds(true);
                    offerCover.setPadding(20,20,20,20);
                    // TableRow  Params  apply on child (RelativeLayout)
                    TableRow.LayoutParams rlp = new TableRow.LayoutParams(0,
                          450
                            ,40 );

                    TableRow.LayoutParams rlp2 = new TableRow.LayoutParams(0,
                            450
                            ,60);
                    rlp2.gravity=Gravity.CENTER;


                    // LinearLayout  Params  appl
                    // y on child (textView Number of pages)
                    final LinearLayout.LayoutParams rlp3 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            0,
                            25
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
                            25
                    );
                    rlp4.gravity= Gravity.CENTER;
                    rlp4.leftMargin=2;
                    //rlp4.topMargin=10;
                    // LinearLayout  Params  apply on child (textView Title)
                    final LinearLayout.LayoutParams rlp5 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            0,
                            50
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
                    }, 100);



                    tr[0].setId( 1200+i) ;
                    offerCover.setId( 2200+i) ;

//                    LinearLayout l1=new LinearLayout(getContext());
//                    l1.setOrientation(LinearLayout.HORIZONTAL);
//
//                    View v1=new View(getContext());
//
//                    l1.addView(numOfPages);
//                    l1.addView(v1);

//                    LinearLayout l2=new LinearLayout(getContext());
//                    l2.setOrientation(LinearLayout.HORIZONTAL);
//                    l2.addView(title);

//                    LinearLayout l3=new LinearLayout(getContext());
//                    l3.setOrientation(LinearLayout.HORIZONTAL);
//                    View v3=new View(getContext());
//                    l3.addView(date);
//                    l3.addView(v3);

                    //add  View
                    relativeLayout.addView(offerCover);
                    linearLayout.addView(numOfPages);
                    linearLayout.addView(title);
                    linearLayout.addView(date);


                    tr[0].addView(relativeLayout);
                    tr[0].addView(linearLayout);

                    MainActivity.latestOffersCoversList.add(offerCover);

                    tr[0].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent pdfViewer = new Intent( );
                            int tableRowId = v.getId();
                            String pdfUrl = MainActivity.latestOffersList.get(tableRowId-1200).getPDF_URL();
                            int idoffer = MainActivity.latestOffersList.get(tableRowId-1200).getId();
                            //Toast.makeText(getContext(),pdfUrl,Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(),"Please,wait.....",Toast.LENGTH_SHORT).show();
                            Log.i("getAllImages", "setOnClickListener: " +pdfUrl);
                            pdfViewer.putExtra(Key,pdfUrl);
                            pdfViewer.putExtra(Key2,idoffer);
                            pdfViewer.setClass( getContext(), PdfViewerActivity.class );
                            startActivity( pdfViewer);

                        }
                    });

                    offerCover.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent pdfViewer = new Intent( );
                            int Cover = v.getId();
                            String pdfUrl = MainActivity.latestOffersList.get(Cover-2200).getPDF_URL();
                            int idoffer = MainActivity.latestOffersList.get(Cover-2200).getId();
                          //  Toast.makeText(getContext(),pdfUrl,Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(),"Please,wait.....",Toast.LENGTH_SHORT).show();
                            Log.i("getAllImages", "setOnClickListener: " +pdfUrl);
                            pdfViewer.putExtra(Key,pdfUrl);
                            pdfViewer.putExtra(Key2,idoffer);
                            pdfViewer.setClass( getContext(), PdfViewerActivity.class );
                            startActivity( pdfViewer);

                        }
                    });

//if(i==MainActivity.latestOffersList.size()-1)
//{rootView.findViewById(R.id.progressbar_latest).setVisibility(View.GONE);
//    rootView.findViewById(R.id.latest_scroll).setVisibility(View.VISIBLE);
//}
                }

            }
        };


        try {
            setupTab2.start();
            setupTab2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

            }
//        }, 100);
//    }

}
