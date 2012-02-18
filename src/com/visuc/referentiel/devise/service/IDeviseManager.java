package com.visuc.referentiel.devise.service;

import java.util.List;

import com.visuc.referentiel.devise.entite.Devise;

public interface IDeviseManager {

	public Devise save(Devise p) ;
	
	public Devise update(Devise p) ;

	public void deleteOne(Devise p) ;
	
// ***** Implementation service de gestion des criteres de recherche *******
	
	public Devise findById(Long aId);
	
	public List<Devise> findByName(String nom,int numPage,int pageSize) ;

	
// ******** Couche de service a rendre a l'action EditerDeviseAction
	
}
