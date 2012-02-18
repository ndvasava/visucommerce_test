package com.visuc.marchand.vendeur.entite;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.visuc.common.entity.Image;
import com.visuc.marchand.commerce.entite.Commerce;


@Entity
@SuppressWarnings("serial")
@Table(name="VENDEUR") 
public class Vendeur implements Serializable {

	@Id
	@Column(name = "ID_VENDEUR")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "NOM")
	private String nom;
	
	@Column(name = "PRENOM")
	private String prenom;
	
	@Column(name = "IS_GERANT")
	private boolean gerant;

	@OneToOne(fetch= FetchType.LAZY)
	@JoinColumn(name = "PHOTO")
	private Image photo;
	
	@ManyToOne
	@JoinColumn(name = "ID_COMMERCE")
	private Commerce commerce;
	
	public Vendeur(){}
		//System.out.println("                                ENTITE VENDEUR CONSTRUCTEUR -> "+"new Vendeur+new Commerce");
		//commerce = new Commerce();

	
	// ******** Getter & Setter **********
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public boolean isGerant() {
		return gerant;
	}
	public void setGerant(boolean isGerant) {
		this.gerant = isGerant;
	}

	public Image getPhoto() {
		return photo;
	}
	public void setPhoto(Image photo) {
		this.photo = photo;
	}

	public Commerce getCommerce() {
		return commerce;
	}
	public void setCommerce(Commerce commerce) {
		this.commerce = commerce;
	};
}
