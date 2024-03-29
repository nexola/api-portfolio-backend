package com.nexola.apiportfolio.models.entities;

import com.nexola.apiportfolio.models.dto.PortfolioDTO;
import com.nexola.apiportfolio.models.embedded.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document
public class Portfolio {
    @Id
    private String id;

    private Header header;
    private Footer footer;
    private Experience experience;
    private Education education;
    private Author author;

    public Portfolio(){}

    public Portfolio(PortfolioDTO dto) {
        header = dto.getHeader();
        footer = dto.getFooter();
        experience = dto.getExperience();
        education = dto.getEducation();
        author = dto.getAuthor();
    }

    public Portfolio(String id, Header header, Footer footer, Experience experience, Education education, Author author) {
        this.id = id;
        this.header = header;
        this.footer = footer;
        this.experience = experience;
        this.education = education;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Footer getFooter() {
        return footer;
    }

    public void setFooter(Footer footer) {
        this.footer = footer;
    }

    public Experience getExperience() {
        return experience;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
    }

    public Education getEducation() {
        return education;
    }

    public void setEducation(Education education) {
        this.education = education;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Portfolio portfolio = (Portfolio) o;
        return Objects.equals(id, portfolio.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
