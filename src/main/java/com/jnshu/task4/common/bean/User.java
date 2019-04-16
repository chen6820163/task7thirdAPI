package com.jnshu.task4.common.bean;


public class User {
    private Long id;

    private String username;

    private String password;

    private String email;

    private Long qq;

    private String address;

    private Long createAt;

    private Long updateAt;

    private String uuid;

    private String phone;

    private String head_img;

    public User() {
    }

    public User(Long id, String username, String password, String email, Long qq, String address, Long createAt, Long updateAt, String uuid, String phone, String head_img) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.qq = qq;
        this.address = address;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.uuid = uuid;
        this.phone = phone;
        this.head_img = head_img;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getQq() {
        return qq;
    }

    public void setQq(Long qq) {
        this.qq = qq;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Long createAt) {
        this.createAt = createAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", qq=" + qq +
                ", address='" + address + '\'' +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                ", uuid='" + uuid + '\'' +
                ", phone='" + phone + '\'' +
                ", head_img='" + head_img + '\'' +
                '}';
    }
}