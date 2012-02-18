package com.visuc.marchand.web.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.transaction.SystemException;

import org.apache.commons.beanutils.BeanUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
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
import com.visuc.marchand.commerce.common.Constants;
import com.visuc.marchand.commerce.entite.Commerce;
import com.visuc.marchand.commerce.service.ICommerceManager;
import com.visuc.marchand.vendeur.entite.Vendeur;
import com.visuc.marchand.vendeur.service.IVendeurManager;


@Name("editerSalespersonAction")
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
@SuppressWarnings("serial")
public class EditerSalespersonAction extends Controller {

	private Vendeur instance;
	private Object vendeurId;
	private Object companyAccId;
	
	private Boolean idDefined;
	private ValueExpression newInstance;
	
	
	private transient boolean dirty;
	private transient EntityManager entityManager;

	@In private Map<String, String> messages;
	@In("#{messages['default.messages.create.successful']}") private String createdMessage;
	
	private String undefinedCommerceIdErrorMessge = "The commerce Id number is not defined for this sales person.";
	private String unmanagedMessage = "Mise à jour non effectuée : l'enregistrement n'existe pas";
	private String updatedMessage = "Mise à jour effectuée";

/********************************************************************************************************************/
	/* Methode en frontal du Backing Bean */
	// clear the upload image area
	public String clearUploadData() {
		System.out.println("--> EditerSalespersonAction.clearUploadData() ");
		getInstance().getPhoto().setContent(null);
		System.out.println("<-- EditerSalespersonAction.clearUploadData() ");
        return null;
    }
	
	// listener pour upload de la photo du vendeur
	public void listener(UploadEvent event) throws Exception {
    	System.out.println("--> EditerSalespersonAction.listener() ");
    	
    	UploadItem item = event.getUploadItem();
        Image img = null;
        
        // Si une photo etait deja lie au vendeur, on update ses attributs
        if((getInstance().getPhoto()!=null)&&(getInstance().getPhoto().getId() != null)){
        	getInstance().getPhoto().setLength(((Integer)item.getData().length).longValue());
        	getInstance().getPhoto().setName(item.getFileName());
        	getInstance().getPhoto().setContent(item.getData());
        } else {
        	img = new Image();
        	img.setLength(((Integer)item.getData().length).longValue());
        	img.setName(item.getFileName());
            img.setContent(item.getData());
            getInstance().setPhoto(img);
            
            System.out.println("	file uploaded '"+img.getName()+"' : '"+img.getLength());
        }
        System.out.println("<-- EditerSalespersonAction.listener() ");
    }
	
	//-------------------------------------------------------
	/* Persistance */
	public String persist(){
		System.out.println("--> EditerSalespersonAction.persist()");
		
		if (instance.getCommerce() == null || instance.getCommerce().getId() == null) {
			getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.INFO, 
					"Error 1", getUndefinedCommerceIdErrorMessage());
		} else {
			System.out.println("	commerce id : " + instance.getCommerce().getId());
		}
		
		IVendeurManager manager = getVendeurManager();
		if (manager == null) {
			System.out.println("	Error : vendeur manager not found.");
			return null;
		} else {
			Vendeur persistedVendeur = getVendeurManager().save(instance);
			this.setInstance(persistedVendeur);
			System.out.println("	newly created sales-person Id : " + persistedVendeur.getId());
		}
		
		flushIfNecessary();
		raiseAfterTransactionSuccessEvent();
		createdMessage();
		
		System.out.println("<-- EditerSalespersonAction.persist()");
		return Constants.PERSIST_OK;
		
	}
	
	public void refreshCommerceInstance(){
		evaluateValueExpression("#{editerCommerceAction.instance}");
	}

	public String find() {
		System.out.println("--> EditerSalespersonAction.find()");
		// On recherche l'entité associée
		initInstance();
		System.out.println("instance : " + instance.toString());
		System.out.println("<-- EditerSalespersonAction.find()");
		return "find_OK";
	}
	

	public String update(){
		System.out.println("-->EditerSalespersonAction.update()");
		
		if (getIdFromObject(getInstance()) == null) {
			unmanagedMessage();
			return null;

		}
		
		joinTransaction();
		
		Vendeur lEntity = getSPManager().update(instance); 
		
		// Si l'entité retourné est null, c'est qu'il y a eu une erreur.
		// on retourne une outcome null (reposition sur la page d'origine)
		// la gestion du message d'erreur est à la charge de doUpdate()
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
		
		System.out.println("<-- EditerSalespersonAction.update()");

		
		
		return "update";
	}
	
	public String delete(){
		System.out.println("--> EditerSalespersonAction.remove()");
		return Constants.DELETE_OK;
	}
	
	public String cancel(){
		System.out.println("--> EditerSalespersonAction.cancel()");
		return "cancel";
	}
	
/********************************************************************************************************************/
	// Retourne vrai si le ville existe en base de données.
	public boolean isManaged() {
		return getInstance() != null && isIdDefined();
	}
	
	private Object getIdFromObject(Vendeur aEntity) {
		if (aEntity == null) {
			NoSuchMethodError lError = new NoSuchMethodError("Méthode getId() inexistante pour l'entité null");
			throw lError;
		}
		
		return (Object)aEntity.getId();
	}
	
	public boolean isIdDefined() {
		idDefined = isNotNullKey(getVendeurId());
		/*if (getVendeurId() == null) {
			return false;
		}
		if (idDefined == null) {
			idDefined = isNotNullKey(getVendeurId());
		}*/
		return idDefined;
	}
	
	// Retourne null si l'action ne gèer actuellement aucune entité (c'est à dire si isIdDefined() return false)
	public Vendeur getDefinedInstance() {
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
    public Long getVendeurId() {
        return (Long)vendeurId;
    }
    public void setVendeurId(Long aId) {
    	System.out.println("> vendeur id is " + aId);
    	this.vendeurId = aId;
    	
    	System.out.println("> " + getVendeurId());
    	if (getVendeurId() == null || getVendeurId() == 0) {
    		instance = null;
    		idDefined = false;
    	}
    }
    
    private String getUndefinedCommerceIdErrorMessage() {
		return undefinedCommerceIdErrorMessge;
	}
    
    // Cette méthode retourne l'instance managée, méthode appelée dans la page .xhtml sous 
    //  la forme de l'EL : #{entity.instance.attribut} pour accéder au résultat à l'issue d'une recherche
    public Vendeur getInstance() {
    	joinTransaction();
    	
		if (instance == null) {
			initInstance();
		}
		return instance;
	}
    // Valorise l'instance de l'entité gérée par cette action
    //	Met à jour setId(Object), en indiquant l'identifiant de l'entité aInstance - l'identifiant étant calculé par la méthode getIdFromObject(Object)
	public void setInstance(Vendeur aInstance) {
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
		setDirty(vendeurId, aId);
		vendeurId = aId;
		idDefined = null;
	}
	
	/** Set the dirty flag if the value has changed. Call whenever a subclass attribute is updated */
	protected <U> boolean setDirty(U aOldValue, U aNewValue) {
		boolean lAttributeDirty = aOldValue != aNewValue && (aOldValue == null || !aOldValue.equals(aNewValue));
		dirty = dirty || lAttributeDirty;
		return lAttributeDirty;
	}
	
	
	private Vendeur createInstance() {
		Vendeur v_tmp = null;
		if (newInstance != null) {
			v_tmp = (Vendeur) newInstance.getValue(FacesContext.getCurrentInstance().getELContext());
			return v_tmp;
		} else {
			try {
				System.out.println("	create sales person : for company -> " + getCompanyAccId());
				v_tmp = new Vendeur();
				Commerce company = (Commerce) getCommerceManager().findCommerceById(getCompanyAccId());
				v_tmp.setCommerce(company);
				return v_tmp;
			} catch (Exception err) {
				throw new RuntimeException(err);
			}
		} 
	}
	
	/** 
	 * The service ICommerceManager is managed by Spring in visu-service.xml 
	 */
	private ICommerceManager getCommerceManager() {
		return super.evaluateValueExpression("#{commerceManager}", ICommerceManager.class); 		
	}

	/** 
	 * The service ICommerceManager is managed by Spring in visu-service.xml 
	 */
	private IVendeurManager getSPManager() {
		return super.evaluateValueExpression("#{spManager}", IVendeurManager.class); 		
	}

	protected Vendeur handleNotFound() throws EntityNotFoundException {
		throw new EntityNotFoundException(getVendeurId(), this.instance.getClass());
	}
	
	/**
	 * Load the instance if it is defined, otherwise create a new instance.
	 * <br />
	 * A method is called by {@link #getInstance()} to load the instance
	 * from the database (via {@link #doFind()}).  
	 * If the salesperson id is defined {@link #isIdDefined()} will be true
	 * If the salesperson id is not defined, a new instance of the entity will be created 
	 * via {@link #createInstance()}.
	 * 
	 * @see #doFind()
	 * @see #createInstance()
	 */
	protected void initInstance() {
		System.out.println("--> EditerSalespersonAction.initInstance()");
		
		if (isIdDefined()) {
			// we cache the instance so that it does not "disappear"
			// after remove() is called on the instance
			// is this really a Good Idea??
			Vendeur lResult = getVendeurManager().findById((Long)getVendeurId());
			if (lResult == null) {
				lResult = handleNotFound();
			}
			setInstance(lResult);
			
		} else {
			setInstance(createInstance());
		}
		System.out.println("<-- EditerSalespersonAction.initInstance()");
	}

	/*public String initNewSpInstance() {
		if (isIdDefined()) {
			this.setVendeurId(null);
		}
		return "OK";
	}*/
/********************************************************************************************************************/
	// -------------------------------------------------------------
	// Gestionnaire de Services
    // -------------------------------------------------------------
	/** 
	 * Service IObjetManager gere par Spring enregistre dans spring-beans.xml
	 */
	private IVendeurManager getVendeurManager() {
		return super.evaluateValueExpression("#{spManager}", IVendeurManager.class); 		
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


	/********************************************************************************************/
	/***** ****** GETTERS and SETTERS ****** ****************************************************/

	public void setCompanyAccId(Long pCompanyAccId) {
		this.companyAccId = pCompanyAccId;
		System.out.println("editerSalespersonAction.setCompanyAccId(" + companyAccId + ")");
	}

	public Long getCompanyAccId() {
		return (Long) companyAccId;
	}
	
	public Object getNullObject() {
		return null;
	}
	
	/********************************************************************************************************************/
	/****************** Gestion des evenements declenches par manipulation de l'entite Ville ******/
	protected void raiseAfterTransactionSuccessEvent() {
		System.out.println(" ---------- DEBUG :EditerLMarchandAction: "+"raiseAfterTransactionSuccessEvent : "+"org.jboss.seam.afterTransactionSuccess." + "Marchand");
		raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess." + "Marchand");
	}

	/** Add a successful creation message to the status messages. */
	private void createdMessage() {
		getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.INFO, 
				getCreatedMessageKey(), getCreatedMessage());
		System.out.println(">>>> " + getCreatedMessage());
	}
	
	private String getCreatedMessageKey() {
		return getMessageKeyPrefix() + "created";
	}

	/** Add a successful update message to the status messages. */
	private void updatedMessage() {
		getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.INFO, 
				getUpdatedMessageKey(), getUpdatedMessage());
	}	

	private String getUpdatedMessageKey() {
		return getMessageKeyPrefix() + "updated";
	}

	public String getUpdatedMessage() {
		return updatedMessage;
	}

	/** Cette méthode permet d'indiquer que l'instance n'est pas managé en Base de donnée */
	protected void unmanagedMessage() {
		getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.WARN, 
				getUnmanagedMessageKey(), getUnmanagedMessage());
	}

	protected String getUnmanagedMessageKey() {
		return getMessageKeyPrefix() + "unmanaged";
	}
	
	public String getUnmanagedMessage() {
		return unmanagedMessage;
	}

	private String getMessageKeyPrefix() {
		String lClassName = this.getClass().getName();
		return lClassName.substring(lClassName.lastIndexOf('.') + 1) + '_';
	}
	
	private String getCreatedMessage() {
		if (createdMessage == null || createdMessage.equals("")) {
			return "default : salesperson is created succsssfully.";
		} else {
			return "message bundle :" + createdMessage;
		}
	}
}
