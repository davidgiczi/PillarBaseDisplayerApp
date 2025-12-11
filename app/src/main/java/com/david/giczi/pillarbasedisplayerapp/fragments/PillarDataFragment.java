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
import com.david.giczi.pillarbasedisplayerapp.db.PillarBaseParamsService;
import com.david.giczi.pillarbasedisplayerapp.service.AzimuthAndDistance;
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
        SpannableString infoForBasePosition = getInfoForPillarBasePositionByControlPoint(measCenterX, measCenterY);
        SpannableStringBuilder titleBuilder = new SpannableStringBuilder();
        titleBuilder.append(abscissaSpan)
                        .append("\n")
                        .append(ordinateSpan);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(titleBuilder);
        SpannableStringBuilder messageBuilder = new SpannableStringBuilder();
        messageBuilder.append(infoForBasePosition)
                        .append("\n")
                        .append("\n")
                        .append("Hozzáadja a mért adatokat?");
        builder.setMessage(messageBuilder);

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

    private SpannableString getInfoForPillarBasePositionByControlPoint(String measCenterX, String measCenterY){
     PillarBaseParamsService service = ((MainActivity) requireActivity()).service;
     String controlPointY = service.actualPillarBase.centerPillarY;
     String controlPointX = service.actualPillarBase.centerPillarX;
        if( controlPointY == null || controlPointX == null ){
            return new SpannableString("Nincs " +
                    service.actualPillarBase.centerPillarId +  ".oh kontrollpont.");
        }
    Point controlPoint = new Point(service.actualPillarBase.controlPointId,
            Double.parseDouble(controlPointX), Double.parseDouble(controlPointY));
    Point centerPoint = new Point(service.actualPillarBase.centerPillarId,
                                Double.parseDouble(service.actualPillarBase.centerPillarY),
                                Double.parseDouble(service.actualPillarBase.centerPillarX));
    Point nextPoint = new Point(service.actualPillarBase.directionPillarId,
                                Double.parseDouble(service.actualPillarBase.directionPillarY),
                                Double.parseDouble(service.actualPillarBase.directionPillarX));
    Point measCenterPoint = new Point("MeasCenterPoint", Double.parseDouble(measCenterX),
                                Double.parseDouble(measCenterY));
    AzimuthAndDistance centerToNextPoint = new AzimuthAndDistance(centerPoint, nextPoint);
    AzimuthAndDistance centerToControlPoint = new AzimuthAndDistance(centerPoint, controlPoint);
    AzimuthAndDistance measCenterToNextPoint = new AzimuthAndDistance(measCenterPoint, nextPoint);
    AzimuthAndDistance measCenterToControlPoint = new AzimuthAndDistance(measCenterPoint, controlPoint);
    double teoGammaAngle = centerToNextPoint.calcAzimuth() - centerToControlPoint.calcAzimuth();
    double measGammaAngle = measCenterToNextPoint.calcAzimuth() - measCenterToControlPoint.calcAzimuth();
    double deltaGammaAngle =  teoGammaAngle - measGammaAngle;
    SpannableString infoText;
    if( 0 > teoGammaAngle ){
        teoGammaAngle += 2 * Math.PI;
    }
    if( 0 > measGammaAngle ){
        measGammaAngle += 2 * Math.PI;
    }
    if( teoGammaAngle > 0  && Math.PI > teoGammaAngle && measGammaAngle > 0 && Math.PI > measGammaAngle ){
        infoText = new SpannableString("BAL oldali törésszög, ΔΥ= " +
                MainActivity.convertAngleMinSecFormat(deltaGammaAngle));
        if( Math.PI / 90 > Math.abs(deltaGammaAngle) ){
            infoText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.green)),
                    22, infoText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else {
            infoText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.red)),
                    22, infoText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
    else if( teoGammaAngle > Math.PI && measGammaAngle > Math.PI){
        infoText = new SpannableString("JOBB oldali törésszög, ΔΥ= " +
                MainActivity.convertAngleMinSecFormat(deltaGammaAngle));
        if( Math.PI / 90 > Math.abs(deltaGammaAngle) ){
            infoText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.green)),
                    23, infoText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else{
            infoText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.red)),
                    23, infoText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
    else {
        infoText = new SpannableString("Hibás törésszög, ΔΥ= " +
                                MainActivity.convertAngleMinSecFormat(deltaGammaAngle));
        infoText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.red)),
                0, infoText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
        return infoText;
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

}