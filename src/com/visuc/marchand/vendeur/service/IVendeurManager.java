package com.visuc.marchand.vendeur.service;

import java.util.List;

import com.visuc.marchand.vendeur.entite.Vendeur;

public interface IVendeurManager {

	public Vendeur save(Vendeur p) ;
	
	public Vendeur update(Vendeur p) ;

	public void deleteOne(Vendeur p) ;

	
// ***** Implementation service de gestion des criteres de recherche *******
	
	public Vendeur findById(Long aId);
	
	public List<Vendeur> findByName(String nom,int numPage,int pageSize) ;

	
// ******** Couche de service a rendre aux actions EditerXXXXXAction
	
}
