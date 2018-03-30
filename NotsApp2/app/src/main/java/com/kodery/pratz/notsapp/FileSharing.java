package com.kodery.pratz.notsapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

/**
 * Created by calden on 30/3/18.
 */
/*
public class FileSharing extends Activity {
    TextView textFile;

    private static final int PICKFILE_RESULT_CODE = 1;

    /** Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button buttonPick = (Button)findViewById(R.id.buttonpick);
        textFile = (TextView)findViewById(R.id.textfile);

        buttonPick.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent,PICKFILE_RESULT_CODE);

            }});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch(requestCode){
            case PICKFILE_RESULT_CODE:
                if(resultCode==RESULT_OK){
                    String FilePath = data.getData().getPath();
                    textFile.setText(FilePath);
                }
                break;

        }
    }
    String path="File Path";
    Intent intent = new Intent();
    intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(path);

    intent.setData(Uri.fromFile(file));

        startActivity(intent);

}
*/