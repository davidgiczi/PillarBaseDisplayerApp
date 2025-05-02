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
    @Query("DELETE FROM pillar_base_params WHERE id = :id")
    void deletePillarBaseParamsById(int id);
    @Query("SELECT * FROM pillar_base_params WHERE name = :baseName")
    List<PillarBaseParams> getPillarBaseNameList(String baseName);


}
