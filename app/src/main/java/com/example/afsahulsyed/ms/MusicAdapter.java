package com.example.afsahulsyed.ms;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by afsahulsyed on 8/6/17.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyHolder> {

    final String TAG = "sMess";
    List<Music> musicList;
    public interface OnItemClickListener {
        void OnItemClick(View view, int position);

        void OnItemLongClick(View view, int position);
    }

    public OnItemClickListener mOnItemClickListener;

    public void SetOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public MusicAdapter(List musicList) {
        super();
        this.musicList = musicList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_layout, parent, false);
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {

        //Log.d(TAG, holder + "  -----" + holder.tTitle + "--------" + musicList);
        holder.tTitle.setText(musicList.get(position).getTitle());
        holder.tArtist.setText(musicList.get(position).getArtist());
        holder.tDuration.setText(musicList.get(position).getDuration());

        if (mOnItemClickListener != null) {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.OnItemClick(holder.itemView, position);
                }

            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.OnItemLongClick(holder.itemView, position);
                    return false;
                }
            });
        }

    }
    public void addData(int position,Music map) {
    }

    public void removeData(int position) {
        musicList.remove(position);
        notifyItemRemoved(position);
    }
    public void RefreshView(){
        for(int i=0;i<getItemCount();i++)
            removeData(i);
        for(int i=0;i<getItemCount();i++)
            addData(i,musicList.get(i));
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public TextView tTitle;
        public TextView tArtist;
        public TextView tDuration;

        public MyHolder(View View) {
            super(View);
            tTitle = (TextView) View.findViewById(R.id.title);
            tArtist = (TextView) View.findViewById(R.id.artist);
            tDuration = (TextView) View.findViewById(R.id.duration);
        }
    }
}
