package com.misoke.proxyman.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.misoke.proxyman.R;
import com.misoke.proxyman.models.ProxyModel;

import java.util.List;


public class ProxyListAdapter extends RecyclerView.Adapter<ProxyListAdapter.ViewHolder> {

    public List<ProxyModel> proxyList;
    private Context context;
    private OnClickListener onClickListener;

    public interface OnClickListener {
        void onClick(ProxyModel proxyModel);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public ProxyListAdapter(List<ProxyModel> proxyList) {
        this.proxyList = proxyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.proxy_list_item, viewGroup, false);
        context = viewGroup.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        final ProxyModel proxyModel = proxyList.get(position);

        viewHolder.setProxyNameText(proxyModel.getName());
        viewHolder.setCountryNameText(proxyModel.getCountry());

        if (proxyModel.isStatus()) {
            viewHolder.setStatusText(context.getResources().getColor(R.color.green), context.getResources().getDrawable(R.drawable.green));
        } else {
            viewHolder.setStatusText(context.getResources().getColor(R.color.blood), context.getResources().getDrawable(R.drawable.red));
        }

        viewHolder.proxyContainer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClickListener.onClick(proxyModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        return proxyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private LinearLayoutCompat proxyContainer;
        private AppCompatTextView txtProxyName;
        private AppCompatTextView txtCountryName;
        private AppCompatTextView txtStatus;
        private AppCompatImageView imgStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            proxyContainer = mView.findViewById(R.id.proxy_container);
        }

        public void setProxyNameText(String proxyName) {
            txtProxyName = mView.findViewById(R.id.txt_proxy_name);
            txtProxyName.setText(proxyName);
        }

        public void setCountryNameText(String countryName) {
            txtCountryName = mView.findViewById(R.id.txt_country_name);
            txtCountryName.setText(countryName);
        }

        public void setStatusText(int color, Drawable image) {
            txtStatus = mView.findViewById(R.id.txt_status);
            imgStatus = mView.findViewById(R.id.img_status);

            txtStatus.setText(context.getResources().getString(R.string.server_status));
            txtStatus.setTextColor(color);
            imgStatus.setImageDrawable(image);
        }

        public void setStatusImage(Drawable drawable) {

        }
    }
}
