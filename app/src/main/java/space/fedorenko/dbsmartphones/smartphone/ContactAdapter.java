package space.fedorenko.dbsmartphones.smartphone;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import space.fedorenko.dbsmartphones.R;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    Activity activity;
    ArrayList<ContactModel> arrayList;

    public ContactAdapter(Activity activity, ArrayList<ContactModel> arrayList){
        this.activity = activity;
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactModel contactModel = arrayList.get(position);
        holder.textPhone.setText(contactModel.getPhone());
        holder.textName.setText(contactModel.getName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textPhone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.name);
            textPhone = itemView.findViewById(R.id.number);
        }
    }
}
