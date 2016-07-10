package com.sampleproj.arun.moviereview.Utilities;

import android.database.Cursor;

/**
 * Created by arunt on 6/25/2016.
 */
public class databaseUtilities {
    public static String printCursorColumnNames(Cursor c)
    {
        String[] columnNames = c.getColumnNames();
        StringBuffer sbf = new StringBuffer();
        if(columnNames.length > 0){

            sbf.append(columnNames[0]);
            for(int i=1; i < columnNames.length; i++){
                sbf.append(" ").append(columnNames[i]);
            }

        }
       return sbf.toString();

    }
}
