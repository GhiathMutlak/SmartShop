package com.applefish.smartshop.classes;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
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


public class MostViewedFragment extends Fragment implements Positioning{

    final static String Key = "com.applefish.smartshop.PdfViewer";
    final static String Key2 = "com.applefish.smartshop.IDOffer";

    private View rootView;
    private static int position = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate( R.layout.tab_most_viewed, container, false );

        if (MainActivity.mostViewedList.size() > 0 )
            rootView.findViewById(R.id.progressbar).setVisibility(View.GONE);

        MainActivity.mostViewedCoversList.clear();

        Thread setupMostViewedTab = new Thread(){

            @Override
            public void run() {
                setupMostViewed();
            }
        };


        try {
            setupMostViewedTab.start();
            setupMostViewedTab.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MainActivity.getOffersImages("mostViewed");

        return rootView;
    }

    private void setupMostViewed() {

        final TableLayout mTlayout = (TableLayout)rootView.findViewById(R.id.most_viewed_table);
        final TableRow[] tr = {new TableRow(getContext())};

        Thread setupTab = new Thread() {

            @Override
            public void run() {

                super.run();


                for ( int i=0; i < MainActivity.latestOffersList.size(); i++ ){


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
                    title.setText( MainActivity.mostViewedList.get(i).getTitle() );
                    //   title.setBackgroundResource(R.drawable.customborder3);
                    title.setTextSize(15);
                    title.setTextColor(Color.GRAY);

                    TextView date = new TextView(getContext());
                    date.setText( MainActivity.mostViewedList.get(i).getDate() );
                    //    date.setBackgroundResource(R.drawable.customborder3);
                    date.setTextSize(15);
                    date.setTextColor(Color.GRAY);
                    date.setTypeface(null, Typeface.BOLD);

                    TextView numOfPages = new TextView(getContext());
                    numOfPages.setText( " Pages=" + MainActivity.mostViewedList.get(i).getNumberOfPages() );
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

                    final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    // Get the background, which has been compiled to an AnimationDrawable object.
                                    AnimationDrawable frameAnimation = (AnimationDrawable) offerCover.getBackground();

                                    // Start the animation (looped playback by default).
                                    frameAnimation.start();
                                }
                                                   }, 200);



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

                    MainActivity.mostViewedCoversList.add(offerCover);

                    tr[0].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent pdfViewer = new Intent( );
                            int tableRowId = v.getId();
                            String pdfUrl = MainActivity.mostViewedList.get(tableRowId-1100).getPDF_URL();
                            int idoffer = MainActivity.mostViewedList.get(tableRowId-1100).getId();
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

    @Override
    public int getPosition() {
        return position;
    }
}
