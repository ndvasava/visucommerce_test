package com.visuc.referentiel.typeCommerce.entite;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@SuppressWarnings("serial")
@Table(name="REF_TYPE_COMMERCE") 	
public class TypeCommerce implements Serializable {

	@Id
	@Column(name = "ID_TYPCOM")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "TYPCOM")
	private String typeCommerceName;

	
	public TypeCommerce(){};
	
	// ******** Getter & Setter **********
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getTypeCommerceName() {
		return typeCommerceName;
	}
	public void setTypeCommerceName(String typcom) {
		this.typeCommerceName = typcom;
	}
	
	
}
