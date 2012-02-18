package com.visuc.referentiel.devise.dao;

import java.util.List;

import com.visuc.referentiel.devise.entite.Devise;

public interface IDeviseDao {

	// ajouter/modifier un pays
	Devise saveOne(Devise p);
	
	// modifier un pays
	Devise updateOne(Devise p);
	
	// supprimer un vendeur(attention au objets liés)
	void deleteOne(Long aId);
	
	//rechercher par identifiant format Long
	Devise findById(Long aId);
	
	// rechercher les vendeurs par le nom, avec pagination du résultat  
	List<Devise> findByName(String nom,int numPage,int pageSize);
	
}
