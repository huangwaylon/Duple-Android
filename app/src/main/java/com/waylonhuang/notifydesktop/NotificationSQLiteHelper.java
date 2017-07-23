package com.waylonhuang.notifydesktop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.waylonhuang.notifydesktop.history.HistoryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by waylon on 9/5/15.
 */
public class NotificationSQLiteHelper extends SQLiteOpenHelper {
    private static final String TAG = NotificationSQLiteHelper.class.getSimpleName();

    private static NotificationSQLiteHelper mDBInstance = null;

    // database version
    private static final int DATABASE_VERSION = 1;
    // database name
    private static final String DATABASE_NAME = "NotificationDB";
    private static final String NOTIFICATION_TABLE = "notifications";
    private static final String _id = "_id";
    private static final String appName = "appName";
    private static final String packageName = "packageName";
    private static final String title = "title";
    private static final String text = "text";
    private static final String time = "time";

    private static final String[] COLUMNS = {_id, appName, packageName, title, text, time};

    public static NotificationSQLiteHelper getInstance(Context context) {
        if (mDBInstance == null) {
            mDBInstance = new NotificationSQLiteHelper(context);
        }
        return mDBInstance;
    }

    public NotificationSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");

        // SQL statement to create book table
        String CREATE_CLASS_TABLE = "CREATE TABLE IF NOT EXISTS " + NOTIFICATION_TABLE + " ( "
                + _id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + appName + " TEXT, "
                + packageName + " TEXT, "
                + title + " TEXT, "
                + text + " TEXT, "
                + time + " INTEGER)";
        db.execSQL(CREATE_CLASS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + NOTIFICATION_TABLE + ";");
        this.onCreate(db);
    }

    public void addNotificationItem(HistoryItem item) {
        Log.d(TAG, "addNotificationItem");
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(_id, item.get_id());
        values.put(appName, item.getAppName());
        values.put(packageName, item.getPackageName());
        values.put(title, item.getTitle());
        values.put(text, item.getText());
        values.put(time, item.getTime());

        db.insert(NOTIFICATION_TABLE, null, values);
        db.close();
    }

    public void addNotificationItem(long _id, String appName, String packageName, String title, String text, String time) {
        Log.d(TAG, "addNotificationItem");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NotificationSQLiteHelper._id, _id);
        values.put(NotificationSQLiteHelper.appName, appName);
        values.put(NotificationSQLiteHelper.packageName, packageName);
        values.put(NotificationSQLiteHelper.title, title);
        values.put(NotificationSQLiteHelper.text, text);
        values.put(NotificationSQLiteHelper.time, time);

        db.insert(NOTIFICATION_TABLE, null, values);

        // close database transaction
        db.close();
    }

    public List<HistoryItem> getAllItems(Context context) {
        Log.d(TAG, "getAllItems");

        List<HistoryItem> itemList = new ArrayList<>();

        String query = "SELECT * FROM " + NOTIFICATION_TABLE + " ORDER BY time DESC;";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                long _id = cursor.getLong(0);
                String appName = cursor.getString(1);
                String packageName = cursor.getString(2);
                String title = cursor.getString(3);
                String text = cursor.getString(4);
                long time = cursor.getLong(5);

                HistoryItem item = new HistoryItem(context, _id, appName, packageName, title, text, time);
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return itemList;
    }

    public void deleteAllItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + NOTIFICATION_TABLE + ";");
        db.close();
    }
}
