package com.david.giczi.pillarbasedisplayerapp.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "pillar_base_params")
public class PillarBaseParams {

    @PrimaryKey
    @ColumnInfo(name = "name")
    @NonNull
    public String baseName;
    @ColumnInfo(name = "type")
    public String baseType;
    @ColumnInfo(name = "directionDistance")
    public String directionDistance;
    @ColumnInfo(name = "ppHoleDistance")
    public String perpendicularHoleDistance;
    @ColumnInfo(name = "parallelHoleDistance")
    public String parallelHoleDistance;
    @ColumnInfo(name = "ppFootDistance")
    public String perpendicularFootDistance;
    @ColumnInfo(name = "parallelFootDistance")
    public String parallelFootDistance;
    @ColumnInfo(name = "ppDirectionDistance")
    public String perpendicularDirectionDistance;
    @ColumnInfo(name = "parallelDirectionDistance")
    public String parallelDirectionDistance;

    public PillarBaseParams(@NonNull String baseName) {
        this.baseName = baseName;
    }

    public String getBaseName() {
        return baseName;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public String getDirectionDistance() {
        return directionDistance;
    }

    public void setDirectionDistance(String directionDistance) {
        this.directionDistance = directionDistance;
    }

    public String getPerpendicularHoleDistance() {
        return perpendicularHoleDistance;
    }

    public void setPerpendicularHoleDistance(String perpendicularHoleDistance) {
        this.perpendicularHoleDistance = perpendicularHoleDistance;
    }

    public String getParallelHoleDistance() {
        return parallelHoleDistance;
    }

    public void setParallelHoleDistance(String parallelHoleDistance) {
        this.parallelHoleDistance = parallelHoleDistance;
    }

    public String getPerpendicularFootDistance() {
        return perpendicularFootDistance;
    }

    public void setPerpendicularFootDistance(String perpendicularFootDistance) {
        this.perpendicularFootDistance = perpendicularFootDistance;
    }

    public String getParallelFootDistance() {
        return parallelFootDistance;
    }

    public void setParallelFootDistance(String parallelFootDistance) {
        this.parallelFootDistance = parallelFootDistance;
    }

    public String getPerpendicularDirectionDistance() {
        return perpendicularDirectionDistance;
    }

    public void setPerpendicularDirectionDistance(String perpendicularDirectionDistance) {
        this.perpendicularDirectionDistance = perpendicularDirectionDistance;
    }

    public String getParallelDirectionDistance() {
        return parallelDirectionDistance;
    }

    public void setParallelDirectionDistance(String parallelDirectionDistance) {
        this.parallelDirectionDistance = parallelDirectionDistance;
    }
}
