package com.applefish.smartshop.classes;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

/**
 * Created by Amro on 14/01/2017.
 */

public class ConnectChecked {


    public static  boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService( context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

    }



    public static boolean isOnline () {

        Runtime runtime = Runtime.getRuntime();

        try {

            Process ipProcess = runtime.exec( "/system/bin/ping -c 1 8.8.8.8" );
            int exitValue = ipProcess.waitFor();

            return ( exitValue == 0 );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        catch ( InterruptedException e ) {
            e.printStackTrace();
        }

        return false;

    }
}
