package DB;

/**
 *http://blog.naver.com/PostView.nhn?blogId=nife0719&logNo=221035148567&parentCategoryNo=&categoryNo=26&viewDate=&isShowPopularPosts=false&from=postView
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Second_MainDBHelper {

    private static final String DATABASE_NAME = "Second_Main.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(Second_MainDB.CreateDB._CREATE0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+ Second_MainDB.CreateDB._TABLENAME0);
            onCreate(db);
        }
    }


    public Second_MainDBHelper(Context context){
        this.mCtx = context;
    }


    public Second_MainDBHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }


    public void create(){
        mDBHelper.onCreate(mDB);
    }


    public void close(){
        mDB.close();
    }


    // Insert DB
    public long insertColumn(String userid, String name){
        ContentValues values = new ContentValues();
        values.put(Second_MainDB.CreateDB.USERID, userid);
        return mDB.insert(Second_MainDB.CreateDB._TABLENAME0, null, values);
    }


    // Delete All
    public void deleteAllColumns() {
        mDB.delete(Second_MainDB.CreateDB._TABLENAME0, null, null);
    }


    // Select DB
    public Cursor selectColumns(){
        return mDB.query(Second_MainDB.CreateDB._TABLENAME0, null, null, null, null, null, null);
    }

}
