package com.david.giczi.pillarbasedisplayerapp.utils;

import java.util.Comparator;

public class ProjectNameComparator implements Comparator<String> {


    @Override
    public int compare(String projectName1, String projectName2) {

        String[] projectName1Data = projectName1.split("_");
        String[] projectName2Data = projectName2.split("_");
        String[] project1NumberData =
                projectName1Data[projectName1Data.length - 2]
                        .substring(0, projectName1Data[projectName1Data.length - 2].lastIndexOf("-")).split("-");
        String[] project2NumberData =
                projectName2Data[projectName2Data.length - 2]
                        .substring(0, projectName2Data[projectName2Data.length - 2].lastIndexOf("-")).split("-");
        try{
            Integer.parseInt(project1NumberData[0]);
            Integer.parseInt(project2NumberData[0]);

        }catch (NumberFormatException e){
            return compare(projectName1, projectName2);
        }

        if( project1NumberData.length == project2NumberData.length ){

            if( project1NumberData.length == 1 ){
                return Integer.compare(Integer.parseInt(project1NumberData[0]), Integer.parseInt(project2NumberData[0]) );
            }
            else {

                int firstCompare = Integer.compare(Integer.parseInt(project1NumberData[0]), Integer.parseInt(project2NumberData[0]) );

                if( firstCompare != 0 ){
                    return  firstCompare;
                }

                return Integer.compare(Integer.parseInt(project1NumberData[1]), Integer.parseInt(project2NumberData[1]) );

            }
        }

        int firstCompare = Integer.compare(Integer.parseInt(project1NumberData[0]), Integer.parseInt(project2NumberData[0]) );

        if( firstCompare != 0 ){
            return firstCompare;
        }

        if( project1NumberData.length > project2NumberData.length ){

            return Integer.compare(Integer.parseInt(project1NumberData[1]), 0);

        }

        return Integer.compare(0, Integer.parseInt(project2NumberData[0]) );
    }
}
