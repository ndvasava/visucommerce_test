package com.visuc.marchand.vendeur.entite;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.visuc.referentiel.typeCommerce.entite.TypeCommerce;

@Entity
@SuppressWarnings("serial")
@Table(name="SOCIETE")
public class Societe implements Serializable {

	@Id
	@Column(name="ID_SOCIETE")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="NOM_SOCIETE")
	private String nomSociete;
	
	@Column(name="ID_REF_TYPE_COMMERCE")
	private TypeCommerce idTypeRefCommerce;
	
	@Column(name="REG_TYPE")
	private String regType;
	
	@Column(name="REG_ID")
	private String regId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNomSociete() {
		return nomSociete;
	}
	public void setNomSociete(String nomSociete) {
		this.nomSociete = nomSociete;
	}
	public TypeCommerce getIdTypeRefCommerce() {
		return idTypeRefCommerce;
	}
	public void setIdTypeRefCommerce(TypeCommerce idTypeRefCommerce) {
		this.idTypeRefCommerce = idTypeRefCommerce;
	}
	public String getRegType() {
		return regType;
	}
	public void setRegType(String regType) {
		this.regType = regType;
	}
	public String getRegId() {
		return regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}

}
