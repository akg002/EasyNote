package in.easynote.easynote;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import in.easynote.easynote.Model.Note;

public class EditNoteActivity extends AppCompatActivity {
ActionBar mActionBar;
    EditText title,noteText;
    String operation;
    ImageView newImage;
    Note note;
    DatabaseHandler databaseHandler ;
    Bitmap tempBitmap;
    LinearLayout imagesLayout;
    ArrayList<Bitmap> noteImages ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        mActionBar = getSupportActionBar();
         mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        title = (EditText)findViewById(R.id.title_editText);
        noteText = (EditText)findViewById(R.id.note_text_editText);
        Intent i = getIntent();
        operation = i.getStringExtra("Operation");
        databaseHandler = new DatabaseHandler(this);
        try{
            note = i.getParcelableExtra("note");
            title.setText(note.title);
            noteText.setText(note.noteText);
            Log.d("Edit note", "title " + note.title);
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_note_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_note || item.getItemId() == android.R.id.home ){
                note.title = title.getText().toString();
                note.noteText = noteText.getText().toString();
                databaseHandler.updateNote(note);

            }
            Intent i = new Intent(this,NotesActivity.class);
        startActivity(i);
            this.finish();
            return true;
    }


}
