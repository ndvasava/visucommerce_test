package com.visuc.marchand.vendeur.dao;

import java.util.List;

import com.visuc.marchand.vendeur.entite.Vendeur;

public interface IVendeurDao {

	// ajouter/modifier un pays
	Vendeur saveOne(Vendeur p);
	
	// modifier un pays
	Vendeur updateOne(Vendeur p);
	
	// supprimer un vendeur(attention au objets li�s)
	void deleteOne(Long aId);
	
	//rechercher par identifiant format Long
	Vendeur findById(Long aId);
	
	// rechercher les vendeurs par le nom, avec pagination du r�sultat  
	List<Vendeur> findByName(String nom,int numPage,int pageSize);

}
