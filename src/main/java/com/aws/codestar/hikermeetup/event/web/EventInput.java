package com.aws.codestar.hikermeetup.event.web;

import java.time.LocalDateTime;

public class EventInput {

    private Integer minSignup;

    private String category;

    private String name;

    private String location;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

//    private Integer price;

    public Integer getMinSignup() {
        return minSignup;
    }

    public void setMinSignup(Integer minSignup) {
        this.minSignup = minSignup;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

//    public Integer getPrice() {
//        return price;
//    }
//
//    public void setPrice(Integer price) {
//        this.price = price;
//    }
}
