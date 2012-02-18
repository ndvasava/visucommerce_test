package com.visuc.referentiel.ville.dao;

import java.util.List;

import com.visuc.referentiel.ville.entite.Ville;

public interface IVilleDao {

	// ajouter/modifier un pays
	Ville saveOne(Ville p);
	
	// modifier un pays
	Ville updateOne(Ville p);
	
	// supprimer un vendeur(attention au objets li�s)
	void deleteOne(Long aId);
	
	//rechercher par identifiant format Long
	Ville findById(Long aId);
	
	// rechercher les vendeurs par le nom, avec pagination du r�sultat  
	List<Ville> findByName(String nom,int numPage,int pageSize);

	
	// Calcul de toutes les villes existantes, en supprimant ceux d�j� li�s au pays courant.
	List<Ville> findAllNotlinkedVilles(Long aId);
	
	// Calcul de toutes les villes existantes pour un pays.
	List<Ville> listAllVillesPays(final Long aId);
}
