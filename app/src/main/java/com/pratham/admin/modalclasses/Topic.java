package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Topic {

    @NonNull
    @PrimaryKey
    @SerializedName("TopicId")
    public String TopicID;
    @SerializedName("TopicName")
    public String TopicName;
    @SerializedName("TopicIdInPos")
    public String TopicIDInPos;
    @SerializedName("TopicCode")
    public String TopicCode;
    @SerializedName("TopicSubject")
    public String TopicSubject;
    @SerializedName("TopicLang")
    public String TopicLang;
    @SerializedName("IsDelete")
    public Boolean IsDelete;
    @SerializedName("sentFlag")
    public int sentFlag;

    @Override
    public String toString() {
        return "Topic{" +
                "TopicID='" + TopicID + '\'' +
                ", TopicName='" + TopicName + '\'' +
                ", TopicIDInPos='" + TopicIDInPos + '\'' +
                ", TopicCode='" + TopicCode + '\'' +
                ", TopicSubject='" + TopicSubject + '\'' +
                ", TopicLang='" + TopicLang + '\'' +
                ", IsDelete=" + IsDelete +
                ", sentFlag=" + sentFlag +
                '}';
    }

    @NonNull
    public String getTopicID() {
        return TopicID;
    }

    public void setTopicID(@NonNull String topicID) {
        TopicID = topicID;
    }

    public String getTopicName() {
        return TopicName;
    }

    public void setTopicName(String topicName) {
        TopicName = topicName;
    }

    public String getTopicIDInPos() {
        return TopicIDInPos;
    }

    public void setTopicIDInPos(String topicIDInPos) {
        TopicIDInPos = topicIDInPos;
    }

    public String getTopicCode() {
        return TopicCode;
    }

    public void setTopicCode(String topicCode) {
        TopicCode = topicCode;
    }

    public String getTopicSubject() {
        return TopicSubject;
    }

    public void setTopicSubject(String topicSubject) {
        TopicSubject = topicSubject;
    }

    public String getTopicLang() {
        return TopicLang;
    }

    public void setTopicLang(String topicLang) {
        TopicLang = topicLang;
    }

    public Boolean getDelete() {
        return IsDelete;
    }

    public void setDelete(Boolean delete) {
        IsDelete = delete;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }
}
