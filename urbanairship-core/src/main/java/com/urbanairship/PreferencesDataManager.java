//package com.urbanairship;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//
//import com.urbanairship.util.DataManager;
//
//import androidx.annotation.NonNull;
//
///**
// * A database manager to help create, open, and modify the preferences
// * database
// */
//class PreferencesDataManager extends DataManager {
//

//    static final int DATABASE_VERSION = 1;
//
//    public PreferencesDataManager(@NonNull Context context, @NonNull String appKey) {
//        super(context, appKey, DATABASE_NAME, DATABASE_VERSION);
//    }
//
////    @Override
////    protected void onCreate(@NonNull SQLiteDatabase db) {
////        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
////                + COLUMN_NAME_KEY + " TEXT PRIMARY KEY, "
////                + COLUMN_NAME_VALUE + " TEXT);");
////    }
////
////    @Override
////    protected void onDowngrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
////        // Drop the table and recreate it
////        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
////        onCreate(db);
//    }
//
//}
