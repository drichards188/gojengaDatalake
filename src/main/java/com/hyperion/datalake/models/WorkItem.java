package com.hyperion.datalake.models;

public class WorkItem {

    private String id;
    private String name;
    private String guide ;
    private String date;
    private String description;
    private String status;
    private int archived ;

    public int getArchived() {
        return this.archived;
    }

    public void setArchived(int archived) {
        this.archived = archived;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDate (String date) {
        this.date = date;
    }

    public String getDate() {
        return this.date;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setGuide (String guide) {
        this.guide = guide;
    }

    public String getGuide() {
        return this.guide;
    }
}