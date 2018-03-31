package com.kodery.pratz.notsapp;

import android.Manifest;
import android.accounts.Account;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.ResponseBody;
import java.net.*;
import java.util.concurrent.TimeUnit;


public class Chatroom extends AppCompatActivity {

    public TextView textFile;
    private static final int PICKFILE_RESULT_CODE = 1;
    public static String ip=Sessions.ip;
    //public TextView stringbox;
    private RecyclerView mMessageRecycler;
    private MessageAdapter mMessageAdapter;
    public ArrayList<Message> messageList=new ArrayList<Message>();
    public String text,value="",valuename;
    public static String id="";
    public static String name="";
    public static String fname="";

    int SELECT_PICTURE = 101;
    int CAPTURE_IMAGE = 102;
    //int PICKFILE_RESULT_CODE=1;
    Button getImage;
    ImageView selectedImage;
    String encodedImage;
    JSONObject jsonObject;
    JSONObject Response;



    private Socket socket;

    {
        try {
            socket = IO.socket(ip);

        } catch (URISyntaxException e) {

        }
    }



    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if(resultCode != RESULT_CANCELED){
        if (requestCode == SELECT_PICTURE) {
            // Make sure the request was successful
            Log.d("Vicky","I'm out.");
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                fname=getFileName(selectedImageUri);
                Log.d("hithere",selectedImageUri.toString());
                Bitmap selectedImageBitmap = null;
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //selectedImage=findViewById(R.id.imageView4);
                //selectedImage.setImageBitmap(selectedImageBitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
                encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                Log.d("Vicky",encodedImage);
                Message msg=new Message("",null,"",selectedImageBitmap);
                messageList.add(msg);
                mMessageAdapter.notifyDataSetChanged();
                mMessageRecycler.scrollToPosition(messageList.size()-1);
                //new FileTry.UploadImages().execute();
            }


        }
        else if(requestCode==PICKFILE_RESULT_CODE){
            //Log.d("ge","egr");
            Bitmap selectedImageBitmap = null;
            try{
            String filepath=data.getData().getPath();
            fname=filepath.substring(filepath.lastIndexOf("/")+1);
            InputStream inputStream = new FileInputStream(filepath);
            //byte[] byteArray = IOUtils.toByteArray(inputStream);
           // encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
            //PrintLog.showTag(TAG,"pdf converted to string : " + encodedPdfString);
            //return encodedPdfString;
                selectedImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_attach_file_black_24dp);                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                //Log.d("good",selectedImageBitmap.toString());
                Message msg=new Message("",null,"",selectedImageBitmap);
                messageList.add(msg);
                mMessageAdapter.notifyDataSetChanged();
                mMessageRecycler.scrollToPosition(messageList.size()-1);
            Log.d("Vicky",encodedImage);
            Log.d("herebro",filepath);}catch(Exception e){e.printStackTrace();}
        }
        }
    }

    public String getStringFile(File f) {
        InputStream inputStream = null;
        String encodedFile= "", lastVal;
        try {
            inputStream = new FileInputStream(f.getAbsolutePath());

            byte[] buffer = new byte[10240];//specify the size to allow
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }
            output64.close();
            encodedFile =  output.toString();
        }
        catch (FileNotFoundException e1 ) {
            e1.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        return lastVal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT>22){
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }


        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        id = sharedPref.getString("userid","");

        Bundle b = getIntent().getExtras();
        //String value = ""; // or other values
        if(b != null)
        { value = b.getString("id");
            valuename=b.getString("name");
        }

        User usr = (User) new User("Lord Kaldon", "Desg2", "Dept2", id);
        Message temp =(Message) new Message("MESSAGE CONTENT", usr, "9pm",null);
        messageList.add(temp);

        setContentView(R.layout.activity_chatroom);

        //stringbox=(TextView)findViewById(R.id.textView2);

/*
            String DownloadUrl = "http://myexample.com/android/";
            String fileName = "myclock_db.db";

            DownloadDatabase(DownloadUrl,fileName);
*/
        //socket.emit("image",encodedImage );


        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);

        //ActionBar actionbar=getSupportActionBar();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(valuename);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Chatroom.this, SessionUsers.class);
                intent.putExtra("sid",value);
                intent.putExtra("name",valuename);
                startActivity(intent);
            }
        });



        mMessageRecycler = (RecyclerView) findViewById(R.id.recyclerview_message_list);

        socket.connect();
        //socket.on("chat", handleIncomingMessages);
        socket.connect();
        socket.on("file",handleIncomingFile);
        socket.connect();
        socket.on("lel",yolo);

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(300, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(300, TimeUnit.SECONDS);

        String resultURL = ip+"/messages/"+value;
        new GetData().execute(resultURL);


        mMessageAdapter = new MessageAdapter(this, messageList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        //llm.setStackFromEnd(true);
        //llm.setReverseLayout(true);
        mMessageRecycler.setLayoutManager(llm);
        mMessageRecycler.setAdapter(mMessageAdapter);

        EditText txt = (EditText) findViewById(R.id.edittext_chatbox);
        txt.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);


        Button btn = (Button) findViewById(R.id.button_chatbox_send);

/*
        ImageButton sendFile = (ImageButton) findViewById(R.id.imageButton3);
        sendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Chatroom.this);
                builder.setTitle("Profile Picture");
                builder.setMessage("Chooose from?");
                builder.setPositiveButton("GALLERY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,SELECT_PICTURE);

                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("file/*");
                        startActivityForResult(intent,PICKFILE_RESULT_CODE);
                    }
                });
                builder.setNegativeButton("CANCEL", null );
                builder.show();

            }
        });*/
/*
        sendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent,PICKFILE_RESULT_CODE);
            }
        });

*/
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText txt = (EditText) findViewById(R.id.edittext_chatbox);
                text = txt.getText().toString();
                txt.setText("");
                if(!text.equals("")) {
                    String resultURL = ip+"/messages/";
                    new Chatroom.PostData().execute(resultURL, text);
                    mMessageAdapter.notifyDataSetChanged();
                }
                String resultURL = "http://192.168.0.30:3020/"+"/fileshare/";
                new Chatroom.PostDataimage().execute(resultURL, text);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        final AlertDialog.Builder builder;
        switch (id){
            case R.id.sendImage:
                builder = new AlertDialog.Builder(Chatroom.this);
                builder.setTitle("Send Photo");
                builder.setMessage("Chooose from?");
                builder.setPositiveButton("GALLERY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,SELECT_PICTURE);
                    }
                });
                builder.setNegativeButton("CANCEL", null );
                builder.show();
                break;
            case R.id.sendDocument:
                builder = new AlertDialog.Builder(Chatroom.this);
                builder.setTitle("Send Document");
                builder.setMessage("Chooose from?");
                builder.setPositiveButton("Explorer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("file/*");
                        startActivityForResult(intent,PICKFILE_RESULT_CODE);
                    }
                });
                builder.setNegativeButton("CANCEL", null );
                builder.show();
                break;
            case R.id.export:

                break;
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();

    }



    //Socket listenr
    private Emitter.Listener handleIncomingMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Chatroom.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject mainObject = (JSONObject) args[0];
                    Log.d("chai",mainObject.toString());
                    Log.d("uu","uu");
                    try {
                        String id = mainObject.getString("sender");
                        String message = mainObject.getString("body");
                        String ses=mainObject.getString("sess_id");
                        String des=mainObject.getString("send_des");
                        String dep=mainObject.getString("send_dep");
                        String name=mainObject.getString("send_name");
                        String time=mainObject.getString("created_at");

                        SimpleDateFormat gg=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        //gg.setTimeZone(Calendar.getInstance().getTimeZone());
                        SimpleDateFormat fd=new SimpleDateFormat("HH:mm");
                        Date dateobj;
                        String ttime;
                        dateobj =gg.parse(time);
                        ttime=fd.format(dateobj);

                        User usr = (User) new User(name, des, dep, id);
                        Message temp =(Message) new Message(message, usr, ttime,null);
                        messageList.add(temp);
                        mMessageAdapter.notifyDataSetChanged();
                        mMessageRecycler.scrollToPosition(messageList.size()-1);

                    } catch (Exception e) {
                        Log.d("yomom",e.toString());
                    }

                }
            });
        }
    };


    private Emitter.Listener yolo = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Chatroom.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                    Log.d("yoman","gege");

                    } catch (Exception e) {
                        Log.d("yomom",e.toString());
                    }

                }
            });
        }
    };



    private Emitter.Listener handleIncomingFile = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Chatroom.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject mainObject = (JSONObject) args[0];
                    Log.d("chai",mainObject.toString());
                    Log.d("uu","uu");
                    try {
                        String id = mainObject.getString("sender");
                        String sid = mainObject.getString("sess_id");
                        String data=mainObject.getString("data");
                        String fname=mainObject.getString("filename");
                        String date=mainObject.getString("created_at");


                        SimpleDateFormat gg=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        //gg.setTimeZone(Calendar.getInstance().getTimeZone());
                        SimpleDateFormat fd=new SimpleDateFormat("HH:mm");
                        Date dateobj;
                        String ttime;
                        dateobj =gg.parse(date);
                        ttime=fd.format(dateobj);

                        final File dwldsPath = new File("/sdcard/Download" + fname);
                        byte[] pdfAsBytes = Base64.decode(data, 0);
                        FileOutputStream os;
                        os = new FileOutputStream(dwldsPath, false);
                        os.write(pdfAsBytes);
                        os.flush();
                        os.close();

                        User usr = (User) new User("Calden", "des", "dep", id);
                        Message temp =(Message) new Message("data", usr, ttime,null);
                        messageList.add(temp);
                        mMessageAdapter.notifyDataSetChanged();
                        mMessageRecycler.scrollToPosition(messageList.size()-1);

                    } catch (Exception e) {
                        Log.d("yomom",e.toString());
                    }

                }
            });
        }
    };




    @Override
        public boolean onCreateOptionsMenu(Menu menu) {

            getMenuInflater().inflate(R.menu.main2, menu);

            MenuItem myMenuItem = menu.findItem(R.id.myprofile);

        return true;
    }

    class GetData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder rs = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                InputStream input = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                String Line;

                Line = br.readLine();
                rs.append(Line);

            }
            catch(IOException ex) {
                return ex.toString();
            }
            return rs.toString();


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray jarr = new JSONArray(s);
                JSONObject mainObject;
                messageList.clear();
                for(int i=0; i<jarr.length(); i++) {
                    mainObject = jarr.getJSONObject(i);
                    String id = mainObject.getString("sender");
                    String message = mainObject.getString("body");
                    String ses=mainObject.getString("sess_id");
                    String des=mainObject.getString("send_des");
                    String dep=mainObject.getString("send_dep");
                    String name=mainObject.getString("send_name");
                    String time=mainObject.getString("created_at");

                    SimpleDateFormat gg=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    //gg.setTimeZone(Calendar.getInstance().getTimeZone());
                    SimpleDateFormat fd=new SimpleDateFormat("HH:mm");
                    Date dateobj;
                    String ttime;
                    dateobj =gg.parse(time);
                    ttime=fd.format(dateobj);

                    User usr = (User) new User(name, des, dep, id);
                    Message temp =(Message) new Message(message, usr, ttime,null);
                    messageList.add(temp);
                    mMessageAdapter.notifyDataSetChanged();
                    mMessageRecycler.scrollToPosition(messageList.size()-1);
                    //Log.d("hi",messageList.get(1).getText());
                }

                //stringbox.setText(name+"    "+message);
            }
            catch (Exception ex){
                Log.d("GG END MID", ex.toString());
            }

        }
    }

    public class PostData extends AsyncTask<String, Void, String>{



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                return postData(params[0]);
            }
            catch (Exception e){
                Log.d("prat", e.toString());
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        public String postData(String urlpath) throws IOException,JSONException{


            JSONObject datatosend=new JSONObject();
            JSONArray temp=new JSONArray();
            datatosend.put("sender",id);
            datatosend.put("sess_id",value);
            datatosend.put("body",text);

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

    public class PostDataimage extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                return postData(params[0]);
            }
            catch (Exception e){
                Log.d("prat", e.toString());
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        public String postData(String urlpath) throws IOException,JSONException{


            JSONObject datatosend=new JSONObject();
            JSONArray temp=new JSONArray();
            datatosend.put("fileName",fname);
            datatosend.put("data",encodedImage);
            datatosend.put("sid",value);
            datatosend.put("id",id);

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

    private class DownloadFilesTask extends AsyncTask<URL, Void, List<String[]>> {
        protected List<String[]> doInBackground(URL... urls) {
            return downloadRemoteTextFileContent();
        }
        protected void onPostExecute(List<String[]> result) {
            if(result != null){
                Log.d("gg",result.toString());
            }
        }
    }

    private List<String[]> downloadRemoteTextFileContent(){
        URL mUrl = null;
        List<String[]> csvLine = new ArrayList<>();
        String[] content = null;
        try {
            mUrl = new URL("http://");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            assert mUrl != null;
            URLConnection connection = mUrl.openConnection();
            BufferedReader br = new BufferedReader(new
                    InputStreamReader(connection.getInputStream()));
            String line = "";
            while((line = br.readLine()) != null){
                content = line.split(",");
                csvLine.add(content);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvLine;
    }

}

