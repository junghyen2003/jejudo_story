package com.namoo.plus.jejurizmapp.ui.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.namoo.plus.jejurizmapp.R;
import com.namoo.plus.jejurizmapp.common.Constants;
import com.namoo.plus.jejurizmapp.model.SearchMenuModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HeungSun-AndBut on 2016. 7. 27..
 */

public class MenuSelectFragment extends Fragment {

    @BindView(R.id.fragment_menu_select_gridview)
    public GridView mGridView;

    private ArrayList<SearchMenuModel> menuList;
    private int kind;
    private MenuAdapter mAdpater;

    public static MenuSelectFragment newInstance(int kind) {

        Bundle args = new Bundle();
        args.putInt("kind", kind);
        MenuSelectFragment fragment = new MenuSelectFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_select, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle bundle) {
        super.onViewCreated(view, bundle);

        menuList = new ArrayList<>();
        kind = getArguments().getInt("kind");

        String[] images = null;
        String[] titles = null;

        switch (kind) {
            case Constants.SEARCH_MENU_OPTION_SELECT_RESTAURANT_KIND:
                titles = getResources().getStringArray(R.array.menu_select_restaurant_kind_title);
                images = getResources().getStringArray(R.array.menu_select_restaurant_kind_image);
                break;
            case Constants.SEARCH_MENU_OPTION_SELECT_RESTAURANT_SITUATION:
                titles = getResources().getStringArray(R.array.menu_select_restaurant_situation_title);
                images = getResources().getStringArray(R.array.menu_select_restaurant_situation_image);
                break;
            case Constants.SEARCH_MENU_OPTION_SELECT_TOUR_KIND:
                titles = getResources().getStringArray(R.array.menu_select_tour_kind_title);
                images = getResources().getStringArray(R.array.menu_select_tour_kind_image);
                break;
            case Constants.SEARCH_MENU_OPTION_SELECT_TOUR_SITUATION:
                titles = getResources().getStringArray(R.array.menu_select_tour_situation_title);
                images = getResources().getStringArray(R.array.menu_select_tour_situation_image);
                break;
            case Constants.SEARCH_MENU_OPTION_SELECT_SHOPPING:
                titles = getResources().getStringArray(R.array.menu_select_shopping_title);
                images = getResources().getStringArray(R.array.menu_select_shopping_image);
                break;
        }

        addListInfo(images, titles);

        mAdpater = new MenuAdapter(getActivity(), menuList);
        mGridView.setAdapter(mAdpater);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                menuList.get(i).setCheck(!menuList.get(i).isCheck());
                mAdpater.notifyDataSetChanged();

            }
        });

    }

    private void addListInfo(String[] images, String[] titles) {
        if (images != null && titles != null && images.length == titles.length) {
            int size = images.length;
            for (int i = 0; i < size; i++) {
                menuList.add(new SearchMenuModel(images[i], titles[i], true));
            }
        }

    }

    class MenuAdapter extends ArrayAdapter<SearchMenuModel> {

        Context context;


        public MenuAdapter(Context context, List<SearchMenuModel> menuModels) {
            super(context, 0, menuModels);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item_menu, null);
                holder = new ViewHolder(convertView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final SearchMenuModel item = getItem(position);

            //괜한짓 하는건가???
            String url = "@drawable/" + item.getmPath();
            int imageResource = getResources().getIdentifier(url,
                    null, getActivity().getPackageName());
            Drawable res = getResources().getDrawable(imageResource);
            holder.ivImage.setImageDrawable(res);

            holder.tvMenuTitle.setText(item.getTitle());

            holder.cbChecked.setChecked(item.isCheck());

            holder.cbChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    item.setCheck(b);
                }
            });
            return convertView;
        }
    }

    class ViewHolder {
        public ImageView ivImage;
        public TextView tvMenuTitle;
        public CheckBox cbChecked;

        public ViewHolder(View view) {
            ivImage = (ImageView) view.findViewById(R.id.listview_item_image);
            tvMenuTitle = (TextView) view.findViewById(R.id.listview_item_text);
            cbChecked = (CheckBox) view.findViewById(R.id.listview_item_check_box);

        }

    }

}