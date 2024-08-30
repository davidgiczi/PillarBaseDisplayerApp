package com.david.giczi.pillarbasedisplayerapp;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.david.giczi.pillarbasedisplayerapp.databinding.ActivityMainBinding;
import com.david.giczi.pillarbasedisplayerapp.service.AzimuthAndDistance;
import com.david.giczi.pillarbasedisplayerapp.service.Point;
import com.david.giczi.pillarbasedisplayerapp.utils.EOV;
import com.david.giczi.pillarbasedisplayerapp.utils.WGS84;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private AppBarConfiguration appBarConfiguration;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ViewGroup gpsDataContainer;
    private ViewGroup northPoleContainer;
    public static PopupWindow gpsDataWindow;
    public static PopupWindow northPoleWindow;
    private SensorManager sensorManager;
    private Sensor sensor;
    private static final int REQUEST_LOCATION = 1;
    public ActivityMainBinding binding;
    public static Menu MENU;
    public static List<String> BASE_DATA;
    public static final String[] BASE_TYPE = {"#WeightBase", "#PlateBase"};
    public static boolean IS_WEIGHT_BASE = true;
    public static int PAGE_COUNTER;
    public static List<Point> PILLAR_BASE_COORDINATES;
    public static boolean IS_SAVE_RTK_FILE;
    public static boolean IS_SAVE_TPS_FILE;
    public static boolean IS_GPS_RUNNING;
    private static float northPoleDirection;
    private int previousPillarDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        PAGE_COUNTER = 0;
        BASE_DATA = new ArrayList<>();
        if( ContextCompat
                .checkSelfPermission(MainActivity.this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ActivityCompat
               .requestPermissions(MainActivity.this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
            }
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    private void stopMeasure(){
        if( gpsDataWindow != null ){
            gpsDataWindow.dismiss();
        }
        gpsDataWindow = null;
        gpsDataContainer = null;
        if( northPoleWindow != null ){
            northPoleWindow.dismiss();
        }
        northPoleWindow = null;
        northPoleContainer = null;
        MainActivity.this.locationManager.removeUpdates(locationListener);
        sensorManager.unregisterListener(this);
        Toast.makeText(MainActivity.this, "GPS leállítva.", Toast.LENGTH_SHORT).show();
    }

    private void startMeasure(){

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                double X_WGS = Double.parseDouble(WGS84.getX(location.getLatitude(),
                        location.getLongitude(),
                        location.getAltitude()).substring(0, WGS84.getX(location.getLatitude(),
                        location.getLongitude(),
                        location.getAltitude()).indexOf("m")));
                double Y_WGS = Double.parseDouble(WGS84.getY(location.getLatitude(),
                        location.getLongitude(),
                        location.getAltitude()).substring(0, WGS84.getY(location.getLatitude(),
                        location.getLongitude(),
                        location.getAltitude()).indexOf("m")));
                double Z_WGS = Double.parseDouble(WGS84.getZ(location.getLatitude(),
                        location.getAltitude()).substring(0, WGS84.getZ(location.getLatitude(),
                        location.getAltitude()).indexOf("m")));
                EOV eov = new EOV(X_WGS, Y_WGS, Z_WGS);
                popupGPSData();
                showNorthSign();
                showPillarDistanceAndDirection(eov);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                LocationListener.super.onProviderDisabled(provider);
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }

        sensorManager.registerListener(this, sensor,
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        MainActivity.this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                100, 0, locationListener);

      Toast.makeText(MainActivity.this, "GPS elindítva.", Toast.LENGTH_SHORT).show();
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
    }

    private void popupGPSData() {
        if ( gpsDataContainer == null && northPoleContainer == null ) {
            gpsDataContainer = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_gps_data, null);
            gpsDataWindow = new PopupWindow(gpsDataContainer, 1000, 500, false);
            northPoleContainer = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_north_pole, null);
            northPoleWindow = new PopupWindow(northPoleContainer, 500, 360, false);
            if( PAGE_COUNTER == 3 ) {
                northPoleWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, -630);
                gpsDataWindow.showAtLocation( binding.getRoot(), Gravity.CENTER, 0, 800);
            }
        }
    }

    private void showPillarDistanceAndDirection(EOV eov){
        TextView gpsDataView = gpsDataContainer.findViewById(R.id.actual_position);
        gpsDataView.setText(eov.toString());
        AzimuthAndDistance pillarData = new AzimuthAndDistance(
                new Point("position", eov.getCoordinatesForEOV().get(0),
                        eov.getCoordinatesForEOV().get(1)),
                new Point("center", Double.parseDouble(BASE_DATA.get(2)), Double.parseDouble(BASE_DATA.get(3))));
        double direction = 0 > Math.toDegrees(pillarData.calcAzimuth()) - northPoleDirection ?
                Math.toDegrees(pillarData.calcAzimuth()) - northPoleDirection + 360 :
                Math.toDegrees(pillarData.calcAzimuth()) - northPoleDirection;
        addPillarDirectionArrowImage((float) direction, (int) Math.round(pillarData.calcDistance()) );
        String pillarDirectionAndDistance = "Irány: "  + String.format(Locale.getDefault(),"%5.1f°", direction) +
          "\t\tTávolság: " + String.format(Locale.getDefault(),"%5.0fm", pillarData.calcDistance());
        TextView pillarDataView = gpsDataContainer.findViewById(R.id.pillar_direction_and_distance);
        pillarDataView.setText(pillarDirectionAndDistance);
    }
    private void showNorthSign(){
        ImageView northPoleView = northPoleContainer.findViewById(R.id.north_pole);
        northPoleView.setRotation(- northPoleDirection);
        northPoleView.setImageResource(R.drawable.north);
    }

    private void addPillarDirectionArrowImage(float rotation, int distance){
        ImageView pillarArrowImageView = gpsDataContainer.findViewById(R.id.pillar_direction_arrow_image);
        if( distance > previousPillarDistance  ){
            pillarArrowImageView.setImageResource(R.drawable.red_arrow_up);
        }
        else{
            pillarArrowImageView.setImageResource(R.drawable.green_arrow_up);
        }
        pillarArrowImageView.setRotation(rotation);
        previousPillarDistance = distance;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        MENU = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.exit_option) {
            exitDialog();
        } else if (id == R.id.input_data) {
            openPillarBaseDataFile();
        } else if (id == R.id.goto_next_fragment) {
           gotoNextFragment();
        }
        else if( id == R.id.weight_base
                && Objects.requireNonNull(item.getTitle()).toString().equals(getString(R.string.weight_base_option))){
            item.setTitle(R.string.ticked_weight_base_option);
            MENU.findItem(R.id.plate_base).setTitle(R.string.plate_base_option);
            getDataFragmentForWeightBase();
            IS_WEIGHT_BASE = true;
        }
        else if( id == R.id.plate_base
                && Objects.requireNonNull(item.getTitle()).toString().equals(getString(R.string.plate_base_option))){
            item.setTitle(R.string.ticked_plate_base_option);
            MENU.findItem(R.id.weight_base).setTitle(R.string.weight_base_option);
            getDataFragmentForPlateBase();
            IS_WEIGHT_BASE = false;
        }
        else if( id == R.id.calc_foot_distance){
                popupPillarFootDistanceCalculator();
        }
        else if( id == R.id.start_stop_gps ){

            if( IS_GPS_RUNNING ){
                stopMeasure();
                MENU.findItem(R.id.start_stop_gps).setTitle(R.string.start_gps);
            }
            else {
                startMeasure();
                 MENU.findItem(R.id.start_stop_gps).setTitle(R.string.stop_gps);
            }

            IS_GPS_RUNNING = !IS_GPS_RUNNING;
        }
            return super.onOptionsItemSelected(item);
    }


    private void popupPillarFootDistanceCalculator(){
        ViewGroup container = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_foot_calc, null);
        PopupWindow footCalcWindow = new PopupWindow(container, 1000, 700, true);
        footCalcWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, -400);
        Button calcButton = container.findViewById(R.id.btn_count);
        calcButton.setOnClickListener(v -> {
            EditText inputFootDistance = container.findViewById(R.id.input_value_of_foot_distance);
            if( inputFootDistance.getText().toString().isEmpty() ){
                Toast.makeText(container.getContext(), "A lábtávolság értékének megadása szükséges.",
                        Toast.LENGTH_LONG).show();
                return;
            }
            EditText inputIllesztesiSik = container.findViewById(R.id.input_value_illesztesi_sik);
            if( inputIllesztesiSik.getText().toString().isEmpty() ){
                Toast.makeText(container.getContext(), "Az illesztési sík értékének megadása szükséges.",
                        Toast.LENGTH_LONG).show();
                return;
            }
            EditText inputSudarasodas = container.findViewById(R.id.input_value_sudarasodas);
            if( inputSudarasodas.getText().toString().isEmpty() ){
                Toast.makeText(container.getContext(), "A sudarasodás értékének megadása szükséges.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            double footDistance = Double.parseDouble(inputFootDistance.getText().toString()) / 1000.0 +
                    (2 * Double.parseDouble(inputSudarasodas.getText().toString() )
                            * Double.parseDouble(inputIllesztesiSik.getText().toString()) / 100.0) / 1000.0;
          ((TextView) container.findViewById(R.id.text_calc_foot_distance)).setText(R.string.value_of_foot_distance);
           TextView resultFootDistance = container.findViewById(R.id.result_foot_distance);
           resultFootDistance.setText(String.format(Locale.getDefault(), "%.3fm", footDistance));
        });
    }


    private void gotoNextFragment(){
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            switch (PAGE_COUNTER % 5){
                case 0 :
                    navController.navigate(R.id.action_StartFragment_to_MeasDataFragment);
                    break;
                case 1 :
                    navController.navigate(R.id.action_MeasDataFragment_to_DataFragment);
                    break;
                case 2 :
                    saveDialog(navController);
                    break;
                case 3 :
                    navController.navigate(R.id.action_CoordsFragment_to_BaseFragment);
                    break;
                case 4 :
                    navController.navigate(R.id.action_BaseFragment_to_StartFragment);
                    break;
            }
    }

    private void getDataFragmentForPlateBase(){
        EditText inputForDirectionDistance = findViewById(R.id.input_distance_of_direction_points);
        if( inputForDirectionDistance == null){
            return;
        }
        inputForDirectionDistance.setVisibility(View.INVISIBLE);
        EditText inputForPerpendicularlyFootDistance = findViewById(R.id.input_foot_distance_perpendicularly);
        inputForPerpendicularlyFootDistance.setHint(R.string.distance_from_side_of_hole_of_base_perpendicularly);
        EditText inputForParallelFootDistance = findViewById(R.id.input_foot_distance_parallel);
        inputForParallelFootDistance.setHint(R.string.distance_from_side_of_hole_of_base_parallel);
    }

    private void getDataFragmentForWeightBase(){
        EditText inputForDirectionDistance = findViewById(R.id.input_distance_of_direction_points);
        if( inputForDirectionDistance == null){
            return;
        }
        inputForDirectionDistance.setVisibility(View.VISIBLE);
        EditText inputForPerpendicularlyFootDistance = findViewById(R.id.input_foot_distance_perpendicularly);
        inputForPerpendicularlyFootDistance.setHint(R.string.distance_of_legs_perpendicularly);
        EditText inputForParallelFootDistance = findViewById(R.id.input_foot_distance_parallel);
        inputForParallelFootDistance.setHint(R.string.distance_of_legs_parallel);
    }

    private void exitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Alkalmazás bezárása");
        builder.setMessage("Biztos, hogy ki akarsz lépni az alkalmazásból?\n\nA nem mentett adatok elvesznek.");

        builder.setPositiveButton("Igen", (dialog, which) -> {
            dialog.dismiss();
           System.exit(0);
        });

        builder.setNegativeButton("Nem", (dialog, which) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.show();
    }
    private void saveDialog(NavController navController) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Adatok mentése");
        builder.setMessage("Kivánja menteni az adatokat?");

        builder.setPositiveButton("Igen", (dialog, which) -> {
            if( isValidInputData() ){
                getDataFromDataFragment();
                saveProjectFile();
                navController.navigate(R.id.action_DataFragment_to_CoordsFragment);
            }
            dialog.dismiss();
        });
        builder.setNegativeButton("Nem", (dialog, which) -> {
            if( isValidInputData() ) {
                getDataFromDataFragment();
                if( IS_SAVE_RTK_FILE ){
                    IS_SAVE_RTK_FILE = false;
                }
                if( IS_SAVE_TPS_FILE ){
                    IS_SAVE_TPS_FILE = false;
                }
                navController.navigate(R.id.action_DataFragment_to_CoordsFragment);
            }
            dialog.dismiss();
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

   @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)|| super.onSupportNavigateUp();
    }

    private void openPillarBaseDataFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(
                Intent.createChooser(intent, "Choose a file"), 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                String path = Objects.requireNonNull(uri).getPath();
                path =  Objects.requireNonNull(path).substring(path.indexOf(":") + 1);
                readProjectFile(path);
            }
        }
    }
    private void readProjectFile(String path){
        BASE_DATA.clear();
        File projectFile = new File(Environment.getExternalStorageDirectory(), path);
        try{
            BufferedReader br = new BufferedReader(
                    new FileReader(projectFile));
            String line;
            while((line = br.readLine()) != null){
                BASE_DATA.add(line);
            }
            br.close();
        }catch (IOException e){
            e.printStackTrace();
        }finally {

            if( !BASE_DATA.get(0).equals(BASE_TYPE[0])
                    && !BASE_DATA.get(0).equals(BASE_TYPE[1]) ){
                BASE_DATA.clear();
            }
            else if( BASE_DATA.get(0).equals(BASE_TYPE[0]) ){
                IS_WEIGHT_BASE = true;
                MENU.findItem(R.id.weight_base).setTitle(R.string.ticked_weight_base_option);
                MENU.findItem(R.id.plate_base).setTitle(R.string.plate_base_option);
            }
            else if( BASE_DATA.get(0).equals(BASE_TYPE[1]) ){
                IS_WEIGHT_BASE = false;
                MENU.findItem(R.id.weight_base).setTitle(R.string.weight_base_option);
                MENU.findItem(R.id.plate_base).setTitle(R.string.ticked_plate_base_option);
            }
        }
        gotoDataFragment();
        if( BASE_DATA.isEmpty() ){
            Toast.makeText(this, "Az adatok beolvasása sikertelen.",
                    Toast.LENGTH_LONG).show();
        }
        else {
            setTitle(projectFile.getName());
            Toast.makeText(this, "Az adatok sikeresen beolvasva.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setTitle(String projectFileName){
        String projectName = projectFileName.substring(0, projectFileName.indexOf("."));
        TextView title = findViewById(R.id.projectNameTitle);
        title.setText(projectName);
    }

    private void gotoDataFragment(){
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        switch (PAGE_COUNTER % 5){
            case 0 :
                navController.navigate(R.id.action_StartFragment_to_DataFragment);
                break;
            case 1 :
                navController.navigate(R.id.action_MeasDataFragment_to_DataFragment);
                break;
            case 2 :
                navController.navigate(R.id.action_DataFragment_to_StartFragment);
                break;
            case 3 :
                navController.navigate(R.id.action_CoordsFragment_to_DataFragment);
                break;
            case 4 :
                navController.navigate(R.id.action_BaseFragment_to_DataFragment);
                break;
        }

    }

    private void saveProjectFile() {
        String fileName = ((TextView) findViewById(R.id.projectNameTitle)).getText().toString();
        File projectFile =
                new File(Environment.getExternalStorageDirectory(),
                        "/Documents/" + fileName + ".txt");
        try {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(projectFile));
            for (String data : BASE_DATA) {
                bw.write(data);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            Toast.makeText(this, projectFile.getName() +
                    "\nprojekt fájl mentése sikertelen.", Toast.LENGTH_SHORT).show();
            return;
        }
            Toast.makeText(this,
                    "Projekt fájl mentve:\n"
                            + projectFile.getName() , Toast.LENGTH_SHORT).show();
    }

    private boolean isValidInputData(){
        if(((TextView) findViewById(R.id.projectNameTitle)).getText().toString().isEmpty() ){
            Toast.makeText(this, "Projeknév megadása szükséges.", Toast.LENGTH_LONG).show();
            return false;
        }
        else if( ((EditText) findViewById(R.id.input_pillar_id)).getText().toString().isEmpty() ){
            Toast.makeText(this, "Az oszlop azonosítójának megadása szükséges.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        else if(((EditText) findViewById(R.id.input_y_coordinate)).getText().toString().isEmpty() ){
            Toast.makeText(this, "Az oszlop Y koordinátájának megadása szükséges.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        else if(((EditText) findViewById(R.id.input_x_coordinate)).getText().toString().isEmpty()){
            Toast.makeText(this, "Az oszlop X koordinátájának megadása szükséges.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        else if(((EditText) findViewById(R.id.input_next_prev_pillar_id)).getText().toString().isEmpty()){
            Toast.makeText(this, "Az előző/következő oszlop azonosítójának megadása szükséges.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        else if(((EditText) findViewById(R.id.input_next_prev_y_coordinate)).getText().toString().isEmpty()){
            Toast.makeText(this, "Az előző/következő oszlop Y koordinátájának megadása szükséges.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        else if(((EditText) findViewById(R.id.input_next_prev_x_coordinate)).getText().toString().isEmpty()){
            Toast.makeText(this, "Az előző/következő oszlop X koordinátájának megadása szükséges.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if( IS_WEIGHT_BASE ){

            if(((EditText) findViewById(R.id.input_distance_of_direction_points)).getText().toString().isEmpty()){
                Toast.makeText(this, "Az iránypontok távolságának megadása szükséges.",
                        Toast.LENGTH_LONG).show();
                return false;
            }
            else if(((EditText) findViewById(R.id.input_foot_distance_perpendicularly)).getText().toString().isEmpty()){
                Toast.makeText(this, "A karra merőleges lábtávolság megadása szükséges.",
                        Toast.LENGTH_LONG).show();
                return false;
            }
            else if(((EditText) findViewById(R.id.input_foot_distance_parallel)).getText().toString().isEmpty()){
                Toast.makeText(this, "A karral párhuzamos lábtávolság megadása szükséges.",
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }
        else {

            if(((EditText) findViewById(R.id.input_foot_distance_perpendicularly)).getText().toString().isEmpty()){
                Toast.makeText(this, "A karra merőleges irány, " +
                                "a gödör szélétől vett távolságának megadása szükséges.",
                        Toast.LENGTH_LONG).show();
                return false;
            }
            else if(((EditText) findViewById(R.id.input_foot_distance_parallel)).getText().toString().isEmpty()){
                Toast.makeText(this, "A karral párhuzamos irány, a gödör szélétől vett " +
                                "távolságának megadása szükséges.",
                        Toast.LENGTH_LONG).show();
                return false;
            }

        }

        if(((EditText) findViewById(R.id.input_hole_distance_perpendicularly)).getText().toString().isEmpty()){
            Toast.makeText(this, "A karra merőleges gödör oldalhosszának megadása szükséges.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        else if(((EditText) findViewById(R.id.input_hole_distance_parallel)).getText().toString().isEmpty()){
            Toast.makeText(this, "A karral párhuzamos gödör oldalhosszának megadása szükséges.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        else if(!((RadioButton)findViewById(R.id.radio_left)).isChecked() &&
                !((RadioButton)findViewById(R.id.radio_right)).isChecked()){
            
                Toast.makeText(this, "A nyomvonal által közbezárt szög bal/jobb helyének megadása szükséges.",
                        Toast.LENGTH_LONG).show();
                return false;
        }
        else if(((EditText) findViewById(R.id.input_angle)).getText().toString().isEmpty()){
            Toast.makeText(this, "A nyomvonal által közbezárt szög értékének megadása szükséges.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        else if(((EditText) findViewById(R.id.input_min)).getText().toString().isEmpty()){
            Toast.makeText(this, "A nyomvonal által közbezárt szög perc értékének megadása szükséges.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        else if(((EditText) findViewById(R.id.input_sec)).getText().toString().isEmpty() ){
            Toast.makeText(this, "A nyomvonal által közbezárt szög másodperc értékének megadása szükséges.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        else if(Integer.parseInt(((EditText) findViewById(R.id.input_angle)).getText().toString()) > 359){
            Toast.makeText(this, "A nyomvonal által közbezárt szög fok értéke 360-nál kisebb lehet.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        else if(Integer.parseInt(((EditText) findViewById(R.id.input_min)).getText().toString()) > 59){
            Toast.makeText(this, "A nyomvonal által közbezárt szög perc értéke 60-nál kisebb lehet.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        else if(Integer.parseInt(((EditText) findViewById(R.id.input_sec)).getText().toString()) > 59 ) {
            Toast.makeText(this, "A nyomvonal által közbezárt szög másodperc értéke 60-nál kisebb lehet.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void getDataFromDataFragment(){
        if( BASE_DATA == null ){
            BASE_DATA = new ArrayList<>();
        }
        else if( !BASE_DATA.isEmpty() ){
            BASE_DATA.clear();
        }
        if( IS_WEIGHT_BASE ){
            BASE_DATA.add(BASE_TYPE[0]);
        } else {
            BASE_DATA.add(BASE_TYPE[1]);
        }
        BASE_DATA.add(((EditText) findViewById(R.id.input_pillar_id)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_y_coordinate)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_x_coordinate)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_next_prev_pillar_id)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_next_prev_y_coordinate)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_next_prev_x_coordinate)).getText().toString());
        if( IS_WEIGHT_BASE ){
        BASE_DATA.add(((EditText) findViewById(R.id.input_distance_of_direction_points)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_foot_distance_perpendicularly)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_foot_distance_parallel)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_hole_distance_perpendicularly)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_hole_distance_parallel)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_angle)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_min)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_sec)).getText().toString());
   }
        else {
        BASE_DATA.add(((EditText) findViewById(R.id.input_hole_distance_perpendicularly)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_hole_distance_parallel)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_foot_distance_perpendicularly)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_foot_distance_parallel)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_angle)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_min)).getText().toString());
        BASE_DATA.add(((EditText) findViewById(R.id.input_sec)).getText().toString());
   }
        if(((RadioButton) findViewById(R.id.radio_right)).isChecked()){
            BASE_DATA.add("0");
        }
        else if(((RadioButton) findViewById(R.id.radio_left)).isChecked()){
            BASE_DATA.add("1");
        }

        IS_SAVE_RTK_FILE = ((CheckBox) findViewById(R.id.save_rtk_format)).isChecked();
        IS_SAVE_TPS_FILE = ((CheckBox) findViewById(R.id.save_tps_format)).isChecked();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMeasure();
        System.exit(0);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
       northPoleDirection = event.values[0];
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
