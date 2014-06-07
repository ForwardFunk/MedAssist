package com.medmobile.pid.medassist.app.DataModels;

/**
 * Created by Pavle on 30.4.14..
 * Specifies alarm attributes and get/set methods
 */
public class Alarm {

    private int id;
    private String medicineName;
    private String medicineType;
    private long startTime;
    private int timesADay;
    private int dosage;
    private int dosageType;
    private int currentAmount;
    private String barcode;
    private boolean vibrate;
    private long nextIntake;
    private boolean doseControlEnabled;

    public Alarm(String medicineName, String medicineType, long startTime, int timesADay,
                 int dosage, int dosageType, int currentAmount, String barcode, boolean vibrate, long nextIntake, boolean doseControlEnabled) {
        this.medicineName = medicineName;
        this.medicineType = medicineType;
        this.startTime = startTime;
        this.timesADay = timesADay;
        this.dosage = dosage;
        this.dosageType = dosageType;
        this.currentAmount = currentAmount;
        this.barcode = barcode;
        this.vibrate = vibrate;
        this.nextIntake = nextIntake;
        this.doseControlEnabled = doseControlEnabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicineType() {
        return medicineType;
    }

    public void setMedicineType(String medicineType) {
        this.medicineType = medicineType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getTimesADay() {
        return timesADay;
    }

    public void setTimesADay(int timesADay) {
        this.timesADay = timesADay;
    }

    public int getDosage() {
        return dosage;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }

    public int getDosageType() {
        return dosageType;
    }

    public void setDosageType(int dosageType) {
        this.dosageType = dosageType;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public long getNextIntake() {
        return nextIntake;
    }

    public void setNextIntake(long nextIntake) {
        this.nextIntake = nextIntake;
    }

    public boolean isDoseControlEnabled() {
        return doseControlEnabled;
    }

    public void setDoseControlEnabled(boolean doseControlEnabled) {
        this.doseControlEnabled = doseControlEnabled;
    }
}
