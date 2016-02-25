package in.easynote.easynote.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.easynote.easynote.EditNoteActivity;
import in.easynote.easynote.Listeners.ItemTouchHelperAdapter;
import in.easynote.easynote.Model.Note;
import in.easynote.easynote.R;

/**
 * Created by AKG002 on 12-02-2016.
 */
public class CompactNoteAdapter extends RecyclerView.Adapter<CompactNoteAdapter.ViewHolder> implements ItemTouchHelperAdapter{
    Context context;
    ArrayList<Note> noteArrayList;

    public CompactNoteAdapter(Context context,ArrayList<Note> noteArrayList) {
        this.context = context;
        this.noteArrayList = noteArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notes_item,parent,false);
        if(viewType == 0)
            view.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String timestamp = noteArrayList.get(position).timestamp ;
        String dateWithoutTime = "";
        try {
            Date date = sdf1.parse(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            dateWithoutTime = sdf.format(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        holder.timestamp.setText(dateWithoutTime);
        holder.title.setText(noteArrayList.get(position).title);
    }

    @Override
    public int getItemViewType(int position) {
        return noteArrayList.get(position).uploadedToServer;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return noteArrayList.size();
    }

    @Override
    public void onItemDismiss(int position) {
        noteArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeRemoved(position,noteArrayList.size());
    }

    public void itemSetChanged(){
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title,timestamp;
        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.note_title);
            timestamp = (TextView)itemView.findViewById(R.id.timestamp);
        }
    }
}
