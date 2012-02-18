package com.visuc.marchand.web.actions;

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

import com.visuc.marchand.commerce.common.Constants;
import com.visuc.marchand.commerce.entite.Commerce;
import com.visuc.marchand.commerce.service.ICommerceManager;

@Name("commerceSearchAction")
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
@SuppressWarnings("serial")
public class CommerceSearchAction extends Controller {

	private String searchString;
	private boolean commerceAValider;
	
	//	--------- Commerce search result management -----------------
	private int page;
    private boolean nextPageAvailable;
    private boolean previousPageAvailable;
    private int pageSize = 5;
	
    
    // ------------- commerce search result list management ----------
    
    private transient DataModel dataModel;
    private List<Commerce> resultList;
 
	// ------ MESSAGES -------------------------------
	@SuppressWarnings("unused")
	private String noResultMessage = "Aucun enregistrement ne correspond à vos critères de recherche";

    // ------ business methods -----------------------
	
	public String search() {
		System.out.println("--> CommerceSearchAction.search()");
		page = 0;
		queryCommerce();
        System.out.println("	page n°" + page);
        System.out.println("	total results : " + resultList.size());
        System.out.println("<-- CommerceSearchAction.search()");
        return "search";
	}

	public String searchCompanyAccToValidate() {
		System.out.println("--> CommerceSearchAction.searchCompanyAccToValidate()");
		page = 0;
		queryAccListListToValidate();
		System.out.println("<-- CommerceSearchAction.searchCompanyAccToValidate()");
		return "search_OK";
	}
	
	public String refresh(){
		System.out.println("--> CommerceSearchAction.refresh() page n°" + page);
		queryCommerce();
		
		return "refresh";
	}

	public String firstComAccToValidate(){
		System.out.println("--> CommerceSearchAction.firstComAccToValidate()");
		page = 0;
		queryAccListListToValidate();
		System.out.println("	page n°" + page);
	    System.out.println("	total results : " + resultList.size());
	    System.out.println("<-- CommerceSearchAction.firstComAccToValidate()");
		return "first";
	}

	public String nextComAccToValidate() {
		System.out.println("--> CommerceSearchAction.nextComAccToValidate()");
		page++;
		queryAccListListToValidate();
        System.out.println("	page n°" + page);
        System.out.println("	total results : " + resultList.size());
        System.out.println("<-- CommerceSearchAction.nextComAccToValidate()");
        return "next";
    }
	
	public String prevComAccToValidate() {
		System.out.println("--> CommerceSearchAction.prevComAccToValidate()");
		page--;
		queryAccListListToValidate();
		System.out.println("	page n°" + page);
        System.out.println("	total results : " + resultList.size());
        System.out.println("<-- CommerceSearchAction.prevComAccToValidate()");
        return "previous";
    }
	
	
	public String first(){
		System.out.println("--> CommerceSearchAction.first()");
		page = 0;
		queryCommerce();
		System.out.println("	page n°" + page);
	    System.out.println("	total results : " + resultList.size());
	    System.out.println("<-- CommerceSearchAction.first()");
		return "first";
	}

	public String nextPage() {
		System.out.println("--> CommerceSearchAction.nextPage()");
		page++;
		queryCommerce();
        System.out.println("	page n°" + page);
        System.out.println("	total results : " + resultList.size());
        System.out.println("<-- CommerceSearchAction.nextPage()");
        return "next";
    }
	
	public String previousPage() {
		System.out.println("--> CommerceSearchAction.previousPage()");
		page--;
		queryCommerce();
		System.out.println("	page n°" + page);
        System.out.println("	total results : " + resultList.size());
        System.out.println("<-- CommerceSearchAction.previousPage()");
        return "previous";
    }

	
    private void queryCommerce() {
    	
		System.out.println(" --> CommerceSearchAction.searchCommerce()");

		List<Commerce> results;
		if (isCommerceAValider()) {
			this.searchString = null;
			results = getCommerceManager()
					.findCommerceByNameAndStatus(getSearchPatternCommerceName(this.searchString), 0, page, pageSize);
		} else {
			results = getCommerceManager()
					.findCommerceByName(getSearchPatternCommerceName(this.searchString), page, pageSize);
		}
		previousPageAvailable = page > 0;
		nextPageAvailable = results.size() > pageSize;

        if (nextPageAvailable) 
        {
            setResultList(new ArrayList<Commerce>(results.subList(0, pageSize)));
        } else {
            setResultList(results);
        }        
        setDataModel(wrap(getResultList()));

		System.out.println(" <-- CommerceSearchAction.searchCommerce()");
    }
    
    private void queryAccListListToValidate() {	
		System.out.println(" --> CommerceSearchAction.queryAccListListToValidate()");
		
		List<Commerce> results;
		results = getCommerceManager()
				.findCommerceByNameAndStatus(getSearchPatternCommerceName
						(this.searchString), Constants.ACC_STATUS_CREATED, page, pageSize);
		previousPageAvailable = page > 0;
		nextPageAvailable = results.size() > pageSize;

        if (nextPageAvailable) 
        {
            setResultList(new ArrayList<Commerce>(results.subList(0, pageSize)));
        } else {
            setResultList(results);
        }        
        setDataModel(wrap(getResultList()));

		System.out.println(" <-- CommerceSearchAction.queryAccListListToValidate()");
    }

	/**Cette méthode permet d'encapsuler la liste résultat dans un objet {@link DataModel} englobant la liste elle-même
	 * mais également les compteurs connexes (nombre total d'enregistrements, nombre d'enregistrements par page...).
	 */
	protected DataModel wrap(List<Commerce> aList) {
		if (aList == null) {
			return null;
		}
		return new ListDataModel(aList);
	}

	@Factory(value="patternCommerceName", scope=ScopeType.EVENT)
	public String getSearchPatternCommerceName(String pSearchString) {
		System.out.println(" --> CommerceSearchAction.getSearchPatternCommerceName()");
	      
		String result = (pSearchString == null ? 
	              "%" : '%' + pSearchString.toLowerCase().replace('*', '%') + '%');
	    
		System.out.println("query => " + result);
	      
	    return result;
	}

	
    // ------ SERVICES --------------------------------
	/** 
	 * ICommerceManager managed by Spring in <myappli>-service.xml
	 */
	private ICommerceManager getCommerceManager() {
		return super.evaluateValueExpression("#{commerceManager}", ICommerceManager.class); 		
	}
    
	
	
	// ------ PAGINATION ------------------------------
    
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

	public boolean isResultListNotEmpty() {
		
		List<Commerce> lResultList = getResultList();
		
		if (lResultList == null || lResultList.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

	
    // ------ GETTERS and SETTERS ---------------------
	
	public void setSearchString(String pCommerceSearchName) {
		this.searchString = pCommerceSearchName;
	}
	
	public String getSearchString() {
		return searchString;
	}

	public void setResultList(List<Commerce> resultList) {
		this.resultList = resultList;
	}

	public List<Commerce> getResultList() {
		return resultList;
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

	public void setCommerceAValider(boolean commerceAValider) {
		this.commerceAValider = commerceAValider;
	}

	public boolean isCommerceAValider() {
		return commerceAValider;
	}


}
