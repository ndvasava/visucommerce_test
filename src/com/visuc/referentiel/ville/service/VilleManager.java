package com.visuc.referentiel.ville.service;

import java.util.List;

import com.visuc.referentiel.pays.entite.Pays;
import com.visuc.referentiel.ville.dao.VilleDao;
import com.visuc.referentiel.ville.entite.Ville;

public class VilleManager implements IVilleManager{

	VilleDao villeDao;

	public void setVilleDao(VilleDao villeDao) {
		this.villeDao = villeDao;
	}

// ***** Implementation CRUD couche service *******	
	public Ville save(Ville p) {
		System.out.println("    -DEBUG-   VilleManager.save "+p);
		return villeDao.saveOne(p);
	}
	
	public Ville update(Ville p) {
		System.out.println("    -DEBUG-   VilleManager.update "+p);
		return villeDao.updateOne(p);
	}

	public void deleteOne(Ville p) {
		System.out.println("    -DEBUG-   VilleManager.deleteOne "+p.getId());
		villeDao.deleteOne(p.getId());
	}
	
// ***** Implementation service de gestion des criteres de recherche *******
	
	public Ville findById(Long aId){
		System.out.println("    -DEBUG service-   VilleManager.findById "+aId);
		return villeDao.findById(aId);
	}
	
	public List<Ville> findByName(String nom,int numPage,int pageSize) {
		System.out.println("    -DEBUG service-   VilleManager.findByName "+nom);
		return villeDao.findByName(nom,numPage,pageSize);
	}
	
	public List<Ville> findAll(){
		return villeDao.findByName("%%",0,1000);
	}

// ******** Couche de service a rendre aux actions EditerXXXXXAction	
	// Calcul de toutes les villes existantes, en supprimant ceux déjà liés au pays courant.
	public List<Ville> findAllNotlinkedVilles(Pays p){
		return villeDao.findAllNotlinkedVilles(p.getId());
	}
	
	// Calcul de toutes les villes existantes, en supprimant ceux déjà liés au pays courant.
	public List<Ville> listAllVillesPays(Pays p){
		return villeDao.listAllVillesPays(p.getId());
	}
}
