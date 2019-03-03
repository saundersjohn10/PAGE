package com.example.johnsaunders.drawer3;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    private ProgressBar progressBarCircle;
    private TextView textViewTime;
    private CountDownTimer countDownTimer;
    private ImageView materButton;
    private TextView alertTxt;
    private TextView calling911Txt;
    private Button call911NowBtn;
    private Button safeNowBtn;
    private MediaRecorder audioRecoding = new MediaRecorder();
    private int timeToCall911;

    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;

    private long timeCountInMilliSeconds = 5 * 60000;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpDrawer();
        initViews();
        initBut();
        getSupportActionBar().setTitle("PAGE");
    }



    /*-----Initalize methods------*/
    private void initBut(){

        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                materButton,
                PropertyValuesHolder.ofFloat("scaleX", 1.05f),
                PropertyValuesHolder.ofFloat("scaleY", 1.05f));
        scaleDown.setDuration(1000);

        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();

        materButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sendMessage("14349446498",myPreferences.getString("message","please send help :("));
                startStop();
                doPhoneCall();
                startRecording();
            }
        });

        safeNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkPasscode();
            }
        });
    }

    public void startRecording(){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, 1);
        if(checkAudioPermission()) {

            File f = new File(Environment.getExternalStorageDirectory() + "/MyRecordings");
            if(f.isDirectory()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateandTime = sdf.format(new Date());
                AudioSavePathInDevice="/sdcard/MyRecordings/"+ currentDateandTime + ".mp3";
            }else{

                File wallpaperDirectory = new File("/sdcard/MyRecordings/");
                wallpaperDirectory.mkdirs();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateandTime = sdf.format(new Date());
                AudioSavePathInDevice =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + currentDateandTime + ".mp3";
            }

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setOutputFile(AudioSavePathInDevice);

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("not valid");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, 1);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case 1:
//                if (grantResults.length> 0) {
//                    boolean StoragePermission = grantResults[0] ==
//                            PackageManager.PERMISSION_GRANTED;
//                    boolean RecordPermission = grantResults[1] ==
//                            PackageManager.PERMISSION_GRANTED;
//                }
//                break;
//        }
//    }

    public void endRecording(){
        mediaRecorder.stop();
    }

    public boolean checkAudioPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }


    private void initViews() {

        progressBarCircle = findViewById(R.id.progressBarCircle);
        textViewTime = findViewById(R.id.textViewTime);
        materButton = findViewById(R.id.master_button);
        alertTxt = findViewById(R.id.alert_text);
        calling911Txt = findViewById(R.id.calling_911_txt);
        call911NowBtn = findViewById(R.id.alert_911_now_btn);
        safeNowBtn = findViewById(R.id.safe_now_btn);

        call911NowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });
    }

    /*------------Messaging-----------*/
    private boolean messagingSetUp(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        } else {
            return true;
        }
        return false;

    }

    public void sendMessage(String number, String text){

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String latitude = String.format("%.5f",location.getLatitude());
            String longitude = String.format("%5.f",location.getLongitude());
            text += " I was last at (" + latitude + "," + longitude + ")";
            System.out.println("here444");
        }
        catch(SecurityException e){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        catch(Exception e){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }

        if(messagingSetUp()){
            try {
                String latitude ="38.031979";
                String longitude="-78.511192";
                text +=  "I was last at (" + latitude + "," + longitude + ")";
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, text, null, null);
            } catch(Exception e1){
            }
        }

    }


    /*----------Make phone calls-------*/
    public void makePhoneCall(){
        String number = "4349446498";
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else{
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    public void doPhoneCall(){

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(progressBarCircle.getVisibility() == View.VISIBLE){
                    makePhoneCall();
                    endAlert();
                    endRecording();
                }
            }
        }, timeCountInMilliSeconds + 1);
    }


    /*----------Progress Bar---------*/
    public void endAlert(){
        findViewById(R.id.content_main).setBackgroundColor(Color.parseColor("#FFFFFF"));
        progressBarCircle.setVisibility(View.INVISIBLE);
        materButton.setVisibility(View.VISIBLE);
        alertTxt.setVisibility(View.VISIBLE);
        calling911Txt.setVisibility(View.INVISIBLE);
        call911NowBtn.setVisibility(View.INVISIBLE);
        call911NowBtn.setBackgroundColor(Color.RED);
        safeNowBtn.setVisibility(View.INVISIBLE);
        textViewTime.setVisibility(View.INVISIBLE);
        stopCountDownTimer();
        getSupportActionBar().show();
    }

    private void checkPasscode(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("Enter passcode to end");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        input.setTextColor(Color.WHITE);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String entered = input.getText().toString();
                endAlert();
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

    private void startStop() {

        //set the different views
        findViewById(R.id.content_main).setBackgroundColor(Color.parseColor("#151B29"));

        materButton.setVisibility(View.INVISIBLE);
        alertTxt.setVisibility(View.INVISIBLE);
        calling911Txt.setVisibility(View.VISIBLE);
        call911NowBtn.setVisibility(View.VISIBLE);
        safeNowBtn.setVisibility(View.VISIBLE);
        progressBarCircle.setVisibility(View.VISIBLE);
        textViewTime.setVisibility(View.VISIBLE);
        getSupportActionBar().hide();

        if (timerStatus == TimerStatus.STOPPED) {
            setTimerValues();
            setProgressBarValues();
            timerStatus = TimerStatus.STARTED;
            startCountDownTimer();

        } else {
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();
            endAlert();
        }

    }

    private void setTimerValues() {

        int time = 5;
        timeCountInMilliSeconds = time * 60 * 1000;
        //timeToCall911 = 6000;
        //timeCountInMilliSeconds = 6000;
    }

    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                setProgressBarValues();
                timerStatus = TimerStatus.STOPPED;
            }

        }.start();
        countDownTimer.start();
    }

    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    private void setProgressBarValues() {
        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }

    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
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
            // Handle the camera action
        } else if (id == R.id.contacts) {
            startActivity(new Intent(getApplicationContext(),contacts.class));
        } else if (id == R.id.message) {
            startActivity(new Intent(getApplicationContext(),Message.class));
        } else if (id == R.id.recordings) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            Uri uri = Uri.parse("/My_Files/Internal_Storage/MyRecordings");
            System.out.println(uri);
            intent.setDataAndType(uri, "*/*");
            startActivity(Intent.createChooser(intent, "Open"));
        } else if (id == R.id.settings) {
            startActivity(new Intent(getApplicationContext(),settings.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
