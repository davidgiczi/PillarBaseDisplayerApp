package com.david.giczi.pillarbasedisplayerapp.db;

import android.content.Context;

import com.david.giczi.pillarbasedisplayerapp.MainActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PillarBaseParamsService {

    private final PillarBaseParamsDAO paramsDAO;
    public List<String> itemList;
    public List<PillarBaseParams> allBaseList;
    public List<PillarBaseParams> projectBaseList;
    public HashSet<String> projectNameSet;
    public PillarBaseParams actualPillarBase;
    public int numberOfBaseOfProject;

    public PillarBaseParamsService(Context context) {
        PillarBaseParamsDataBase dataBase = PillarBaseParamsDataBase.getInstance(context);
        this.paramsDAO = dataBase.PillarBaseParamsDAO();
    }

    public void getItems(){
        itemList = new ArrayList<>();
        itemList.add("Alapok");
        PillarBaseParamsDataBase.databaseExecutor.execute(() -> {
            for (String baseName : paramsDAO.getPillarBaseNameList()) {
             itemList.add(baseName + "\t\t[ " + paramsDAO.getBaseNumberOfMeasureByName(baseName) + " ]");
            }
        });
    }

    public void getProjectNameSet(){
        projectNameSet = new HashSet<>();
        PillarBaseParamsDataBase.databaseExecutor.execute(() -> {
            for (String pillarBaseName : paramsDAO.getPillarBaseNameList()) {
                projectNameSet.add(pillarBaseName.split("_")[0]);
            }
        });
    }

    public void updatePillarBaseParams(){
        PillarBaseParamsDataBase.databaseExecutor.execute(() ->
                paramsDAO.updatePillarBaseParams(actualPillarBase));
    }

    public void insertOrUpdatePillarBaseParams(String baseName) {
                List<String> baseData = MainActivity.BASE_DATA;
                actualPillarBase = new PillarBaseParams(baseName);
                actualPillarBase.setBaseType(baseData.get(0));
                actualPillarBase.setCenterPillarId(baseData.get(1));
                actualPillarBase.setCenterPillarY(baseData.get(2));
                actualPillarBase.setCenterPillarX(baseData.get(3));
                actualPillarBase.setDirectionPillarId(baseData.get(4));
                actualPillarBase.setDirectionPillarY(baseData.get(5));
                actualPillarBase.setDirectionPillarX(baseData.get(6));
                if( MainActivity.BASE_TYPE[0].equals(baseData.get(0)) ){
                    actualPillarBase.setDirectionDistance(baseData.get(7));
                    actualPillarBase.setPerpendicularFootDistance(baseData.get(8));
                    actualPillarBase.setParallelFootDistance(baseData.get(9));
                    actualPillarBase.setPerpendicularHoleDistance(baseData.get(10));
                    actualPillarBase.setParallelHoleDistance(baseData.get(11));
                    actualPillarBase.setRotationAngle(baseData.get(12));
                    actualPillarBase.setRotationMin(baseData.get(13));
                    actualPillarBase.setRotationSec(baseData.get(14));
                    if( baseData.size() == 22 ){
                        actualPillarBase.setControlPointId(baseData.get(16));
                        actualPillarBase.setControlPointY(baseData.get(17));
                        actualPillarBase.setControlPointX(baseData.get(18));
                        actualPillarBase.setHoleReady(Boolean.parseBoolean(baseData.get(19)));
                        actualPillarBase.setAxisReady(Boolean.parseBoolean(baseData.get(20)));
                        actualPillarBase.setNumberOfMeasure(Integer.parseInt(baseData.get(21)));
                    }
                   else if( baseData.size() == 19 ){
                        actualPillarBase.setHoleReady(Boolean.parseBoolean(baseData.get(16)));
                        actualPillarBase.setAxisReady(Boolean.parseBoolean(baseData.get(17)));
                        actualPillarBase.setNumberOfMeasure(Integer.parseInt(baseData.get(18)));
                    }
                } else if( MainActivity.BASE_TYPE[1].equals(baseData.get(0)) ){
                    actualPillarBase.setPerpendicularHoleDistance(baseData.get(7));
                    actualPillarBase.setParallelHoleDistance(baseData.get(8));
                    actualPillarBase.setPerpendicularDirectionDistance(baseData.get(9));
                    actualPillarBase.setParallelDirectionDistance(baseData.get(10));
                    actualPillarBase.setRotationAngle(baseData.get(11));
                    actualPillarBase.setRotationMin(baseData.get(12));
                    actualPillarBase.setRotationSec(baseData.get(13));
                    if( baseData.size() == 21 ){
                        actualPillarBase.setControlPointId(baseData.get(15));
                        actualPillarBase.setControlPointY(baseData.get(16));
                        actualPillarBase.setControlPointX(baseData.get(17));
                        actualPillarBase.setHoleReady(Boolean.parseBoolean(baseData.get(18)));
                        actualPillarBase.setAxisReady(Boolean.parseBoolean(baseData.get(19)));
                        actualPillarBase.setNumberOfMeasure(Integer.parseInt(baseData.get(20)));
                    }
                    else if( baseData.size() == 18 ){
                        actualPillarBase.setHoleReady(Boolean.parseBoolean(baseData.get(15)));
                        actualPillarBase.setAxisReady(Boolean.parseBoolean(baseData.get(16)));
                        actualPillarBase.setNumberOfMeasure(Integer.parseInt(baseData.get(17)));
                    }
                }
                if( baseData.get(14).equals("0") ){
                 actualPillarBase.setRotationSide("right");
                }
                else if( baseData.get(14).equals("1") ) {
                actualPillarBase.setRotationSide("left");
                 }
                else if( baseData.get(15).equals("0") ){
                actualPillarBase.setRotationSide("right");
                }
                else if( baseData.get(15).equals("1") ) {
                actualPillarBase.setRotationSide("left");
                }
                PillarBaseParamsDataBase.databaseExecutor.execute(() ->
                         paramsDAO.insertPillarBaseParams(actualPillarBase));
    }

    public void deletePillarParamsByName(String baseName){
        PillarBaseParamsDataBase.databaseExecutor.execute(() ->
                paramsDAO.deletePillarBaseParamsByName(baseName));
    }

    public void getPillarBaseData(String baseName){
        PillarBaseParamsDataBase.databaseExecutor.execute(() ->
                actualPillarBase = paramsDAO.getPillarBaseDataByName(baseName));
    }

    public void getAllPillarBaseParams(){
        PillarBaseParamsDataBase.databaseExecutor.execute(() ->
            allBaseList = paramsDAO.getAllPillarBaseParams());
    }

    public void getNumberOfBaseOfProject(String baseName){
        PillarBaseParamsDataBase.databaseExecutor.execute(() ->
               numberOfBaseOfProject = paramsDAO.getNumberOfBaseOfProject(baseName));
    }

    public void getPillarBaseParamsByProjectName(String projectName){
        PillarBaseParamsDataBase.databaseExecutor.execute(() ->
                projectBaseList = paramsDAO.getPillarBaseDataByProjectName(projectName));
    }

    public void deletePillarBaseParamsByProjectName(String projectName){
        PillarBaseParamsDataBase.databaseExecutor.execute(() ->
                paramsDAO.deletePillarBaseProjectByProjectName(projectName));
    }

}
