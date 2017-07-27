package com.namoo.plus.jejurizmapp.ui.listmodel;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.namoo.plus.jejurizmapp.R;
import com.namoo.plus.jejurizmapp.model.MenuModel;

import java.util.ArrayList;

import static com.namoo.plus.jejurizmapp.common.Constants.NAMOO_PLUS_MENU_URL;

/**
 * Created by jungh on 2017-05-17.
 */

public class ListMenuAdapter extends BaseAdapter{
    Context context;
    ArrayList<MenuModel> list_menu;
    ViewHolder viewHolder;

    class ViewHolder{
        ImageView menu_image;
        TextView menu_name;
        TextView menu_price;
/*
        RatingBar pungency_star;
        RatingBar sourness_star;
        RatingBar sweetness_star;
        RatingBar saltiness_star;
*/
    }

    public ListMenuAdapter(Context context, ArrayList<MenuModel> list_menu){
        this.context = context;
        this.list_menu = list_menu;
    }

    @Override
    public int getCount() {
        return this.list_menu.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list_menu.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_menu_nostar,null);
            viewHolder = new ViewHolder();
            viewHolder.menu_image = (ImageView)convertView.findViewById(R.id.menu_image);
            viewHolder.menu_name = (TextView)convertView.findViewById(R.id.menu_name);
            viewHolder.menu_price = (TextView)convertView.findViewById(R.id.menu_price);
/*
            viewHolder.pungency_star = (RatingBar)convertView.findViewById(R.id.pungency_star);
            viewHolder.sourness_star = (RatingBar)convertView.findViewById(R.id.sourness_star);
            viewHolder.sweetness_star = (RatingBar)convertView.findViewById(R.id.sweetness_star);
            viewHolder.saltiness_star = (RatingBar)convertView.findViewById(R.id.saltiness_star);
            viewHolder.pungency_star.setRating(list_menu.get(position).getPungency());
            viewHolder.sourness_star.setRating(list_menu.get(position).getSourness());
            viewHolder.sweetness_star.setRating(list_menu.get(position).getSweetness());
            viewHolder.saltiness_star.setRating(list_menu.get(position).getSaltiness());
*/

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.menu_name.setText(list_menu.get(position).getMenu());
        viewHolder.menu_price.setText(String.valueOf(list_menu.get(position).getPrice()));
        Glide.with(context)
                .load(NAMOO_PLUS_MENU_URL+list_menu.get(position).getImage())
                .error(R.drawable.no_menu_img)
                .into(viewHolder.menu_image);

        return convertView;
    }
}
