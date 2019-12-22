package com.corptia.bringero.model;

import java.util.List;

public class UserModel  {

    private String id;
    private String token;
    private String language;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String status;
    private String avatarName;
    private String avatarImageId;
    private String phone;

    private CurrentDeliveryAddress currentDeliveryAddress;
    private List<DeliveryAddresses> deliveryAddressesList;

    public UserModel() {

    }


    public String getAvatarImageId() {
        return avatarImageId;
    }

    public void setAvatarImageId(String avatarImageId) {
        this.avatarImageId = avatarImageId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<DeliveryAddresses> getDeliveryAddressesList() {
        return deliveryAddressesList;
    }

    public void setDeliveryAddressesList(List<DeliveryAddresses> deliveryAddressesList) {
        this.deliveryAddressesList = deliveryAddressesList;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public CurrentDeliveryAddress getCurrentDeliveryAddress() {
        return currentDeliveryAddress;
    }

    public void setCurrentDeliveryAddress(CurrentDeliveryAddress currentDeliveryAddress) {
        this.currentDeliveryAddress = currentDeliveryAddress;
    }

}
