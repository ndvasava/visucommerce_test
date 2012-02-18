package com.visuc.referentiel.langue.web.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.transaction.SystemException;

import org.apache.commons.beanutils.BeanUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.framework.Controller;
import org.jboss.seam.framework.EntityNotFoundException;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.transaction.Transaction;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.visuc.common.entity.Image;
import com.visuc.referentiel.langue.entite.Langue;
import com.visuc.referentiel.langue.service.ILangueManager;
import com.visuc.referentiel.pays.entite.Pays;
import com.visuc.referentiel.pays.service.IPaysManager;

@Name("editerLangueAction")
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
@SuppressWarnings("serial")
public class EditerLangueAction extends Controller {

	private Langue instance;
	private Object langueId;
	
	
	private Boolean idDefined;
	private ValueExpression newInstance;
	
	
	private transient boolean dirty;
	private transient EntityManager entityManager;
	
	
	private transient List<Pays> listAvailablePays;
	private transient List<Pays> listSelectedPays;
	
	
	/******* Declaration des messages specifiques a l'entite langue et ses actions disponibles ***********/
	private String deletedMessage = "Suppression effectuée";
	private String createdMessage = "Création effectuée";
	private String alreadyCreatedMessage = "Création non effectuée : l'enregistrement existe déjà";
	private String updatedMessage = "Mise à jour effectuée";
	private String unmanagedMessage = "Mise à jour non effectuée : l'enregistrement n'existe pas";
	
	
	
/*********************************************************************************************************************/	
	/********** Methode en frontal du Backing Bean **********/
	public String find() {
		// On recherche l'entité associée
		initInstance();
		
		System.out.println(" ---------- DEBUG :EditerLangueAction: "+"find "+instance.toString());
		return "find";
	}
	
	public String edit() {
		// On recherche l'entité associée
		initInstance();
			
		System.out.println(" ---------- DEBUG :EditerLangueAction: "+"edit "+instance.toString());
		return "edit";
	}
	
	// refresh des champs de l'instance de l'action
	public void refresh(){
		evaluateValueExpression("#{editerLangueAction.instance.nom}");
		evaluateValueExpression("#{editerLangueAction.instance.codeIso}");
	}
	
	// clear the upload image area
	public String clearUploadData() {
		// pour gérer la suppression du drapeau, on set les data à null, lors de l'update d'une langue, ce cas est detecté et un remove sera lancé sur le drapeau
		// Ensuite au retour on effectue le setDrapeau(null). 
        getInstance().getDrapeau().setContent(null);
        return null;
    }
	
	// listener pour upload du drapeau
	public void listener(UploadEvent event) throws Exception{
    	System.out.println(" ---------- DEBUG :EditerLangueAction: "+"listener ");
    	
    	UploadItem item = event.getUploadItem();
        Image img = null;
        // Si un drapeau etait deja lie a la langue, on update ses attributs
        if((getInstance().getDrapeau()!=null)&&(getInstance().getDrapeau().getId() != null)){
        	getInstance().getDrapeau().setLength(((Integer)item.getData().length).longValue());
        	getInstance().getDrapeau().setName(item.getFileName());
        	getInstance().getDrapeau().setContent(item.getData());
        }else {
        	img = new Image();
        	img.setLength(((Integer)item.getData().length).longValue());
        	img.setName(item.getFileName());
            img.setContent(item.getData());
            getInstance().setDrapeau(img);
            
            System.out.println("                                                     file uploaded '"+img.getName()+"' : '"+img.getLength());
        }
    }
	
	/**
     * <p>Cette methode permet gerer les associations et de rafraichir
     * l'instance courante une fois que les associations ont ete mises a jour.
     * Cette methode <code>wire</code> est declaree dans le fichier langue.page.xml,
     * dans la section consacree a la page /views/langue/edit_langue.xhtml
     * exemple:
     * <pre>
     * &lt;page view-id="/views/langue/edit_langue.xhtml"&gt;
     *	&lt;!-- recuperation des donnees en sortie d'une navigation interfonctionnalite. --&gt;
     *	&lt;action execute="#{editerLangueAction.wire}"/&gt;
     *	</pre>
     */
 	public void wire() {
 		System.out.println(" ---------- DEBUG :EditerLangueAction: "+"WIRE");
 		
 		// gestion lors de la création des saisies existantes avant la selection de l'objet lie, ici saisie du nom
 		//Events.instance().raiseEvent("event.test");
 		
		/*EditerPaysAction lEditerPaysAction = getEditerPaysAction();
		if (lEditerPaysAction != null) {
			Pays lPays = lEditerPaysAction.getDefinedInstance();
			// Au second appel a selectionner, le pays sera correctement rafraichit
			//lEditerPaysAction.clearInstance();
	        if (lPays!=null ) {
	        	// tente avant le sePays de le rafraichir pour resoudre le probleme du lien en lazy
	        	//getEntityManager().refresh(lPays);
	        	getInstance().setPays(lPays);
	        	System.out.println("                                            "+getInstance());
	        }
	    }
		
		System.out.println("                                             "+this.instance);*/
    }
	
	//-------------------------------------------------------
	/* Persistance */
	public String persist(){
		// On sauvegarde l'entité
		Langue lEntity = getLangueManager().save(getInstance());
		
		//relie la nouvelle entite du cache JPA a la conversation seam et son bean action
		setInstance(lEntity);
		
		//vide le cache de la transaction (l'insert ayant ete joue)
		flushIfNecessary();
		
		// stocke le nouvel ID de l'entite depuis le bean action, celui-ci etant vide lors de la demande de creation
		assignId(getIdFromObject(lEntity));
		
		// Creation des messages dans le cache Seam, envoi de l'event de refresh de la liste existante dans la recherche
		createdMessage();
		raiseAfterTransactionSuccessEvent();
		
		System.out.println(" ---------- DEBUG :EditerLangueAction: "+"persist");
		return "persist";
	}
	
	/* Mise a jour */
	public String update(){
		if (getIdFromObject(getInstance()) == null) {
			unmanagedMessage();
			return null;
		}
		
		joinTransaction();
		
		//lancement de la mise a jour de la liste de pays de l'instance avant d'appeler l'update JPA
		updateListSelectedPays();
		
		Langue lEntity = getLangueManager().update(getInstance()); 
		
		// Si l'entité retourné est null, c'est qu'il y a eu une erreur.
		// on retourne une outcome null (reposition sur la page d'origine)
		// la gestion du message d'erreur est à la charge de doUpdate()
		if (lEntity == null) {
			return null;
		}
		// Rafraichit la liste des pays devant etre supprimés
		lEntity.setSetPays(new HashSet<Pays>(listSelectedPays));
		
		// relie l'entite modifiee a partir du cache JPA dans la conversation seam et son bean action
		setInstance(lEntity);
		// flush de la transaction JPA
		flushIfNecessary();

		// envoi de l'evt pour declenche le rafraichissement des datas
		updatedMessage();
		raiseAfterTransactionSuccessEvent();
		
		System.out.println(" ---------- DEBUG :EditerLangueAction: "+"update");
		return "update";
	}
	
	/* Suppression */
	public String remove(){
		if (getIdFromObject(getInstance()) == null) {
			unmanagedMessage();
			return null;
		}
		getLangueManager().deleteOne(getInstance());
		if (getInstance() == null) {
			// Retour 'null' de delete = erreur fonctionnelle
			return null;
		}
		
		flushIfNecessary();
		
		setInstance(null);
		
		// Creation du message resultat de l'action demande et envoi de l'evt pour declenche le rafraichissement des datas
		deletedMessage();
		raiseAfterTransactionSuccessEvent();
		
		System.out.println(" ---------- DEBUG :EditerLangueAction: "+"delete");
		return "remove";
	}
	
	public String cancel(){
		if (getDefinedInstance() == null) {
			return "cancel";
		}
		if (!isManaged()) {
			return "cancel";
		}
		
		// Doit verifier si l'entite est attaché au cache JPA, il suffit alors de rafraichir l'instance de l'action a partir du cache JPA, sinon il faut jouer le
		//	initInstance qui lancera un find pour rattacher l'instance de l'action
		EntityManager lEntityManager = getEntityManager();
		if (lEntityManager != null && lEntityManager.contains(getInstance())) {
			// l'entité est attachée
			lEntityManager.refresh(getInstance());
		} else {
			// l'entité est détachée
			initInstance();
		}
		
		System.out.println(" ---------- DEBUG :EditerLangueAction: "+"cancel");
		return "cancel";
	}
	

/********************************************************************************************************************/
	// Retourne vrai si le langue existe en base de données.
	public boolean isManaged() {
		return getInstance() != null && isIdDefined();
	}
	
	private Object getIdFromObject(Langue aEntity) {
		if (aEntity == null) {
			NoSuchMethodError lError = new NoSuchMethodError("Méthode getId() inexistante pour l'entité null");
			throw lError;
		}
		
		return (Object)aEntity.getId();
	}
	
	public boolean isIdDefined() {
		if (getLangueId() == null) {
			return false;
		}
		if (idDefined == null) {
			idDefined = isNotNullKey(getLangueId());
		}
		return idDefined;
	}
	
	// Retourne null si l'action ne gèer actuellement aucune entité (c'est à dire si isIdDefined() return false)
	public Langue getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isNotNullKey(Object aKey){
    	if (aKey == null) {
            return false;
        }
        if (org.springframework.beans.BeanUtils.isSimpleProperty(aKey.getClass())) {
            return !"".equals(aKey);
        }
        Map lMap = null;
        try {
            lMap = BeanUtils.describe(aKey);
        } catch (IllegalAccessException err) {
            err.printStackTrace();
        } catch (InvocationTargetException err) {
            err.printStackTrace();
        } catch (NoSuchMethodException err) {
            err.printStackTrace();
        }
        // on itère sur chacune des propriétés 
        // dès que une non null on renvoie true => mode update
        for (Object lEntry : lMap.entrySet()){
            Map.Entry lMapEntry = (Map.Entry)lEntry;
            String lKey = (String)lMapEntry.getKey();
            Object lValue = lMapEntry.getValue();
            // la clef class fait partie automatiquement de la Map en plus des attributs
            // elle ne nous intéresse pas...
            if (!"class".equalsIgnoreCase(lKey) && lValue != null && !"".equals(lValue)) {
            	return true;
            }
        }
      return false;
    }

/********************************************************************************************************************/
	/****************** Getter & Setter *********************/
    public Long getLangueId() {
        return (Long)langueId;
    }
    public void setLangueId(Long aId) {
    	this.langueId = aId;
    }
    
    // Cette méthode retourne l'instance managée, méthode appelée dans la page .xhtml sous 
    //  la forme de l'EL : #{entity.instance.attribut} pour accéder au résultat à l'issue d'une recherche
    public Langue getInstance() {
    	joinTransaction();
		if (instance == null) {
			initInstance();
		}
		return instance;
	}
    // Valorise l'instance de l'entité gérée par cette action
    //	Met à jour setId(Object), en indiquant l'identifiant de l'entité aInstance - l'identifiant étant calculé par la méthode getIdFromObject(Object)
	public void setInstance(Langue aInstance) {
		setDirty(this.instance, aInstance);
		if (aInstance != null) {
			assignId(getIdFromObject(aInstance));
		}
		this.instance = aInstance;
	}
	
	public ValueExpression getNewInstance() {
		return newInstance;
	}
	public void setNewInstance(ValueExpression aNewInstance) {
		this.newInstance = aNewInstance;
	}
	

/*********************************************************************************************************************/
/****************   Gestion de l'etat instance-entite-Id du bean action de la conversation   *****************

	/** Met à jour l'identifiant de l'entité gérée par cette action. Mais ne modifie pas l'entite(c'est en fait son instance) de cette action */
	void assignId(Object aId) {
		setDirty(langueId, aId);
		langueId = aId;
		idDefined = null;
	}
	
	/** Set the dirty flag if the value has changed. Call whenever a subclass attribute is updated */
	protected <U> boolean setDirty(U aOldValue, U aNewValue) {
		boolean lAttributeDirty = aOldValue != aNewValue && (aOldValue == null || !aOldValue.equals(aNewValue));
		dirty = dirty || lAttributeDirty;
		return lAttributeDirty;
	}
	
	
	private Langue createInstance() {
		if (newInstance != null) {
			return (Langue) newInstance.getValue(FacesContext.getCurrentInstance().getELContext());
		} else {
			try {
				return new Langue();
			} catch (Exception err) {
				throw new RuntimeException(err);
			}
		} 
	}
	
	protected Langue handleNotFound() throws EntityNotFoundException {
		throw new EntityNotFoundException(getLangueId(), this.instance.getClass());
	}
	
	/**
	 * Charge l'instance si l'id est défini, sinon crée une nouvelle instance.
	 * <br />
	 * Méthode utilitaire appelée par {@link #getInstance()} pour charger
	 * l'instance à partir de la base de données (via
	 * {@link #doFind())si l'id est défini {@link #isIdDefined()} vaut true. Si
	 * l'id n'est pas défini, une nouvelle instance de l'entité est créé via
	 * l'appel à {@link #createInstance()}.
	 * 
	 * @see #doFind()
	 * @see #createInstance()
	 */
	protected void initInstance() {

		if (isIdDefined()) {
			// we cache the instance so that it does not "disappear"
			// after remove() is called on the instance
			// is this really a Good Idea??
			Langue lResult = getLangueManager().findById((Long)getLangueId());
			if (lResult == null) {
				lResult = handleNotFound();
			}
			setInstance(lResult);
			
			// met a jour la liste Transient de SelectedItem en mirroir de la liste des pays d'une devise
			setListAvailablePays(getPaysManager().findAllNotlinkedCountries(getInstance()));
			setListSelectedPays(new ArrayList<Pays>(getInstance().getSetPays()));
		} else {
			setInstance(createInstance());
		}
	}

/********************************************************************************************************************/
	/* Gestion des listes d'items disponibles et d'items selectionnes pour gérer la collection de pays liés a une langue */
	public List<Pays> getListAvailablePays(){
		return listAvailablePays;
	}
	public void setListAvailablePays(List<Pays> aList){
		this.listAvailablePays = aList;
	}
	
	public List<Pays> getListSelectedPays() {
		return listSelectedPays;
	}
	public void setListSelectedPays(List<Pays> aListSelectedPays) {
		this.listSelectedPays = new ArrayList<Pays>();
		for(Pays p : aListSelectedPays){
			listSelectedPays.add(p);
		}
	}

	// remove devise from pays if it is not already existing in instance.getListPays()
	public void updateListSelectedPays(){
		System.out.println(" - - - - - - - - - - - - - - - - - -  "+"updateListSelectedPays");
		int i=0;
		// Parcours la liste des pays lies a l'instance Devise courante
		// 		-> Si listSelectedPays ne contient plus un pays de l'instance, on supprime son lien sur la devise.
		for(Pays p : this.instance.getSetPays()){
			if(!this.listSelectedPays.contains(p)){
				p.getSetLangues().remove(instance);
				System.out.println(i+"/  "+p.getNom()+" isRemove");
			}
		}
		i=0;
		// Parcours la liste des pays selectionnes, pour ajouter le pays a la liste s'il n'existe pas, et ajouter la langue a la liste de langues liées au pays
		// 		-> Sinon Si le pays n'a pas devise mais existe dans la liste des pays selectionnes a droite, il faut rajouter son lien sur la devise courante
		//		-> le parcours des instances de la liste et leur modification en direct implique que la liste SelectedPays est mise à jour en meme temps que instance.getListPays 
		for(Pays p : this.listSelectedPays) {
			Set<Langue> s = null;
			if(p.getSetLangues() == null) {
				s = new HashSet<Langue>();
			}else{
				s = p.getSetLangues();
			}
			s.add(instance);
			this.instance.getSetPays().add(p);
			System.out.println(i+"/  "+p.getNom()+" isAdd");
		}
	}
	

/********************************************************************************************************************/
	// -------------------------------------------------------------
	// Gestionnaire de Services
    // -------------------------------------------------------------
	
	/** 
	 * Service IEquipeManager gere par Spring 
	 * enregistre dans monappli-service.xml
	 */
	private ILangueManager getLangueManager() {
		return super.evaluateValueExpression("#{langueManager}", ILangueManager.class); 		
	}
	private IPaysManager getPaysManager() {
		return super.evaluateValueExpression("#{paysManager}", IPaysManager.class); 		
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

	
/********************************************************************************************************************/
	/****************** Gestion des evenements declenches par manipulation de l'entite Langue ******/
	protected void raiseAfterTransactionSuccessEvent() {
		raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess");
		String lSimpleEntityName = getSimpleEntityName();
		if (lSimpleEntityName != null) {
			System.out.println(" ---------- DEBUG :EditerLangueAction: "+"raiseAfterTransactionSuccessEvent : "+"org.jboss.seam.afterTransactionSuccess." + lSimpleEntityName);
			raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess." + lSimpleEntityName);
		}
	}
	
	// -----------Getter for the entityName
	/**Retourne le nom de la classe de l'entité gérée par cette Action sans
	 * prendre en compte le nom de paquetage (i.e. Employe pour l'entité
	 * <code>com.natixis.aws.employe.entite.Employe</code>).
	 */
	protected String getSimpleEntityName() {
		String lName = Langue.class.getName();
		return lName.lastIndexOf(".") > 0 && lName.lastIndexOf(".") < lName.length() ? 
				lName.substring(lName.lastIndexOf(".") + 1, lName.length()) : lName;
	}
	

/********************************************************************************************************************/
/****************** Gestion de l'alimentation des messages Seam *********************/
	
	// ----- Prefix a tout message provenant de l'entite Langue
	private String getMessageKeyPrefix() {
		String lClassName = this.getClass().getName();
		return lClassName.substring(lClassName.lastIndexOf('.') + 1) + '_';
	}
	
	// ----- Details des cas de messages : create, update, delete
		/** Cette méthode permet d'indiquer que l'instance a bien été créée. */
		private void createdMessage() {
			getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.INFO, 
					getCreatedMessageKey(), getCreatedMessage());
		}
		
		private String getCreatedMessageKey() {
			return getMessageKeyPrefix() + "created";
		}
		
		/** Cette méthode permet d'indiquer que l'instance a bien été modifiée. */
		private void updatedMessage() {
			getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.INFO, 
					getUpdatedMessageKey(), getUpdatedMessage());
		}
		private String getUpdatedMessageKey() {
			return getMessageKeyPrefix() + "updated";
		}
		
		/** Cette méthode permet d'indiquer que l'instance a bien été supprimée. */
		private void deletedMessage() {
			getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.INFO, 
					getDeletedMessageKey(), getDeletedMessage());
		}
		private String getDeletedMessageKey() {
			return getMessageKeyPrefix() + "deleted";
		}
		
		/** Cette méthode permet d'indiquer que l'instance a déjà été créée */
		@SuppressWarnings("unused")
		private void alreadyCreatedMessage() {
			getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.WARN, 
					getAlreadyCreatedMessageKey(), getAlreadyCreatedMessage());
		}
		private String getAlreadyCreatedMessageKey() {
			return getMessageKeyPrefix() + "alreadyCreated";
		}

		/** Cette méthode permet d'indiquer que l'instance n'est pas managé en Base de donnée */
		protected void unmanagedMessage() {
			getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.WARN, 
					getUnmanagedMessageKey(), getUnmanagedMessage());
		}
		protected String getUnmanagedMessageKey() {
			return getMessageKeyPrefix() + "unmanaged";
		}
	
	// -----------Getter & Setter des messages de Langue
	public String getDeletedMessage() {
		return deletedMessage;
	}
	public void setDeletedMessage(String deletedMessage) {
		this.deletedMessage = deletedMessage;
	}
	public String getCreatedMessage() {
		return createdMessage;
	}

	public void setCreatedMessage(String createdMessage) {
		this.createdMessage = createdMessage;
	}

	public String getAlreadyCreatedMessage() {
		return alreadyCreatedMessage;
	}

	public void setAlreadyCreatedMessage(String alreadyCreatedMessage) {
		this.alreadyCreatedMessage = alreadyCreatedMessage;
	}
	public String getUpdatedMessage() {
		return updatedMessage;
	}

	public void setUpdatedMessage(String updatedMessage) {
		this.updatedMessage = updatedMessage;
	}

	public String getUnmanagedMessage() {
		return unmanagedMessage;
	}

	public void setUnmanagedMessage(String unmanagedMessage) {
		this.unmanagedMessage = unmanagedMessage;
	}
}