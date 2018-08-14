package com.batura.stas.notesaplication.ImageFuncs;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.batura.stas.notesaplication.R;

import java.util.List;


public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.MyViewHolder> {

    private Context mContext;
    private List<ImageMy> images;
    private ImageAdapterListener listener;
    private SparseBooleanArray selectedItems;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView imageV;
        ImageButton button;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.textView);
            imageV = view.findViewById(R.id.imageView);
            button = view.findViewById(R.id.button);
        }

    }

    //определяем адаптер
    public ImagesAdapter(Context context, List<ImageMy> images, ImageAdapterListener listener) {
        this.mContext = context;
        this.images = images;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
//        animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ImageMy image  = images.get(position);

        // displaying text view data
        holder.name.setText(image.getName());
        holder.imageV.setImageDrawable(image.getDraw());

        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));

        // apply click events
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {

        holder.imageV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onImageClicked(position);
            }
        });

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onButtonClicked(position);
            }
        });
    }


    // проверка щелчков
    public interface ImageAdapterListener {
        void onImageClicked(int position);

        void onButtonClicked(int position);

//        void onMessageRowClicked(int position);
//
//        void onRowLongClicked(int position);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void removeData(int position) {
        images.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }
}
