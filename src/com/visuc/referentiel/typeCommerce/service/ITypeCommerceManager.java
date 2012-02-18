package com.visuc.referentiel.typeCommerce.service;

import java.util.List;

import com.visuc.referentiel.typeCommerce.entite.TypeCommerce;

public interface ITypeCommerceManager {

	public TypeCommerce save(TypeCommerce p) ;
	
	public TypeCommerce update(TypeCommerce p) ;

	public void deleteOne(TypeCommerce p) ;
	
// ***** Implementation service de gestion des criteres de recherche *******
	
	public TypeCommerce findById(Long aId);
	
	public List<TypeCommerce> findByName(String nom,final int numPage,final int pageSize);

	public List<TypeCommerce> findAll();
	
// ******** Couche de service a rendre a l'action EditerTypeCommerceAction

}
