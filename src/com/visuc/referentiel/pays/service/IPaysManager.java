package com.visuc.referentiel.pays.service;

import java.util.List;

import com.visuc.referentiel.devise.entite.Devise;
import com.visuc.referentiel.langue.entite.Langue;
import com.visuc.referentiel.pays.entite.Pays;

public interface IPaysManager {

	public Pays save(Pays p) ;
	
	public Pays update(Pays p) ;

	public void deleteOne(Pays p) ;
	
// ***** Implementation service de gestion des criteres de recherche *******
	
	public Pays findById(Long aId);
	
	public List<Pays> findByName(String nom,int numPage,int pageSize) ;


// ******** Couche de service a rendre aux actions EditerXXXXXAction

	// Calcul de tous les pays existants.
	public List<Pays> findAll();
	
	// Calcul tous les pays existants qui n'ont pas de devises définies
	List<Pays> findAllNotlinkedCountries_WithoutDevise();
	
	// Calcul de tous les pays existants, en supprimant ceux déjà liés à la devise courante.
	List<Pays> findAllNotlinkedCountries(Devise d);
	
	// Calcul de tous les pays existants, en supprimant ceux déjà liés à la langue courante.
	List<Pays> findAllNotlinkedCountries(Langue l);
	
}
