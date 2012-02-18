package com.visuc.referentiel.ville.service;

import java.util.List;

import com.visuc.referentiel.pays.entite.Pays;
import com.visuc.referentiel.ville.entite.Ville;

public interface IVilleManager {

	public Ville save(Ville p) ;
	
	public Ville update(Ville p) ;

	public void deleteOne(Ville p) ;

	
// ***** Implementation service de gestion des criteres de recherche *******
	
	public Ville findById(Long aId);
	
	public List<Ville> findByName(String nom,int numPage,int pageSize) ;

	public List<Ville> findAll();
	
// ******** Couche de service a rendre aux actions EditerXXXXXAction
	// Calcul de toutes les villes existantes, en supprimant ceux déjà liés au pays courant.
	public List<Ville> findAllNotlinkedVilles(Pays p);
	
	public List<Ville> listAllVillesPays(Pays p);
}
