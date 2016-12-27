package com.applefish.smartshop.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;


import com.applefish.smartshop.R;
import com.applefish.smartshop.classes.FileDownloader;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class PdfViewerActivity extends AppCompatActivity {

    final  static  String Key="com.applefish.smartshop.PdfViewer";
    private   String pdfurl;
    private   String PDF_Name;
    private   ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
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

        String [] PDF_URL=pdfurl.split("/");
        PDF_Name=PDF_URL[PDF_URL.length-1];

//        WebView webView = (WebView) findViewById(R.id.webView1);
//        webView.getSettings().setJavaScriptEnabled(true);
//       // webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url="+pdfurl);
//        webView.loadUrl(pdfurl);

            //-------------------1------------downlaod pdf file
        download(fab2, pdfurl,PDF_Name);

        //-------------------2------------ GONE progressBar
        progressBar.setVisibility(View.GONE);

        //-------------------3------------ view pdf
        PDFView pdfView=(PDFView)findViewById(R.id.pdfView);
        try{

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

}
