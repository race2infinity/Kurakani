package com.kodery.calden.newsession;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class newSession extends AppCompatActivity {
    private static newSession snewSession;
    ListView lst_chat;
    String msg="";
    public ArrayList<String> msglist = new ArrayList<String>();
    static public String mystr="";
    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        snewSession = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //Getting data
        msglist.add("Calden");
        msglist.add("Roshan");
        msglist.add("Pratik");
        msglist.add("Shree");
        msglist.add("Kyle");
        msglist.add("Shadrak");
        //Initiating list
        lst_chat = (ListView) findViewById(R.id.lstdata);
        filler adapter=new filler(newSession.this,msglist);
        lst_chat.setAdapter(adapter);

        //Setting text field
        TextView txt=(TextView)findViewById(R.id.totalemp);
        txt.setText(mystr);

        //Code for SIDE MENU
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        Toast.makeText(newSession.this,menuItem.toString(), Toast.LENGTH_SHORT).show();

                        return true;
                    }
                });


    }


    public void setme(ArrayList<String> str)
    {
        //Removing repeated items
        Set<String> hs = new HashSet<>();
        hs.addAll(str);
        str.clear();
        str.addAll(hs);
        TextView totalemp=(TextView)findViewById(R.id.totalemp);

        //Converting arrarlist to string
        String listString = "";
        for (String s : str)
        {
            listString += s + "\t";
        }

        totalemp.setText(listString);
        Log.d("YOLO",Integer.toString(str.size()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static newSession getInstance() {
        return snewSession;
    }
}


