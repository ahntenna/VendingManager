package com.example.ahn.vendingmanager;

/**
 * Created by Ahn on 2017. 12. 1..
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter implements Filterable {

    public static String TAG;

    private Filter listFilter;

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;
    // 필터링 될 ArrayList
    private ArrayList<ListViewItem> filteredItemList = listViewItemList;

    // ListViewAdapter의 생성자
    public ListViewAdapter(String tag) {
        this.TAG = tag;
    }

    public void clear() {
        this.listViewItemList.clear();
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter();
        }

        return listFilter ;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return filteredItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = filteredItemList.get(position);

        if(TAG.equals("ManageActivity")) {
            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_view_vending, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            ImageView imgVending = (ImageView)convertView.findViewById(R.id.image_view_left);
            ImageView imgWarning = (ImageView)convertView.findViewById(R.id.img_warning);
            TextView vendingName = (TextView)convertView.findViewById(R.id.text_name);
            TextView vendingType = (TextView)convertView.findViewById(R.id.text_type);
            TextView vendingLocation = (TextView)convertView.findViewById(R.id.text_address);
            TextView vendingOwnerPhone = (TextView)convertView.findViewById(R.id.text_phone);

            // 아이템 내 각 위젯에 데이터 반영
            imgVending.setImageDrawable(listViewItem.getDrawableVending());
            imgWarning.setImageDrawable(listViewItem.getDrawableWarning());
            vendingName.setText(listViewItem.getVendingName());
            vendingType.setText("종류  :  " + listViewItem.getVendingType());
            vendingLocation.setText("위치  :  " + listViewItem.getVendingLocation());
            vendingOwnerPhone.setText("소유자 번호  :  " + listViewItem.getVendingOwnerPhone());

        }
        if(TAG.equals("VendingDetailActivity")) {
            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_view_detail, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            ImageView imgWarning = (ImageView)convertView.findViewById(R.id.img_warning_detail);
            TextView prodName = (TextView)convertView.findViewById(R.id.text_prod_name);
            TextView prodStock = (TextView)convertView.findViewById(R.id.text_stock);
            TextView prodPrice = (TextView)convertView.findViewById(R.id.text_prod_price);
            TextView prodSold = (TextView)convertView.findViewById(R.id.text_prod_sold);

            // 아이템 내 각 위젯에 데이터 반영
            imgWarning.setImageDrawable(listViewItem.getProdWarning());
            prodName.setText("품명 : " + listViewItem.getProdName());
            prodStock.setText("재고 : " + listViewItem.getProdStock());
            prodPrice.setText("가격 : " + listViewItem.getProdPrice());
            prodSold.setText("판매량 : " + listViewItem.getProdSold());
        }
        if(TAG.equals("UserActivity")) {
            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_view_vending, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            ImageView imgVending = (ImageView)convertView.findViewById(R.id.image_view_left);
            ImageView imgWarning = (ImageView)convertView.findViewById(R.id.img_warning);
            TextView vendingName = (TextView)convertView.findViewById(R.id.text_name);
            TextView vendingType = (TextView)convertView.findViewById(R.id.text_type);
            TextView vendingLocation = (TextView)convertView.findViewById(R.id.text_address);
            TextView vendingOwnerPhone = (TextView)convertView.findViewById(R.id.text_phone);

            // 아이템 내 각 위젯에 데이터 반영
            imgVending.setImageDrawable(listViewItem.getDrawableVending());
            imgWarning.setImageDrawable(listViewItem.getDrawableWarning());
            vendingName.setText(listViewItem.getVendingName());
            vendingType.setText("종류  :  " + listViewItem.getVendingType());
            vendingLocation.setText("위치  :  " + listViewItem.getVendingLocation());
            vendingOwnerPhone.setText("소유자 번호  :  " + listViewItem.getVendingOwnerPhone());
        }
        if(TAG.equals("UserVendingDetailActivity")) {
            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_view_detail, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            ImageView imgWarning = (ImageView)convertView.findViewById(R.id.img_warning_detail);
            TextView prodName = (TextView)convertView.findViewById(R.id.text_prod_name);
            TextView prodStock = (TextView)convertView.findViewById(R.id.text_stock);
            TextView prodPrice = (TextView)convertView.findViewById(R.id.text_prod_price);
            TextView prodSold = (TextView)convertView.findViewById(R.id.text_prod_sold);

            // 아이템 내 각 위젯에 데이터 반영
            imgWarning.setImageDrawable(listViewItem.getProdWarning());
            prodName.setText("품명 : " + listViewItem.getProdName());
            prodStock.setText("재고 : " + listViewItem.getProdStock());
            prodPrice.setText("가격 : " + listViewItem.getProdPrice());
            prodSold.setText("판매량 : " + listViewItem.getProdSold());
        }

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return filteredItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 메소드. ManageActivity
    public void addItem(Drawable drawableVending, Drawable drawableError, String vendingName,
                        String vendingType, String vendingLocation, String vendingOwnerPhone, String TAG) {
        this.TAG = TAG;

        ListViewItem item = new ListViewItem();

        item.setDrawableVending(drawableVending);
        item.setDrawableWarning(drawableError);
        item.setVendingName(vendingName);
        item.setVendingType(vendingType);
        item.setVendingLocation(vendingLocation);
        item.setVendingOwnerPhone(vendingOwnerPhone);

        listViewItemList.add(item);
    }

    // 아이템 데이터 추가를 위한 메소드. VendingDetailActivity
    public void addItem(Drawable drawableWarning, String prodName, String prodStock, String price, String sold, String TAG) {
        this.TAG = TAG;

        ListViewItem item = new ListViewItem();

        item.setProdWarning(drawableWarning);
        item.setProdName(prodName);
        item.setProdStock(prodStock);
        item.setProdPrice(price);
        item.setProdSold(sold);

        listViewItemList.add(item);
    }

    private class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if(constraint == null || constraint.length() == 0) {
                results.values = listViewItemList;
                results.count = listViewItemList.size();
            } else {
                ArrayList<ListViewItem> itemList = new ArrayList<ListViewItem>();

                for(ListViewItem item : listViewItemList) {
                    if(item.getVendingName().toUpperCase().contains(constraint.toString().toUpperCase())) {
                        itemList.add(item);
                    }
                }

                results.values = itemList;
                results.count = itemList.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredItemList = (ArrayList<ListViewItem>)results.values;

            if(results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}