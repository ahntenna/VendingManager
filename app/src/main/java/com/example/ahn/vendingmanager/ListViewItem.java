package com.example.ahn.vendingmanager;

import android.graphics.drawable.Drawable;

/**
 * Created by Ahn on 2017. 12. 1..
 */

public class ListViewItem {

    private Drawable drawableWarning;
    private Drawable drawableVending;
    private String vendingName;
    private String vendingType;
    private String vendingLocation;
    private String vendingOwnerPhone;

    private Drawable prodWarning;
    private String prodName;
    private String prodStock;
    private String prodPrice;
    private String prodSold;

    public String getProdSold() {
        return prodSold;
    }

    public void setProdSold(String prodSold) {
        this.prodSold = prodSold;
    }

    public String getProdPrice() {
        return prodPrice;
    }

    public void setProdPrice(String prodPrice) {
        this.prodPrice = prodPrice;
    }

    public Drawable getProdWarning() {
        return prodWarning;
    }

    public void setProdWarning(Drawable prodWarning) {
        this.prodWarning = prodWarning;
    }

    public String getProdName() {
        return this.prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getProdStock() {
        return this.prodStock;
    }

    public void setProdStock(String prodStock) {
        this.prodStock = prodStock;
    }

    public Drawable getDrawableVending() {
        return this.drawableVending;
    }

    public void setDrawableVending(Drawable drawableVending) {
        this.drawableVending = drawableVending;
    }

    public Drawable getDrawableWarning() {
        return this.drawableWarning;
    }

    public void setDrawableWarning(Drawable drawableWarning) {
        this.drawableWarning = drawableWarning;
    }

    public String getVendingName() {
        return this.vendingName;
    }

    public void setVendingName(String vendingName) {
        this.vendingName = vendingName;
    }

    public String getVendingType() {
        return this.vendingType;
    }

    public void setVendingType(String vendingType) {
        this.vendingType = vendingType;
    }

    public String getVendingLocation() {
        return this.vendingLocation;
    }

    public void setVendingLocation(String vendingLocation) {
        this.vendingLocation = vendingLocation;
    }

    public String getVendingOwnerPhone() {
        return this.vendingOwnerPhone;
    }

    public void setVendingOwnerPhone(String vendingOwnerPhone) {
        this.vendingOwnerPhone = vendingOwnerPhone;
    }
}
