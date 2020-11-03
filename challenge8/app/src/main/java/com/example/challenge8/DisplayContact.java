package com.example.challenge8;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class DisplayContact extends AppCompatActivity {
   int from_Where_I_Am_Coming = 0;
   private DBHelper mydb ;

   TextView name ;
   TextView url;
   TextView phone;
   TextView email;
   TextView services;
   Spinner classification;


   int id_To_Update = 0;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_display_contact);
      name = (TextView) findViewById(R.id.editTextName);
      url = (TextView) findViewById(R.id.editTextUrl);
      phone = (TextView) findViewById(R.id.editTextPhone);
      email = (TextView) findViewById(R.id.editTextEmail);
      services = (TextView) findViewById(R.id.editTextServices);
      classification = (Spinner) findViewById(R.id.editTextClass_enterprise);
      mydb = new DBHelper(this);

      Bundle extras = getIntent().getExtras();
      if(extras !=null) {
         int Value = extras.getInt("id");
         Log.i("Display Contact", "The value is " + Integer.toString(Value));
         if(Value>0){
            //means this is the view part not the add contact part.
            Cursor rs = mydb.getData(Value);
            id_To_Update = Value;
            rs.moveToFirst();

            String nam = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_NAME));
            String url_addres = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_URL));
            String phon = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_PHONE));
            String emai = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_EMAIL));
            String servic = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_SERVICES));
            String classificat = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_CLASSIFICATION));

            ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.class_types, android.R.layout.simple_spinner_item);
//            ArrayList <String> tmp = new ArrayList<>();
//            tmp.add(classificat);
//            ArrayAdapter adapter = new ArrayAdapter (this,  android.R.layout.simple_spinner_item, tmp);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            classification.setAdapter(adapter);

            if (!rs.isClosed())  {
               rs.close();
            }
            Button b = (Button)findViewById(R.id.button1);
            b.setVisibility(View.INVISIBLE);

            name.setText((CharSequence)nam);
            name.setFocusable(false);
            name.setClickable(false);

            url.setText((CharSequence)url_addres);
            url.setFocusable(false);
            url.setClickable(false);

            phone.setText((CharSequence)phon);
            phone.setFocusable(false);
            phone.setClickable(false);

            email.setText((CharSequence)emai);
            email.setFocusable(false);
            email.setClickable(false);

            services.setText((CharSequence)servic);
            services.setFocusable(false);
            services.setClickable(false);

            if (classificat != null){
               int spinnerPosition = adapter.getPosition(classificat);
               classification.setSelection(spinnerPosition);
            }
            classification.setEnabled(false);
         }
      }else {Log.i("Display Contact", "Is NULL");}
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      Log.i("Display contact:", "crea el options menu");

      // Inflate the menu; this adds items to the action bar if it is present.
      Bundle extras = getIntent().getExtras();

      if(extras !=null) {
         int Value = extras.getInt("id");
         System.out.println("The value in display contact is " + Integer.toString(Value));
         if(Value>0){
            getMenuInflater().inflate(R.menu.display_contact, menu);
         } else{
            getMenuInflater().inflate(R.menu.main_menu, menu);
         }
      }
      return true;
   }

   public boolean onOptionsItemSelected(MenuItem item) {
      super.onOptionsItemSelected(item);
      switch(item.getItemId()) {
         case R.id.Edit_Contact:
         Button b = (Button)findViewById(R.id.button1);
         b.setVisibility(View.VISIBLE);
         name.setEnabled(true);
         name.setFocusableInTouchMode(true);
         name.setClickable(true);

         url.setEnabled(true);
         url.setFocusableInTouchMode(true);
         url.setClickable(true);

         phone.setEnabled(true);
         phone.setFocusableInTouchMode(true);
         phone.setClickable(true);

         email.setEnabled(true);
         email.setFocusableInTouchMode(true);
         email.setClickable(true);

         services.setEnabled(true);
         services.setFocusableInTouchMode(true);
         services.setClickable(true);

         classification.setEnabled(true);
         classification.setFocusableInTouchMode(true);
         classification.setClickable(true);

         return true;
         case R.id.Delete_Contact:

         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setMessage(R.string.deleteContact)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                  mydb.deleteContact(id_To_Update);
                  Toast.makeText(getApplicationContext(), "Deleted Successfully",
                     Toast.LENGTH_SHORT).show();
                  Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                  startActivity(intent);
               }
         })
         .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               // User cancelled the dialog
            }
         });

         AlertDialog d = builder.create();
         d.setTitle("Are you sure");
         d.show();

         return true;
         default:
         return super.onOptionsItemSelected(item);

      }
   }

   public void run(View view) {
      Bundle extras = getIntent().getExtras();
      if(extras !=null) {
         int Value = extras.getInt("id");
         if(Value>0){
            if(mydb.updateContact(id_To_Update,name.getText().toString(), url.getText().toString(),
               phone.getText().toString(), email.getText().toString(),
				   services.getText().toString(), String.valueOf(classification.getSelectedItem()))){
               Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(getApplicationContext(),MainActivity.class);
               startActivity(intent);
            } else{
               Toast.makeText(getApplicationContext(), "not Updated", Toast.LENGTH_SHORT).show();
            }
         } else{
            if(mydb.insertContact(name.getText().toString(), url.getText().toString(), phone.getText().toString(),
				   email.getText().toString(), services.getText().toString(),
				   classification.getSelectedItem().toString())){
                  Toast.makeText(getApplicationContext(), "done",
						   Toast.LENGTH_SHORT).show();
            } else{
               Toast.makeText(getApplicationContext(), "not done",
					   Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
         }
      }
   }

}
