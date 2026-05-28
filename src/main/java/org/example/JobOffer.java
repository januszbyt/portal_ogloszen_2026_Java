package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class JobOffer {
    private String title;
    private String category;
    private String location;
    private String salaryRange;
    private String description;
    private String status;
    private ObservableList<String> applications;

    public JobOffer(String title, String category, String location, String salaryRange, String description) {
        this.title = title;
        this.category = category;
        this.location = location;
        this.salaryRange = salaryRange;
        this.description = description;
        this.status = "Aktywna";
        this.applications = FXCollections.observableArrayList(
            "Jan Kowalski - CV.pdf (Oczekująca)",
            "Anna Nowak - CV.pdf (Oczekująca)",
            "Michał Wiśniewski - CV.pdf (Oczekująca)"
        );
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
