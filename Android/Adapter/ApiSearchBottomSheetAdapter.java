package distributor.w2a.com.distributor.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import distributor.w2a.com.distributor.R;

public class ApiSearchBottomSheetAdapter<X> extends RecyclerView.Adapter<ApiSearchBottomSheetAdapter.OptionBottomSheetHolder> {
    private List<X> list;
    private Context mContext;
    private int[] androidColors;
    private ListOnItemClickListener listOnItemClickListener;

    public interface ListOnItemClickListener<X> {
        void onItemClick(X stateForSpinner);
    }

    public ApiSearchBottomSheetAdapter(Context mContext, List<X> list, ListOnItemClickListener<X> listOnItemClickListener) {
        this.mContext = mContext;
        this.list = list;
        this.listOnItemClickListener = listOnItemClickListener;
        androidColors = mContext.getResources().getIntArray(R.array.android_colors_list);
    }

    public void setList(List<X> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public OptionBottomSheetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_api_search, parent, false);
        OptionBottomSheetHolder viewHolder = new OptionBottomSheetHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ApiSearchBottomSheetAdapter.OptionBottomSheetHolder holder, final int position) {
        X x = list.get(position);
        if (!TextUtils.isEmpty(x.toString())) {
            int randomAndroidColor = androidColors[position % androidColors.length];

            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.hollow_circle);
            drawable.setColorFilter(randomAndroidColor, PorterDuff.Mode.SRC_ATOP);

            holder.itemTag.setBackground(drawable);
            holder.itemTag.setText(String.valueOf(x.toString().charAt(0)));
            holder.item.setText(x.toString());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listOnItemClickListener.onItemClick(list.get(position));
                }
            });
            switchView(holder, false);
        } else {
            switchView(holder, true);
        }
    }

    private void switchView(OptionBottomSheetHolder holder, boolean showProgress) {
        if (showProgress) {
            holder.linearLayout.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.VISIBLE);
        } else {
            holder.progressBar.setVisibility(View.GONE);
            holder.linearLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class OptionBottomSheetHolder extends RecyclerView.ViewHolder {
        TextView itemTag;
        TextView item;
        LinearLayout linearLayout;
        ProgressBar progressBar;

        public OptionBottomSheetHolder(View itemView) {
            super(itemView);
            itemTag = itemView.findViewById(R.id.option_label);
            item = itemView.findViewById(R.id.option_item);
            linearLayout = itemView.findViewById(R.id.linear_layout);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}