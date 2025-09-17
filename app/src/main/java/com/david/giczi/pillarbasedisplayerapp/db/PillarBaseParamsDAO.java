package com.david.giczi.pillarbasedisplayerapp.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PillarBaseParamsDAO {

    @Insert
    void insertPillarBaseParams(PillarBaseParams pillarBaseParams);
    @Update
    void updatePillarBaseParams(PillarBaseParams pillarBaseParams);
    @Query("DELETE FROM pillar_base_params WHERE Name = :baseName")
    void deletePillarBaseParamsByName(String baseName);
    @Query("SELECT name FROM pillar_base_params")
    List<String> getPillarBaseNameList();
    @Query("SELECT NumberOfMeasure FROM pillar_base_params WHERE Name = :baseName")
    Integer getBaseNumberOfMeasureByName(String baseName);
    @Query("SELECT * FROM pillar_base_params WHERE Name = :baseName")
    PillarBaseParams getPillarBaseDataByName(String baseName);
    @Query("SELECT * FROM pillar_base_params")
    List<PillarBaseParams> getAllPillarBaseParams();
    @Query("SELECT COUNT(*) FROM pillar_base_params WHERE Name LIKE :baseName || '%' COLLATE NOCASE")
    int getNumberOfBaseOfProject(String baseName);
    @Query("SELECT * FROM pillar_base_params WHERE Name LIKE :projectName || '%' COLLATE NOCASE")
    List<PillarBaseParams> getPillarBaseDataByProjectName(String projectName);
}
