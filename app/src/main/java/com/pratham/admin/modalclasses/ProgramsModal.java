package com.pratham.admin.modalclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProgramsModal {

    @SerializedName("ProgramId")
    @Expose
    private Integer programId;
    @SerializedName("ProgramName")
    @Expose
    private String programName;

    /*public ProgramsModal(Integer programId, String programName) {
        this.programId = programId;
        this.programName = programName;
    }*/

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

}