package com.visuc.referentiel.pays.dao;

import java.util.List;

import com.visuc.referentiel.pays.entite.Pays;

public interface IPaysDao {

	// ajouter/modifier un pays
	Pays saveOne(Pays p);
	
	// modifier un pays
	Pays updateOne(Pays p);
	
	// supprimer un vendeur(attention au objets li�s)
	void deleteOne(Long aId);
	
	//rechercher par identifiant format Long
	Pays findById(Long aId);
	
	// rechercher les vendeurs par le nom, avec pagination du r�sultat  
	List<Pays> findByName(String nom,int numPage,int pageSize);
	
	// Calcul tous les pays sans devises li�es
	List<Pays> findAllNotlinkedCountries_WithoutDevise();
	
	// Calcul de tous les pays existants, en supprimant ceux d�j� li�s � la devise courante.
	List<Pays> findAllNotlinkedCountries_4Langue(Long aId);
	
	// Calcul de tous les pays existants, en supprimant ceux d�j� li�s � la devise courante.
	List<Pays> findAllNotlinkedCountries_4Devise(Long aId);
}
