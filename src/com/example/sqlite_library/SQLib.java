package com.example.sqlite_library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLib {

	private String database_name;
	private String database_table;
	private final String copy_database_table = "COPYTABLE";
	private int database_version;

	private Context ourContext;
	private DBhelper ourDBhelper;
	private SQLiteDatabase ourDatabse;

	// Columns name defined by the user will be defined in this variable
	private String[] columns_name;

	// TAGS
	private final static String TAG_TABLE_CREATED = "TABLE_CREATED";
	private final static String TAG_VALUE_ADDED = "VALUE_ADDED";
	private final static String TAG_VALUE_DRAWN = "VALUE_DRAWN";
	private final static String TAG_VALUE_DELETE = "VALUE DELETED";
	private final static String TAG_VALUE_DELETE_TABLE = "TABLE DELETED";

	private class DBhelper extends SQLiteOpenHelper {

		private String database_table;

		// Initializes table for DBhelper class
		public DBhelper(Context context, String database_name,
				String database_table, int database_version) {
			super(context, database_name, null, database_version);
			this.database_table = database_table;
			// TODO Auto-generated constructor stub
		}

		// Used to create a dummy table
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + this.database_table + " (" + "key_id"
					+ " INTEGER PRIMARY KEY AUTOINCREMENT); ");
			Log.d(TAG_TABLE_CREATED, this.database_table + " CREATED");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL(" DROP TABLE IF EXISTS " + this.database_table);
			onCreate(db);
		}

	}

	// Initializes table attributes
	public SQLib(Context ourContext, String database_name,
			String database_table, int database_version) {
		this.ourContext = ourContext;
		this.database_name = database_name;
		this.database_table = database_table;
		this.database_version = database_version;
		this.Open();
	}

	public SQLib Open() throws SQLException {
		this.ourDBhelper = new DBhelper(this.ourContext, this.database_name,
				this.database_table, this.database_version);
		this.ourDatabse = this.ourDBhelper.getWritableDatabase();
		return this;
	}

	public void Close() {
		ourDBhelper.close();
	}

	// Used to add columns in the table with the help of columns array as
	// parameterS
	public void Create_columns(Column[] columns) {
		this.columns_name = new String[columns.length];
		String statement = new String();
		for (int i = 0; i < columns.length; i++) {
			if (i == 0) {

				statement = columns[i].data_name + " " + columns[i].data_type
						+ " " + columns[i].data_constraint_name;

			} else {
				statement = statement + ", " + columns[i].data_name + " "
						+ columns[i].data_type + " "
						+ columns[i].data_constraint_name;

			}
			this.columns_name[i] = columns[i].data_name;

		}
		Log.d("CREATEDCOLUMN", "" + statement);

		this.ourDatabse.execSQL("CREATE TABLE " + this.copy_database_table
				+ " (" + statement + ");");
		this.ourDatabse.execSQL("DROP TABLE " + this.database_table);
		this.ourDatabse.execSQL("ALTER TABLE " + this.copy_database_table
				+ " RENAME TO " + this.database_table);
	}

	// Used to add values in the columns defined by user, you have to specify
	// column name in variable column_name
	// and column value in variable column_value
	public void CreateEntry(String[]... values) {
		int max = 0;
		for (int k = 0; k < values.length; k++) {
			if (values[k].length > max) {
				max = values[k].length;
			}
		}
		ContentValues cv = new ContentValues();
		for (int i = 0; i < max; i++) {

			for (int j = 0; j < this.columns_name.length; j++) {

				cv.put(this.columns_name[j], values[j][i]);
				Log.d(TAG_VALUE_ADDED, values[j][i]);
			}
			long id = this.ourDatabse.insert(this.database_table, null, cv);
			Log.d(TAG_VALUE_ADDED, "LAST ID: " + id);
		}

	}

	// Used to retrieve values of a column_name of the table
	public String[] getValues(String column_name) {

		Cursor c = this.ourDatabse.query(this.database_table,
				this.columns_name, null, null, null, null, null);
		int get = c.getColumnIndex(column_name);

		int i = 0, count = 0;
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			count = count + 1;
		}
		String[] result = new String[count];
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			result[i] = c.getString(get);
			Log.d(TAG_VALUE_DRAWN, "Drawn  " + result[i] + " from myTable");
			i++;
		}
		return result;
	}

	// Used to retrieve vale of a particular column(as column_name) and
	// position(as position)
	public String getPosition_data(String column_name, int position) {

		Cursor c = this.ourDatabse.query(this.database_table,
				this.columns_name, null, null, null, null, null);
		int get = c.getColumnIndex(column_name);

		int i = 0;
		int count = 0;
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			count = count + 1;
		}
		String[] result = new String[count];
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			result[i] = c.getString(get);
			i++;

		}
		return result[position];
	}

	// Used to delete an entry specified by column name(as column_name) and
	// value to be deleted(as name)
	public void DeleteEntry(String column_name, String name) {

		this.ourDatabse.delete(this.database_table, column_name + "='" + name
				+ "'", null);
		Log.d(TAG_VALUE_DELETE, "Deleted: " + name);
	}

	public void DeleteAll() {
		this.ourDatabse.delete(this.database_table, null, null);
		Log.d(TAG_VALUE_DELETE_TABLE, "Deleted Table: " + this.database_table);
	}
}
