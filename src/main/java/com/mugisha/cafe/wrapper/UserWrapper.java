package com.mugisha.cafe.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserWrapper {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("contactNumber")
    private String contactNumber;

    @JsonProperty("status")
    private String status;

    public UserWrapper() {}

    public UserWrapper(Integer id, String name, String email,
                       String contactNumber, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.status = status;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getContactNumber() { return contactNumber; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return "UserWrapper{id=" + id + 
               ", name=" + name + 
               ", email=" + email + "}";
    }
}