package com.servlets.web;

public class Candidate {
    private int id;
    private String firstName;
    private String lastName;
    private String voterId;
    private String dob;
    private String mobile;
    private String gender;
    private String party;

    public Candidate(int id, String firstName, String lastName, String voterId, String dob, String mobile, String gender, String party) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.voterId = voterId;
        this.dob = dob;
        this.mobile = mobile; 
        this.gender = gender;
        this.party = party;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getVoterId() { return voterId; }
    public void setVoterId(String voterId) { this.voterId = voterId; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getParty() { return party; }
    public void setParty(String party) { this.party = party; }
}
