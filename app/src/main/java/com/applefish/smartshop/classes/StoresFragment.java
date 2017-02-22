package com.applefish.smartshop.classes;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.applefish.smartshop.R;
import com.applefish.smartshop.activities.MainActivity;
import com.applefish.smartshop.activities.OffersActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoresFragment extends Fragment {

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab_stores, container, false);

        if (MainActivity.storesList.size() > 0)
            rootView.findViewById(R.id.progressbar).setVisibility(View.GONE);

        MainActivity.storesLogosList.clear();

        Thread setupStoresTab = new Thread() {

            @Override
            public void run() {
                setupStores();
            }
        };

        try {
            setupStoresTab.start();
            setupStoresTab.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MainActivity.getStoresImages();

        return rootView;
    }

    private void setupStores() {

        final TableLayout mTlayout = (TableLayout) rootView.findViewById(R.id.store_table);
        final TableRow[] tr = {new TableRow(getContext())};

        //do your stuff
        for (int i = 0; i < MainActivity.storesList.size(); i++) {

            if (i % 2 == 0) {
                tr[0] = new TableRow(getContext());
                mTlayout.addView(tr[0]);
            }

            //create component
            final RelativeLayout linearLayout1 = new RelativeLayout(getContext());

            TextView storeName = new TextView(getContext());
            storeName.setText(MainActivity.storesList.get(i).getStoreName());
            storeName.setTypeface(null, Typeface.BOLD);
            storeName.setGravity(Gravity.CENTER);
            storeName.setTextColor(Color.rgb(24, 155, 226));
            storeName.setTextSize(13);
            final ImageButton storeLogo = new ImageButton(getContext());

            // TableRow  Params  apply on child (RelativeLayout)
            TableRow.LayoutParams rlp = new TableRow.LayoutParams(350, 350, 1f);
            rlp.rightMargin = 7;
            rlp.leftMargin = 7;
            rlp.topMargin = 7;
            rlp.bottomMargin = 7;

            // RelativeLayout  Params  apply on child (imageButton )
            RelativeLayout.LayoutParams rlp2 = new RelativeLayout.LayoutParams(52, 52);
            rlp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rlp2.addRule(RelativeLayout.CENTER_VERTICAL);
            rlp2.rightMargin = 10;
            rlp2.leftMargin = 10;

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
            storeLogo.setAdjustViewBounds(true);
            storeLogo.setPadding(20,20,20,20);

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                public void run() {
                    storeLogo.setBackgroundResource(R.drawable.spin_animation);
                    // Get the background, which has been compiled to an AnimationDrawable object.
                    AnimationDrawable frameAnimation = (AnimationDrawable) storeLogo.getBackground();

                    // Start the animation (looped playback by default).
                  frameAnimation.start();
                }
            }, 200);



            //add id for imageButton  &  linearLayout1
            storeLogo.setId(1000 + i);
            linearLayout1.setId(2000 + i);

            //add  View
            linearLayout1.addView(storeLogo);
            linearLayout1.addView(storeName);
            tr[0].addView(linearLayout1);

            MainActivity.storesLogosList.add(storeLogo);

            if(i == MainActivity.storesList.size()-1)
            {
                TextView footer = new TextView(getContext());
                footer.setText( "Stores \n");
                // title.setBackgroundResource(R.drawable.customborder4);
                footer.setTextSize(16);
                footer.setTextColor(Color.rgb(24, 155, 226));
                footer.setTypeface(null, Typeface.BOLD);
                footer.setGravity(Gravity.CENTER);
                TableLayout.LayoutParams params2 = new TableLayout.LayoutParams(0,
                        450,1);
                params2.rightMargin = 25;
                params2.leftMargin = 25;
                params2.topMargin = 5;
                params2.bottomMargin = 5;
                params2.gravity=Gravity.CENTER;
                TableRow trstore = new TableRow(getContext());
                trstore.setGravity(Gravity.CENTER);
                trstore.setLayoutParams(params2);
                //tr[0].setBackgroundColor(Color.BLACK);
                trstore.addView(footer);
                mTlayout.addView(trstore);
            }

            storeLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent offers = new Intent();
                    Bundle bundle = new Bundle();

                    offers.setClass(getContext(), OffersActivity.class);
                    bundle.putParcelable( MainActivity.TAG_NAME, MainActivity.storesList.get(storeLogo.getId() - 1000));
                    bundle.putString("ACTIVITY_NAME", "MAIN");
                    offers.putExtras(bundle);
                    startActivity(offers);

                }
            });
            linearLayout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent offers = new Intent();
                    Bundle bundle = new Bundle();

                    offers.setClass(getContext(), OffersActivity.class);
                    bundle.putParcelable( MainActivity.TAG_NAME, MainActivity.storesList.get(linearLayout1.getId() - 2000));
                    bundle.putString("ACTIVITY_NAME", "MAIN");
                    offers.putExtras(bundle);
                    startActivity(offers);

                }
            });


        }

    }

}
