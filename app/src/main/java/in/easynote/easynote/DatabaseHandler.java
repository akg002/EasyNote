package in.easynote.easynote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import in.easynote.easynote.Model.Note;

/**
 * Created by AKG002 on 12-02-2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    public static final int  DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "easynote";
    public static final String TABLE_NOTES = "notes";

    public static final String KEY_TITLE = "title";
    public static final String KEY_NOTE_TEXT = "note_text";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_UPLOADED_TO_SERVER = "uploaded_to_server";
    public SQLiteDatabase writableDatabase ;
    public SQLiteDatabase readableDatabase;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTES_TABLE = "Create table "+TABLE_NOTES+"("+KEY_TITLE+" VARCHAR (15),"+
                KEY_NOTE_TEXT+" TEXT ,"+KEY_TIMESTAMP+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+
                KEY_UPLOADED_TO_SERVER+" INT DEFAULT 0 );";
        db.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         db.execSQL("DROP TABLE IF EXISTS "+TABLE_NOTES);
        onCreate(db);
    }

    public ArrayList<Note> getAllNotes(){
        ArrayList<Note> noteArrayList = new ArrayList<>();
        String selectQuery = "Select * From "+TABLE_NOTES+" ORDER BY "+KEY_TIMESTAMP+" DESC ;";
        readableDatabase = this.getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                note.noteText = cursor.getString(cursor.getColumnIndex(KEY_NOTE_TEXT));
                note.timestamp = cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP));
                note.uploadedToServer = cursor.getInt(cursor.getColumnIndex(KEY_UPLOADED_TO_SERVER));
                noteArrayList.add(note);
            } while (cursor.moveToNext());
        }
        return noteArrayList;
    }

    public ArrayList<Note> getNotesToUpload(){
        ArrayList<Note> noteArrayList = new ArrayList<>();
        String selectQuery = "Select * From "+TABLE_NOTES+" WHERE "+KEY_UPLOADED_TO_SERVER+"=0 ORDER BY "+KEY_TIMESTAMP+" DESC ;";
        readableDatabase = this.getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                note.noteText = cursor.getString(cursor.getColumnIndex(KEY_NOTE_TEXT));
                note.timestamp = cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP));
                noteArrayList.add(note);
            } while (cursor.moveToNext());
        }
         writableDatabase = this.getWritableDatabase();
        writableDatabase.execSQL("UPDATE " + TABLE_NOTES + " SET " + KEY_UPLOADED_TO_SERVER + "= 1 WHERE "
                +KEY_UPLOADED_TO_SERVER+"=0  ;");
        writableDatabase.close();
        return noteArrayList;
    }

    public void addNote(Note note){
         
         writableDatabase = this.getWritableDatabase();
        writableDatabase.execSQL("INSERT INTO "+TABLE_NOTES+"("+KEY_TITLE+","+
                 KEY_NOTE_TEXT+","+KEY_TIMESTAMP+","+KEY_UPLOADED_TO_SERVER+") " +
                 "VALUES (\""+note.title+"\",\""+note.noteText+"\",\'"+note.timestamp+"\'," +
                note.uploadedToServer+");");
          writableDatabase.close();
    }
    public void addNewNote(Note note){
        writableDatabase = this.getWritableDatabase();
        writableDatabase.execSQL("INSERT INTO "+TABLE_NOTES+"("+KEY_TITLE+","+KEY_NOTE_TEXT+","+KEY_TIMESTAMP+","+
                KEY_UPLOADED_TO_SERVER+") VALUES (\""+note.title+"\",\""+note.noteText+"\",\'"+note.timestamp+"\'," +
                note.uploadedToServer+")");
        writableDatabase.close();
    }

    public void updateNote(Note note){
         
         writableDatabase = this.getWritableDatabase();
        writableDatabase.execSQL("UPDATE " + TABLE_NOTES + " SET " + KEY_TITLE + "= \"" + note.title + "\"," +
                KEY_NOTE_TEXT + "= \"" + note.noteText + "\" ," + KEY_TIMESTAMP + "= CURRENT_TIMESTAMP WHERE " +
                KEY_TIMESTAMP + " = '" + note.timestamp+"\' ;");
          writableDatabase.close();
    }

    public void deleteNote(Note note){
         
         writableDatabase = this.getWritableDatabase();
        writableDatabase.execSQL("DELETE FROM "+TABLE_NOTES+" WHERE  "+
                KEY_TIMESTAMP + " = '" + note.timestamp+" ;");
         writableDatabase.close();
    }


}
