package com.david.giczi.pillarbasedisplayerapp.db;

import android.content.Context;
import android.os.Handler;

import com.david.giczi.pillarbasedisplayerapp.MainActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PillarBaseParamsService {

    private final PillarBaseParamsDAO paramsDAO;
    public List<String> itemList;
    public List<PillarBaseParams> allParamList;
    public HashSet<String> projectNameSet;
    public PillarBaseParams actualPillarBase;
    public int numberOfBaseOfProject;

    public PillarBaseParamsService(Context context) {
        PillarBaseParamsDataBase dataBase = PillarBaseParamsDataBase.getInstance(context);
        this.paramsDAO = dataBase.PillarBaseParamsDAO();
    }

    public void getItems(){
        itemList = new ArrayList<>();
        itemList.add("Projektek");
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

    public void insertOrUpdatePillarBaseParams(String baseName){
        PillarBaseParamsDataBase.databaseExecutor.execute(() -> actualPillarBase = paramsDAO.getPillarBaseDataByName(baseName));
        List<String> baseData = MainActivity.BASE_DATA;
        Handler handler = new Handler();
        handler.postDelayed(() ->{
            if( actualPillarBase == null ){
                PillarBaseParams params = new PillarBaseParams(baseName);
                params.setBaseType(baseData.get(0));
                params.setCenterPillarId(baseData.get(1));
                params.setCenterPillarY(baseData.get(2));
                params.setCenterPillarX(baseData.get(3));
                params.setDirectionPillarId(baseData.get(4));
                params.setDirectionPillarY(baseData.get(5));
                params.setDirectionPillarX(baseData.get(6));
                if( MainActivity.BASE_TYPE[0].equals(baseData.get(0)) ){
                    params.setDirectionDistance(baseData.get(7));
                    params.setPerpendicularFootDistance(baseData.get(8));
                    params.setParallelFootDistance(baseData.get(9));
                    params.setPerpendicularHoleDistance(baseData.get(10));
                    params.setParallelHoleDistance(baseData.get(11));
                    params.setRotationAngle(baseData.get(12));
                    params.setRotationMin(baseData.get(13));
                    params.setRotationSec(baseData.get(14));
                } else if( MainActivity.BASE_TYPE[1].equals(baseData.get(0)) ){
                    params.setPerpendicularHoleDistance(baseData.get(7));
                    params.setParallelHoleDistance(baseData.get(8));
                    params.setPerpendicularDirectionDistance(baseData.get(9));
                    params.setParallelDirectionDistance(baseData.get(10));
                    params.setRotationAngle(baseData.get(11));
                    params.setRotationMin(baseData.get(12));
                    params.setRotationSec(baseData.get(13));
                }
                if( baseData.get(baseData.size() - 1).equals("0") ){
                    params.setRotationSide("right");
                }
                else if( baseData.get(baseData.size() - 1).equals("1") ){
                    params.setRotationSide("left");
                }
                PillarBaseParamsDataBase.databaseExecutor.execute(() ->
                        paramsDAO.insertPillarBaseParams(params));
                return;
            }
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
            } else if( MainActivity.BASE_TYPE[1].equals(baseData.get(0)) ){
                actualPillarBase.setPerpendicularHoleDistance(baseData.get(7));
                actualPillarBase.setParallelHoleDistance(baseData.get(8));
                actualPillarBase.setPerpendicularDirectionDistance(baseData.get(9));
                actualPillarBase.setParallelDirectionDistance(baseData.get(10));
                actualPillarBase.setRotationAngle(baseData.get(11));
                actualPillarBase.setRotationMin(baseData.get(12));
                actualPillarBase.setRotationSec(baseData.get(13));
            }
            if( baseData.get(baseData.size() - 1).equals("0") ){
                actualPillarBase.setRotationSide("right");
            }
            else if( baseData.get(baseData.size() - 1).equals("1") ){
                actualPillarBase.setRotationSide("left");
            }
            PillarBaseParamsDataBase.databaseExecutor.execute(() ->
                    paramsDAO.updatePillarBaseParams(actualPillarBase));

        }, 1000 );

    }

    public void deletePillarParamsByName(String baseName){
        PillarBaseParamsDataBase.databaseExecutor.execute(() ->
                paramsDAO.deletePillarBaseParamsByName(baseName));
                actualPillarBase = null;
    }

    public void getPillarBaseData(String baseName){
        PillarBaseParamsDataBase.databaseExecutor.execute(() ->
                actualPillarBase = paramsDAO.getPillarBaseDataByName(baseName));
    }

    public void getAllPillarBaseParams(){
        PillarBaseParamsDataBase.databaseExecutor.execute(() ->
            allParamList = paramsDAO.getAllPillarBaseParams());
    }

    public void getNumberOfBaseOfProject(String baseName){
        PillarBaseParamsDataBase.databaseExecutor.execute(() ->
               numberOfBaseOfProject = paramsDAO.getNumberOfBaseOfProject(baseName));
    }

}
