package com.david.giczi.pillarbasedisplayerapp.fragments;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.david.giczi.pillarbasedisplayerapp.MainActivity;
import com.david.giczi.pillarbasedisplayerapp.R;
import com.david.giczi.pillarbasedisplayerapp.databinding.FragmentDataBinding;
import com.david.giczi.pillarbasedisplayerapp.service.Point;

import java.util.List;
import java.util.Objects;

public class PillarDataFragment extends Fragment {

    private FragmentDataBinding fragmentDataBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentDataBinding = FragmentDataBinding.inflate(inflater, container, false);
        displayPillarLocationData();
        if( !MainActivity.IS_WEIGHT_BASE ){
            fragmentDataBinding.inputDistanceOfDirectionPoints.setVisibility(View.INVISIBLE);
            fragmentDataBinding.inputFootDistancePerpendicularly
                    .setHint(R.string.distance_from_side_of_hole_of_base_perpendicularly);
            fragmentDataBinding.inputFootDistanceParallel
                    .setHint(R.string.distance_from_side_of_hole_of_base_parallel);
        }
        displayInputData();
        MainActivity.PAGE_COUNTER = 2;
        MainActivity.MENU.findItem(R.id.weight_base).setEnabled(true);
        MainActivity.MENU.findItem(R.id.plate_base).setEnabled(true);
        MainActivity.MENU.findItem(R.id.start_stop_gps).setEnabled(false);
        MainActivity.MENU.findItem(R.id.save_pillar_center).setEnabled(false);
        if( MainActivity.northPoleWindow != null ){
            MainActivity.northPoleWindow.dismiss();
        }
        if( MainActivity.gpsDataWindow != null ){
            MainActivity.gpsDataWindow.dismiss();
        }
        return fragmentDataBinding.getRoot();
    }


    private void displayPillarLocationData(){
        getParentFragmentManager().setFragmentResultListener("results", this, (requestKey, result) -> {
            String measCenterX = Objects.requireNonNull(result.get("centerX")).toString();
            String measCenterY = Objects.requireNonNull(result.get("centerY")).toString();
            String measDirectionX = Objects.requireNonNull(result.get("directionX")).toString();
            String measDirectionY = Objects.requireNonNull(result.get("directionY")).toString();
            String basePointY =  fragmentDataBinding.inputYCoordinate.getText().toString();
            String basePointX = fragmentDataBinding.inputXCoordinate.getText().toString();
            if( (basePointY.isEmpty() || MainActivity.isInvalidInputChars(basePointY)) &&
                    (basePointX.isEmpty() || MainActivity.isInvalidInputChars(basePointX)) ){
                fragmentDataBinding.inputYCoordinate.setText(measCenterX);
                fragmentDataBinding.inputXCoordinate.setText(measCenterY);
                fragmentDataBinding.inputNextPrevYCoordinate.setText(measDirectionX);
                fragmentDataBinding.inputNextPrevXCoordinate.setText(measDirectionY);
                return;
            }
            showCalculatedPillarBaseDataDialog(new Point("BasePoint",
                            Double.parseDouble(basePointY), Double.parseDouble(basePointX)),
                    measCenterX, measCenterY, measDirectionX, measDirectionY);
        });
    }

    private void showCalculatedPillarBaseDataDialog(Point basePoint, String measCenterX, String measCenterY,
                                                    String measDirectionX, String measDirectionY) {
        String ordinate = MainActivity.calcPillarLocationData.getOrdinateAsString(basePoint);
        String abscissa = MainActivity.calcPillarLocationData.getAbscissaAsString(basePoint);
        if( ordinate.startsWith("NaN") || abscissa.startsWith("NaN")){
            Toast.makeText(getContext(), "A kezdő-, és végpont nem lehet egyező.", Toast.LENGTH_SHORT).show();
            return;
        }
        SpannableString ordinateSpan = new SpannableString("Merőlegesen: " + ordinate);
        if( MainActivity.calcPillarLocationData.isOkOrdinateValue(basePoint) ){
            ordinateSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.green)),
                    13, ordinateSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else {
            ordinateSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.red)),
                    13, ordinateSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        SpannableString abscissaSpan = new SpannableString("Vonalban: " + abscissa);
        if( MainActivity.calcPillarLocationData.isOkAbscissaValue(basePoint) ){
            abscissaSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.green)),
                    10, abscissaSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else {
            abscissaSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.red)),
                    10, abscissaSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        SpannableStringBuilder titleBuilder = new SpannableStringBuilder();
        titleBuilder.append(abscissaSpan)
                        .append("\n")
                                .append(ordinateSpan);
        String infoForBasePosition =
                getInfoForPillarBasePositionByControlPoint(measCenterX, measCenterY, measDirectionX, measDirectionY);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(titleBuilder);
        builder.setMessage("Hozzáadja a mért adatokat?");

        builder.setPositiveButton("Igen", (dialog, which) -> {
            fragmentDataBinding.inputYCoordinate.setText(measCenterX);
            fragmentDataBinding.inputXCoordinate.setText(measCenterY);
            fragmentDataBinding.inputNextPrevYCoordinate.setText(measDirectionX);
            fragmentDataBinding.inputNextPrevXCoordinate.setText(measDirectionY);
        });
        builder.setNegativeButton("Nem", (dialog, which) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String getInfoForPillarBasePositionByControlPoint(String measCenterX, String measCenterY,
                                                              String measDirectionX, String measDirectionY){
     String controlPointY = ((MainActivity) requireActivity()).service.actualPillarBase.centerPillarY;
     String controlPointX = ((MainActivity) requireActivity()).service.actualPillarBase.centerPillarX;
        if( controlPointY == null || controlPointX == null ){
            return "Nincs oh kontrollpont.";
        }
    String centerPillarId = fragmentDataBinding.inputPillarId.getText().toString();
    String centerPillarY= fragmentDataBinding.inputYCoordinate.getText().toString();
    String centerPillarX= fragmentDataBinding.inputXCoordinate.getText().toString();
    String nextPillarId = fragmentDataBinding.inputNextPrevPillarId.getText().toString();
    String nextPillarY = fragmentDataBinding.inputNextPrevYCoordinate.getText().toString();
    String nextPillarX = fragmentDataBinding.inputNextPrevXCoordinate.getText().toString();
    if( centerPillarId.isEmpty() || MainActivity.isInvalidInputChars(centerPillarId) ){
        return "Nem megfelelő az oh alap azonosítója.";
    }
    else if(  centerPillarY.isEmpty() || MainActivity.isInvalidInputChars(centerPillarY) ||
            centerPillarX.isEmpty() || MainActivity.isInvalidInputChars(centerPillarX) ){
        return "Nem megfelelő az oh alap elméleti koordinátája";
    }
    else if( nextPillarId.isEmpty() || MainActivity.isInvalidInputChars(nextPillarId) ){
        return "Nem megfelelő az előző/következő oh alap azonosítója.";
    }
    else if( nextPillarY.isEmpty() || MainActivity.isInvalidInputChars(nextPillarY) ||
            nextPillarX.isEmpty() || MainActivity.isInvalidInputChars(nextPillarX)){
        return "Nem megfelelő az előző/következő oh alap elméleti koordinátája";
    }
    Point controlPoint = new Point(((MainActivity) requireActivity()).service.actualPillarBase.controlPointId,
            Double.parseDouble(controlPointX), Double.parseDouble(controlPointY));
    Point centerPillarPoint = new Point(centerPillarId, Double.parseDouble(centerPillarY), Double.parseDouble(centerPillarX));
    Point nextPillarPoint = new Point(nextPillarId, Double.parseDouble(nextPillarY), Double.parseDouble(nextPillarX));
    Point measCenterPoint = new Point("MeasCenterPoint", Double.parseDouble(measCenterX), Double.parseDouble(measCenterY));
    Point measDirectionPoint = new Point("MeasDirectionPoint", Double.parseDouble(measCenterX), Double.parseDouble(measCenterY));

        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentDataBinding = null;
    }
    private void displayInputData(){
        List<String> inputData =  MainActivity.BASE_DATA;
        if( inputData == null || inputData.isEmpty() ){
            return;
        }
        MainActivity.MENU.findItem(R.id.start_stop_gps).setEnabled(true);
        fragmentDataBinding.inputPillarId.setText(inputData.get(1));
        fragmentDataBinding.inputYCoordinate.setText(inputData.get(2));
        fragmentDataBinding.inputXCoordinate.setText(inputData.get(3));
        fragmentDataBinding.inputNextPrevPillarId.setText(inputData.get(4));
        fragmentDataBinding.inputNextPrevYCoordinate.setText(inputData.get(5));
        fragmentDataBinding.inputNextPrevXCoordinate.setText(inputData.get(6));

        if( MainActivity.IS_WEIGHT_BASE ){
            fragmentDataBinding.inputDistanceOfDirectionPoints.setVisibility(View.VISIBLE);
            fragmentDataBinding.inputDistanceOfDirectionPoints.setText(inputData.get(7));
            fragmentDataBinding.inputFootDistancePerpendicularly.setText(inputData.get(8));
            fragmentDataBinding.inputFootDistanceParallel.setText(inputData.get(9));
            fragmentDataBinding.inputHoleDistancePerpendicularly.setText(inputData.get(10));
            fragmentDataBinding.inputHoleDistanceParallel.setText(inputData.get(11));

            if(inputData.size() == 16 && "1".equals(inputData.get(15))){
                fragmentDataBinding.radioLeft.setChecked(true);
            }
            else if(inputData.size() == 16 && "0".equals(inputData.get(15))){
                fragmentDataBinding.radioRight.setChecked(true);
            }
            else {
                fragmentDataBinding.radioRight.setChecked(true);
            }
            if( inputData.get(12).contains(".") ){
                fragmentDataBinding.inputAngle.setText(inputData.get(12).substring(0, inputData.get(12).indexOf('.')));
            }
            else{
                fragmentDataBinding.inputAngle.setText(inputData.get(12));
            }
            if( inputData.get(13).contains(".") ){
                fragmentDataBinding.inputMin.setText(inputData.get(13).substring(0, inputData.get(13).indexOf('.')));
            }
            else{
                fragmentDataBinding.inputMin.setText(inputData.get(13));
            }
            if( inputData.get(14).contains(".") ) {
                fragmentDataBinding.inputSec.setText(inputData.get(14).substring(0, inputData.get(14).indexOf('.')));
            }
            else{
                fragmentDataBinding.inputSec.setText(inputData.get(14));
            }
        }
        else {
            fragmentDataBinding.inputDistanceOfDirectionPoints.setVisibility(View.INVISIBLE);
            fragmentDataBinding.inputHoleDistancePerpendicularly.setText(inputData.get(7));
            fragmentDataBinding.inputHoleDistanceParallel.setText(inputData.get(8));
            fragmentDataBinding.inputFootDistancePerpendicularly.setText(inputData.get(9));
            fragmentDataBinding.inputFootDistanceParallel.setText(inputData.get(10));

            if(inputData.size() == 15 && "1".equals(inputData.get(14))){
                fragmentDataBinding.radioLeft.setChecked(true);
            }
           else if(inputData.size() == 15 && "0".equals(inputData.get(14))){
                fragmentDataBinding.radioRight.setChecked(true);
            }
            else {
                fragmentDataBinding.radioRight.setChecked(true);
            }
            if( inputData.get(11).contains(".") ){
                fragmentDataBinding.inputAngle.setText(inputData.get(11).substring(0, inputData.get(11).indexOf('.')));
            }
            else{
                fragmentDataBinding.inputAngle.setText(inputData.get(11));
            }
            if( inputData.get(12).contains(".") ){
                fragmentDataBinding.inputMin.setText(inputData.get(12).substring(0, inputData.get(12).indexOf('.')));
            }
            else{
                fragmentDataBinding.inputMin.setText(inputData.get(12));
            }
            if( inputData.get(13).contains(".") ) {
                fragmentDataBinding.inputSec.setText(inputData.get(13).substring(0, inputData.get(13).indexOf('.')));
            }
            else{
                fragmentDataBinding.inputSec.setText(inputData.get(13));
            }
        }
    }

    public String convertAngleMinSecFormat(double radianAngle){
        double angleData = Math.toDegrees(radianAngle);
        int angle = (int) angleData;
        int min = (int) ((angleData - angle) * 60);
        int sec = ((int) ((angleData - angle) * 3600 - min * 60));
        return (0 > angleData ? "-" :  "") + Math.abs(angle) + "° "
                + (9 < Math.abs(min) ? Math.abs(min) : "0" + Math.abs(min)) + "' "
                + (9 < Math.abs(sec) ? Math.abs(sec) : "0" + Math.abs(sec)) + "\"";
    }

}