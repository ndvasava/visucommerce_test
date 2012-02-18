package com.visuc.referentiel.pays.service;

import java.util.List;

import com.visuc.referentiel.devise.entite.Devise;
import com.visuc.referentiel.langue.entite.Langue;
import com.visuc.referentiel.pays.dao.PaysDao;
import com.visuc.referentiel.pays.entite.Pays;

public class PaysManager implements IPaysManager{

	PaysDao paysDao;

	public void setPaysDao(PaysDao paysDao) {
		this.paysDao = paysDao;
	}

// ***** Implementation CRUD couche service *******	
	public Pays save(Pays p) {
		System.out.println("    -DEBUG-   PaysManager.save "+p);
		return paysDao.saveOne(p);
	}
	
	public Pays update(Pays p) {
		System.out.println("    -DEBUG-   PaysManager.update "+p);
		return paysDao.updateOne(p);
	}

	public void deleteOne(Pays p) {
		System.out.println("    -DEBUG-   PaysManager.deleteOne "+p.getId());
		paysDao.deleteOne(p.getId());
	}
	
// ***** Implementation service de gestion des criteres de recherche *******
	
	public Pays findById(Long aId){
		System.out.println("    -DEBUG service-   PaysManager.findById "+aId);
		return paysDao.findById(aId);
	}
	
	public List<Pays> findByName(String nom,int numPage,int pageSize) {
		System.out.println("    -DEBUG service-   PaysManager.findByName "+nom);
		return paysDao.findByName(nom,numPage,pageSize);
	}

// ******** Couche de service a rendre aux actions EditerXXXXXAction

	// Calcul de tous les pays existants.
	public List<Pays> findAll(){
		return paysDao.findByName("%%",0,1000);
	}
	
	// Calcul de tous les pays existants, sans devises définies.
	public List<Pays> findAllNotlinkedCountries_WithoutDevise(){
		return paysDao.findAllNotlinkedCountries_WithoutDevise();
	}
	
	// Calcul de tous les pays existants, en supprimant ceux déjà liés à la devise courante.
	public List<Pays> findAllNotlinkedCountries(Devise d){
		return paysDao.findAllNotlinkedCountries_4Devise(d.getId());
	}
	
	// Calcul de tous les pays existants, en supprimant ceux déjà liés à la langue courante.
	public List<Pays> findAllNotlinkedCountries(Langue l){
		return paysDao.findAllNotlinkedCountries_4Langue(l.getId());
	}
}
