package com.bergerlavy.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "BolePoDB.db";

	private static final String SQL_CREATE_MEETINGS_TABLE = 
			"CREATE TABLE " + DbContract.Meetings.TABLE_NAME + " (" +
					DbContract.Meetings._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
					DbContract.Meetings.COLUMN_NAME_MEETING_NAME + " TEXT NOT NULL" + "," +
					DbContract.Meetings.COLUMN_NAME_MEETING_DATE + " DATE NOT NULL" + "," +
					DbContract.Meetings.COLUMN_NAME_MEETING_TIME + " TIME NOT NULL" + "," +
					DbContract.Meetings.COLUMN_NAME_MEETING_LOCATION + " TEXT NOT NULL" + "," +
					DbContract.Meetings.COLUMN_NAME_MEETING_SHARE_LOCATION_TIME + " TIME NOT NULL" + "," +
					DbContract.Meetings.COLUMN_NAME_MEETING_MANAGER + " TEXT NOT NULL" + "," +
					DbContract.Meetings.COLUMN_NAME_MEETING_HASH + " TEXT NOT NULL" + " )";
	
	private static final String SQL_CREATE_PARTICIPANTS_TABLE = 
			"CREATE TABLE " + DbContract.Participants.TABLE_NAME + " (" +
					DbContract.Participants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
					DbContract.Participants.COLUMN_NAME_PARTICIPANT_PHONE + " TEXT NOT NULL" + "," +
					DbContract.Participants.COLUMN_NAME_PARTICIPANT_NAME + " TEXT NOT NULL" + "," +
					DbContract.Participants.COLUMN_NAME_PARTICIPANT_CREDENTIALS + " TEXT NOT NULL" + "," +
					DbContract.Participants.COLUMN_NAME_PARTICIPANT_RSVP + " TEXT NOT NULL" + "," +
					DbContract.Participants.COLUMN_NAME_PARTICIPANT_SHARE_LOCATION_STATUS + " TEXT NOT NULL" + "," +
					DbContract.Participants.COLUMN_NAME_PARTICIPANT_HASH + " TEXT NOT NULL" + "," + 
					DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + " INTEGER NOT NULL" + ", " +
					"FOREIGN KEY(" + DbContract.Participants.COLUMN_NAME_PARTICIPANT_MEETING_ID + ") " +
					"REFERENCES " + DbContract.Meetings.TABLE_NAME + "(" + DbContract.Meetings._ID + ") " +
					"ON DELETE CASCADE" + " )";

	private static final String SQL_DELETE_MEETINGS_TABLE = 
			"DROP TABLE IF EXISTS " + DbContract.Meetings.TABLE_NAME;
	
	private static final String SQL_DELETE_PARTICIPANTS_TABLE =
			"DROP TABLE IF EXISTS " + DbContract.Participants.TABLE_NAME;

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (!db.isReadOnly())
			db.execSQL("PRAGMA foreign_keys = ON");
		
		db.execSQL(SQL_CREATE_MEETINGS_TABLE);
		db.execSQL(SQL_CREATE_PARTICIPANTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_MEETINGS_TABLE);
		db.execSQL(SQL_DELETE_PARTICIPANTS_TABLE);
		onCreate(db);

	}

}
