package com.kodery.pratz.notsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import android.app.AlertDialog;

public class newSession extends AppCompatActivity {
    //public static String id=Sessions.id;

    public static String ip=Sessions.ip;
    private static newSession snewSession;
    ListView lst_chat;
    public String m_Text="";
    String msg="";
    public ArrayList<String> msglist = new ArrayList<String>();
    public ArrayList<String> dep = new ArrayList<String>();
    static public String mystr="";
    private DrawerLayout mDrawerLayout;
    ArrayList<ArrayList<String>> emplist = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> empidlist = new ArrayList<ArrayList<String>>();
    public ArrayList<String> finallist = new ArrayList<String>();
    public ArrayList<String> finallist2 = new ArrayList<String>();
    public ArrayList<Dictionary> empfinal=new ArrayList<Dictionary>();
    //Dictionary inner=new Hashtable<String, String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Dont change
        snewSession = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //When the Button is pressed POST request to database has to be sent
        FloatingActionButton butnext=(FloatingActionButton)findViewById(R.id.butnext2);
        butnext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String m_Text = "";
                AlertDialog.Builder builder = new AlertDialog.Builder(newSession.this);
                builder.setTitle("Session Title");

                final EditText input = new EditText(newSession.this);

                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    String m_Text = "";
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        //Log.d("cali",m_Text);
                        setText(m_Text);
                        String resultURL = ip+"/newsession";
                        new PostData().execute(resultURL);
                        //Change here to go to next page
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();



            }
        });

        String resultURL = ip+"/depdata";
        new RestOperation().execute(resultURL);

        //Code for SIDE MENU
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mDrawerLayout.openDrawer(Gravity.LEFT);

        // This is called every time someone clicks on the department
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Log.d("Calden",Integer.toString(menuItem.getItemId()));
                        lst_chat = (ListView) findViewById(R.id.lstdata);
                        filler adapter=new filler(newSession.this,empfinal.get(menuItem.getItemId()));
                        lst_chat.setAdapter(adapter);

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

    public  void setText(String temp)
    {
        m_Text=temp;
    }


    //Sets the ARRAY LIST passed in the textbox (Changes to be made so that people from other departments can be seleccted)
    public void setme(String str)
    {

        int flag=0;
        if(finallist.isEmpty()){
            finallist.add(str);
            String hello="";
            for(int i=0;i<empfinal.size();i++){
                if(empfinal.get(i).get(str)==null){

                }
                else{
                    Log.d("kk",empfinal.get(i).get(str).toString());
                    hello=empfinal.get(i).get(str).toString();
                }
            }
            finallist2.add(hello);
        }
        else{
            for(int i=0;i<finallist.size();i++){
                if(finallist.get(i).equals(str)){
                    finallist.remove(i);
                    flag=1;
                    finallist2.remove(i);
                }
            }
            if(flag==0){
                finallist.add(str);
                String hello="";
                for(int i=0;i<empfinal.size();i++){
                    if(empfinal.get(i).get(str)==null){

                    }
                    else{
                        Log.d("kk",empfinal.get(i).get(str).toString());
                        hello=empfinal.get(i).get(str).toString();
                    }
                }
                finallist2.add(hello);
            }
        }

        TextView totalemp=(TextView)findViewById(R.id.totalemp);



        //Converting arrarlist to string
        String listString = "";
        for (String s : finallist2)
        {
            listString += s + ", ";
        }

        totalemp.setText(listString);
    }

    //Pre-defined, don't change
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Sends data to filler class
    public static newSession getInstance() {
        return snewSession;
    }

    //Sets the items present in "dep" to the menu drawer
    public void setlist()
    {
        NavigationView navigationView = findViewById(R.id.nav_view);
        final Menu menu = navigationView.getMenu();
        menu.clear();
        for(int i=0;i<dep.size();i++){
            menu.add(R.id.group,i,Menu.NONE,dep.get(i));
        }

    }


    //Calling the API To get List of departments and storing them in ArrayList "dep"
    public class RestOperation extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params)
        {
            StringBuilder result= new StringBuilder();
            try{

                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                line=bufferedReader.readLine();

                result.append(line).append("\n");


            }catch(IOException ex){
                return ex.toString();
            }
            return result.toString();
        }


        @Override
        protected void onPreExecute()
        {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {

                super.onPostExecute(result);
                //Log.d("kyle",result);

                JSONArray arr = new JSONArray(result);
                JSONArray arr2 = new JSONArray(result);
                JSONObject jObj,jObj2;
                //temp.clear();
                for(int i=0;i<arr.length();i++) {
                    jObj = arr.getJSONObject(i);
                    String name = jObj.getString("name");
                    String id=jObj.getString("id");

                    String resultURL = ip+"/depdata/"+id;
                    new RestOperation2().execute(resultURL);

                    dep.add(name);

                }


            }
            catch (Exception e)
            {
                Log.d("ERROR",e.toString());
                dep.add("There has been an error!!!");

            }
            setlist();
        }
    }

    //Calling the API To get List of employees in each department and storing them in a 2d Arraylist "emplist"
    public class RestOperation2 extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params)
        {
            StringBuilder result= new StringBuilder();
            try{

                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                line=bufferedReader.readLine();

                result.append(line).append("\n");


            }catch(IOException ex){
                return ex.toString();
            }
            return result.toString();
        }


        @Override
        protected void onPreExecute()
        {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {
                JSONArray arr = new JSONArray(result);
                Log.d("mytag",result);
                JSONObject jObj;
                ArrayList<String> temp10 = new ArrayList<String>();
                ArrayList<String> temp11 = new ArrayList<String>();
                Dictionary inner=new Hashtable<String, String>();
                for(int i=0;i<arr.length();i++) {
                    jObj = arr.getJSONObject(i);
                    String id=jObj.getString("empid");
                    String name = jObj.getString("name");
                    //Log.d("noah",name);
                    inner.put(id,name);
                    temp10.add(name);
                    temp11.add(id);

                }
                empfinal.add(inner);
                emplist.add(temp10);
                empidlist.add(temp11);



            }
            catch (Exception e)
            {
                Log.d("ERROR",e.toString());
                dep.add("There has been an error!!!");

            }

        }
    }

    //Call the API to POST the newSession details to the server
    public class PostData extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                return postData(params[0]);
            }catch (IOException e){
                return "Network Error";
            }catch (JSONException e){
                return "Data Invalid";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            TextView totalemp=(TextView)findViewById(R.id.totalemp);
            totalemp.setText(result);
        }

        public String postData(String urlpath) throws IOException,JSONException{

            SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            String id = sharedPref.getString("userid","");

            JSONObject datatosend=new JSONObject();
            JSONArray temp=new JSONArray();
            JSONArray mem=new JSONArray();
            datatosend.put("name",m_Text);
            datatosend.put("admin",id);
            mem.put(id);
            datatosend.put("members",mem);
            for(int i=0;i<finallist.size();i++){
                try {
                    temp.put(finallist.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                datatosend.put("invited",temp);
            }

            Log.d("cal",datatosend.toString());

            StringBuilder result=new StringBuilder();

            URL url = new URL(urlpath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.connect();

            OutputStream outputStream=urlConnection.getOutputStream();
            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(datatosend.toString());
            bufferedWriter.flush();

            InputStream inputStream= urlConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            String line;
            line=bufferedReader.readLine();
            result.append(line);

            return result.toString();
        }


    }

}

class filler extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> msglist=new ArrayList<String>();
    ArrayList<String> msglistid=new ArrayList<String>();
    ArrayList<String> listofemp=new ArrayList<String>();

    public filler(Context context, Dictionary msg){
        this.context=context;
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (Enumeration k = msg.keys(); k.hasMoreElements();)
        {
            String temp=k.nextElement().toString();
            msglistid.add(temp);
            msglist.add(temp+" "+msg.get(temp));
        }
    }

    @Override
    public int getCount() {
        return msglist.size();
    }

    @Override
    public Object getItem(int i) {
        return msglist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view=layoutInflater.inflate(R.layout.activity_filler,null);
        final TextView txtView=(TextView)view.findViewById(R.id.txtView);
        txtView.setText(msglist.get(i));



        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String temp=txtView.getText().toString();
                if(temp.contains(" ")){
                    temp= temp.substring(0, temp.indexOf(" "));
                }

                //listofemp.add(temp);
                newSession.getInstance().setme(temp);
            }
        });



        return view;

    }



}