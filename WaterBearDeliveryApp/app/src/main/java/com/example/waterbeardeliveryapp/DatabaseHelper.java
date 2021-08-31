package com.example.waterbeardeliveryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "DeliveryProfile.db";
    private static final String DB_TABLE = "DeliveryProfile_Table";

    private static String ID = "ID";
    private static String DeliveryID = "DeliveryID";
    private static String DeliveryFirstName = "DeliveryFirstName";
    private static String DeliveryLastName = "DeliveryLastName";

    private static final String CREATE_TABLE = "CREATE TABLE "+ DB_TABLE+"("+
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            DeliveryID + " TEXT, "+
            DeliveryFirstName + " TEXT, "+
            DeliveryLastName + " TEXT "+ ")";

    public DatabaseHelper( Context context) {
        super(context, DB_NAME, null,1);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int il) {
        db.execSQL("DROP TABLE IF EXISTS "+ DB_NAME);

        onCreate(db);
    }
    public Boolean insertData (String deliveryID, String deliveryFirstName, String deliveryLastName){

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DeliveryID,deliveryID);
        contentValues.put(DeliveryFirstName,deliveryFirstName);
        contentValues.put(DeliveryLastName,deliveryLastName);

        long result = database.insert(DB_TABLE, null, contentValues);
        return result != -1;
    }

    public static boolean isDeliveryManAvailable(Context context) {

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean exists = false;

        try {
            String query = "SELECT * FROM " + DatabaseHelper.DB_TABLE;
            Cursor cursor = db.rawQuery(query, null);
            exists = (cursor.getCount() > 0);
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            db.close();
        }

        return exists;
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from "+DB_TABLE;
        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }
    public static void deleteData(Context context){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            db.execSQL("DELETE FROM " + DatabaseHelper.DB_TABLE);
        }
        catch (SQLException e){
            Log.e("TAG",e.getMessage());
            e.printStackTrace();
        }


    }
}
