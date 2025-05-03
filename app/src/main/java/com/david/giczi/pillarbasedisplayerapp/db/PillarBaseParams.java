package com.david.giczi.pillarbasedisplayerapp.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "pillar_base_params")
public class PillarBaseParams {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "Name")
    public String baseName;
    @ColumnInfo(name = "Type")
    public String baseType;
    @ColumnInfo(name = "CenterPillarId")
    public String centerPillarId;
    @ColumnInfo(name = "CenterPillarY")
    public String centerPillarY;
    @ColumnInfo(name = "CenterPillarX")
    public String centerPillarX;
    @ColumnInfo(name = "DirectionPillarId")
    public String directionPillarId;
    @ColumnInfo(name = "DirectionPillarY")
    public String directionPillarY;
    @ColumnInfo(name = "DirectionPillarX")
    public String directionPillarX;
    @ColumnInfo(name = "DirectionDistance")
    public String directionDistance;
    @ColumnInfo(name = "PPHoleDistance")
    public String perpendicularHoleDistance;
    @ColumnInfo(name = "ParallelHoleDistance")
    public String parallelHoleDistance;
    @ColumnInfo(name = "PPFootDistance")
    public String perpendicularFootDistance;
    @ColumnInfo(name = "ParallelFootDistance")
    public String parallelFootDistance;
    @ColumnInfo(name = "PPDirectionDistance")
    public String perpendicularDirectionDistance;
    @ColumnInfo(name = "ParallelDirectionDistance")
    public String parallelDirectionDistance;
    @ColumnInfo(name = "Angle")
    public String rotationAngle;
    @ColumnInfo(name = "Min")
    public String rotationMin;
    @ColumnInfo(name = "Sec")
    public String rotationSec;

    public PillarBaseParams(String baseName) {
        this.baseName = baseName;
    }

    public String getCenterPillarId() {
        return centerPillarId;
    }

    public void setCenterPillarId(String centerPillarId) {
        this.centerPillarId = centerPillarId;
    }

    public String getDirectionPillarId() {
        return directionPillarId;
    }

    public void setDirectionPillarId(String directionPillarId) {
        this.directionPillarId = directionPillarId;
    }

    @NonNull
    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(@NonNull String baseName) {
        this.baseName = baseName;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public String getCenterPillarY() {
        return centerPillarY;
    }

    public void setCenterPillarY(String centerPillarY) {
        this.centerPillarY = centerPillarY;
    }

    public String getCenterPillarX() {
        return centerPillarX;
    }

    public void setCenterPillarX(String centerPillarX) {
        this.centerPillarX = centerPillarX;
    }

    public String getDirectionPillarY() {
        return directionPillarY;
    }

    public void setDirectionPillarY(String directionPillarY) {
        this.directionPillarY = directionPillarY;
    }

    public String getDirectionPillarX() {
        return directionPillarX;
    }

    public void setDirectionPillarX(String directionPillarX) {
        this.directionPillarX = directionPillarX;
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

    public String getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(String rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    public String getRotationMin() {
        return rotationMin;
    }

    public void setRotationMin(String rotationMin) {
        this.rotationMin = rotationMin;
    }

    public String getRotationSec() {
        return rotationSec;
    }

    public void setRotationSec(String rotationSec) {
        this.rotationSec = rotationSec;
    }

    @Override
    public String toString() {
        return "PillarBaseParams{" +
                "baseName='" + baseName + '\'' +
                ", baseType='" + baseType + '\'' +
                ", centerPillarId='" + centerPillarId + '\'' +
                ", centerPillarY='" + centerPillarY + '\'' +
                ", centerPillarX='" + centerPillarX + '\'' +
                ", directionPillarId='" + directionPillarId + '\'' +
                ", directionPillarY='" + directionPillarY + '\'' +
                ", directionPillarX='" + directionPillarX + '\'' +
                ", directionDistance='" + directionDistance + '\'' +
                ", perpendicularHoleDistance='" + perpendicularHoleDistance + '\'' +
                ", parallelHoleDistance='" + parallelHoleDistance + '\'' +
                ", perpendicularFootDistance='" + perpendicularFootDistance + '\'' +
                ", parallelFootDistance='" + parallelFootDistance + '\'' +
                ", perpendicularDirectionDistance='" + perpendicularDirectionDistance + '\'' +
                ", parallelDirectionDistance='" + parallelDirectionDistance + '\'' +
                ", rotationAngle='" + rotationAngle + '\'' +
                ", rotationMin='" + rotationMin + '\'' +
                ", rotationSec='" + rotationSec + '\'' +
                '}';
    }
}
