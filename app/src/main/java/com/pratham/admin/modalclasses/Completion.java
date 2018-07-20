package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Completion {
    @NonNull
    @PrimaryKey
    public String CompletionID;
    public int VillageID;
    public String GroupID;
    public int CourseCompleted;
    public int TopicCompleted;
    public String StartDate;
    public String EndDate;
    public String GroupType; // Community/School
    public int ParentParticipation;
    public int PresentStudent;
}
