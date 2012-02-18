package com.visuc.referentiel.langue.service;

import java.util.List;

import com.visuc.referentiel.langue.entite.Langue;
import com.visuc.referentiel.pays.entite.Pays;

public interface ILangueManager {

	public Langue save(Langue p) ;
	
	public Langue update(Langue p) ;

	public void deleteOne(Langue p) ;
	
// ***** Implementation service de gestion des criteres de recherche *******
	
	public Langue findById(Long aId);
	
	List<Langue> findByNameOrCode(String nomLangue,String codeIso,final int numPage,final int pageSize);

	
// ******** Couche de service a rendre a l'action EditerLangueAction
	// Calcul de tous les langages existants, calcul exhaustif du a la relation manyToMany et l'affichage en rich:pickList.
	List<Langue> findAllNotlinkedLanguages(Pays p);
}
