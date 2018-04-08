package com.maxfill.escom.bpm.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Date;

/**
 * Класс сущности Лицензия
 */
@Entity
@Table(name = "licenses")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class License implements Serializable{
    private static final long serialVersionUID = 3853678932708707007L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;

    @Basic(optional = false)
    @Size(max = 255)
    @Column(name = "Name")
    @NotNull
    @XmlElement(name = "Name")
    private String name;

    @Basic(optional = false)
    @Size(max = 32)
    @Column(name = "Number")
    @NotNull
    @XmlElement(name = "Number")
    private String number;

    @Basic(optional = false)
    @Size(max = 255)
    @Column(name = "KeyInfo")
    private String key;

    @Basic(optional = false)
    @Column(name = "DateTerm")
    @XmlElement(name = "DateTerm")
    private Date dateTerm;

    @Basic(optional = false)
    @Column(name = "DateActivate")
    private Date dateActivate;

    @Basic(optional = false)
    @Column(name = "Total")
    @XmlElement(name = "Total")
    @NotNull
    private Integer total;

    @Basic(optional = false)
    @Size(max = 255)
    @Column(name = "Licensor")
    @NotNull
    @XmlElement(name = "Licensor")
    private String licensor;

    public License() {
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDateTerm() {
        return dateTerm;
    }
    public void setDateTerm(Date dateTerm) {
        this.dateTerm = dateTerm;
    }

    public Date getDateActivate() {
        return dateActivate;
    }
    public void setDateActivate(Date dateActivate) {
        this.dateActivate = dateActivate;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public Integer getTotal() {
        return total;
    }
    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getLicensor() {
        return licensor;
    }
    public void setLicensor(String licensor) {
        this.licensor = licensor;
    }

    public String toXML(){
        StringWriter sw = new StringWriter();
        JAXB.marshal(this, sw);
        return sw.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        License license = (License) o;

        return id.equals(license.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
