package com.android.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.datatype.DeviceBaseInfo;
import com.android.datatype.LocationInfo;

public class DeviceBaseInfoHelper extends SQLiteOpenHelper{
	private static final int VERSION = 1;//�汾
    private static final String DB_NAME = "server.db";//���ݿ���
    public static final String TABLE_MOBILEINFO = "mobileInfo";//������Ϣ

    //mobileInfo

	private static final String CREATE_TABLE_MOBILEINFO =
			"create table " + TABLE_MOBILEINFO + " ( " +
				"_ID Integer primary key autoincrement," +
				DeviceBaseInfo.ITEM_IMEI + " text," +
				DeviceBaseInfo.ITEM_PUSHID + " text," +
				DeviceBaseInfo.ITEM_REGDATE + " text," +
				DeviceBaseInfo.ITEM_NICKNAME + " text," +
				DeviceBaseInfo.ITEM_EXTINFO + " text," +

				// last location
				DeviceBaseInfo.ITEM_TIME + " text," +
				DeviceBaseInfo.ITEM_LOCTYPE + " Integer," +
				DeviceBaseInfo.ITEM_LATITUDE + " REAL," +
				DeviceBaseInfo.ITEM_LONGITUDE + " REAL," +
				DeviceBaseInfo.ITEM_RADIUS + " REAL," +
				DeviceBaseInfo.ITEM_ADDR + " TEXT " +
				")";


	public DeviceBaseInfoHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}
	public DeviceBaseInfoHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

    //���ݿ��һ�α�����ʱ����
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MOBILEINFO);
//        ContentValues values = new ContentValues();
//        values.put(DeviceBaseInfo.ITEM_REGDATE, "012345678");
//        values.put(DeviceBaseInfo.ITEM_PUSHID, "pushid");
//        db.insert(TABLE_MOBILEINFO, null, values);
    }

    //�汾����ʱ������
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}