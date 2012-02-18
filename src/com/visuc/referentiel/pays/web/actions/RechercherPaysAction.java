package com.visuc.referentiel.pays.web.actions;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.framework.Controller;

import com.visuc.referentiel.pays.entite.Pays;
import com.visuc.referentiel.pays.service.IPaysManager;


@Name("rechercherPaysAction")
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
@SuppressWarnings("serial")
public class RechercherPaysAction extends Controller {

	//----------------------------
	//	Criteres de recherche
	private String searchNomPays;

	// ---------------------------
	//	Gestion du résultat
	private int page;
    private boolean nextPageAvailable;
    private boolean previousPageAvailable;
    private int pageSize = 5;
	
    //----------------------------
    //  Gestion de la liste
    private transient DataModel dataModel;
    private List<Pays> resultList;
    
	Pays objet = null;

	//----------------------------
	//  Gestion des messages
	@SuppressWarnings("unused")
	private String noResultMessage = "Aucun enregistrement ne correspond à vos critères de recherche";
	
	
	
	/********** Methode en frontal du Backing Bean **********/
	public String first(){
		search();
		
		return "first";
	}
	
	public String search() {
		System.out.println(" ---------- DEBUG :RechercherPaysAction: "+"search n°"+page);
		page = 0;
        queryPays();
        
        return "search";
	}
	
	/* Refresh est appele par event suite a une creation, l'objectif est de mettre 
	 * a jour la liste de recherchePaysAction dans le cas ou elle a deja ete construite
	 * correspond a l'implementation du loadPage pour une action de recherche Sphinx
	 * Idee, pouvoir represente le meme resultat de recherche avec le nouveau champ en plus.
	 * 		1) presenter l'ancien critere de recherche
	 * 		2) presenter la liste resultat (avec le nouveau champ)
	 * => il faut rafraichir le DataModel du bean action RechercherPaysAction, egalement dans le cas
	 * ou on supprime un objet a partir de la liste resultat de recherche.
	 */
	public String refresh(){
		System.out.println(" ---------- DEBUG :RechercherPaysAction: "+"refresh n°"+page);
		queryPays();
		
		return "refresh";
	}

	public String nextPage() {
		page++;
		System.out.println(" ---------- DEBUG :RechercherPaysAction: "+"nextPage n°"+page);
        queryPays();
        
        return "next";
    }
	
	public String previousPage() {
		page--;
		System.out.println(" ---------- DEBUG :RechercherPaysAction: "+"previousPage n°"+page);
        queryPays();
        
        return "previous";
    }
	
/********** Methodes de requetage **********/
    
    /* Utilise pour la recherche queryDevises afin de reconstruire le pattern de recherche par Nom a la volée 
     * Le fait de déclarer cette méthode en factory, provoque son appel des la création du bean action, et donc l'instanciation de la string de recherche searchNom
     * Sans cette factory, on obtient une erreur : value="#{rechercherPaysAction.searchNom}": Error reading 'searchNom' on type com.visuc.referentiel.pays.web.actions.RechercherPaysAction */
	@Factory(value="patternNomPays", scope=ScopeType.EVENT)
	public String getSearchPatternNomPays() {
		System.out.println(" ---------- DEBUG :RechercherPaysAction: "+"getSearchPatternNomPays");
	      
		String result = (searchNomPays==null ? 
	              "%" : '%' + searchNomPays.toLowerCase().replace('*', '%') + '%');
	    
		System.out.println("                     searchPatternNomPays = "+result);
	      
	    return result;
	}
    
    /* Execution de la requete de recherche */
	private void queryPays() {
		System.out.println(" ---------- DEBUG :RechercherPaysAction: "+"queryPays");
		
		List<Pays> results = getPaysManager().findByName(getSearchPatternNomPays(),page,pageSize);
		
		previousPageAvailable = page > 0;
		nextPageAvailable = results.size() > pageSize;
		
		System.out.println("                     nextPageAvailable="+nextPageAvailable+", n°"+page);
		System.out.println("                     previousPageAvailable="+previousPageAvailable+", n°"+page);
		
        if (nextPageAvailable) 
        {
            resultList = new ArrayList<Pays>(results.subList(0,pageSize));
        } else {
            resultList = results;
        }
        
        setDataModel(wrap(getResultList()));
	}
	
	// ---------------------------------------------------------------------
	// Méthodes de manipulation des données
	// ---------------------------------------------------------------------
	/**Cette méthode permet d'encapsuler la liste résultat dans un objet {@link DataModel} englobant la liste elle-même
	 * mais également les compteurs connexes (nombre total d'enregistrements, nombre d'enregistrements par page...).
	 */
	protected DataModel wrap(List<Pays> aList) {
		if (aList == null) {
			return null;
		}
		return new ListDataModel(aList);
	}
	
	/**Permet de determiner le rendered du tableau résultat des recherches */
	public boolean isResultListNotEmpty() {
		List<Pays> lResultList = getResultList();
		if (lResultList == null || lResultList.size() == 0) {
			return false;
		}
		return true;
	}
	
/*********** Gestion de la pagination ************/
    
	public boolean isNextPageAvailable() {
        return nextPageAvailable;
    }
	
	public boolean isPreviousPageAvailable() {
        return previousPageAvailable;
    }
	
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
/*************** Getter&Setter pour critere de recherche ********/
	
	public String getSearchNomPays() {
		return searchNomPays;
	}
	public void setSearchNomPays(String searchNomPays) {
		this.searchNomPays = searchNomPays;
	}
	public DataModel getDataModel() {
		if (dataModel == null) {
			dataModel = wrap(getResultList());
		}
		return dataModel;
	}
	public void setDataModel(DataModel aDataModel) {
		dataModel = aDataModel;
	}
	public List<Pays> getResultList() {
		return resultList;
	}
	public void setResultList(List<Pays> aResultList) {
		resultList = aResultList;
	}
	
	// -------------------------------------------------------------
	// Gestionnaire de Services
    // -------------------------------------------------------------
	
	/** 
	 * Service IEquipeManager gere par Spring 
	 * enregistre dans monappli-service.xml
	 */
	private IPaysManager getPaysManager() {
		return super.evaluateValueExpression("#{paysManager}", IPaysManager.class); 		
	}
	
}
