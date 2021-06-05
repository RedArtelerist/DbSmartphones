package space.fedorenko.dbsmartphones.smartphone;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import space.fedorenko.dbsmartphones.R;

public class SmartphoneAdapter extends RecyclerView.Adapter<SmartphoneAdapter.SmartphoneViewHolder> {
    private final Context mContext;
    private final List<Smartphone> mSmartphones;
    private OnItemClickListener mListener;

    public SmartphoneAdapter(Context context, List<Smartphone> uploads) {
        mContext = context;
        mSmartphones = uploads;
    }
    @NonNull
    @Override
    public SmartphoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.smartphone_item, parent, false);
        return new SmartphoneViewHolder(v);
    }
    @Override
    public void onBindViewHolder(SmartphoneViewHolder holder, int position) {
        Smartphone uploadCurrent = mSmartphones.get(position);
        holder.textViewModel.setText(uploadCurrent.getModel());
        holder.textViewCompany.setText(uploadCurrent.getCompany());
        holder.textViewScreen.setText(String.valueOf(uploadCurrent.getScreen()));
        holder.textViewPrice.setText(String.valueOf(uploadCurrent.getPrice()));
        holder.textViewAddress.setText(uploadCurrent.getAddress());

        Picasso.get().load(uploadCurrent.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .fit()
                .into(holder.imageView);
    }
    @Override
    public int getItemCount() {
        return mSmartphones.size();
    }

    public class SmartphoneViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView textViewModel;
        public TextView textViewCompany;
        public TextView textViewScreen;
        public TextView textViewPrice;
        public TextView textViewAddress;
        public ImageView imageView;

        public SmartphoneViewHolder(View itemView) {
            super(itemView);
            textViewModel = itemView.findViewById(R.id.tViewModel);
            textViewCompany = itemView.findViewById(R.id.tViewCompany);
            textViewScreen = itemView.findViewById(R.id.tViewScreen);
            textViewPrice = itemView.findViewById(R.id.tViewPrice);
            textViewAddress = itemView.findViewById(R.id.tViewAddress);
            imageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem edit = menu.add(Menu.NONE, 1, 1, "Edit");
            MenuItem route = menu.add(Menu.NONE, 2, 2, "Route");
            MenuItem delete = menu.add(Menu.NONE, 3, 3, "Delete");
            edit.setOnMenuItemClickListener(this);
            route.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            mListener.onEditClick(position);
                            return true;
                        case 2:
                            mListener.onRouteClick(position);
                            return true;
                        case 3:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onEditClick(int position);
        void onRouteClick(int position);
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}