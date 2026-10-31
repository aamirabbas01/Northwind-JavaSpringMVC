package com.example.javatest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {

    @JsonProperty("EmployeeID")      private Integer employeeId;
    @JsonProperty("LastName")        private String lastName;
    @JsonProperty("FirstName")       private String firstName;
    @JsonProperty("Title")           private String title;
    @JsonProperty("TitleOfCourtesy") private String titleOfCourtesy;
    @JsonProperty("BirthDate")       private String birthDate;
    @JsonProperty("HireDate")        private String hireDate;
    @JsonProperty("Address")         private String address;
    @JsonProperty("City")            private String city;
    @JsonProperty("Region")          private String region;
    @JsonProperty("PostalCode")      private String postalCode;
    @JsonProperty("Country")         private String country;
    @JsonProperty("HomePhone")       private String homePhone;
    @JsonProperty("Extension")       private String extension;
    @JsonProperty("Notes")           private String notes;
    @JsonProperty("PhotoPath")       private String photoPath;
    @JsonProperty("PhotoBase64")     private String photoBase64;
    @JsonProperty("ReportsTo")       private Integer reportsTo;
    @JsonProperty("ReportsToName")   private String reportsToName;

    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer v) { this.employeeId = v; }
    public String getLastName() { return lastName; }
    public void setLastName(String v) { this.lastName = v; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }
    public String getTitle() { return title; }
    public void setTitle(String v) { this.title = v; }
    public String getTitleOfCourtesy() { return titleOfCourtesy; }
    public void setTitleOfCourtesy(String v) { this.titleOfCourtesy = v; }
    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String v) { this.birthDate = v; }
    public String getHireDate() { return hireDate; }
    public void setHireDate(String v) { this.hireDate = v; }
    public String getAddress() { return address; }
    public void setAddress(String v) { this.address = v; }
    public String getCity() { return city; }
    public void setCity(String v) { this.city = v; }
    public String getRegion() { return region; }
    public void setRegion(String v) { this.region = v; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String v) { this.postalCode = v; }
    public String getCountry() { return country; }
    public void setCountry(String v) { this.country = v; }
    public String getHomePhone() { return homePhone; }
    public void setHomePhone(String v) { this.homePhone = v; }
    public String getExtension() { return extension; }
    public void setExtension(String v) { this.extension = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String v) { this.photoPath = v; }
    public String getPhotoBase64() { return photoBase64; }
    public void setPhotoBase64(String v) { this.photoBase64 = v; }
    public Integer getReportsTo() { return reportsTo; }
    public void setReportsTo(Integer v) { this.reportsTo = v; }
    public String getReportsToName() { return reportsToName; }
    public void setReportsToName(String v) { this.reportsToName = v; }

    public String getFullName() {
        return (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
    }

    // replaces Django's |slice:':10' (turns 1948-12-08T00:00:00Z into 1948-12-08)
    public String getBirthDateOnly() { return dateOnly(birthDate); }
    public String getHireDateOnly() { return dateOnly(hireDate); }

    private static String dateOnly(String value) {
        return (value != null && value.length() >= 10) ? value.substring(0, 10) : "";
    }

}