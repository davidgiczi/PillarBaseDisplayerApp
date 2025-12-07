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
    @ColumnInfo(name = "RotationSide")
    public String rotationSide;
    @ColumnInfo(name = "HoleReady")
    public boolean isHoleReady;
    @ColumnInfo(name = "AxisReady")
    public boolean isAxisReady;
    @ColumnInfo(name = "NumberOfMeasure")
    public int numberOfMeasure;
    @ColumnInfo(name = "ControlPointId")
    public String controlPointId;
    @ColumnInfo(name = "ControlPointY")
    public String controlPointY;
    @ColumnInfo(name = "ControlPointX")
    public String controlPointX;



    public PillarBaseParams(@NonNull String baseName) {
        this.baseName = baseName;
    }

    public void setCenterPillarId(String centerPillarId) {
        this.centerPillarId = centerPillarId;
    }

    public void setDirectionPillarId(String directionPillarId) {
        this.directionPillarId = directionPillarId;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public void setCenterPillarY(String centerPillarY) {
        this.centerPillarY = centerPillarY;
    }

    public void setCenterPillarX(String centerPillarX) {
        this.centerPillarX = centerPillarX;
    }

    public void setDirectionPillarY(String directionPillarY) {
        this.directionPillarY = directionPillarY;
    }

    public void setDirectionPillarX(String directionPillarX) {
        this.directionPillarX = directionPillarX;
    }

    public void setDirectionDistance(String directionDistance) {
        this.directionDistance = directionDistance;
    }

    public void setPerpendicularHoleDistance(String perpendicularHoleDistance) {
        this.perpendicularHoleDistance = perpendicularHoleDistance;
    }

    public void setParallelHoleDistance(String parallelHoleDistance) {
        this.parallelHoleDistance = parallelHoleDistance;
    }

    public void setPerpendicularFootDistance(String perpendicularFootDistance) {
        this.perpendicularFootDistance = perpendicularFootDistance;
    }

    public void setParallelFootDistance(String parallelFootDistance) {
        this.parallelFootDistance = parallelFootDistance;
    }

    public void setPerpendicularDirectionDistance(String perpendicularDirectionDistance) {
        this.perpendicularDirectionDistance = perpendicularDirectionDistance;
    }

    public void setParallelDirectionDistance(String parallelDirectionDistance) {
        this.parallelDirectionDistance = parallelDirectionDistance;
    }

    public void setRotationAngle(String rotationAngle) {
        this.rotationAngle = rotationAngle;
    }


    public void setRotationMin(String rotationMin) {
        this.rotationMin = rotationMin;
    }

    public void setRotationSec(String rotationSec) {
        this.rotationSec = rotationSec;
    }

    public String getRotationSide() {
        return rotationSide;
    }

    public void setRotationSide(String rotationSide) {
        this.rotationSide = rotationSide;
    }

    public void setHoleReady(boolean holeReady) {
        isHoleReady = holeReady;
    }

    public void setAxisReady(boolean axisReady) {
        isAxisReady = axisReady;
    }

    public void setNumberOfMeasure(int numberOfMeasure) {
        this.numberOfMeasure = numberOfMeasure;
    }

    public void setControlPointId(String controlPointId) {
        this.controlPointId = controlPointId;
    }

    public void setControlPointY(String controlPointY) {
        this.controlPointY = controlPointY;
    }

    public void setControlPointX(String controlPointX) {
        this.controlPointX = controlPointX;
    }

    @NonNull
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
                ", rotationSide='" + rotationSide + '\'' +
                ", isHoleReady=" + isHoleReady +
                ", isAxisReady=" + isAxisReady +
                ", numberOfMeasure=" + numberOfMeasure +
                ", controlPointId='" + controlPointId + '\'' +
                ", controlPointY='" + controlPointY + '\'' +
                ", controlPointX='" + controlPointX + '\'' +
                '}';
    }
}

