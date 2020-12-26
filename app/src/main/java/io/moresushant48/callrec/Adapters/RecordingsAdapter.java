package io.moresushant48.callrec.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import io.moresushant48.callrec.MainActivity;
import io.moresushant48.callrec.R;

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.ViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> arrayOfRecordings;

    OnItemClickListener onItemClickListener;

    public RecordingsAdapter(@NonNull Context context, ArrayList<HashMap<String, String>> arrayOfRecordings, OnItemClickListener clickListener) {
        this.context = context;
        this.arrayOfRecordings = arrayOfRecordings;
        this.onItemClickListener = clickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtRecordingName;
        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            txtRecordingName = itemView.findViewById(R.id.txtRecordingName);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClickListener(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_recording_view, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtRecordingName.setText(arrayOfRecordings.get(position).get("song_name"));
    }

    @Override
    public int getItemCount() {
        return arrayOfRecordings.size();
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position);
    }
}
