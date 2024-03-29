package com.visuc.referentiel.typeCommerce.web.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

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

import com.visuc.referentiel.typeCommerce.entite.TypeCommerce;
import com.visuc.referentiel.typeCommerce.service.ITypeCommerceManager;

@Name("editerTypeCommerceAction")
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
@SuppressWarnings("serial")
public class EditerTypeCommerceAction extends Controller {

	private TypeCommerce instance;
	private Object typeCommerceId;
	
	
	private Boolean idDefined;
	private ValueExpression newInstance;
	
	
	private transient boolean dirty;
	private transient EntityManager entityManager;
	
	
	/******* Declaration des messages specifiques a l'entite typeCommerce et ses actions disponibles ***********/
	private String deletedMessage = "Suppression effectu�e";
	private String createdMessage = "Cr�ation effectu�e";
	private String alreadyCreatedMessage = "Cr�ation non effectu�e : l'enregistrement existe d�j�";
	private String updatedMessage = "Mise � jour effectu�e";
	private String unmanagedMessage = "Mise � jour non effectu�e : l'enregistrement n'existe pas";
	
	
	
/*********************************************************************************************************************/	
	/********** Methode en frontal du Backing Bean **********/
	public String find() {
		// On recherche l'entit� associ�e
		initInstance();
		
		System.out.println(" ---------- DEBUG :EditerTypeCommerceAction: "+"find "+instance.toString());
		return "find";
	}
	
	public String edit() {
		// On recherche l'entit� associ�e
		initInstance();
			
		System.out.println(" ---------- DEBUG :EditerTypeCommerceAction: "+"edit "+instance.toString());
		return "edit";
	}
	// refresh des champs de l'instance de l'action
	public void refresh(){
		evaluateValueExpression("#{editerTypeCommerceAction.instance.nom}");
	}
	
	
	
	//-------------------------------------------------------
	/* Persistance */
	public String persist(){
		// On sauvegarde l'entit�
		TypeCommerce lEntity = getTypeCommerceManager().save(getInstance());
		
		//relie la nouvelle entite du cache JPA a la conversation seam et son bean action
		setInstance(lEntity);
		
		//vide le cache de la transaction (l'insert ayant ete joue)
		flushIfNecessary();
		
		// stocke le nouvel ID de l'entite depuis le bean action, celui-ci etant vide lors de la demande de creation
		assignId(getIdFromObject(lEntity));
		
		// Creation des messages dans le cache Seam, envoi de l'event de refresh de la liste existante dans la recherche
		createdMessage();
		raiseAfterTransactionSuccessEvent();
		
		System.out.println(" ---------- DEBUG :EditerTypeCommerceAction: "+"persist");
		return "persist";
	}
	
	/* Mise a jour */
	public String update(){
		if (getIdFromObject(getInstance()) == null) {
			unmanagedMessage();
			return null;
		}
		
		joinTransaction();
		
		TypeCommerce lEntity = getTypeCommerceManager().update(getInstance()); 
		
		// Si l'entit� retourn� est null, c'est qu'il y a eu une erreur.
		// on retourne une outcome null (reposition sur la page d'origine)
		// la gestion du message d'erreur est � la charge de doUpdate()
		if (lEntity == null) {
			return null;
		}
		
		// relie l'entite modifiee a partir du cache JPA dans la conversation seam et son bean action
		setInstance(lEntity);
		// flush de la transaction JPA
		flushIfNecessary();

		// envoi de l'evt pour declenche le rafraichissement des datas
		updatedMessage();
		raiseAfterTransactionSuccessEvent();
		
		System.out.println(" ---------- DEBUG :EditerTypeCommerceAction: "+"update");
		return "update";
	}
	
	/* Suppression */
	public String remove(){
		if (getIdFromObject(getInstance()) == null) {
			unmanagedMessage();
			return null;
		}
		getTypeCommerceManager().deleteOne(getInstance());
		if (getInstance() == null) {
			// Retour 'null' de delete = erreur fonctionnelle
			return null;
		}
		
		flushIfNecessary();
		
		setInstance(null);
		
		// Creation du message resultat de l'action demande et envoi de l'evt pour declenche le rafraichissement des datas
		deletedMessage();
		raiseAfterTransactionSuccessEvent();
		
		System.out.println(" ---------- DEBUG :EditerTypeCommerceAction: "+"delete");
		return "remove";
	}
	
	public String cancel(){
		if (getDefinedInstance() == null) {
			return "cancel";
		}
		if (!isManaged()) {
			return "cancel";
		}
		
		// Doit verifier si l'entite est attach� au cache JPA, il suffit alors de rafraichir l'instance de l'action a partir du cache JPA, sinon il faut jouer le
		//	initInstance qui lancera un find pour rattacher l'instance de l'action
		EntityManager lEntityManager = getEntityManager();
		if (lEntityManager != null && lEntityManager.contains(getInstance())) {
			// l'entit� est attach�e
			lEntityManager.refresh(getInstance());
		} else {
			// l'entit� est d�tach�e
			initInstance();
		}
		
		System.out.println(" ---------- DEBUG :EditerTypeCommerceAction: "+"cancel");
		return "cancel";
	}
	

/********************************************************************************************************************/
	/***************** Methodes de requetage ****************/
/*	
	// Execution de la requete de creation
	private TypeCommerce querySaveTypeCommerce() {
		System.out.println(" ---------- DEBUG :EditerTypeCommerceAction: "+"querySaveTypeCommerce");
		return getTypeCommerceManager().save(getInstance());
	}
	
	// Execution de la requete de de mise a jour
	private TypeCommerce queryUpdateTypeCommerce() {
		System.out.println(" ---------- DEBUG :EditerTypeCommerceAction: "+"queryUpdateTypeCommerce");
		return getTypeCommerceManager().update(getInstance());
	}
	
	// Execution de la requete de suppression
	private void queryDeleteTypeCommerce() {
		System.out.println(" ---------- DEBUG :EditerTypeCommerceAction: "+"queryDeleteTypeCommerce : "+getInstance().getId());
		getTypeCommerceManager().deleteOne(getInstance());
	}
*/

/********************************************************************************************************************/
	// Retourne vrai si le typeCommerce existe en base de donn�es.
	public boolean isManaged() {
		return getInstance() != null && isIdDefined();
	}
	
	private Object getIdFromObject(TypeCommerce aEntity) {
		if (aEntity == null) {
			NoSuchMethodError lError = new NoSuchMethodError("M�thode getId() inexistante pour l'entit� null");
			throw lError;
		}
		
		return (Object)aEntity.getId();
	}
	
	public boolean isIdDefined() {
		if (getTypeCommerceId() == null) {
			return false;
		}
		if (idDefined == null) {
			idDefined = isNotNullKey(getTypeCommerceId());
		}
		return idDefined;
	}
	
	// Retourne null si l'action ne g�er actuellement aucune entit� (c'est � dire si isIdDefined() return false)
	public TypeCommerce getDefinedInstance() {
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
        // on it�re sur chacune des propri�t�s 
        // d�s que une non null on renvoie true => mode update
        for (Object lEntry : lMap.entrySet()){
            Map.Entry lMapEntry = (Map.Entry)lEntry;
            String lKey = (String)lMapEntry.getKey();
            Object lValue = lMapEntry.getValue();
            // la clef class fait partie automatiquement de la Map en plus des attributs
            // elle ne nous int�resse pas...
            if (!"class".equalsIgnoreCase(lKey) && lValue != null && !"".equals(lValue)) {
            	return true;
            }
        }
      return false;
    }

/********************************************************************************************************************/
	/****************** Getter & Setter *********************/
    public Long getTypeCommerceId() {
        return (Long)typeCommerceId;
    }
    public void setTypeCommerceId(Long aId) {
    	this.typeCommerceId = aId;
    }
    
    // Cette m�thode retourne l'instance manag�e, m�thode appel�e dans la page .xhtml sous 
    //  la forme de l'EL : #{entity.instance.attribut} pour acc�der au r�sultat � l'issue d'une recherche
    public TypeCommerce getInstance() {
    	joinTransaction();
		if (instance == null) {
			initInstance();
		}
		return instance;
	}
    // Valorise l'instance de l'entit� g�r�e par cette action
    //	Met � jour setId(Object), en indiquant l'identifiant de l'entit� aInstance - l'identifiant �tant calcul� par la m�thode getIdFromObject(Object)
	public void setInstance(TypeCommerce aInstance) {
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

	/** Met � jour l'identifiant de l'entit� g�r�e par cette action. Mais ne modifie pas l'entite(c'est en fait son instance) de cette action */
	void assignId(Object aId) {
		setDirty(typeCommerceId, aId);
		typeCommerceId = aId;
		idDefined = null;
	}
	
	/** Set the dirty flag if the value has changed. Call whenever a subclass attribute is updated */
	protected <U> boolean setDirty(U aOldValue, U aNewValue) {
		boolean lAttributeDirty = aOldValue != aNewValue && (aOldValue == null || !aOldValue.equals(aNewValue));
		dirty = dirty || lAttributeDirty;
		return lAttributeDirty;
	}
	
	
	private TypeCommerce createInstance() {
		if (newInstance != null) {
			return (TypeCommerce) newInstance.getValue(FacesContext.getCurrentInstance().getELContext());
		} else {
			try {
				return new TypeCommerce();
			} catch (Exception err) {
				throw new RuntimeException(err);
			}
		} 
	}
	
	protected TypeCommerce handleNotFound() throws EntityNotFoundException {
		throw new EntityNotFoundException(getTypeCommerceId(), this.instance.getClass());
	}
	
	/**
	 * Charge l'instance si l'id est d�fini, sinon cr�e une nouvelle instance.
	 * <br />
	 * M�thode utilitaire appel�e par {@link #getInstance()} pour charger
	 * l'instance � partir de la base de donn�es (via
	 * {@link #doFind())si l'id est d�fini {@link #isIdDefined()} vaut true. Si
	 * l'id n'est pas d�fini, une nouvelle instance de l'entit� est cr�� via
	 * l'appel � {@link #createInstance()}.
	 * 
	 * @see #doFind()
	 * @see #createInstance()
	 */
	protected void initInstance() {

		if (isIdDefined()) {
			// we cache the instance so that it does not "disappear"
			// after remove() is called on the instance
			// is this really a Good Idea??
			TypeCommerce lResult = getTypeCommerceManager().findById((Long)getTypeCommerceId());
			if (lResult == null) {
				lResult = handleNotFound();
			}
			setInstance(lResult);
		} else {
			setInstance(createInstance());
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
	private ITypeCommerceManager getTypeCommerceManager() {
		return super.evaluateValueExpression("#{typeCommerceManager}", ITypeCommerceManager.class); 		
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
	/****************** Gestion des evenements declenches par manipulation de l'entite TypeCommerce ******/
	protected void raiseAfterTransactionSuccessEvent() {
		raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess");
		String lSimpleEntityName = getSimpleEntityName();
		if (lSimpleEntityName != null) {
			System.out.println(" ---------- DEBUG :EditerTypeCommerceAction: "+"raiseAfterTransactionSuccessEvent : "+"org.jboss.seam.afterTransactionSuccess." + lSimpleEntityName);
			raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess." + lSimpleEntityName);
		}
	}
	
	// -----------Getter for the entityName
	/**Retourne le nom de la classe de l'entit� g�r�e par cette Action sans
	 * prendre en compte le nom de paquetage (i.e. Employe pour l'entit�
	 * <code>com.natixis.aws.employe.entite.Employe</code>).
	 */
	protected String getSimpleEntityName() {
		String lName = TypeCommerce.class.getName();
		return lName.lastIndexOf(".") > 0 && lName.lastIndexOf(".") < lName.length() ? 
				lName.substring(lName.lastIndexOf(".") + 1, lName.length()) : lName;
	}
	

/********************************************************************************************************************/
/****************** Gestion de l'alimentation des messages Seam *********************/
	
	// ----- Prefix a tout message provenant de l'entite TypeCommerce
	private String getMessageKeyPrefix() {
		String lClassName = this.getClass().getName();
		return lClassName.substring(lClassName.lastIndexOf('.') + 1) + '_';
	}
	
	// ----- Details des cas de messages : create, update, delete
		/** Cette m�thode permet d'indiquer que l'instance a bien �t� cr��e. */
		private void createdMessage() {
			getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.INFO, 
					getCreatedMessageKey(), getCreatedMessage());
		}
		
		private String getCreatedMessageKey() {
			return getMessageKeyPrefix() + "created";
		}
		
		/** Cette m�thode permet d'indiquer que l'instance a bien �t� modifi�e. */
		private void updatedMessage() {
			getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.INFO, 
					getUpdatedMessageKey(), getUpdatedMessage());
		}
		private String getUpdatedMessageKey() {
			return getMessageKeyPrefix() + "updated";
		}
		
		/** Cette m�thode permet d'indiquer que l'instance a bien �t� supprim�e. */
		private void deletedMessage() {
			getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.INFO, 
					getDeletedMessageKey(), getDeletedMessage());
		}
		private String getDeletedMessageKey() {
			return getMessageKeyPrefix() + "deleted";
		}
		
		/** Cette m�thode permet d'indiquer que l'instance a d�j� �t� cr��e */
		@SuppressWarnings("unused")
		private void alreadyCreatedMessage() {
			getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.WARN, 
					getAlreadyCreatedMessageKey(), getAlreadyCreatedMessage());
		}
		private String getAlreadyCreatedMessageKey() {
			return getMessageKeyPrefix() + "alreadyCreated";
		}

		/** Cette m�thode permet d'indiquer que l'instance n'est pas manag� en Base de donn�e */
		protected void unmanagedMessage() {
			getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.WARN, 
					getUnmanagedMessageKey(), getUnmanagedMessage());
		}
		protected String getUnmanagedMessageKey() {
			return getMessageKeyPrefix() + "unmanaged";
		}
	
	// -----------Getter & Setter des messages de TypeCommerce
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