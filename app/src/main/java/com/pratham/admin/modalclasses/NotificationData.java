package com.pratham.admin.modalclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationData {

    @SerializedName("AutoId")
    @Expose
    private Integer autoId;
    @SerializedName("TransId")
    @Expose
    private String transId;
    @SerializedName("FromId")
    @Expose
    private String fromId;
    @SerializedName("FromName")
    @Expose
    private String fromName;
    @SerializedName("ToId")
    @Expose
    private String toId;
    @SerializedName("ToName")
    @Expose
    private String toName;
    @SerializedName("PrathamId")
    @Expose
    private String prathamId;
    @SerializedName("QrID")
    @Expose
    private String qrID;
    @SerializedName("SerialId")
    @Expose
    private String serialId;
    @SerializedName("ReceiveDate")
    @Expose
    private String receiveDate;
    @SerializedName("Comment")
    @Expose
    private String comment;
    @SerializedName("DamageType")
    @Expose
    private String damageType;
    @SerializedName("IsDamaged")
    @Expose
    private String isDamaged;
    @SerializedName("TextDesc")
    @Expose
    private String textDesc;
    @SerializedName("DateAdded")
    @Expose
    private String dateAdded;
    @SerializedName("PushStatus")
    @Expose
    private String pushStatus;
    @SerializedName("LastPushDate")
    @Expose
    private String lastPushDate;

    public Integer getAutoId() {
        return autoId;
    }

    public void setAutoId(Integer autoId) {
        this.autoId = autoId;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getPrathamId() {
        return prathamId;
    }

    public void setPrathamId(String prathamId) {
        this.prathamId = prathamId;
    }

    public String getQrID() {
        return qrID;
    }

    public void setQrID(String qrID) {
        this.qrID = qrID;
    }

    public String getSerialId() {
        return serialId;
    }

    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDamageType() {
        return damageType;
    }

    public void setDamageType(String damageType) {
        this.damageType = damageType;
    }

    public String getIsDamaged() {
        return isDamaged;
    }

    public void setIsDamaged(String isDamaged) {
        this.isDamaged = isDamaged;
    }

    public String getTextDesc() {
        return textDesc;
    }

    public void setTextDesc(String textDesc) {
        this.textDesc = textDesc;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getPushStatus() {
        return pushStatus;
    }

    public void setPushStatus(String pushStatus) {
        this.pushStatus = pushStatus;
    }

    public String getLastPushDate() {
        return lastPushDate;
    }

    public void setLastPushDate(String lastPushDate) {
        this.lastPushDate = lastPushDate;
    }

}