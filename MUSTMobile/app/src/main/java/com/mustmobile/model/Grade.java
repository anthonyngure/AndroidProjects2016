package com.mustmobile.model;

/**
 * Created by Tosh on 10/11/2015.
 */
public class Grade {

    private String grade, unitCode, unitName;

    public Grade(String grade, String unitCode, String unitName){
        this.grade = grade;
        this.unitCode = unitCode;
        this.unitName = unitName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
