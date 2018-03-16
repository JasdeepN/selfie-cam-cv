package uoit.cv.selfiecam;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import uoit.cv.selfiecam.util.PermissionChecker;


public class Main extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
    }

    @Override
    protected void onStart(){
        new PermissionChecker(getBaseContext(), this).getPermissions();
        super.onStart();
    }


    public void run(View v){
        Intent cam = new Intent(this, Camera.class);
        startActivityForResult(cam, 0);
    }

}
