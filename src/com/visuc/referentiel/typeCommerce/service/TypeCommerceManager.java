package com.visuc.referentiel.typeCommerce.service;

import java.util.List;

import com.visuc.referentiel.typeCommerce.dao.TypeCommerceDao;
import com.visuc.referentiel.typeCommerce.entite.TypeCommerce;


public class TypeCommerceManager implements ITypeCommerceManager{

	TypeCommerceDao typeCommerceDao;

	public void setTypeCommerceDao(TypeCommerceDao typeCommerceDao) {
		this.typeCommerceDao = typeCommerceDao;
	}

// ***** Implementation CRUD couche service *******	
	public TypeCommerce save(TypeCommerce d) {
		System.out.println("    -DEBUG-   TypeCommerceManager.save "+d);
		return typeCommerceDao.saveOne(d);
	}
	
	public TypeCommerce update(TypeCommerce d) {
		System.out.println("    -DEBUG-   TypeCommerceManager.update "+d);
		return typeCommerceDao.updateOne(d);
	}

	public void deleteOne(TypeCommerce d) {
		System.out.println("    -DEBUG-   TypeCommerceManager.deleteOne "+d.getId());
		typeCommerceDao.deleteOne(d.getId());
	}
	
// ***** Implementation service de gestion des criteres de recherche *******
	
	public TypeCommerce findById(Long aId){
		System.out.println("    -DEBUG service-   TypeCommerceManager.findById "+aId);
		return typeCommerceDao.findById(aId);
	}
	
	public List<TypeCommerce> findByName(String nom,int numPage,int pageSize) {
		System.out.println("    -DEBUG service-   TypeCommerceManager.findByName "+nom);
		return typeCommerceDao.findByName(nom,numPage,pageSize);
	}
	
	public List<TypeCommerce> findAll(){
		return typeCommerceDao.findByName("%%",0,1000);
	}


// ******** Couche de service a rendre a l'action EditerTypeCommerceAction

	
	
}