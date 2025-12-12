package com.contacts.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "company", length = 100)
    private String company;

    @Column(name = "avatar", columnDefinition = "TEXT")
    private String avatar;

    // 新增：社交媒体字段
    @Column(name = "wechat", length = 50)
    private String wechat;  // 微信账号

    @Column(name = "qq", length = 20)
    private String qq;      // QQ账号

    @Column(name = "bookmarked", nullable = false)
    private Boolean bookmarked = false;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
        if (bookmarked == null) {
            bookmarked = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }

    // 更新所有构造函数以包含新字段
    public Contact() {}

    public Contact(String name, String phone, String email, String address, String company) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.company = company;
        this.bookmarked = false;
    }

    public Contact(String name, String phone, String email, String address, String company, String avatar) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.company = company;
        this.avatar = avatar;
        this.bookmarked = false;
    }

    public Contact(String name, String phone, String email, String address, String company, String avatar, String wechat, String qq) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.company = company;
        this.avatar = avatar;
        this.wechat = wechat;
        this.qq = qq;
        this.bookmarked = false;
    }
}
