package com.david.giczi.pillarbasedisplayerapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
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
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.david.giczi.pillarbasedisplayerapp.databinding.ActivityMainBinding;
import com.david.giczi.pillarbasedisplayerapp.db.PillarBaseParams;
import com.david.giczi.pillarbasedisplayerapp.db.PillarBaseParamsService;
import com.david.giczi.pillarbasedisplayerapp.service.AzimuthAndDistance;
import com.david.giczi.pillarbasedisplayerapp.service.Point;
import com.david.giczi.pillarbasedisplayerapp.utils.AppExceptionHandler;
import com.david.giczi.pillarbasedisplayerapp.utils.EOV;
import com.david.giczi.pillarbasedisplayerapp.utils.PillarBaseComparator;
import com.david.giczi.pillarbasedisplayerapp.utils.WGS84;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private ActivityResultLauncher<String[]> openMultipleDocsLauncher;
    private Spinner openingProjectSpinner;
    private Spinner openingStatisticsSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new AppExceptionHandler(this));
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        PAGE_COUNTER = 0;
        BASE_DATA = new ArrayList<>();
        openMultipleDocsLauncher =  registerForActivityResult(
                new ActivityResultContracts.OpenMultipleDocuments(),
                (uriList) -> {
                    if( uriList.isEmpty() ){
                        return;
                    }
                    inputBaseDataDialog(uriList);
                }
        );
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
        this.service = new PillarBaseParamsService(this);
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

    @SuppressLint("InflateParams")
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
        double direction = Math.toDegrees(pillarData.calcAzimuth()) - northPoleDirection;
        direction = 0 > direction ? direction + 360 : direction >= 360 ? direction - 360 : direction;
        addPillarDirectionArrowImage((float) direction, (int) Math.round(pillarData.calcDistance()) );
        String pillarDirectionAndDistance = "Irány: "  + String.format(Locale.getDefault(),"%5.1f°", direction) +
          "\t\tTávolság: " + String.format(Locale.getDefault(),"%5.0fm", pillarData.calcDistance());
        TextView pillarDataView = gpsDataContainer.findViewById(R.id.pillar_direction_and_distance);
        pillarDataView.setText(pillarDirectionAndDistance);
    }
    @SuppressLint("InflateParams")
    private void showNorthSign(){
        if( northPoleContainer == null  ){
            northPoleContainer = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_north_pole, null);
            northPoleWindow = new PopupWindow(northPoleContainer, 500, 360, false);
            northPoleWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, -630);
        }
        ImageView northPoleView = northPoleContainer.findViewById(R.id.north_pole);
        northPoleView.setRotation( - northPoleDirection );
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
            openPillarBaseDataFiles();
        }
        else if( id == R.id.project_process ){
            if( IS_GPS_RUNNING ){
                stopMeasure();
                MENU.findItem(R.id.start_stop_gps).setTitle(R.string.start_gps);
            }
            FIND_POINT_INDEX = null;
            popupProjectOpenWindow();
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
        else if( id == R.id.meas_pillar_input_data ){
            popupMeasuredBaseInputDataWindow();
        }
        else if( id == R.id.meas_pillar_stat ){
            popupStatisticsWindow();
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

    public void popupProjectOpenWindow() {
        @SuppressLint("InflateParams") ViewGroup container = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_open_project, null);
        PopupWindow openingProjectWindow = new PopupWindow(container, 1000, 700, true);
        openingProjectWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, -400);
        openingProjectSpinner = container.findViewById(R.id.opening_project_spinner);
        openingProjectSpinner.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        openingProjectSpinner.setSelection(0);
        container.findViewById(R.id.ok_button).setEnabled(false);
        service.getItems();
        service.getAllPillarBaseParams();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.fragment_for_spinners, service.itemList.toArray(new String[0])){
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                if( position == 0 ){
                  ((TextView)  view.findViewById(R.id.project_spinner))
                                    .setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));}

                    return view;
                }

                @Override
                public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    TextView textView = (TextView) super.getDropDownView(position, convertView, parent);

                    if( position ==  0 ){
                        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    }

                    String baseText = textView.getText().toString().split("\\s+")[0];

                    for (PillarBaseParams pillarBaseParam : service.allBaseList) {
                        if( pillarBaseParam.isHoleReady && pillarBaseParam.isAxisReady &&
                                baseText.equals(pillarBaseParam.baseName)){
                            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                        }
                        else if( !pillarBaseParam.isHoleReady && !pillarBaseParam.isAxisReady &&
                                baseText.equals(pillarBaseParam.baseName)){
                            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                        }
                        else if( pillarBaseParam.isHoleReady && !pillarBaseParam.isAxisReady &&
                                baseText.equals(pillarBaseParam.baseName)){
                            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                        }
                        else if( !pillarBaseParam.isHoleReady && pillarBaseParam.isAxisReady &&
                                baseText.equals(pillarBaseParam.baseName)){
                            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.orange_yellow));
                        }
                    }

                    return  textView;
                }
                @Override
                public boolean isEnabled(int position) {
                    return position != 0;
                }
            };
            adapter.setDropDownViewResource(R.layout.fragment_for_spinners);
            openingProjectSpinner.setAdapter(adapter);
            container.findViewById(R.id.ok_button).setEnabled(true);
        }, 1000);

        openingProjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBaseName = openingProjectSpinner.getSelectedItem().toString().split("\\s+")[0];
                if( selectedBaseName.equals("Alapok") ){
                    return;
                }
                service.getPillarBaseData(selectedBaseName);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        Button okButton = container.findViewById(R.id.ok_button);
        okButton.setOnClickListener(c -> {
            String selectedItem =  openingProjectSpinner.getSelectedItem().toString().split("\\s+")[0];
            if( selectedItem.equals("Alapok") ){
                Toast.makeText(this, "Alap választása szükéges.", Toast.LENGTH_LONG).show();
                return;
            }
            if( ((CheckBox) container.findViewById(R.id.delete_project)).isChecked() ){
                deleteBaseDataDialog(selectedItem, openingProjectWindow);
                return;
            }
            readPillarBaseParamsFromDatabase();
            setupMenu();
            MENU.findItem(R.id.meas_pillar_input_data).setEnabled(true);
            openingProjectWindow.dismiss();
        });
    }
    @SuppressLint("InflateParams")
    public void popupMeasuredBaseInputDataWindow() {
       ViewGroup container =
                (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_measured_base_data, null);
        PopupWindow measuredInputDataWindow = new PopupWindow(container, 1000, 700, true);
        measuredInputDataWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, -400);
        ((TextView) container.findViewById(R.id.base_name_text)).setText(binding.baseNameTitle.getText().toString());
        CheckBox measHoleCheckbox = container.findViewById(R.id.checkbox_measured_base_hole);
        CheckBox measAxisCheckbox = container.findViewById(R.id.checkbox_measured_base_axis);
        EditText numberOfMeasureField = container.findViewById(R.id.number_of_measure_field);
        measHoleCheckbox.setOnClickListener( c -> {
            if( measHoleCheckbox.isChecked() ){
                service.actualPillarBase.setNumberOfMeasure(service.actualPillarBase.numberOfMeasure + 1);
                numberOfMeasureField.setText(String.valueOf(service.actualPillarBase.numberOfMeasure));
            }
        });
        measAxisCheckbox.setOnClickListener( c -> {
            if( measAxisCheckbox.isChecked() ){
                service.actualPillarBase.setNumberOfMeasure(service.actualPillarBase.numberOfMeasure + 1);
                numberOfMeasureField.setText(String.valueOf(service.actualPillarBase.numberOfMeasure));
            }
        });
        measHoleCheckbox.setChecked(service.actualPillarBase.isHoleReady);
        measAxisCheckbox.setChecked(service.actualPillarBase.isAxisReady);
        numberOfMeasureField.setText( String.valueOf(service.actualPillarBase.numberOfMeasure) );
        Button okButton =  container.findViewById(R.id.ok_button);
        okButton.setOnClickListener( b -> {
            if( !binding.baseNameTitle.getText().toString().equals(service.actualPillarBase.baseName) ) {
                Toast.makeText(this, "Az adatok nem menthetők, az alap mentése szükséges.",
                        Toast.LENGTH_LONG).show();
                return;
            }
            service.actualPillarBase.setHoleReady(measHoleCheckbox.isChecked());
            service.actualPillarBase.setAxisReady(measAxisCheckbox.isChecked());
            service.actualPillarBase.setNumberOfMeasure(Integer.parseInt(numberOfMeasureField.getText().toString()));
            service.updatePillarBaseParams();
            measuredInputDataWindow.dismiss();
                Toast.makeText(this, "Az adatok sikeresen mentve az eszközre.",
                        Toast.LENGTH_LONG).show();

        });

    }
    public void popupStatisticsWindow() {
        @SuppressLint("InflateParams") ViewGroup container =
                (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_open_statistics, null);
        PopupWindow openingStatisticsWindow = new PopupWindow(container, 1000, 700, true);
        openingStatisticsWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, -400);
        openingStatisticsSpinner = container.findViewById(R.id.stat_opening_spinner);
        openingStatisticsSpinner.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        openingStatisticsSpinner.setSelection(0);
        container.findViewById(R.id.ok_button).setEnabled(false);
        service.getProjectNameSet();
        new Handler().postDelayed(() -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.fragment_for_spinners, service.projectNameSet.toArray(new String[0]));
            adapter.setDropDownViewResource(R.layout.fragment_for_spinners);
            openingStatisticsSpinner.setAdapter(adapter);
            if( !binding.baseNameTitle.getText().toString().isEmpty() ){
                String projectName = binding.baseNameTitle.getText().toString().split("_")[0];
                openingStatisticsSpinner.setSelection(adapter.getPosition(projectName));
            }
        }, 1000);

        openingStatisticsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                container.findViewById(R.id.ok_button).setEnabled(false);
                service.getNumberOfBaseOfProject(openingStatisticsSpinner.getSelectedItem().toString());
                new Handler().postDelayed(() -> {((EditText) container.findViewById(R.id.number_of_project_field))
                        .setText(String.valueOf(service.numberOfBaseOfProject));
                        container.findViewById(R.id.ok_button).setEnabled(true);}
                        , 1000);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        container.findViewById(R.id.ok_button).setOnClickListener(b -> {
            if( ((CheckBox) container.findViewById(R.id.checkbox_delete_project_base)).isChecked() ){
                deleteProjectBaseDialog(openingStatisticsSpinner.getSelectedItem().toString(), openingStatisticsWindow);
                return;
            }
            int numberOfBase =
                    Integer.parseInt( ((EditText) container.findViewById(R.id.number_of_project_field)).getText().toString());
            if( service.numberOfBaseOfProject > numberOfBase ){
                Toast.makeText(this,  "Az alapok száma legalább " + service.numberOfBaseOfProject + " db lehet.",
                        Toast.LENGTH_LONG).show();
                return;
            }
            String projectName = openingStatisticsSpinner.getSelectedItem().toString();
            service.getPillarBaseParamsByProjectName(projectName);
            new Handler().postDelayed(() -> {
            if(  ((CheckBox) container.findViewById(R.id.checkbox_save_statistics)).isChecked() ){
                service.projectBaseList.sort(new PillarBaseComparator());
                saveProjectFile(projectName, numberOfBase);
            }
            popupChartForProjectWindow(projectName, numberOfBase);},1000);
            openingStatisticsWindow.dismiss();});
    }

    private void deleteProjectBaseDialog(String projectName, PopupWindow window) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("\"" + projectName + "\" projekt törlése");
        builder.setMessage("Biztos, hogy törlöd a(z) " + service.numberOfBaseOfProject + " db alap adatait az eszközről?");

        builder.setPositiveButton("Igen", (dialog, which) -> {
            service.deletePillarBaseParamsByProjectName(projectName);
            dialog.dismiss();
            window.dismiss();
            Toast.makeText(this, "\"" + projectName + "\" " + service.numberOfBaseOfProject +
                            " db alap törölve az eszközről.",
                    Toast.LENGTH_LONG).show();
        });
        builder.setNegativeButton("Nem", (dialog, which) -> {
                window.setFocusable(true);
                dialog.dismiss();});

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void openPillarBaseDataFiles() {
        openMultipleDocsLauncher.launch(new String[]{"*/*"});
    }
    private void inputBaseDataDialog(List<Uri> uriList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Oszlophely alap fájlok beolvasása");
        builder.setMessage("Biztos, hogy beolvasod a(z) " + uriList.size() + " db fájlt." );

        builder.setPositiveButton("Igen", (dialog, which) -> {
         int numberOfInputFiles = readBaseFiles(uriList);
         Toast.makeText(this,
                 numberOfInputFiles + " db fájl beolvasva az eszközre.", Toast.LENGTH_LONG).show();
         dialog.dismiss();
        });

        builder.setNegativeButton("Nem", (dialog, which) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.show();
    }

    private int readBaseFiles(List<Uri> uriList){
        int numberOfInputFiles = 0;

        for (Uri uri : uriList) {

            String fileName = Objects.requireNonNull
                    (uri.getPath()).substring(uri.getPath().lastIndexOf("/") + 1);

            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                BASE_DATA.clear();
                while ((line = reader.readLine()) != null) {
                    BASE_DATA.add(line);
                }

                if( !BASE_TYPE[0].equals(BASE_DATA.get(0)) && !BASE_TYPE[1].equals(BASE_DATA.get(0))){
                    BASE_DATA.clear();
                    Toast.makeText(this,  "\"" + fileName + "\"" + " fájl beolvasása sikertelen.",
                            Toast.LENGTH_SHORT).show();
                    continue;
                }
              service.insertOrUpdatePillarBaseParams(fileName.substring(0, fileName.indexOf(".")));
              numberOfInputFiles++;
            } catch (Exception e) {
                Toast.makeText(this,  "\"" + fileName + "\"" + " fájl beolvasása sikertelen.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        return numberOfInputFiles;
    }

    private void popupChartForProjectWindow(String projectName, int numberOfBase){
        @SuppressLint("InflateParams") ViewGroup container =
                (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_chart, null);
        PopupWindow openingChartWindow = new PopupWindow(container, 1000, 700, true);
        openingChartWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, - 400);
        displayChart(container, projectName, numberOfBase);
    }

    private void displayChart(@NonNull ViewGroup container, String projectName, int numberOfBase){
        Bitmap bitmap = Bitmap.createBitmap(990, 690, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        ((ImageView)  container.findViewById(R.id.drawing_chart)).setImageBitmap(bitmap);
        paint.setColor(ContextCompat.getColor(this, R.color.red));
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(60);
        String title = projectName + " projekt";
        canvas.drawText(title, (990 - title.length() * 25) / 2f, 60, paint);
        int[] baseData = getMeasuredBaseData(numberOfBase);
        RectF rectangle = new RectF(100, 100, 650, 650);
        rectangle.offset(-80, 0);
        int[] colors = {R.color.green, R.color.orange_yellow,R.color.red, R.color.colorPrimary, R.color.steel_gray};
        float[] angles = getAngles(baseData, numberOfBase);
        String[] legends = getLegendTexts(baseData, numberOfBase);
        float startAngle = 0f;
        float text_y = 200f;
        for (int i = 0; i < angles.length; i++) {
                paint.setColor(ContextCompat.getColor(this, colors[i]));
                canvas.drawArc(rectangle, startAngle, angles[i], true, paint);
                if( angles[i] != 0 ){
                    canvas.drawText("■" , 600f, text_y, paint);
                    paint.setTextSize(40);
                    paint.setColor(Color.BLACK);
                    paint.setTypeface(Typeface.SANS_SERIF);
                    canvas.drawText(legends[i], 650f,  text_y, paint);
                    paint.setTextSize(60);
                }
            startAngle += angles[i];
            text_y += 50;
            }
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        canvas.drawText("Σ: ", 650f,  text_y + 50, paint);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText(numberOfBase + " db ", 700f,  text_y + 50, paint);
        paint.setTypeface(Typeface.SANS_SERIF);
        canvas.drawText("alap", 850f,  text_y + 50, paint);
    }

    private int[] getMeasuredBaseData(int numberOfBase){
        int[] baseData = new int[5];

            for (PillarBaseParams pillarBaseParam : service.projectBaseList) {

                if( pillarBaseParam.isHoleReady && pillarBaseParam.isAxisReady ){
                    baseData[0]++;
                }
                else if( !pillarBaseParam.isHoleReady && pillarBaseParam.isAxisReady ){
                    baseData[1]++;
                }
                else if( pillarBaseParam.isHoleReady ){
                    baseData[2]++;
                }
                else {
                    baseData[3]++;
                }
            }
            baseData[4] = numberOfBase - service.numberOfBaseOfProject;

        return baseData;
    }

    private float[] getAngles(int[] baseData, int numberOfBase){
        float[] angles = new float[5];
        angles[0] = Math.round( 360f * baseData[0] / numberOfBase );
        angles[1] = Math.round( 360f * baseData[1] / numberOfBase );
        angles[2] = Math.round( 360f * baseData[2] / numberOfBase );
        angles[3] = Math.round( 360f * baseData[3] / numberOfBase );
        angles[4] = Math.round( 360f * baseData[4] / numberOfBase );
        return angles;
    }
    @NonNull
    private String[] getLegendTexts(int[] baseData, int numberOfBase){
        String[] legends = new String[5];
        legends[0] = "Teljes " + baseData[0] + " db " + Math.round( 100f * baseData[0] / numberOfBase ) + "%";
        legends[1] = "Tengely " + baseData[1] + " db " + Math.round( 100f * baseData[1] / numberOfBase ) + "%";
        legends[2] = "Gödör " + baseData[2] + " db " + Math.round( 100f * baseData[2] / numberOfBase ) + "%";
        legends[3] = "Nincs " + baseData[3] + " db " + Math.round( 100f * baseData[3] / numberOfBase ) + "%";
        legends[4] = "Hátral. " + baseData[4] + " db " +  Math.round( 100f * baseData[4] / numberOfBase ) + "%";
        return  legends;
    }

    private void saveProjectFile(String projectName, int numberOfBase) {
        File projectFile = new File(Environment.getExternalStorageDirectory() , "Documents/" +
                projectName + "_project_report.txt");

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(projectFile, true));
            bw.write("Dátum:\t" + new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(new Date(System.currentTimeMillis())));
            bw.newLine();
            bw.write("Projekt neve:\t" + projectName);
            bw.newLine();
            bw.write( getMeasuredBase(numberOfBase) );
            bw.newLine();
            bw.write( getMeasuredBaseHole(numberOfBase) );
            bw.newLine();
            bw.write( getMeasuredBaseAxis(numberOfBase) );
            bw.newLine();
            bw.write( getMeasuredNotMeasuredBase(numberOfBase) );
            bw.newLine();
            bw.write( getNotProcessedBase(numberOfBase) );
            bw.newLine();
            bw.write("Összesen:\t" + numberOfBase + " db alap");
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            Toast.makeText(this, projectFile.getName() +
                    "\nprojekt fájl mentése sikertelen.", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this,   projectFile.getName() + " fájl mentve.",
                Toast.LENGTH_LONG).show();
    }

    @NonNull
    private String getMeasuredBase(int numberOfBase){
        StringBuilder sb = new StringBuilder();
        int pcs = 0;
        sb.append("Kitűzött alapok:")
                .append("\t");
        for (PillarBaseParams pillarBaseParam : service.projectBaseList) {
            if( pillarBaseParam.isHoleReady && pillarBaseParam.isAxisReady ){
                pcs++;
                sb.append(pillarBaseParam.centerPillarId)
                        .append(",").append(" ");
            }
        }
        if( pcs == 0 ){
            return sb.append("-").toString();
        }
                sb.setLength(sb.length() - 2);
                sb.append("\t")
                .append(pcs)
                .append(" ")
                .append("db")
                .append("\t")
                .append(Math.round(100f * pcs / numberOfBase))
                .append("%");
        return sb.toString();
    }

    @NonNull
    private String getMeasuredBaseHole(int numberOfBase){
        StringBuilder sb = new StringBuilder();
        int pcs = 0;
        sb.append("Kitűzött gödör:")
                .append("\t");
        for (PillarBaseParams pillarBaseParam : service.projectBaseList) {
            if( pillarBaseParam.isHoleReady && !pillarBaseParam.isAxisReady ){
                pcs++;
                sb.append(pillarBaseParam.centerPillarId)
                        .append(",").append(" ");
            }
        }
        if( pcs == 0 ){
            return sb.append("-").toString();
        }
        sb.setLength(sb.length() - 2);
        sb.append("\t")
                .append(pcs)
                .append(" ")
                .append("db")
                .append("\t")
                .append(Math.round(100f * pcs / numberOfBase))
                .append("%");
        return sb.toString();
    }

    @NonNull
    private String getMeasuredBaseAxis(int numberOfBase){
        StringBuilder sb = new StringBuilder();
        int pcs = 0;
        sb.append("Kitűzött tengely:")
                .append("\t");
        for (PillarBaseParams pillarBaseParam : service.projectBaseList) {
            if( !pillarBaseParam.isHoleReady && pillarBaseParam.isAxisReady ){
                pcs++;
                sb.append(pillarBaseParam.centerPillarId)
                        .append(",").append(" ");
            }
        }
        if( pcs == 0 ){
            return sb.append("-").toString();
        }
        sb.setLength(sb.length() - 2);
        sb.append("\t")
                .append(pcs)
                .append(" ")
                .append("db")
                .append("\t")
                .append(Math.round(100f * pcs / numberOfBase))
                .append("%");
        return sb.toString();
    }

    @NonNull
    private String getMeasuredNotMeasuredBase(int numberOfBase){
        StringBuilder sb = new StringBuilder();
        int pcs = 0;
        sb.append("Nincs kitűzve:")
                .append("\t");
        for (PillarBaseParams pillarBaseParam : service.projectBaseList) {
            if( !pillarBaseParam.isHoleReady && !pillarBaseParam.isAxisReady ){
                pcs++;
                sb.append(pillarBaseParam.centerPillarId)
                        .append(",").append(" ");
            }
        }
        if( pcs == 0 ){
            return sb.append("-").toString();
        }
        sb.setLength(sb.length() - 2);
        sb.append("\t")
                .append(pcs)
                .append(" ")
                .append("db")
                .append("\t")
                .append(Math.round(100f * pcs / numberOfBase))
                .append("%");
        return sb.toString();
    }

    @NonNull
    private String getNotProcessedBase(int numberOfBase){
        int pcs = numberOfBase - service.numberOfBaseOfProject;
        if( pcs == 0 ){
            return "Nincs feldolgozva:\n-";
        }
        return "Nincs feldolgozva:\t" + pcs + " db\t" + Math.round(100f * pcs / numberOfBase) + "%";
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
        TextView title = findViewById(R.id.baseNameTitle);
        title.setText(service.actualPillarBase.baseName);
        gotoDataFragment();
        if( BASE_DATA.isEmpty() ){
        Toast.makeText(this, "Alap adatainak beolvasása sikertelen.",
                Toast.LENGTH_LONG).show();
    }
        else {
        Toast.makeText(this, "Alap adatai sikeresen beolvasva.",
                Toast.LENGTH_LONG).show();
        }
    }

    private void popupPillarFootDistanceCalculator(){
        @SuppressLint("InflateParams") ViewGroup container = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_foot_calc, null);
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

    private void deleteBaseDataDialog(String baseName, PopupWindow window) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("\"" + baseName + "\" alap törlése");
        builder.setMessage("Biztos, hogy törlöd az alap adatait?");

        builder.setPositiveButton("Igen", (dialog, which) -> {
                    service.deletePillarParamsByName(baseName.split("\\s+")[0]);
                    dialog.dismiss();
                    window.dismiss();
                    Toast.makeText(this, "\"" + baseName + "\" alap törölve az eszközről.",
                    Toast.LENGTH_LONG).show();
                });
        builder.setNegativeButton("Nem", (dialog, which) ->{
            dialog.dismiss();
            window.setFocusable(true);
        });
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
                service.insertOrUpdatePillarBaseParams(((EditText)findViewById(R.id.baseNameTitle)).getText().toString());

                   Toast.makeText(this, "Az alap adatai sikeresen mentve az eszközre.",
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

    private boolean isValidInputData(){
        if(((TextView) findViewById(R.id.baseNameTitle)).getText().toString().isEmpty() ){
            Toast.makeText(this, "Az alap nevének megadása szükséges.", Toast.LENGTH_LONG).show();
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
