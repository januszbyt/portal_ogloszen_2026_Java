package org.example;

import java.math.BigDecimal;

public class JobOffer {
    private int id;
    private String title;
    private String category;
    private String location;
    private BigDecimal salaryMin; 
    private BigDecimal salaryMax; 
    private String description;
    private String status;
    private String companyName;


    public JobOffer(int id, String title, String category, String location, BigDecimal salaryMin, BigDecimal salaryMax, String description) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.location = location;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.description = description;
    }

 
    public String getSalaryRange() {
        if (salaryMin == null && salaryMax == null) return "Do uzgodnienia";
        return salaryMin + " - " + salaryMax + " PLN";
    }

    // Gettery i Settery
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public BigDecimal getSalaryMin() { return salaryMin; }
    public void setSalaryMin(BigDecimal salaryMin) { this.salaryMin = salaryMin; }

    public BigDecimal getSalaryMax() { return salaryMax; }
    public void setSalaryMax(BigDecimal salaryMax) { this.salaryMax = salaryMax; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
}