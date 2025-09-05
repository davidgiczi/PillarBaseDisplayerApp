package com.david.giczi.pillarbasedisplayerapp;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.david.giczi.pillarbasedisplayerapp.databinding.ActivityMainBinding;
import com.david.giczi.pillarbasedisplayerapp.db.PillarBaseParamsService;
import com.david.giczi.pillarbasedisplayerapp.service.AzimuthAndDistance;
import com.david.giczi.pillarbasedisplayerapp.service.Point;
import com.david.giczi.pillarbasedisplayerapp.utils.EOV;
import com.david.giczi.pillarbasedisplayerapp.utils.WGS84;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private Sensor accelerometerSensor;
    private Sensor magnetometerSensor;
    private float[] gravityValues = new float[3];
    private float[] geomagneticValues = new float[3];
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
    public static Integer FIND_POINT_INDEX;
    public PillarBaseParamsService service;
    private static float northPoleDirection;
    private int previousPillarDistance;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Spinner openingProjectSpinner;


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
        activityResultLauncher =  registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if( result.getResultCode() == Activity.RESULT_OK ) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            readProjectFile(uri);
                        }
                    }
                });
       /* if( ContextCompat
                .checkSelfPermission(MainActivity.this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ActivityCompat
               .requestPermissions(MainActivity.this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
            }
        }*/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //this.deleteDatabase("pillar_base_params_database");
        this.service = new PillarBaseParamsService(this);
        //service.getAllPillarBaseParams();
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
        IS_GPS_RUNNING = !IS_GPS_RUNNING;
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
                if( PAGE_COUNTER == 4 ){
                    showNorthSign();
                }
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

        sensorManager.registerListener(this, accelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        MainActivity.this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                100, 0, locationListener);
        IS_GPS_RUNNING = !IS_GPS_RUNNING;
      Toast.makeText(MainActivity.this, "GPS elindítva..", Toast.LENGTH_SHORT).show();
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
    }

    private void popupGPSData() {
        if ( gpsDataContainer == null ) {
            gpsDataContainer = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_gps_data, null);
            gpsDataWindow = new PopupWindow(gpsDataContainer, 1000, 500, false);
            gpsDataWindow.showAtLocation( binding.getRoot(), Gravity.CENTER, 0, 800);
        }
    }

    private void showPillarDistanceAndDirection(EOV eov){
        if( FIND_POINT_INDEX == null ){
            if( IS_WEIGHT_BASE ){
                FIND_POINT_INDEX = 10;
            }
            else{
                FIND_POINT_INDEX = 1;
            }
        }
        TextView gpsDataView = gpsDataContainer.findViewById(R.id.actual_position);
        gpsDataView.setText(eov.toString());
        AzimuthAndDistance pillarData = new AzimuthAndDistance(
                new Point("position", eov.getCoordinatesForEOV().get(0),
                        eov.getCoordinatesForEOV().get(1)),
                new Point("center", PILLAR_BASE_COORDINATES.get(FIND_POINT_INDEX).getX_coord(),
                       PILLAR_BASE_COORDINATES.get(FIND_POINT_INDEX).getY_coord()));
        double direction = Math.toDegrees(pillarData.calcAzimuth()) + northPoleDirection > 360 ?
                Math.toDegrees(pillarData.calcAzimuth()) + northPoleDirection - 360 :
                Math.toDegrees(pillarData.calcAzimuth()) + northPoleDirection;
        addPillarDirectionArrowImage((float) direction, (int) Math.round(pillarData.calcDistance()) );
        String pillarDirectionAndDistance = "Irány: "  + String.format(Locale.getDefault(),"%5.1f°", direction) +
          "\t\tTávolság: " + String.format(Locale.getDefault(),"%5.0fm", pillarData.calcDistance());
        TextView pillarDataView = gpsDataContainer.findViewById(R.id.pillar_direction_and_distance);
        pillarDataView.setText(pillarDirectionAndDistance);
    }
    private void showNorthSign(){
        if( northPoleContainer == null  ){
            northPoleContainer = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_north_pole, null);
            northPoleWindow = new PopupWindow(northPoleContainer, 500, 360, false);
            northPoleWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, -630);
        }
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
            if( IS_GPS_RUNNING ){
                stopMeasure();
                MENU.findItem(R.id.start_stop_gps).setTitle(R.string.start_gps);
            }
            FIND_POINT_INDEX = null;
            openPillarBaseDataFile();
        }
        else if( id == R.id.project_process ){
            if( IS_GPS_RUNNING ){
                stopMeasure();
                MENU.findItem(R.id.start_stop_gps).setTitle(R.string.start_gps);
            }
            FIND_POINT_INDEX = null;
            popupProjectOpenDialog();
        }
        else if (id == R.id.goto_next_fragment) {
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
                gotoDataFragmentForCalcDistanceBetweenPillarLegs();
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
        }
            return super.onOptionsItemSelected(item);
    }

    public void popupProjectOpenDialog() {
        ViewGroup container = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_open_project, null);
        PopupWindow openingProjectWindow = new PopupWindow(container, 1000, 700, true);
        openingProjectWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, -400);
        openingProjectSpinner = container.findViewById(R.id.opening_project_spinner);
        openingProjectSpinner.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        openingProjectSpinner.setSelection(0);
        service.getItems();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.spinner_opening_project, service.itemList.toArray(new String[0])){
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if( position == 0 ){
                        ((TextView)  view.findViewById(R.id.project_spinner)).setTextColor(Color.RED);
                    }
                    return view;
                }

                @Override
                public boolean isEnabled(int position) {
                    return position != 0;
                }
            };
            adapter.setDropDownViewResource(R.layout.spinner_opening_project);

            openingProjectSpinner.setAdapter(adapter);
        }, 1000);

        openingProjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                service.getPillarBaseData((String) openingProjectSpinner.getSelectedItem());

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button okButton = container.findViewById(R.id.ok_button);
        okButton.setOnClickListener(c -> {
            String selectedItem = (String) openingProjectSpinner.getSelectedItem();
            if( selectedItem.equals("Projektek") ){
                Toast.makeText(this, "Projekt választása szükéges.", Toast.LENGTH_LONG).show();
                return;
            }
            if( ((CheckBox) container.findViewById(R.id.delete_project)).isChecked() ){
                deleteProjectDialog(selectedItem);
                openingProjectWindow.dismiss();
                return;
            }

            readPillarBaseParamsFromDatabase();
            setupMenu();
            openingProjectWindow.dismiss();
        });
    }

    private void readPillarBaseParamsFromDatabase(){
        BASE_DATA.clear();
        BASE_DATA.add(service.actualPillarBase.baseType);
        BASE_DATA.add(service.actualPillarBase.centerPillarId);
        BASE_DATA.add(service.actualPillarBase.centerPillarY);
        BASE_DATA.add(service.actualPillarBase.centerPillarX);
        BASE_DATA.add(service.actualPillarBase.directionPillarId);
        BASE_DATA.add(service.actualPillarBase.directionPillarY);
        BASE_DATA.add(service.actualPillarBase.directionPillarX);
        if( service.actualPillarBase.baseType.equals(BASE_TYPE[0]) ){
            BASE_DATA.add(service.actualPillarBase.directionDistance);
            BASE_DATA.add(service.actualPillarBase.perpendicularFootDistance);
            BASE_DATA.add(service.actualPillarBase.parallelFootDistance);
            BASE_DATA.add(service.actualPillarBase.perpendicularHoleDistance);
            BASE_DATA.add(service.actualPillarBase.parallelHoleDistance);
            BASE_DATA.add(service.actualPillarBase.rotationAngle);
            BASE_DATA.add(service.actualPillarBase.rotationMin);
            BASE_DATA.add(service.actualPillarBase.rotationSec);
        }
        else if( service.actualPillarBase.baseType.equals(BASE_TYPE[1]) ) {
            BASE_DATA.add(service.actualPillarBase.perpendicularHoleDistance);
            BASE_DATA.add(service.actualPillarBase.parallelHoleDistance);
            BASE_DATA.add(service.actualPillarBase.perpendicularDirectionDistance);
            BASE_DATA.add(service.actualPillarBase.parallelDirectionDistance);
            BASE_DATA.add(service.actualPillarBase.rotationAngle);
            BASE_DATA.add(service.actualPillarBase.rotationMin);
            BASE_DATA.add(service.actualPillarBase.rotationSec);
        }

        if( service.actualPillarBase.getRotationSide().equals("left") ){
            BASE_DATA.add("1");
        }
        else if( service.actualPillarBase.getRotationSide().equals("right") ){
            BASE_DATA.add("0");
        }
    }
    private void setupMenu(){

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
        TextView title = findViewById(R.id.projectNameTitle);
        title.setText(service.actualPillarBase.baseName);
        gotoDataFragment();
        if( BASE_DATA.isEmpty() ){
        Toast.makeText(this, "Projekt adatok beolvasása sikertelen.",
                Toast.LENGTH_LONG).show();
    }
        else {
        Toast.makeText(this, "Projekt adatok sikeresen beolvasva.",
                Toast.LENGTH_LONG).show();
        }
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
           TextView resultFootDistanceView = container.findViewById(R.id.result_foot_distance);
           String resultDistance = String.format(Locale.getDefault(), "%.3f", footDistance);
           resultFootDistanceView.setText(String.format(Locale.getDefault(), "%.3fm", footDistance));
           TextView ppFootDistanceView = findViewById(R.id.input_foot_distance_perpendicularly);
        if( ppFootDistanceView != null && ppFootDistanceView.getText().toString().isEmpty() ){
            ppFootDistanceView.setText(resultDistance);
        }
            TextView parallelFootDistanceView = findViewById(R.id.input_foot_distance_parallel);
        if( parallelFootDistanceView != null && parallelFootDistanceView.getText().toString().isEmpty() ){
            parallelFootDistanceView.setText(resultDistance);
        }

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

    private void deleteProjectDialog(String baseName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("\"" + baseName + "\" alap törlése");
        builder.setMessage("Biztos, hogy törlöd a projektet?");

        builder.setPositiveButton("Igen", (dialog, which) -> {
                    service.deletePillarParamsByName(baseName);
                    dialog.dismiss();
                    Toast.makeText(this, "\"" + baseName + "\" projekt törölve az eszközről.",
                    Toast.LENGTH_LONG).show();
                });
        builder.setNegativeButton("Nem", (dialog, which) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.show();
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
                service.insertOrUpdatePillarBaseParams(((EditText)findViewById(R.id.projectNameTitle)).getText().toString());

                   Toast.makeText(this, "Projekt adatai sikeresen mentve az eszközre.",
                           Toast.LENGTH_LONG).show();

                navController.navigate(R.id.action_DataFragment_to_CoordsFragment);
            }
            dialog.dismiss();
        });
        builder.setNegativeButton("Nem", (dialog, which) -> {
            if( isValidInputData() ) {
                getDataFromDataFragment();
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
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        activityResultLauncher.launch(intent);
    }

    private void readProjectFile(Uri uri){
        BASE_DATA.clear();
        try{
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
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
            setTitle(Objects.requireNonNull(uri.getPath()).substring(uri.getPath().lastIndexOf("/") + 1));
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

    private void gotoDataFragmentForCalcDistanceBetweenPillarLegs(){
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        switch (PAGE_COUNTER % 5){
            case 0 :
                navController.navigate(R.id.action_StartFragment_to_DataFragment);
                break;
            case 1 :
                navController.navigate(R.id.action_MeasDataFragment_to_DataFragment);
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
       String fileName = "mod_" + ((TextView) findViewById(R.id.projectNameTitle)).getText().toString() + ".txt";

       File projectFile = new File(Environment.getExternalStorageDirectory() , "Documents/" + fileName);

        try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(projectFile, false));
            for (String data : BASE_DATA) {
                bw.write(data);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            Toast.makeText(this, fileName +
                    "\nprojekt fájl mentése sikertelen.", Toast.LENGTH_SHORT).show();
            return;
        }
            Toast.makeText(this,
                    "Projekt fájl mentve:\n"
                            + fileName , Toast.LENGTH_SHORT).show();
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

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravityValues = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagneticValues = event.values.clone();
        }
        float[] R = new float[9];
        float[] I = new float[9];
        boolean success = SensorManager.getRotationMatrix(R, I, gravityValues, geomagneticValues);

        if (success) {
            float[] orientation = new float[3];
            SensorManager.getOrientation(R, orientation);

            northPoleDirection = (float) Math.toDegrees(orientation[0]);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
