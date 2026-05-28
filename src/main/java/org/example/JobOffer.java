package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class JobOffer {
    
    private int id; 
    private String title;
    private String category;
    private String location;
    private String salaryRange;
    private String description;
    private String status;
    private ObservableList<String> applications;

    // Konstruktor do odczytu z bazy danych (wymaga ID)
    public JobOffer(int id, String title, String category, String location, String salaryRange, String description) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.location = location;
        this.salaryRange = salaryRange;
        this.description = description;
        this.status = "Aktywna";
        this.applications = FXCollections.observableArrayList();
    }
    
    // Konstruktor dla Pracodawcy dodającego nową ofertę(baza sama nadaje ID)
    public JobOffer(String title, String category, String location, String salaryRange, String description) {
        this.title = title;
        this.category = category;
        this.location = location;
        this.salaryRange = salaryRange;
        this.description = description;
        this.status = "Aktywna";
        this.applications = FXCollections.observableArrayList();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ObservableList<String> getApplications() {
        return applications;
    }

    public void setApplications(ObservableList<String> applications) {
        this.applications = applications;
    }
}