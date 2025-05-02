package com.david.giczi.pillarbasedisplayerapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {PillarBaseParams.class}, version = 1)
public abstract class PillarBaseParamsDataBase extends RoomDatabase {


    private static volatile PillarBaseParamsDataBase INSTANCE;
    public abstract PillarBaseParamsDAO PillarBaseParamsDAO();
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static PillarBaseParamsDataBase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (PillarBaseParams.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    PillarBaseParamsDataBase.class, "pillar_base_params_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}
