package com.david.giczi.pillarbasedisplayerapp.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "pillar_base_params")
public class PillarBaseParams {

    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "name")
    public String baseName;
    @ColumnInfo(name = "type")
    public String baseType;
    @ColumnInfo(name = "directionDistance")
    public Double directionDistance;
    @ColumnInfo(name = "ppHoleDistance")
    public Double perpendicularHoleDistance;
    @ColumnInfo(name = "parallelHoleDistance")
    public Double parallelHoleDistance;
    @ColumnInfo(name = "ppFootDistance")
    public Double perpendicularFootDistance;
    @ColumnInfo(name = "parallelFootDistance")
    public Double parallelFootDistance;
    @ColumnInfo(name = "ppDirectionDistance")
    public Double perpendicularDirectionDistance;
    @ColumnInfo(name = "parallelDirectionDistance")
    public Double parallelDirectionDistance;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public Double getDirectionDistance() {
        return directionDistance;
    }

    public void setDirectionDistance(Double directionDistance) {
        this.directionDistance = directionDistance;
    }

    public Double getPerpendicularHoleDistance() {
        return perpendicularHoleDistance;
    }

    public void setPerpendicularHoleDistance(Double perpendicularHoleDistance) {
        this.perpendicularHoleDistance = perpendicularHoleDistance;
    }

    public Double getParallelHoleDistance() {
        return parallelHoleDistance;
    }

    public void setParallelHoleDistance(Double parallelHoleDistance) {
        this.parallelHoleDistance = parallelHoleDistance;
    }

    public Double getPerpendicularFootDistance() {
        return perpendicularFootDistance;
    }

    public void setPerpendicularFootDistance(Double perpendicularFootDistance) {
        this.perpendicularFootDistance = perpendicularFootDistance;
    }

    public Double getParallelFootDistance() {
        return parallelFootDistance;
    }

    public void setParallelFootDistance(Double parallelFootDistance) {
        this.parallelFootDistance = parallelFootDistance;
    }

    public Double getPerpendicularDirectionDistance() {
        return perpendicularDirectionDistance;
    }

    public void setPerpendicularDirectionDistance(Double perpendicularDirectionDistance) {
        this.perpendicularDirectionDistance = perpendicularDirectionDistance;
    }

    public Double getParallelDirectionDistance() {
        return parallelDirectionDistance;
    }

    public void setParallelDirectionDistance(Double parallelDirectionDistance) {
        this.parallelDirectionDistance = parallelDirectionDistance;
    }
}
