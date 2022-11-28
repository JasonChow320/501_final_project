package com.cs501.project.Model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SQLDataBase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "userDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USER = "account";
    private static final String ACCOUNT_ID = "ACCOUNT_ID";

    public SQLDataBase( Context context ) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    public void onCreate( SQLiteDatabase db ) {
        // build sql create statement
        String sqlCreate = "create table " + TABLE_USER + "(" + ACCOUNT_ID;
        sqlCreate += " text)";

        db.execSQL( sqlCreate );
    }

    public void onUpgrade( SQLiteDatabase db,
                           int oldVersion, int newVersion ) {
        // Drop old table if it exists
        db.execSQL( "drop table if exists " + TABLE_USER );
        // Re-create tables
        onCreate( db );
    }

    public void onUpgrade() {
        SQLiteDatabase db = this.getWritableDatabase( );
        // Drop old table if it exists
        db.execSQL( "drop table if exists " + TABLE_USER );
        // Re-create tables
        onCreate( db );
    }


    public void insert( String account_id ) {
        System.out.println(account_id);
        SQLiteDatabase db = this.getWritableDatabase( );
        String sqlInsert = "insert into " + TABLE_USER;
        sqlInsert += " values('" + account_id + "' )";
        System.out.println(sqlInsert);
        db.execSQL( sqlInsert );
        db.close( );
    }
//
//    public void deleteById( int id ) {
//        SQLiteDatabase db = this.getWritableDatabase( );
//        String sqlDelete = "delete from " + TABLE_USER;
//        sqlDelete += " where " + EMAIL + " = " + id;
//
//        db.execSQL( sqlDelete );
//        db.close( );
//    }

//    public void updateById( int id, String name, double price ) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        String sqlUpdate = "update " + TABLE_USER;
//        sqlUpdate += " set " + FIRSTNAME + " = '" + name + "', ";
//        sqlUpdate += LASTNAME + " = '" + price + "'";
//        sqlUpdate += " where " + EMAIL + " = " + id;
//
//        db.execSQL( sqlUpdate );
//        db.close( );
//    }

    public ArrayList<String> selectAll( ) {
        String sqlQuery = "select * from " + TABLE_USER;

        SQLiteDatabase db = this.getWritableDatabase( );
        Cursor cursor = db.rawQuery( sqlQuery, null );

        ArrayList<String> account = new ArrayList<String>( );
        while( cursor.moveToNext( ) ) {
            String curr = new String( cursor.getString( 0 ));
            account.add( curr );
        }
        db.close( );
        return account;
    }
}
