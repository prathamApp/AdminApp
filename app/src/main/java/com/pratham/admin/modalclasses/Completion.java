package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;

@Entity
public class Completion {
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
