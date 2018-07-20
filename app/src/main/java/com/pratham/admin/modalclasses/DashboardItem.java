package com.pratham.admin.modalclasses;

public class DashboardItem {
    // Save name.
    private String Name;

    // Save image resource id.
    private int ImageId;

    public DashboardItem(String name, int imgId) {
        this.Name = name;
        this.ImageId = imgId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public int getImageId() {
        return ImageId;
    }

    public void setImageId(int imageId) {
        this.ImageId = imageId;
    }
}
