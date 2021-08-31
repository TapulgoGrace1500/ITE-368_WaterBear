package com.example.waterbear2021;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Cart.db";
    private static final String DB_TABLE = "Cart_Table";

    private static String ID = "ID";
    private static String CartID = "CartID";
    private static String BuyerID = "BuyerID";
    private static String BuyerName = "BuyerName";
    private static String BuyerAddress = "BuyerAddress";
    private static String StoreID = "StoreID";
    private static String StoreName = "StoreName";
    private static String ProductType = "ProductType";
    private static String ProductID = "ProductID";
    private static String ProductName = "ProductName";
    private static String Status = "Status";

    private static String ProductPrice = "ProductPrice";
    private static String TotalPrice = "TotalPrice";
    private static String Quantity = "Quantity";

    private static final String CREATE_TABLE = "CREATE TABLE "+ DB_TABLE+"("+
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            CartID + " TEXT, "+
            BuyerID + " TEXT, "+
            BuyerName + " TEXT, "+
            BuyerAddress + " TEXT, "+
            StoreID + " TEXT, "+
            StoreName + " TEXT, "+
            ProductType + " TEXT, "+
            ProductID + " TEXT, "+
            ProductName + " TEXT, "+
            Status + " TEXT, "+
            ProductPrice + " NUMERIC, "+
            TotalPrice + " NUMERIC, "+
            Quantity + " INTEGER "+ ")";

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
    public Boolean insertData (String cartID, String buyerID, String buyerName, String buyerAddress, String storeID,
                                String storeName, String productType, String productID, String productName, String status,
                                Double productPrice, Double totalPrice, int quantity){

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CartID,cartID);
        contentValues.put(BuyerID,buyerID);
        contentValues.put(BuyerName,buyerName);
        contentValues.put(BuyerAddress,buyerAddress);
        contentValues.put(StoreID,storeID);
        contentValues.put(StoreName,storeName);
        contentValues.put(ProductType,productType);
        contentValues.put(ProductID,productID);
        contentValues.put(ProductName,productName);
        contentValues.put(Status,status);
        contentValues.put(ProductPrice,productPrice);
        contentValues.put(TotalPrice,totalPrice);
        contentValues.put(Quantity,quantity);

        long result = database.insert(DB_TABLE, null, contentValues);
        return result != -1;
    }
    public static boolean isCartAvailable(Context context) {

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
