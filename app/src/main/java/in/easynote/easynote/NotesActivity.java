package in.easynote.easynote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.easynote.easynote.Adapters.CompactNoteAdapter;
import in.easynote.easynote.Control.FetchData;
import in.easynote.easynote.Control.SendData;
import in.easynote.easynote.Listeners.FetchDataListener;
import in.easynote.easynote.Listeners.ItemTouchHelperAdapter;
import in.easynote.easynote.Model.Note;

public class NotesActivity extends AppCompatActivity  implements FetchDataListener{
    ArrayList<Note> noteArrayList = new ArrayList<Note>();
    RecyclerView notesRecyclerView;
    ActionBar mActionBar;
    CompactNoteAdapter noteAdapter;
    String LOG_TAG = "Note central";
    SharedPreferences sp;
    FetchData fetchData;
    int op;
    DatabaseHandler databaseHandler ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        databaseHandler = new DatabaseHandler(this);
        mActionBar = getSupportActionBar();
        sp = getApplicationContext().getSharedPreferences("LogIn",0);
        getNotesFromDB();
        noteAdapter = new CompactNoteAdapter(this, noteArrayList);
        notesRecyclerView = (RecyclerView)findViewById(R.id.noteList);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        notesRecyclerView.setAdapter(noteAdapter);
        notesRecyclerView.addOnItemTouchListener(new RecyclerTouchListner());
            SimpleItemTouchHelperCallBack callBack = new SimpleItemTouchHelperCallBack(noteAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBack);
        itemTouchHelper.attachToRecyclerView(notesRecyclerView);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notes_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()){
            case R.id.new_note : i = new Intent(this,EditNoteActivity.class);
                i.putExtra("Operation", "new_note");
                Note note = new Note();
                note.title = "Note Title";
                note.noteText = "Note Text here";
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentTimeStamp = dateFormat.format(new Date());
                note.timestamp = currentTimeStamp;
                i.putExtra("note",note);
                databaseHandler.addNewNote(note);
                startActivity(i);
                finish();
                break;
            case R.id.fetch :
                op = FetchData.GET_NOTES;
                fetchData = new FetchData(this,this,op);
                fetchData.setGetNotesArgs(sp.getString("username",""));
                fetchData.execute();
                break;
            case R.id.upload :
                op = 4;
                uploadNotes();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void uploadNotes(){
        String username  = sp.getString("username","");
        String data = "";
        ArrayList<Note> notesToUpload = databaseHandler.getNotesToUpload();
        JSONArray array = new JSONArray();
        try {
            for (int i=0;i<notesToUpload.size();++i) {
                Note note = notesToUpload.get(i);
                JSONObject noteJSON = new JSONObject();
                noteJSON.put("title", note.title);
                noteJSON.put("note_text", note.noteText);
                noteJSON.put("timestamp", note.timestamp);
                array.put(i,noteJSON);
            }
            data = URLEncoder.encode("username","UTF-8") +"="+URLEncoder.encode(username,"UTF-8")+"&"+
                    URLEncoder.encode("json","UTF-8")+"="+URLEncoder.encode(array.toString(),"UTF-8");
            SendData sendNotes = new SendData(this,this,data);
            sendNotes.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        getNotesFromDB();
        noteAdapter= new CompactNoteAdapter(this,noteArrayList);
        notesRecyclerView.setAdapter(noteAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNotesFromDB();
        noteAdapter.notifyDataSetChanged();
        Log.d(LOG_TAG,"Activity resumed");
    }

    void getNotesFromDB(){
        noteArrayList = databaseHandler.getAllNotes();
    }

    void itemClicked(int position){
        Note note = noteArrayList.get(position);
        Intent i = new Intent(this,EditNoteActivity.class);
        i.putExtra("Operation","edit_note");
        Log.d(LOG_TAG,"OPeration edit note");
        i.putExtra("note",note);
        startActivity(i);
        finish();
    }

    @Override
    public void preRequest() {

    }

    @Override
    public void postRequest(String result) {

        try {
            if (op == fetchData.GET_NOTES) {
                JSONArray notesArray = new JSONArray(result);
                for (int i = 0; i < notesArray.length(); ++i) {
                    JSONObject note = notesArray.getJSONObject(i);
                    Note n = new Note(note.getString("title"),
                            note.getString("note_text"), note.getString("timestamp"));
                    n.uploadedToServer = 1;

                    databaseHandler.addNote(n);
                }
                getNotesFromDB();
                noteAdapter = new CompactNoteAdapter(this,noteArrayList);
                notesRecyclerView.setAdapter(noteAdapter);
            }else {
                if (result.equals("success")){
                    Toast.makeText(this,"Notes uploaded",Toast.LENGTH_LONG).show();
                    getNotesFromDB();
                    noteAdapter.itemSetChanged();
                }
                else {
                    Toast.makeText(this, "Error in connection", Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public class RecyclerTouchListner implements RecyclerView.OnItemTouchListener{
        GestureDetector SingleTapDetector = new GestureDetector(getApplicationContext(),new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View view = rv.findChildViewUnder(e.getX(),e.getY());
            if (view!=null && SingleTapDetector.onTouchEvent(e)){
                itemClicked(rv.getChildLayoutPosition(view));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    class SimpleItemTouchHelperCallBack extends ItemTouchHelper.Callback {
        ItemTouchHelperAdapter adapter;
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(0, swipeFlags);
        }

        public SimpleItemTouchHelperCallBack(ItemTouchHelperAdapter adapter){
            this.adapter = adapter;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int pos = viewHolder.getAdapterPosition();
            Note note = noteArrayList.get(pos);
            DatabaseHandler handler = new DatabaseHandler(NotesActivity.this);
            handler.deleteNote(note);
            adapter.onItemDismiss(pos);

            Toast.makeText(NotesActivity.this,"Note removed",Toast.LENGTH_LONG).show();
        }
    }
}
