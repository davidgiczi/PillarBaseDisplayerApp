package com.david.giczi.pillarbasedisplayerapp.db;

import android.content.Context;
import com.david.giczi.pillarbasedisplayerapp.MainActivity;
import java.util.ArrayList;
import java.util.List;

public class PillarBaseParamsService {

    private final PillarBaseParamsDAO paramsDAO;
    private List<String> itemList;
    public boolean isSavedParams;
    public static PillarBaseParams ACTUAL_PILLAR_BASE;

    public PillarBaseParamsService(Context context) {
        PillarBaseParamsDataBase dataBase = PillarBaseParamsDataBase.getInstance(context);
        this.paramsDAO = dataBase.PillarBaseParamsDAO();
    }

    public String[] getItems(){
        itemList = new ArrayList<>();
        itemList.add("Projektek");
        PillarBaseParamsDataBase.databaseExecutor.execute(() ->
                itemList.addAll(paramsDAO.getPillarBaseNameList()));
        return itemList.toArray(new String[0]);
    }

    public void insertPillarParams(String baseName){
        PillarBaseParams params = new PillarBaseParams(baseName);
        List<String> baseData = MainActivity.BASE_DATA;
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
        } else if( MainActivity.BASE_TYPE[1].equals(baseData.get(1)) ){
            params.setPerpendicularHoleDistance(baseData.get(7));
            params.setParallelHoleDistance(baseData.get(8));
            params.setPerpendicularDirectionDistance(baseData.get(9));
            params.setParallelDirectionDistance(baseData.get(10));
            params.setRotationAngle(baseData.get(11));
            params.setRotationMin(baseData.get(12));
            params.setRotationSec(baseData.get(13));
        }
        PillarBaseParamsDataBase.databaseExecutor.execute(() ->{
            paramsDAO.insertPillarBaseParams(params);
            isSavedParams = true;
        });
    }

    public void deletePillarParamsByName(){
        PillarBaseParamsDataBase.databaseExecutor.execute(() ->
                paramsDAO.deletePillarBaseParamsByName(ACTUAL_PILLAR_BASE.baseName));
    }

    public void getPillarBaseData(String baseName){
        PillarBaseParamsDataBase.databaseExecutor.execute(() ->
                ACTUAL_PILLAR_BASE = paramsDAO.getPillarBaseData(baseName));
    }

}
