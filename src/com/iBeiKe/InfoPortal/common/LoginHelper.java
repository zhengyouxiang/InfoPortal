package com.iBeiKe.InfoPortal.common;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.AESEncryptor;
import com.iBeiKe.InfoPortal.database.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

/**
 * 通用的login数据表访问类，
 * 提供登录信息获取，密码解密，以及存储登录信息，加密密码的功能。
 *
 */
public class LoginHelper {
	Context context;
	Database db;
	String seed = "su4dp5xm37";
	
	public LoginHelper(Context context) {
		this.context = context;
		db = new Database(context);
	}
	
	public ContentValues getLoginData(String name) {
		String[] columns = new String[] {"user", "passwd", "type"};
		String where = "name=\'" + name + "\'";
		ContentValues result = new ContentValues();
		db.read();
		Cursor cursor = db.getCursor("login", columns, where, null);
		if(cursor.getCount() == 0) {
			db.close();
			return null;
		}
		cursor.moveToNext();
		String user = cursor.getString(0);
		String passwd = cursor.getString(1);
		String type = cursor.getString(2);

        try{
            passwd = AESEncryptor.decrypt(seed, passwd);
        }catch(Exception ex){
            Toast.makeText(context, context.getString(R.string.decrypt_error), Toast.LENGTH_SHORT);
            return null;
        }
        result.put(columns[0], user);
        result.put(columns[1], passwd);
        result.put(columns[2], type);
		db.close();
		return result;
	}
	
	/**
	 * Save the universal login data into login table.
	 * @param loginData
	 * Contains:
	 * name, user, passwd, type
	 */
	public void saveLoginData(ContentValues loginData) {
		String tableName = loginData.getAsString("name");
		String passwd = loginData.getAsString("passwd");
        try{
            passwd = AESEncryptor.encrypt(seed, passwd);
        }catch(Exception ex){
            Toast.makeText(context, context.getString(R.string.encrypt_error), Toast.LENGTH_SHORT);
        }
        loginData.put("passwd", passwd);
		db.write();
		db.delete("login", "name=\'" + tableName + "\'");
		db.insert("login", loginData);
		db.close();
	}
}
