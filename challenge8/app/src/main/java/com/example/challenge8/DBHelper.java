package com.example.challenge8;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{
   public static final String DATABASE_NAME = "MyDBName.db";
   public static final String CONTACTS_TABLE_NAME = "contacts";
   public static final String CONTACTS_COLUMN_ID = "id";
   public static final String CONTACTS_COLUMN_NAME = "name";
   public static final String CONTACTS_COLUMN_URL = "url";
   public static final String CONTACTS_COLUMN_PHONE = "phone";
   public static final String CONTACTS_COLUMN_EMAIL = "email";
   public static final String CONTACTS_COLUMN_SERVICES = "services";
   public static final String CONTACTS_COLUMN_CLASSIFICATION = "classification";


   private HashMap hp;

   public DBHelper(Context context) {
      super(context, DATABASE_NAME , null, 1);
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
      // TODO Auto-generated method stub
      db.execSQL(
         "create table contacts " +
         "(id integer primary key, name text, url text, phone text,email text, services text,classification text)"
      );
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      // TODO Auto-generated method stub
      db.execSQL("DROP TABLE IF EXISTS contacts");
      onCreate(db);
   }

   public boolean insertContact (String name, String url, String phone , String email, String services,String classification) {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put("name", name);
      contentValues.put("phone", phone);
      contentValues.put("url"  ,  url);
      contentValues.put("email", email);
      contentValues.put("services", services);
      contentValues.put("classification", classification);
      db.insert("contacts", null, contentValues);
      return true;
   }

   public Cursor getData(int id) {
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
      Log.i("Returned Value: ", String.valueOf(res));
      return res;
   }

   public int numberOfRows(){
      SQLiteDatabase db = this.getReadableDatabase();
      int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
      return numRows;
   }

   public boolean updateContact (Integer id, String name, String url, String phone, String email, String services,String classification) {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put("name", name);
      contentValues.put("url", url);
      contentValues.put("phone", phone);
      contentValues.put("email", email);
      contentValues.put("services", services);
      contentValues.put("classification", classification);
      db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
      return true;
   }

   public Integer deleteContact (Integer id) {
      SQLiteDatabase db = this.getWritableDatabase();
      return db.delete("contacts",
      "id = ? ",
      new String[] { Integer.toString(id) });
   }

   public ArrayList<String> getAllContacts(String name, String classification) {
      ArrayList<String> array_list = new ArrayList<String>();
      //hp = new HashMap();
      SQLiteDatabase db = this.getReadableDatabase();
      String query = "select * from contacts " ;
      boolean has_name = false;
      if (name.length() > 0 || classification.length() > 0){
         query += "where ";
         if (name.length()>0){
            query += "name like '%" + name + "%' ";
            has_name = true;
         }
         if (classification.length()>0){
            if (has_name){
               query += "and ";
            }
            query += "classification like '%" + classification + "%' ";
         }
      }
      Cursor res =  db.rawQuery( query , null );
      res.moveToFirst();

      while(res.isAfterLast() == false){
         array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
         res.moveToNext();
      }
      return array_list;
   }
}
