package com.medmobile.pid.medassist.app.DataModels;

/**
 * Created by Pavle on 30.4.14..
 */
public class User {


    private int id;
    private String name;
    private String lastname;
    private String dOB;
    private String alternativePhoneNo;
    private String chronicalStates;
    private String allergies;
    private int bloodType;
    private String contact;
    private boolean locationSharing;

    public User()
    {

    }

    public User(String name, String lastname, String dOB, String chronicalStates, String allergies, int bloodType, String alternativePhoneNo, String contact, boolean locationSharing)
    {
        this.name = name;
        this.dOB = dOB;
        this.lastname = lastname;
        this.chronicalStates = chronicalStates;
        this.alternativePhoneNo = alternativePhoneNo;
        this.bloodType = bloodType;
        this.allergies = allergies;
        this.contact = contact;
        this.locationSharing = locationSharing;
    }

    public boolean isLocationShared() {
        return locationSharing;
    }

    public void setLocationSharing(Boolean locationSharing) {
        this.locationSharing = locationSharing;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getBloodType() {
        return bloodType;
    }

    public void setBloodType(int bloodType) {
        this.bloodType = bloodType;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getChronicalStates() {
        return chronicalStates;
    }

    public void setChronicalStates(String chronicalStates) {
        this.chronicalStates = chronicalStates;
    }

    public String getAlternativePhoneNo() {
        return alternativePhoneNo;
    }

    public void setAlternativePhoneNo(String alternativePhoneNo) {
        this.alternativePhoneNo = alternativePhoneNo;
    }

    public String getdOB() {
        return dOB;
    }

    public void setdOB(String dOB) {
        this.dOB = dOB;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;

        User user = (User) obj;

        if (user.getName().equals(this.getName()))
            return false;
        else if (user.getLastname().equals(this.getLastname()))
            return false;
        else if (user.getdOB().equals(this.getdOB()))
            return false;
        else if (user.getChronicalStates().equals(this.getChronicalStates()))
            return false;
        else if (user.getAllergies().equals(this.getAllergies()))
            return false;
        else if (user.getAlternativePhoneNo().equals(this.getAlternativePhoneNo()))
            return false;
        else if (user.getBloodType() == this.getBloodType())
            return false;
        else if (user.getContact().equals(this.getContact()))
            return false;
        else if (user.isLocationShared() == this.isLocationShared())
            return false;
        else
            return true;
    }
}
