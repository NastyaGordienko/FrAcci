package com.example.fracci;

import static com.example.fracci.crash.GeneralData.car;

import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fracci.crash.Car;
import com.example.fracci.crash.Crash;
import com.example.fracci.crash.GeneralData;
import com.example.fracci.server.Coords;
import com.example.fracci.server.CoordsInterface;
import com.example.fracci.server.FirstOpenInstruction;
import com.example.fracci.server.HomeMenu;
import com.example.fracci.server.Server;
import com.example.fracci.server.ServerConnection;
import com.example.fracci.server.SettingsMenu;
import com.example.fracci.server.WelcomeMenuPage;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.auth.VKAuthenticationResult;
import com.vk.api.sdk.auth.VKScope;
import com.vk.api.sdk.exceptions.VKAuthException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {



    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    SettingsMenu setM=new SettingsMenu();
    HomeMenu homeM=new HomeMenu();


    NavigationView navigationView;

    NavigationView bottomNavigationView;
    private View view;


    FirstOpenInstruction firstOpenInstruction;
    private Toolbar toolbar;
    ImageView mImageView;





    Boolean flag=false;

    Car car;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  VK.login(new MainActivity(), new ArrayList<>());
        //String[] fingerprints = VKUtils.getCertificateFingerprint(this, this.getPackageName());



        //Объявляем элементы дизайна(кнопки,картинки)----------------------------------------------------------
        Button btn_start=findViewById(R.id.btn_start);
        mImageView = (ImageView) findViewById(R.id.imageView1);
        mImageView.setImageResource(R.drawable.main_img_2);



        //Объявляем toolbar & navDrawer-----------------------------------------------------------------------
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);

        //Подключаем toolbar+navDrawer------------------------------------------------------------------------
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.nav_open, R.string.nav_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Вынуждаем клиента согласиться с разрешениями--------------------------------------------------------
        while (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        }
        startService(new Intent(getApplicationContext(), MainService.class));


//Серверочик----------------------------------------------------------------------------------------------
//        btn_start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Call<LinkedList<Coords>> call=serv.test(text_first.getText().toString(),text_last.getText().toString());
//                call.enqueue(new Callback<LinkedList<Coords>>() {
//                    @Override
//                    public void onResponse(Call<LinkedList<Coords>> call, Response<LinkedList<Coords>> response) {
//                        LinkedList<Coords> list=response.body();
//                        Toast.makeText(MainActivity.this, ""+list.getLast().lat+" "+list.getLast().lng, Toast.LENGTH_LONG).show();
//                    }
//                    //http://192.168.0.160:8080/java/test
//                    @Override
//                    public void onFailure(Call<LinkedList<Coords>> call, Throwable t) {
//                        Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      VKAuthCallback   callback=new VKAuthCallback() {
          @Override
          public void onLogin(@NonNull VKAccessToken vkAccessToken) {
              //user passed authorization
          }

          @Override
          public void onLoginFailed(@NonNull VKAuthException e) {
//user fail authorization
          }
      };
      if(data==null || !VK.onActivityResult(requestCode,resultCode,data,callback)){
            super.onActivityResult(requestCode, resultCode, data);
        }

    }


//Методы для интерфейса/дизайна navDrawer--------------------------------------------------------------
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;//or super.onOptionsItemSelected, false won't show menu
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
                switch (item.getItemId()) {
                    case R.id.nav_settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.place_holder, setM).commit();
                        return true;

                    case R.id.nav_back_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.place_holder, homeM).commit();
                        return true;


                }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





}