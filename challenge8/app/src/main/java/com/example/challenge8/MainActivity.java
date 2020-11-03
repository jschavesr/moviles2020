package com.example.challenge8;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Iterator;


public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "MESSAGE";
    private ListView obj;
    DBHelper mydb;
    TextView filterName ;
    TextView filterClassification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showRegisters("","");
    }

    protected void showRegisters(String name, String classification){
        mydb = new DBHelper(this);
        ArrayList array_list = mydb.getAllContacts(name, classification);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, array_list);

        obj = (ListView)findViewById(R.id.listView1);
        obj.setAdapter(arrayAdapter);
        obj.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                // TODO Auto-generated method stub
                int id_To_Search = arg2 + 1;
                System.out.println("The id is " + Integer.toString(id_To_Search));


                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", id_To_Search);

                Intent intent = new Intent(getApplicationContext(),DisplayContact.class);

                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });
    }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {

       MenuInflater inflater = getMenuInflater();
       Log.i("Main Activity:", "crea el options main menu");
       inflater.inflate(R.menu.main_menu, menu );
      return true;
   }
   @Override
   public boolean onOptionsItemSelected(MenuItem item){
      super.onOptionsItemSelected(item);

      switch(item.getItemId()) {
         case R.id.item1:Bundle dataBundle = new Bundle();
         System.out.println("Entro en el caso primero del switch");
         dataBundle.putInt("id", 0);

         Intent intent = new Intent(getApplicationContext(),DisplayContact.class);
         intent.putExtras(dataBundle);

         startActivity(intent);
         return true;
         default:
         System.out.println("Entro en el caso default del switch");
         return super.onOptionsItemSelected(item);
      }
   }

   public boolean onKeyDown(int keycode, KeyEvent event) {
      if (keycode == KeyEvent.KEYCODE_BACK) {
         moveTaskToBack(true);
      }
      return super.onKeyDown(keycode, event);
   }
    public void filter(View view) {
        filterName = (TextView) findViewById(R.id.editFilterName);
        filterClassification = (TextView) findViewById(R.id.editFilterClass);
        String name = (String) filterName.getText().toString();
        String classification = (String) filterClassification.getText().toString();
        if (classification.equals("-")){
            classification = "";
        }
        showRegisters(name,classification);
        return ;
    }
    public void deleteFilter(View view) {
        filterName = (TextView) findViewById(R.id.editFilterName);
        filterClassification = (TextView) findViewById(R.id.editFilterClass);
        filterName.setText("");
        filterClassification.setText("");
        showRegisters("","");
        return ;
    }
}

