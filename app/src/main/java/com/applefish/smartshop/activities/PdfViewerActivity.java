package com.applefish.smartshop.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.applefish.smartshop.R;
import com.applefish.smartshop.classes.FileDownloader;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PdfViewerActivity extends AppCompatActivity {

    final static String Key="com.applefish.smartshop.PdfViewer";
    final static String Key2 = "com.applefish.smartshop.IDOffer";

    private String pdfurl;
    private String PDF_Name;
    private int IDOffer;
    private ProgressBar progressBar;
    PDFView pdfView;
    //private static final String AddView_URL = "http://192.168.1.2/smartshop/addview.php";
    private static final String AddView_URL = "http://samrtshop-uae.org/smartshop/addview.php";
    private  int pdfsize=0;
    private CheckBox starBTN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton share = (FloatingActionButton) findViewById(R.id.fab);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Share Offer", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        final FloatingActionButton download = (FloatingActionButton) findViewById(R.id.fab2);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   download(view, pdfurl,PDF_Name);

                Snackbar.make(view, "DownLoad File Success", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent=getIntent();
        pdfurl=intent.getStringExtra(Key);
        IDOffer=intent.getIntExtra(Key2,0);
        String [] PDF_URL=pdfurl.split("/");
        PDF_Name=PDF_URL[PDF_URL.length-1];

        progressBar=(ProgressBar)findViewById(R.id.progressbarPdf);
        pdfView=(PDFView)findViewById(R.id.pdfView);
        starBTN=(CheckBox)findViewById(R.id.btn_star);
        starBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String favoriteOffer=readSharedPreference();
                String [] offersId=favoriteOffer.split(",");
                String newFavoritoffers="";
                if(starBTN.isChecked())
                {
                    if(offersId.length==1 && offersId[0].equals(""))
                    {
                        newFavoritoffers=""+IDOffer;
                    }
                     else if(offersId.length>=1 &&  !offersId[0].equals(""))
                    {
                        newFavoritoffers=favoriteOffer+","+IDOffer;
                    }
                    writeSharedPreference(newFavoritoffers);
                    Toast.makeText(getBaseContext(), "Add To Favorite", Toast.LENGTH_LONG).show();
                }
                else
                {
                    //
                    if(offersId.length==1 && !offersId[0].equals(""))
                    {
                        newFavoritoffers="";
                    }
                    else if(offersId.length>=1 && !offersId[0].equals(""))
                    {
                        for (int i=0;i<offersId.length;i++)
                        {
                            if(!offersId[i].equals(IDOffer+""))
                            {
                                if(i==(offersId.length-1))
                                {
                                    newFavoritoffers=newFavoritoffers+offersId[i];
                                }
                                else
                                {
                                    newFavoritoffers=newFavoritoffers+offersId[i]+",";
                                }
                            }
                        }
                    }
                    writeSharedPreference(newFavoritoffers);
                    Toast.makeText(getBaseContext(), "Delete From Favorite", Toast.LENGTH_LONG).show();
                    //


                }


            }


        });
        String favoriteOffer=readSharedPreference();
        String [] offersId=favoriteOffer.split(",");
        Boolean find=false;
        if(offersId.length==1 && offersId[0].equals(""))
        {

            starBTN.setChecked(false);
        }
        else if(offersId.length>=1 && !offersId[0].equals(""))
        {
            for (int i=0;i<offersId.length;i++)
            {

                if(offersId[i].equals((IDOffer+"")))
                {
                    starBTN.setChecked(true);
                    find=true;
                    break;
                }
            }
            if(!find)
                starBTN.setChecked(false);
        }



        //-------------------1------------downlaod pdf file
        Thread getPdfsize = new Thread(){
            @Override
            public void run() {
                getPdfsize(pdfurl);

            }
        };

        try {
            getPdfsize.start();
            getPdfsize.join();


        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public  void displayViewPDF()
    {


        // Uri uri=Uri.fromFile(new File(pdfurl)) ;


            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    File file1 = new File(Environment.getExternalStorageDirectory() + "/SmartShopOffers/" + PDF_Name);
                    Log.i("displayViewPDF", "file1 size " + file1.length());


//                    while(pdfSize==0 && pdfSize!=file1.length())
//                    {
//
//                        file1 = new File(Environment.getExternalStorageDirectory() + "/SmartShopOffers/" + PDF_Name);
//                        pdfSize=file1.length();
//                    }

                        if(file1.length()>=pdfsize){

                            try {
                            pdfView.fromFile(file1)
                                    .enableSwipe(true)
                                    .enableDoubletap(true)
                                    .swipeHorizontal(true)
                                    .defaultPage(0)
                                    .onLoad(new OnLoadCompleteListener() {
                                        @Override
                                        public void loadComplete(int nbPages) {

                                        }
                                    })
                                    .onPageChange(new OnPageChangeListener() {
                                        @Override
                                        public void onPageChanged(int page, int pageCount) {

                                        }
                                    })
                                    .enableAnnotationRendering(false)
                                    .password(null)
                                    .load();

                        } catch (Exception uriex) {
                            Log.i("catch PdfViewerActivity", "displayViewPDF,   " + uriex);

                        } finally {

                            progressBar.setVisibility(View.GONE);
                            pdfView.setVisibility(View.VISIBLE);
                            pdfView = (PDFView) findViewById(R.id.pdfView);
                            increaseNumOfView(AddView_URL + "/?idoffer=" + IDOffer);
                            Log.i("displayViewPDF", "displayViewPDF IDOffer= " + IDOffer);

                        }

                        }
                    else
                        {
                            if(file1.exists())
                            displayViewPDF();
                        }

                }
            }, 2000);





        }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);




    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {
//        ProgressDialog loading;
//        @Override
        protected void onPreExecute() {
            super.onPreExecute();

       //     loading = ProgressDialog.show(getBaseContext(), "Fetching Data","Please Wait...",true,true);

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    displayViewPDF();
                }
            }, 3000);


        }


        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://192.168.1.2/smartshop/pdf/Mercedes.pdf
            String fileName = strings[1];  // -> Mercedes.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "SmartShopOffers");//create folder
            folder.mkdir();

            File pdfFile = new File(folder, fileName);
            long pdfFileLenght=pdfFile.length();
           if( pdfFile.exists() && pdfFileLenght== pdfsize)
           {
               Log.i("DownloadFile1", "exist pdf ");
           }
            else
           {
               try{
                   pdfFile.createNewFile();
                   Log.i("DownloadFile1", "NOT exist pdf ");
               }catch (IOException e){

                   Log.i("DownloadFile2", "doInBackground: " +e);
                   e.printStackTrace();
               }
               Log.i("DownloadFile3", "doInBackground: " +pdfFile+"--->"+fileName);
               FileDownloader.downloadFile(fileUrl, pdfFile);
           }

            return null;
        }

//@Override
//protected void onCancelled() {   super.onCancelled();
//    loading.dismiss();}

    }

    public void download(View v,String pdfurl,String PDF_Name )
    {
        new DownloadFile().execute(pdfurl, PDF_Name);
    }

    private void increaseNumOfView(String url) {


        class IncreaseNumOfView extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String result;
                BufferedReader bufferedReader ;
                try {

                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    int status = con.getResponseCode();


                    Log.i("increaseNumOfView", "doInBackground: " +status);

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

            }
        }

        IncreaseNumOfView gj = new IncreaseNumOfView();
        gj.execute(url);

    }
    private void getPdfsize(String url) {


        class GetPdfSize extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                int result;
                BufferedReader bufferedReader ;
                try {

                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    int status = con.getResponseCode();

                    Log.i("GetPdfsize", "doInBackground: status" +status);

                    pdfsize=con.getContentLength();
                    Log.i("GetPdfsize", "doInBackground:pdfsize= " +pdfsize);
                    return null;

                }catch(Exception e){
                    return null;
                }

            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                download(pdfView, pdfurl,PDF_Name);
            }
        }

        GetPdfSize gj = new GetPdfSize();
        gj.execute(url);

    }

    public String readSharedPreference()
    {
        SharedPreferences sharedPref =getBaseContext().getSharedPreferences("com.applefish.smartshop.FAVORITE_KEY",MODE_PRIVATE);
        //0 is default_value if no vaule
        String savedFavoriteOffer = sharedPref .getString(getString(R.string.saved_favorite), "");

        return savedFavoriteOffer;
    }
    public  void  writeSharedPreference(String savedFavoriteOffer)
    {
        SharedPreferences sharedPref =getBaseContext().getSharedPreferences("com.applefish.smartshop.FAVORITE_KEY",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_favorite), savedFavoriteOffer);
        editor.commit();
    }

}
