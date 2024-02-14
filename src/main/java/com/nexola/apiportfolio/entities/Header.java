package com.nexola.apiportfolio.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "tb_header")
public class Header {
    @Id
    private Long id;
    private String title;
    private String state;

    @OneToOne
    @MapsId
    private User user;

    public Header() {}

    public Header(Long id, String title, String state, User user) {
        this.id = id;
        this.title = title;
        this.state = state;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Header header = (Header) o;

        return Objects.equals(id, header.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
