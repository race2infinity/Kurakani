package com.kodery.pratz.notsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by calden on 30/3/18.
 */

public class FileTry extends AppCompatActivity {

    int SELECT_PICTURE = 101;
    int CAPTURE_IMAGE = 102;
    Button getImage;
    ImageView selectedImage;
    String encodedImage;
    JSONObject jsonObject;
    JSONObject Response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_try);
        getImage = (Button) findViewById(R.id.get_image);
        selectedImage = (ImageView) findViewById(R.id.selected_image);

        final AlertDialog.Builder builder = new AlertDialog.Builder(FileTry.this);
        builder.setTitle("Profile Picture");
        builder.setMessage("Chooose from?");
        builder.setPositiveButton("GALLERY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });
        builder.setNegativeButton("CANCEL", null );

        builder.show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == SELECT_PICTURE) {
            // Make sure the request was successful
            Log.d("Vicky","I'm out.");
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                Bitmap selectedImageBitmap = null;
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                selectedImage.setImageBitmap(selectedImageBitmap);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
                encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                Log.d("Vicky","I'm in.");
                new UploadImages().execute();
            }
        }
    }

    private class UploadImages extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Log.d("Vicky", "encodedImage = " + encodedImage);
                jsonObject = new JSONObject();
                jsonObject.put("imageString", encodedImage);
                jsonObject.put("imageName", "+917358513024");
                String data = jsonObject.toString();
                String yourURL = "http://54.169.88.65/events/eventmain/upload_image.php";
                URL url = new URL(yourURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setFixedLengthStreamingMode(data.getBytes().length);
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(data);
                Log.d("Vicky", "Data to php = " + data);
                writer.flush();
                writer.close();
                out.close();
                connection.connect();

                InputStream in = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        in, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                in.close();
                String result = sb.toString();
                Log.d("Vicky", "Response from php = " + result);
                //Response = new JSONObject(result);
                connection.disconnect();
            } catch (Exception e) {
                Log.d("Vicky", "Error Encountered");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

        }
    }
}