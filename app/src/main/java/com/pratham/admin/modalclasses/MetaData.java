package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class MetaData {
    @NonNull
    @PrimaryKey
    String keys;
    String value;

    @NonNull
    public String getKeys() {
        return keys;
    }

    public void setKeys(@NonNull String keys) {
        this.keys = keys;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

