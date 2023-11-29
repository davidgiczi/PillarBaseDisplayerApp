package com.david.giczi.pillarbasedisplayerapp;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.david.giczi.pillarbasedisplayerapp.databinding.ActivityMainBinding;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    public static List<String> BASE_DATA;
    private static final String[] BASE_TYPE = {"#WeightBase", "#PlateBase"};
    private static int PAGE_COUNTER;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
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
        } else if (id == R.id.goto_fragment_data) {
           gotoNextFragment();
        }
            return super.onOptionsItemSelected(item);
    }

    private void gotoNextFragment(){
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            switch (PAGE_COUNTER % 4){
                case 0 :
                    navController.navigate(R.id.action_StartFragment_to_DataFragment);
                    break;
                case 1 :
                    saveDialog(navController);
                    break;
                case 2 :
                    navController.navigate(R.id.action_CoordsFragment_to_BaseFragment);
                    break;
                case 3 :
                    navController.navigate(R.id.action_BaseFragment_to_StartFragment);
                    break;
            }
            PAGE_COUNTER++;
    }

    private void exitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Alkalmazás bezárása");
        builder.setMessage("Biztos, hogy ki akarsz lépni az alkalmazásból?");

        builder.setPositiveButton("Igen", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
               System.exit(0);
            }
        });

        builder.setNegativeButton("Nem", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
    private void saveDialog(NavController navController) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Adatok mentése");
        builder.setMessage("Kivánja menteni az adatokat?");

        builder.setPositiveButton("Igen", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                saveProjectFile();
                navController.navigate(R.id.action_DataFragment_to_CoordsFragment);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Nem", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                navController.navigate(R.id.action_DataFragment_to_CoordsFragment);
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

   @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        PAGE_COUNTER--;
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
                String path = uri.getPath();
                path =  path.substring(path.indexOf(":") + 1);
                readProjectFile(path);
            }
        }
    }
    private void readProjectFile(String path){
        BASE_DATA = new ArrayList<>();
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
        }

        gotoStartFragment();
        PAGE_COUNTER = 0;
        if( BASE_DATA.isEmpty() ){
            Toast.makeText(this, "Az adatok beolvasása sikertelen",
                    Toast.LENGTH_LONG).show();
        }
        else {

            Toast.makeText(this, "Az adatok sikeresen beolvasva",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void gotoStartFragment(){
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        switch (PAGE_COUNTER % 4){
            case 1 :
                navController.navigate(R.id.action_DataFragment_to_StartFragment);
                break;
            case 2 :
                navController.navigate(R.id.action_CoordsFragment_to_StartFragment);
                break;
            case 3 :
                navController.navigate(R.id.action_BaseFragment_to_StartFragment);
                break;
        }
    }

    private void saveProjectFile() {
        if( BASE_DATA == null ){
            Toast.makeText(this, "Az adatok mentése sikertelen:\nNincs menthető adat.", Toast.LENGTH_SHORT).show();
            return;
        }
        File projectFile =
                new File(Environment.getExternalStorageDirectory(),
                        "/Documents/SavedData.txt");
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
                    " fájl mentése sikertelen", Toast.LENGTH_SHORT).show();
        }finally {
            Toast.makeText(this,
                    "Projekt fájl mentve:\n"
                            + projectFile.getName() , Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PAGE_COUNTER--;
        }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        PAGE_COUNTER = 0;
        BASE_DATA = null;
    }
}
