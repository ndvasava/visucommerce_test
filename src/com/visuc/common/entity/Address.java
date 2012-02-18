package com.visuc.common.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.visuc.referentiel.ville.entite.Ville;

@Entity
@Table(name = "ADDRESS")
@SuppressWarnings("serial")
public class Address implements Serializable {

	@Id
	@Column(name = "ID_ADDRESS")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "ADDRESS1")
	private String address1;
	
	@Column(name = "ADDRESS2")
	private String address2;
	
	@OneToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="ID_CITY")
	private Ville city;
	
	//----- GETTERS & SETTERS ---------------------------
	
	public Address() {};
	
	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String adress1) {
		this.address1 = adress1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String adress2) {
		this.address2 = adress2;
	}
	public Ville getCity() {
		return city;
	}
	public void setCity(Ville city) {
		this.city = city;
	}
}
