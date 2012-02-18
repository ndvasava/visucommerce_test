package com.visuc.referentiel.typeCommerce.dao;

import java.util.List;

import com.visuc.referentiel.typeCommerce.entite.TypeCommerce;

public interface ITypeCommerceDao {

	// ajouter/modifier un pays
	TypeCommerce saveOne(TypeCommerce tc);
	
	// modifier un pays
	TypeCommerce updateOne(TypeCommerce tc);
	
	// supprimer un vendeur(attention au objets liés)
	void deleteOne(Long aId);
	
	//rechercher par identifiant format Long
	TypeCommerce findById(Long aId);
	
	// rechercher les langues par le nom, avec pagination du résultat  
	List<TypeCommerce> findByName(String nom,int numPage,int pageSize);
	
}
