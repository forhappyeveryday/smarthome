package com.android.testjetty.testDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelpler extends SQLiteOpenHelper{
	private static final int VERSION = 1;//�汾  
    private static final String DB_NAME = "people.db";//���ݿ���  
    public static final String STUDENT_TABLE = "student";//����  
    public static final String _ID = "_id";//���е�����  
    public static final String NAME = "name";//���е�����  
    public static final String AGE = "age";//���е�����  
    //�������ݿ���䣬STUDENT_TABLE��_ID ��NAME��ǰ��Ҫ�ӿո�  
	private static final String CREATE_TABLE = 
			"create table " + STUDENT_TABLE + " ( " + 
				_ID + " Integer primary key autoincrement," + 
				NAME + " text," +
				AGE + " Integer)";
//	private static final String INSERT_DATA =
//			"insert into " + STUDENT_TABLE + "(name, age) values(" +
//			 "'zhangsan', 0);";
	public DBHelpler(Context context) {
		super(context, DB_NAME, null, VERSION);
	}
	public DBHelpler(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

    //���ݿ��һ�α�����ʱ����   
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        db.execSQL(CREATE_TABLE);
        
        ContentValues values = new ContentValues();  
        values.put(NAME, "zhangsan");  
        values.put(AGE, 0);
        db.insert(STUDENT_TABLE, null, values);
    }  
  
    //�汾����ʱ������   
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
  
    } 

}
