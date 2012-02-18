package com.visuc.referentiel.langue.service;

import java.util.List;

import com.visuc.referentiel.langue.dao.LangueDao;
import com.visuc.referentiel.langue.entite.Langue;
import com.visuc.referentiel.pays.entite.Pays;

public class LangueManager implements ILangueManager{

	LangueDao langueDao;

	public void setLangueDao(LangueDao langueDao) {
		this.langueDao = langueDao;
	}

// ***** Implementation CRUD couche service *******	
	public Langue save(Langue d) {
		System.out.println("    -DEBUG-   LangueManager.save "+d);
		return langueDao.saveOne(d);
	}
	
	public Langue update(Langue d) {
		System.out.println("    -DEBUG-   LangueManager.update "+d);
		return langueDao.updateOne(d);
	}

	public void deleteOne(Langue d) {
		System.out.println("    -DEBUG-   LangueManager.deleteOne "+d.getId());
		langueDao.deleteOne(d.getId());
	}
	
// ***** Implementation service de gestion des criteres de recherche *******
	
	public Langue findById(Long aId){
		System.out.println("    -DEBUG service-   LangueManager.findById "+aId);
		return langueDao.findById(aId);
	}
	
	public List<Langue> findByNameOrCode(String nomLangue,String codeIso,int numPage,int pageSize) {
		System.out.println("    -DEBUG service-   LangueManager.findByName "+nomLangue+", "+codeIso);
		return langueDao.findByNameOrCode(nomLangue,codeIso,numPage,pageSize);
	}


// ******** Couche de service a rendre a l'action EditerLangueAction

	// Calcul de tous les langages existants, calcul exhaustif du a la relation manyToMany et l'affichage en rich:pickList.
	public List<Langue> findAllNotlinkedLanguages(Pays p){
		return langueDao.findAllNotlinkedLanguages_4Country(p.getId());
	}
	
}