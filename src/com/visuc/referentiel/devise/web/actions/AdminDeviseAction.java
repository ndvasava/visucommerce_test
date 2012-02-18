package com.visuc.referentiel.devise.web.actions;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.SystemException;


import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.framework.Controller;
import org.jboss.seam.transaction.Transaction;
import org.richfaces.event.DropEvent;

import com.visuc.referentiel.devise.entite.Devise;
import com.visuc.referentiel.devise.service.IDeviseManager;
import com.visuc.referentiel.pays.entite.Pays;
import com.visuc.referentiel.pays.service.IPaysManager;

@Name("adminDeviseAction")
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
@SuppressWarnings("serial")
public class AdminDeviseAction extends Controller {

	// ---- Liste manipule par l'action
	private List<Devise> listDevises;
	private List<Pays> listPays;
	private List<Pays> listNewNoDevisePays;
	/*private List<Pays> listAvailablePays;*/
	
	// ---- 
	// we have to know which which desc in the list was touched  
    // the value for this is sent via a a4j:actionparam tag  
    private Integer rowUpdated = new Integer(0);
	private List<Devise> devisesToUpdate = new ArrayList<Devise>();
	
	
	private transient EntityManager entityManager;
	
/*********************************************************************************************************************/	
	/********** Methode en frontal du Backing Bean **********/
	public String reset() {
		System.out.println("                 dEbUg  - - >  AdminDeviseAction.reset");
		
		// Si on fait uniquement faire au reset un chargement depuis le manager JPA, alors il recharge les devises avec les modifications des pays 
		// Déjà effectuées. Il faut donc jouer du refresh sur les différentes entités Devises !
		//initDevisesList();
		initPaysList();
		cancel();
		
		return "reset";
	}
	
	/** Process JSF Drop event in one Devise detination */
	public void processDrop(DropEvent event) {
		System.out.println("                 dEbUg  - - >  AdminDeviseAction.processDrop");
		
		System.out.println("                                                      "+event.getDragValue()+", "+event.getDropValue());
		System.out.println("                                                      "+"position de la devise dans la liste : "+listDevises.indexOf(event.getDropValue()));
		
		// 1-Set la devise dans le Pays (non execute pour identification au moment de l'update)
		// 2-Ajoute le pays a la liste des pays de la devise
		// 3-Supprime le pays de la liste des options non deja liées à une devise
		// 4-Supprime s'il existe le pays de la liste des NewNoDevise
		((Pays)event.getDragValue()).setDevise(listDevises.get(listDevises.indexOf(event.getDropValue())));
		listDevises.get(listDevises.indexOf(event.getDropValue())).getListPays().add((Pays)event.getDragValue());
		listPays.remove((Pays)event.getDragValue());
		if(listNewNoDevisePays != null)
			listNewNoDevisePays.remove((Pays)event.getDragValue());
	}
	
	
	/** Process JSF Drop in the source */
	public void processDropNoDevise(DropEvent event) {
		System.out.println("                 dEbUg  - - >  AdminDeviseAction.processDropNoDevise");
		
		System.out.println("                                                      "+event.getDragValue()+", "+event.getDropValue());
		//System.out.println("                                                      "+"position de la devise dans la liste : "+listDevises.indexOf(event.getDropValue()));
		
		// 1-recupere la devise
		// 2-met a jour la liste des devises et la liste des pays de la devise concernée
		// 3-annule le lien du pays sur la devise
		// 4-rajoute le pays dans la liste des noDevises
		// 5-save le pays en tant que pays sans devise à mettre à jour
		Pays p_temp = (Pays)event.getDragValue();
		listDevises.get(listDevises.indexOf(p_temp.getDevise())).getListPays().remove(p_temp);
		p_temp.setDevise(null);
		listPays.add(p_temp);
		listNewNoDevisePays.add(p_temp);
	}
	
/********************************************************************************************************************/	
	/* - - - - - -  Getter&Setter  - - - - - - - */
	public List<Devise> getDevisesToUpdate() {  
        System.out.println(" starting getDevisesToUpdate");  
        return devisesToUpdate;  
    }  
    public void setDevisesToUpdate(List<Devise> devisesToUpdate) {  
    	System.out.println(" starting setDevisesToUpdate");  
        this.devisesToUpdate = devisesToUpdate;  
    }
    
    public Integer getRowUpdated() {  
    	System.out.println("starting getRowUpdated");  
        return rowUpdated;  
    }
    public void setRowUpdated(Integer rowUpdated) {  
    	System.out.println("starting setRowUpdated: " + rowUpdated);  
        this.rowUpdated = rowUpdated;  
    }
	
	
/********************************************************************************************************************/
	/* Gestion des listes d'items disponibles et d'items selectionnes pour gérer la collection de langues liés a un pays */
	public List<Devise> getListDevises(){
		if(listDevises == null){initDevisesList();}
		return listDevises;
	}
	public void setListDevises(List<Devise> aList){
		this.listDevises = aList;
	}
	
	public List<Pays> getListPays(){
		if(listPays == null){initPaysList();}
		return listPays;
	}
	public void setListPays(List<Pays> aList){
		this.listPays = aList;
	}
	
	public List<Pays> getListNewNoDevisePays(){
		return listNewNoDevisePays;
	}
	public void setListNewNoDevisePays(List<Pays> aList){
		this.listNewNoDevisePays = aList;
	}
	
	/*public List<Pays> getListAvailablePays(){
		if(listAvailablePays == null){initPaysList();}
		return listAvailablePays;
	}
	public void setListAvailablePays(List<Pays> aList){
		this.listAvailablePays = aList;
	}*/
	
	
/********************************************************************************************************************/
	 private void initPaysList() {
		 /*listPays = new ArrayList<Pays>();
		 listPays.add(new Pays(1L,"France",null,new Devise(1L,"Euro","€",null),null));
		 listPays.add(new Pays(2L,"Belgique",null,new Devise(1L,"Euro","€",null),null));
		 listPays.add(new Pays(3L,"Allemagne",null,new Devise(1L,"Euro","€",null),null));
		 listPays.add(new Pays(4L,"Italie",null,new Devise(1L,"Euro","€",null),null));
		 listPays.add(new Pays(5L,"Angleterre",null,new Devise(2L,"Livre","£",null),null));
		 listPays.add(new Pays(6L,"Etats-Unis",null,new Devise(3L,"Dollar","$",null),null));*/
		 listPays = null;
		 listPays = getPaysManager().findAllNotlinkedCountries_WithoutDevise();
		 
		 listNewNoDevisePays = new ArrayList<Pays>();
		 
		 /*listEntitePays = getPaysManager().findAllNotlinkedCountries_WithoutDevise();
		 listAvailablePays = new ArrayList<Pays>();
		 listAvailablePays.addAll(listEntitePays);*/
	 }
	 
	 private void initDevisesList() {
		 /*listDevises = new ArrayList<Devises>();
		 listDevises(new Devise(1L,"Euro","€",null));
		 listDevises(new Devise(2L,"Livre","£",null));
		 listDevises(new Devise(3L,"Dollar","$",null));
		 */
		 listDevises = null;
		 listDevises = getDeviseManager().findByName("%%", 0, 100);
	 }	
	 
/********************************************************************************************************************/
	/* Mise a jour */
	public String update(){
		
		joinTransaction();
		
		for(Devise d : listDevises){
			Devise lEntity = getDeviseManager().update(d);
			
			if(lEntity == null)
				return null;
		}
		
		for(Pays p : listNewNoDevisePays){
			Pays lEntity = getPaysManager().save(p);
			
			if(lEntity == null)
				return null;
		}
		
		// Le flush est indispensable pour communiquer les changements vers la base de données. Un 
		//	objet merge avec un changement ne sera pas validé sauf a fin de conversation ou flush JPA
		// REMARQUE : le declenchement du update est en realité facultatif !
		flushIfNecessary();
		
		// Avant de renvoyer vers la JSF, on remet à zero les listes de puis la BD
		//initDevisesList();
		//initPaysList();
		
		System.out.println(" ---------- DEBUG :AdminDeviseAction: "+"update");
		return "update";
	}
	
	public String cancel(){
		// Doit verifier si l'entite est attaché au cache JPA, il suffit alors de rafraichir l'instance de l'action a partir du cache JPA, sinon il faut jouer le
		//	initInstance qui lancera un find pour rattacher l'instance de l'action
		EntityManager lEntityManager = getEntityManager();
		
		// vide reellement tout le cache, permet de s'assurer qu'une precedente operation ne traine pas sur l'entité
		lEntityManager.clear();
		
		/* Pb avec le refresh d'element, l'entitymanager n'est pas vidé des operations, et ainsi le validate declenche la mise à jour malgré le reset des datas
		 * 
		 * if (lEntityManager != null){
			for(Devise d : listDevises){
				if(lEntityManager.contains(d)) {
					// l'entité devise est attachée, elle est rafraichit depuis la base
					lEntityManager.refresh(d);
					System.out.println("   ==>   Devise refresh : "+d);
					// ses pays égalements
					for(Pays p : d.getListPays()){
						System.out.println("   ==>   pays n°X : "+p.getNom()+", "+p.getDevise());
						if(lEntityManager.contains(p))
							lEntityManager.refresh(p);
					}
				}
			}
		}*/
		
		initDevisesList();
		initPaysList();
		
		return "cancel";
	}
	 
/********************************************************************************************************************/
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
	private IDeviseManager getDeviseManager() {
		return super.evaluateValueExpression("#{deviseManager}", IDeviseManager.class); 		
	}
	
	
	private void joinTransaction() {
		EntityManager lEntityManager = getEntityManager();
		if (lEntityManager != null && lEntityManager.isOpen()) {
			try {
				Transaction.instance().enlist(lEntityManager);
			} catch (SystemException e) {
				throw new RuntimeException("could not join transaction", e);
			}
		}
	}
	
	private EntityManager getEntityManager() {
		if (entityManager == null) {
			entityManager = (EntityManager) getComponentInstance(getPersistenceContextName());
		}
		return entityManager;
	}
	
	private String getPersistenceContextName() {
		return "entityManager";
	}
	
	private void flushIfNecessary() {
		EntityManager lEntityManager = getEntityManager();
		if (lEntityManager != null) {
			lEntityManager.flush();
		}
	}
	
}

