package com.visuc.referentiel.typeCommerce.web.actions;

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

import com.visuc.referentiel.typeCommerce.entite.TypeCommerce;
import com.visuc.referentiel.typeCommerce.service.ITypeCommerceManager;


@Name("rechercherTypeCommerceAction")
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
@SuppressWarnings("serial")
public class RechercherTypeCommerceAction extends Controller {

	//----------------------------
	//	Criteres de recherche
	private String searchNomTypeCommerce;

	// ---------------------------
	//	Gestion du r�sultat
	private int page;
    private boolean nextPageAvailable;
    private boolean previousPageAvailable;
    private int pageSize = 5;
	
    //----------------------------
    //  Gestion de la liste
    private transient DataModel dataModel;
    private List<TypeCommerce> resultList;
    
	TypeCommerce objet = null;

	//----------------------------
	//  Gestion des messages
	@SuppressWarnings("unused")
	private String noResultMessage = "Aucun enregistrement ne correspond � vos crit�res de recherche";
	
	
	
	/********** Methode en frontal du Backing Bean **********/
	public String first(){
		search();
		
		return "first";
	}
	
	public String search() {
		System.out.println(" ---------- DEBUG :RechercherTypeCommerceAction: "+"search n�"+page);
		page = 0;
        queryTypeCommerce();
        
        return "search";
	}
	
	/* Refresh est appele par event suite a une creation, l'objectif est de mettre 
	 * a jour la liste de rechercheTypeCommerceAction dans le cas ou elle a deja ete construite
	 * correspond a l'implementation du loadPage pour une action de recherche Sphinx
	 * Idee, pouvoir represente le meme resultat de recherche avec le nouveau champ en plus.
	 * 		1) presenter l'ancien critere de recherche
	 * 		2) presenter la liste resultat (avec le nouveau champ)
	 * => il faut rafraichir le DataModel du bean action RechercherTypeCommerceAction, egalement dans le cas
	 * ou on supprime un objet a partir de la liste resultat de recherche.
	 */
	public String refresh(){
		System.out.println(" ---------- DEBUG :RechercherTypeCommerceAction: "+"refresh n�"+page);
		queryTypeCommerce();
		
		return "refresh";
	}

	public String nextPage() {
		page++;
		System.out.println(" ---------- DEBUG :RechercherTypeCommerceAction: "+"nextPage n�"+page);
        queryTypeCommerce();
        
        return "next";
    }
	
	public String previousPage() {
		page--;
		System.out.println(" ---------- DEBUG :RechercherTypeCommerceAction: "+"previousPage n�"+page);
        queryTypeCommerce();
        
        return "previous";
    }
	
/********** Methodes de requetage **********/
    
    /* Utilise pour la recherche queryDevises afin de reconstruire le pattern de recherche par Nom a la vol�e 
     * Le fait de d�clarer cette m�thode en factory, provoque son appel des la cr�ation du bean action, et donc l'instanciation de la string de recherche searchNom
     * Sans cette factory, on obtient une erreur : value="#{rechercherTypeCommerceAction.searchNom}": Error reading 'searchNom' on type com.visuc.referentiel.typeCommerce.web.actions.RechercherTypeCommerceAction */
	@Factory(value="patternNomTypeCommerce", scope=ScopeType.EVENT)
	public String getSearchPatternNomTypeCommerce() {
		System.out.println(" ---------- DEBUG :RechercherTypeCommerceAction: "+"getSearchPatternNomTypeCommerce");
	      
		String result = (searchNomTypeCommerce==null ? 
	              "%" : '%' + searchNomTypeCommerce.toLowerCase().replace('*', '%') + '%');
	    
		System.out.println("                     searchPatternNomTypeCommerce = "+result);
	      
	    return result;
	}
    
    /* Execution de la requete de recherche */
	private void queryTypeCommerce() {
		System.out.println(" ---------- DEBUG :RechercherTypeCommerceAction: "+"queryTypeCommerce");
		
		List<TypeCommerce> results = getTypeCommerceManager().findByName(getSearchPatternNomTypeCommerce(),page,pageSize);
		
		previousPageAvailable = page > 0;
		nextPageAvailable = results.size() > pageSize;
		
		System.out.println("                     nextPageAvailable="+nextPageAvailable+", n�"+page);
		System.out.println("                     previousPageAvailable="+previousPageAvailable+", n�"+page);
		
        if (nextPageAvailable) 
        {
            resultList = new ArrayList<TypeCommerce>(results.subList(0,pageSize));
        } else {
            resultList = results;
        }
        
        setDataModel(wrap(getResultList()));
	}
	
	// ---------------------------------------------------------------------
	// M�thodes de manipulation des donn�es
	// ---------------------------------------------------------------------
	/**Cette m�thode permet d'encapsuler la liste r�sultat dans un objet {@link DataModel} englobant la liste elle-m�me
	 * mais �galement les compteurs connexes (nombre total d'enregistrements, nombre d'enregistrements par page...).
	 */
	protected DataModel wrap(List<TypeCommerce> aList) {
		if (aList == null) {
			return null;
		}
		return new ListDataModel(aList);
	}
	
	/**Permet de determiner le rendered du tableau r�sultat des recherches */
	public boolean isResultListNotEmpty() {
		List<TypeCommerce> lResultList = getResultList();
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
	
	public String getSearchNomTypeCommerce() {
		return searchNomTypeCommerce;
	}
	public void setSearchNomTypeCommerce(String searchNomTypeCommerce) {
		this.searchNomTypeCommerce = searchNomTypeCommerce;
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
	public List<TypeCommerce> getResultList() {
		return resultList;
	}
	public void setResultList(List<TypeCommerce> aResultList) {
		resultList = aResultList;
	}
	
	// -------------------------------------------------------------
	// Gestionnaire de Services
    // -------------------------------------------------------------
	
	/** 
	 * Service IEquipeManager gere par Spring 
	 * enregistre dans monappli-service.xml
	 */
	private ITypeCommerceManager getTypeCommerceManager() {
		return super.evaluateValueExpression("#{typeCommerceManager}", ITypeCommerceManager.class); 		
	}
	
}
