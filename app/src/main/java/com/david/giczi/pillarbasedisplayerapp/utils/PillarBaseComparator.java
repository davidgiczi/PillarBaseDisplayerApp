package com.david.giczi.pillarbasedisplayerapp.utils;

import androidx.annotation.NonNull;

import com.david.giczi.pillarbasedisplayerapp.db.PillarBaseParams;
import com.david.giczi.pillarbasedisplayerapp.fragments.PillarBaseFragment;

import java.util.Comparator;

public class PillarBaseComparator implements Comparator<PillarBaseParams> {

    @Override
    public int compare(@NonNull PillarBaseParams base1, @NonNull PillarBaseParams base2) {
        int value1 = 0;
        int value2 = 0;
        try {
            value1 = Integer.parseInt(base1.centerPillarId);
            value2 = Integer.parseInt(base2.centerPillarId);
        }
        catch (NumberFormatException e) {

            String id1 = base1.centerPillarId.split("_")[0].toUpperCase();
            String id2 = base2.centerPillarId.split("_")[0].toUpperCase();

            for (int i = 0; i < id1.length(); i++) {
                if ( Character.isAlphabetic(id1.charAt(i)) ) {
                    value1 += PillarBaseFragment.LETTERS.indexOf(String.valueOf(id1.charAt(i)));
                } else if ( Character.isDigit(id1.charAt(i)) ) {
                    value1 += PillarBaseFragment.NUMBERS.indexOf(String.valueOf(id1.charAt(i)));

                }
            }
            for (int i = 0; i < id2.length(); i++) {
                if ( Character.isAlphabetic(id2.charAt(i)) ) {
                    value2 += PillarBaseFragment.LETTERS.indexOf(String.valueOf(id2.charAt(i)));
                } else if ( Character.isDigit(id2.charAt(i)) ) {
                    value2 += PillarBaseFragment.NUMBERS.indexOf(String.valueOf(id2.charAt(i)));
                }
            }
        }
        return Integer.compare(value1, value2);
    }
}
