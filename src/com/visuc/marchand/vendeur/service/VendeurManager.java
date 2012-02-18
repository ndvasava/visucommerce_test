package com.visuc.marchand.vendeur.service;

import java.util.List;

import com.visuc.marchand.vendeur.dao.VendeurDao;
import com.visuc.marchand.vendeur.entite.Vendeur;

public class VendeurManager implements IVendeurManager{

	VendeurDao spDao;

	public void setSpDao(VendeurDao vendeurDao) {
		this.spDao = vendeurDao;
	}

// ***** Implementation CRUD couche service *******	
	public Vendeur save(Vendeur p) {
		System.out.println("    -DEBUG-   VendeurManager.save "+p);
		return spDao.saveOne(p);
	}
	
	public Vendeur update(Vendeur p) {
		System.out.println("    -DEBUG-   VendeurManager.update "+p);
		return spDao.updateOne(p);
	}

	public void deleteOne(Vendeur p) {
		System.out.println("    -DEBUG-   VendeurManager.deleteOne "+p.getId());
		spDao.deleteOne(p.getId());
	}
	
// ***** Implementation service de gestion des criteres de recherche *******
	
	public Vendeur findById(Long aId){
		System.out.println("    -DEBUG service-   VendeurManager.findById "+aId);
		return spDao.findById(aId);
	}
	
	public List<Vendeur> findByName(String nom,int numPage,int pageSize) {
		System.out.println("    -DEBUG service-   VendeurManager.findByName "+nom);
		return spDao.findByName(nom,numPage,pageSize);
	}
	

// ******** Couche de service a rendre aux actions EditerXXXXXAction	
	
}
