package com.applefish.smartshop.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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
    //private static final String AddView_URL = "http://192.168.1.2/smartshop/addview.php";
    private static final String AddView_URL = "http://gherasbirr.org/smartshop/addview.php";

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        final FloatingActionButton download = (FloatingActionButton) findViewById(R.id.fab2);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action 2", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                download(view, pdfurl,PDF_Name);

                Snackbar.make(view, "DownLoad File Success", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar=(ProgressBar)findViewById(R.id.progressbarPdf);

        Intent i=getIntent();
        pdfurl=i.getStringExtra(Key);
        IDOffer=i.getIntExtra(Key2,0);
        String [] PDF_URL=pdfurl.split("/");
        PDF_Name=PDF_URL[PDF_URL.length-1];

        //-------------------1------------downlaod pdf file
        Thread downloadFile = new Thread(){
            @Override
            public void run() {
                download(download, pdfurl,PDF_Name);

            }
        };

        try {
            downloadFile.start();
            downloadFile.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        PDFView pdfView=(PDFView)findViewById(R.id.pdfView);
        try{

            Thread.sleep(2000);
           // Uri uri=Uri.fromFile(new File(pdfurl)) ;
            File file1 = new File(Environment.getExternalStorageDirectory()+"/SmartShopOffers/"+PDF_Name);
          //  pdfView.fromUri(uri).load();
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

            progressBar.setVisibility(View.GONE);
            increaseNumOfView(AddView_URL+"/?idoffer="+IDOffer);
            Log.i("PdfViewerActivity", "pdfView : " +IDOffer );
               }

        catch(Exception uriex){
            Log.i("PdfViewerActivity", "pdfView : " + uriex);

        }



    }


    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://192.168.1.2/smartshop/pdf/Mercedes.pdf
            String fileName = strings[1];  // -> Mercedes.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "SmartShopOffers");//create folder
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            Log.i("DownloadFile", "doInBackground: " +pdfFile+"---"+fileName);
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }

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


}
