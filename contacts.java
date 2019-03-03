package com.example.johnsaunders.drawer3;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class contacts extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,  AdapterView.OnItemSelectedListener {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private ArrayList<Android_Contact> android_Contacts = new ArrayList<Android_Contact>();
    private HashMap<String, String> contactmap = new HashMap<String, String>();

    public class Android_Contact {
        public String contact_Name = "";
        public String phone_num = "";
        public int android_contact_ID = 0;
    }

    private void loadContacts() {
        //ArrayList<Android_Contact> android_Contacts = new ArrayList<Android_Contact>();
        //HashMap<String, String> contactmap = new HashMap<String, String>();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            Cursor cursor = null;

            ContentResolver contentResolver = getContentResolver();
            try {
                cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            } catch (Exception ex) {
                Log.e("Error on contact", ex.getMessage());
            }
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Android_Contact android_contact = new Android_Contact();
                    String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String contact_display_name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    android_contact.contact_Name = contact_display_name;

                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {

                        Cursor phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                                , null
                                , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                                , new String[]{contact_id}
                                , null);

                        while (phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            android_contact.phone_num = phoneNumber;
                        }
                        phoneCursor.close();

                    }
                    android_Contacts.add(android_contact);
                    System.out.println(android_contact);
                }
                contactmap.put("-----------------------", " ");
                for (int i = 0; i < android_Contacts.size(); i++) {
                    contactmap.put(android_Contacts.get(i).contact_Name, android_Contacts.get(i).phone_num);
                }
                String[] arr = new String[android_Contacts.size()+1];
                arr[0] = "-----------------------";
                for (int i = 0; i < android_Contacts.size(); i++) {
                    arr[i+1] = android_Contacts.get(i).contact_Name;
                }

                Spinner spin1 = (Spinner) findViewById(R.id.contactspinner1);
                spin1.setOnItemSelectedListener(this);
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arr);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//Setting the ArrayAdapter data on the Spinner
                spin1.setAdapter(adapter);

                Spinner spin2 = (Spinner) findViewById(R.id.contactspinner2);
                spin2.setOnItemSelectedListener(this);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin2.setAdapter(adapter);

                Spinner spin3 = (Spinner) findViewById(R.id.contactspinner3);
                spin3.setOnItemSelectedListener(this);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin3.setAdapter(adapter);

                Spinner spin4 = (Spinner) findViewById(R.id.contactspinner4);
                spin4.setOnItemSelectedListener(this);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin4.setAdapter(adapter);

                Spinner spin5 = (Spinner) findViewById(R.id.contactspinner5);
                spin5.setOnItemSelectedListener(this);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin5.setAdapter(adapter);


                /*
                Adapter_for_Android_Contacts adapter = new Adapter_for_Android_Contacts(this, android_Contacts);
                setContentView(R.layout.fragment_contacts);
                ListView listView_Android_Contacts = (ListView) findViewById(R.id.listview_Android_Contacts);
                listView_Android_Contacts.setAdapter(adapter);*/
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Spinner spin1 = (Spinner) findViewById(R.id.contactspinner1);
        Spinner spin2 = (Spinner) findViewById(R.id.contactspinner2);
        Spinner spin3 = (Spinner) findViewById(R.id.contactspinner3);
        Spinner spin4 = (Spinner) findViewById(R.id.contactspinner4);
        Spinner spin5 = (Spinner) findViewById(R.id.contactspinner5);

        if (spin1 != null && spin1.getSelectedItem() != null) {
            TextView textView = (TextView) findViewById(R.id.contactnum1);
            textView.setText(contactmap.get(spin1.getSelectedItem()));
        }
        if (spin2 != null && spin2.getSelectedItem() != null) {
            TextView textView = (TextView) findViewById(R.id.contactnum2);
            textView.setText(contactmap.get(spin2.getSelectedItem()));
        }
        if (spin3 != null && spin3.getSelectedItem() != null) {
            TextView textView = (TextView) findViewById(R.id.contactnum3);
            textView.setText(contactmap.get(spin3.getSelectedItem()));
        }
        if (spin4 != null && spin4.getSelectedItem() != null) {
            TextView textView = (TextView) findViewById(R.id.contactnum4);
            textView.setText(contactmap.get(spin4.getSelectedItem()));
        }
        if (spin5 != null && spin5.getSelectedItem() != null) {
            TextView textView = (TextView) findViewById(R.id.contactnum5);
            textView.setText(contactmap.get(spin5.getSelectedItem()));
        }
        //Toast.makeText(getApplicationContext(), arr[position], Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
// TODO Auto-generated method stub

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.activity_contact_list, menu);
//        loadContacts();
//
//        return true;
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                loadContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        setUpDrawer();
        loadContacts();

    }

    /*-----Set Up drawer--------*/
    private  void setUpDrawer(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView username = (TextView) header.findViewById(R.id.insertName);
        username.setText("Bennet");
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.alert) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        } else if (id == R.id.contacts) {
        } else if (id == R.id.message) {
            startActivity(new Intent(getApplicationContext(),Message.class));
        } else if (id == R.id.recordings) {
            startActivity(new Intent(getApplicationContext(),recordings.class));
        } else if (id == R.id.settings) {
            startActivity(new Intent(getApplicationContext(),settings.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
