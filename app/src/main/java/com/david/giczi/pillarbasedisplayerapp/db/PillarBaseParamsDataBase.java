package com.david.giczi.pillarbasedisplayerapp.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {PillarBaseParams.class}, version = 3)
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

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add the new column "age" with a default value
            database.execSQL("ALTER TABLE pillar_base_params ADD COLUMN HoleReady INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE pillar_base_params ADD COLUMN AxisReady INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE pillar_base_params ADD COLUMN NumberOfMeasure INTEGER NOT NULL DEFAULT 0");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add the new column "age" with a default value
            database.execSQL("ALTER TABLE pillar_base_params ADD COLUMN ControlPointId TEXT");
            database.execSQL("ALTER TABLE pillar_base_params ADD COLUMN ControlPointY TEXT");
            database.execSQL("ALTER TABLE pillar_base_params ADD COLUMN ControlPointX TEXT");
        }
    };
}
