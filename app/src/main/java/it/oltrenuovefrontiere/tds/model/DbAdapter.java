package it.oltrenuovefrontiere.tds.model;

/**
 * Created by Utente on 01/12/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;

import it.oltrenuovefrontiere.tds.filer.FileEnumerator;
import it.oltrenuovefrontiere.tds.helper.DatabaseHelper;

public class DbAdapter {
    public static final String KEY_SHEETID = "rowid";
    public static final String KEY_NAME = "name";
    public static final String KEY_PATH = "path";
    public static final String KEY_TYPE = "type";
    public static final String KEY_LINEA = "linea";
    @SuppressWarnings("unused")
    private static final String LOG_TAG = DbAdapter.class.getSimpleName();
    // Database fields
    private static final String DATABASE_TABLE = "technical";
    private Context context;
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public DbAdapter(Context context) {
        this.context = context;
    }

    public DbAdapter open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    private ContentValues createContentValues(String name, String path, String type, String linea) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_PATH, path);
        values.put(KEY_TYPE, type);
        values.put(KEY_LINEA, linea);

        return values;
    }

    //create a contact
    public long createTechnical(String name, String path, String type, String linea) {
        ContentValues initialValues = createContentValues(name, path, type, linea);
        return database.insertOrThrow(DATABASE_TABLE, null, initialValues);
    }

    //update a contact
    public boolean updateTechnical(long sheetID, String name, String path, String type, String linea) {
        ContentValues updateValues = createContentValues(name, path, type, linea);
        return database.update(DATABASE_TABLE, updateValues, KEY_SHEETID + "=" + sheetID, null) > 0;
    }

    //delete a contact
    public boolean deleteTechnical(long contactID) {
        return database.delete(DATABASE_TABLE, KEY_SHEETID + "=" + contactID, null) > 0;
    }

    //fetch all contacts
    public Cursor fetchAllTechnical() {
        return database.query(DATABASE_TABLE, new String[]{KEY_SHEETID, KEY_NAME, KEY_PATH, KEY_TYPE, KEY_LINEA}, null, null, null, null, null);
    }

    public Cursor fetchTecnicalByFilter(String name) {
        return database.query(true, DATABASE_TABLE, new String[]{
                        KEY_SHEETID, KEY_NAME, KEY_PATH, KEY_TYPE, KEY_LINEA},
                KEY_NAME + " like '%" + name + "%'", null, null, null, null, null);
    }

    //fetch contacts filter by a string
    public Cursor fetchTechnicalByFilter(String name, String type) {
        return database.query(true, DATABASE_TABLE, new String[]{
                        KEY_SHEETID, KEY_NAME, KEY_PATH, KEY_TYPE, KEY_LINEA},
                KEY_NAME + " like '%" + name + "%' AND " + KEY_TYPE + " like '%" + type + "%'", null, null, null, null, null);
    }

    public Cursor fetchTechnicalByID(String filter) {
        return database.query(true, DATABASE_TABLE, new String[]{
                        KEY_SHEETID, KEY_NAME, KEY_PATH, KEY_TYPE, KEY_LINEA},
                KEY_SHEETID + " like '%" + filter + "%'", null, null, null, null, null);
    }

    public Cursor fetchTechnicalWithBuilder(TechnicalQueryBuilder _builder) {
        return database.query(DATABASE_TABLE, new String[]{
                        KEY_SHEETID, KEY_NAME, KEY_PATH, KEY_TYPE, KEY_LINEA},
                _builder.getQueryString(), null, null, null, null, null);
    }

    private void resetDB() {
        database.execSQL("DROP TABLE IF EXISTS technical");
        database.execSQL(DatabaseHelper.DATABASE_CREATE);
    }

    public void aggiornaDB() {
        resetDB();
        FileEnumerator.listToDB(this);
        Toast.makeText(context, "Ho aggiornato il database", Toast.LENGTH_LONG).show();
    }


    public ArrayList<String> fetchDistinctLinea() {
        Cursor mCursor = database.query(true, DATABASE_TABLE, new String[]{KEY_LINEA}, null, null, null, null, KEY_LINEA, null);
        ArrayList<String> lineeList = new ArrayList<>();
        lineeList.add("Tutte le linee");
        mCursor.moveToFirst();
        if (!mCursor.isNull(0)) {
            do {
                String _linea = mCursor.getString(0);
                lineeList.add(_linea.replaceFirst("Linea ", ""));
                mCursor.moveToNext();
            } while (!mCursor.isLast());
        }
        mCursor.close();
        return lineeList;
    }
}
