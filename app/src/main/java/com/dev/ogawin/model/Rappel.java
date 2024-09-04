package com.dev.ogawin.model;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.Serializable;
import java.util.List;

public class Rappel implements Serializable{
    private int rappelId;
    private String rappelTitle;
    private String rappelNotes;
    private LocalDate rappelDate;
    private LocalTime rappelTime;
    private String rappelRecurrence;
    private LocalDate rappelDateFinRecurrence;
    private String rappelPriorite;
    private List<String> rappelIdsSousTaches;
    private Long rappelPlannedAlarmTimeInMillis;


    public Rappel(int rappelId, String rappelTitle, String rappelNotes, LocalDate rappelDate, LocalTime rappelTime, String rappelRecurrence,
                  LocalDate rappelDateFinRecurrence, String rappelPriorite, List<String> rappelIdsSousTaches) {
        this.rappelId = rappelId;
        this.rappelTitle = rappelTitle;
        this.rappelNotes = rappelNotes;
        this.rappelDate = rappelDate;
        this.rappelTime = rappelTime;
        this.rappelRecurrence = rappelRecurrence;
        this.rappelDateFinRecurrence = rappelDateFinRecurrence;
        this.rappelPriorite = rappelPriorite;
        this.rappelIdsSousTaches = rappelIdsSousTaches;
    }

    public Rappel( String rappelTitle, String rappelNotes, LocalDate rappelDate, LocalTime rappelTime, String rappelRecurrence,
                  LocalDate rappelDateFinRecurrence, String rappelPriorite, List<String> rappelIdsSousTaches) {
        this.rappelTitle = rappelTitle;
        this.rappelNotes = rappelNotes;
        this.rappelDate = rappelDate;
        this.rappelTime = rappelTime;
        this.rappelRecurrence = rappelRecurrence;
        this.rappelDateFinRecurrence = rappelDateFinRecurrence;
        this.rappelPriorite = rappelPriorite;
        this.rappelIdsSousTaches = rappelIdsSousTaches;
    }
    public Rappel(String rappelTitle)
    {
        this.setRappelTitle(rappelTitle);
    }

    public int getRappelId() {
        return rappelId;
    }

    public void setRappelId(int rappelId) {
        this.rappelId = rappelId;
    }

    public String getRappelTitle() {
        return rappelTitle;
    }

    public void setRappelTitle(String rappelTitle) {
        this.rappelTitle = rappelTitle;
    }

    public String getRappelNotes() {
        return rappelNotes;
    }

    public void setRappelNotes(String rappelNotes) {
        this.rappelNotes = rappelNotes;
    }

    public LocalDate getRappelDate() {
        return rappelDate;
    }

    public void setRappelDate(LocalDate rappelDate) {
        this.rappelDate = rappelDate;
    }

    public LocalTime getRappelTime() {
        return rappelTime;
    }

    public void setRappelTime(LocalTime rappelTime) {
        this.rappelTime = rappelTime;
    }

    public String getRappelRecurrence() {
        return rappelRecurrence;
    }

    public void setRappelRecurrence(String rappelRecurrence) {
        this.rappelRecurrence = rappelRecurrence;
    }

    public LocalDate getRappelDateFinRecurrence() {
        return rappelDateFinRecurrence;
    }

    public void setRappelDateFinRecurrence(LocalDate rappelDateFinRecurrence) {
        this.rappelDateFinRecurrence = rappelDateFinRecurrence;
    }

    public String getRappelPriorite() {
        return rappelPriorite;
    }

    public void setRappelPriorite(String rappelPriorite) {
        this.rappelPriorite = rappelPriorite;
    }

    public List<String> getRappelIdsSousTaches() {
        return rappelIdsSousTaches;
    }

    public void setRappelIdsSousTaches(List<String> rappelIdsSousTaches) {
        this.rappelIdsSousTaches = rappelIdsSousTaches;
    }

    public Long getRappelPlannedAlarmTimeInMillis() {
        return rappelPlannedAlarmTimeInMillis;
    }

    public void setRappelPlannedAlarmTimeInMillis(Long rappelPlannedAlarmTimeInMillis) {
        this.rappelPlannedAlarmTimeInMillis = rappelPlannedAlarmTimeInMillis;
    }

}
