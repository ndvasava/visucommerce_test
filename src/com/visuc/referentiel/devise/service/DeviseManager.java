package com.visuc.referentiel.devise.service;

import java.util.List;

import com.visuc.referentiel.devise.dao.DeviseDao;
import com.visuc.referentiel.devise.entite.Devise;

public class DeviseManager implements IDeviseManager{

	DeviseDao deviseDao;

	public void setDeviseDao(DeviseDao deviseDao) {
		this.deviseDao = deviseDao;
	}

// ***** Implementation CRUD couche service *******	
	public Devise save(Devise d) {
		System.out.println("    -DEBUG-   DeviseManager.save "+d);
		return deviseDao.saveOne(d);
	}
	
	public Devise update(Devise d) {
		System.out.println("    -DEBUG-   DeviseManager.update "+d);
		return deviseDao.updateOne(d);
	}

	public void deleteOne(Devise d) {
		System.out.println("    -DEBUG-   DeviseManager.deleteOne "+d.getId());
		deviseDao.deleteOne(d.getId());
	}
	
// ***** Implementation service de gestion des criteres de recherche *******
	
	public Devise findById(Long aId){
		System.out.println("    -DEBUG service-   DeviseManager.findById "+aId);
		return deviseDao.findById(aId);
	}
	
	public List<Devise> findByName(String nom,int numPage,int pageSize) {
		System.out.println("    -DEBUG service-   DeviseManager.findByName "+nom);
		return deviseDao.findByName(nom,numPage,pageSize);
	}


// ******** Couche de service a rendre a l'action EditerDeviseAction

	
}