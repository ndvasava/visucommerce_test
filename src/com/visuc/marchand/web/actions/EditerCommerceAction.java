package com.visuc.marchand.web.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.persistence.EntityManager;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.SystemException;

import org.apache.commons.beanutils.BeanUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.Controller;
import org.jboss.seam.framework.EntityNotFoundException;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;
import org.jboss.seam.transaction.Transaction;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.visuc.common.entity.Address;
import com.visuc.common.entity.Image;
import com.visuc.common.tools.MailUtility;
import com.visuc.marchand.commerce.common.Constants;
import com.visuc.marchand.commerce.entite.Commerce;
import com.visuc.marchand.commerce.entite.ContractToCommerce;
import com.visuc.marchand.commerce.entite.ShopInfo;
import com.visuc.marchand.commerce.service.ICommerceManager;
import com.visuc.marchand.vendeur.entite.Vendeur;
import com.visuc.referentiel.contract.entite.Contract;
import com.visuc.referentiel.contract.service.IContractManager;
import com.visuc.referentiel.pays.entite.Pays;
import com.visuc.referentiel.typeCommerce.entite.TypeCommerce;
import com.visuc.referentiel.typeCommerce.service.ITypeCommerceManager;
import com.visuc.referentiel.ville.entite.Ville;
import com.visuc.referentiel.ville.service.IVilleManager;

@Name("editerCommerceAction")
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
@SuppressWarnings("serial")
public class EditerCommerceAction extends Controller {

	private Commerce instance;
	private Object commerceId;
	private Boolean idDefined;
	private boolean toValidate;
	private ShopInfo shopInstance;
	private Address companyAddress;
	private Address shopAddress;
	
	private List<Ville> listVille;
	private Ville selectedVille;

	private List<TypeCommerce> listTypeCommerce;
	private TypeCommerce selectedTypeCommerce;
	private List<TypeCommerce> listSelectedTypeCommerce;
	
	private List<Contract> listContract;
	private Contract selectedContract;
	private ContractToCommerce contractInstance;
	
	//private boolean contractValidation;
	
	private boolean contractAccepted;
	private String passwordReType;
		
	private transient EntityManager entityManager;
	private transient boolean dirty;

	@Logger
	private Log log;
	
	@In(value="#{facesContext.externalContext}")
	private ExternalContext extCtx;
	
	@In(value="#{facesContext}")
	FacesContext facesContext;
	
	
	/******* Declaration des messages specifiques a l'entite ville et ses actions disponibles ***********/
	private String deletedMessage = "Suppression effectuée";
	private String createdMessage = "Création effectuée";
	@SuppressWarnings("unused")
	private String alreadyExistsMessage = "Création non effectuée : l'enregistrement existe déjà";
	private String updatedMessage = "Mise à jour effectuée";
	private String unmanagedMessage = "Mise à jour non effectuée : l'enregistrement n'existe pas";
	private String accValidatedMessage = "Le compte est validé.";
	private String accNotValidMessage = "Le compte n'est pas encore validé.";

	public String find() {
		System.out.println("--> EditerCommerceAction.find()");
		
		// search associated entity
		initInstance();
		
		System.out.println("instance : " + instance.toString());
		System.out.println("<-- EditerCommerceAction.find()");
		
		return "find_OK";
	}

	public String findForValidation() {
		System.out.println("--> EditerCommerceAction.findForValidation()");
		find();
		System.out.println("<-- EditerCommerceAction.findForValidation()");
		return "FIND_ACC_FOR_VALIDATION_OK";
	}

	
	public String edit() {
		// On recherche l'entité associée
		initInstanceForEdit();
			
		System.out.println(" ---------- DEBUG :EditerCommerceAction.edit() "+instance.toString());
		return "edit";
	}
	
	// refresh des champs de l'instance de l'action
	public void refresh(){
		evaluateValueExpression("#{editerCommerceAction.instance.id}");
	}
	

	public String validateUserIdAndPassword() {
		
		if (!this.passwordReType.equals(this.instance.getPassword())) {
			System.out.println("--> ERROR : Password retype is not equal to the password !!");
			FacesMessages.instance().add("Passwod confirmation does not match the password.");
			return null;
		}
		return "step1OK";
	}
	
	/** Data persistance method for <Commerce> entity.
	 * @return "persist" if entity is stored successfully in DB. 
	 */
	public String persistCommerceAccount() {
		
		System.out.println("--> EditerCommerceAction.persistCommerceContract() ");

		System.out.println("->	" + instance.getUserId() + ", " + instance.getPassword());
		System.out.println("->	" + instance.getNomCommerce());

		// check if the password and re-type of password matches
		if (!this.passwordReType.equals(this.instance.getPassword())) {
			System.out.println("--> ERROR : Password retype is not equal to the password !!");
			FacesMessages.instance().add("Passwod confirmation does not match the password.");    
			return null;
		}
		
		// check if the user ID is already in use by other account. 
		// In that case, user must provide another user id.
		if (!checkAccIdAvailability(instance.getUserId())) {
			System.out.println("--> ERROR : user id already exists !!");
			FacesMessages.instance().add("Account already exists with same user id.");
			
			return null;
		}
		
		instance.setAddress(companyAddress);
		System.out.println("=> " + instance.getAddress().getAddress1());
		System.out.println("=> " + instance.getAddress().getAddress2());
		System.out.println("=> " + instance.getAddress().getCity().getNom());

		if (instance.getId() == null) {
			instance.setAccStatus(Constants.ACC_STATUS_CREATED);
			instance.setVisible(Constants.ACC_NOT_VISIBLE);
		}

		Commerce persistedCommerce = getCommerceManager().saveCommerce(instance); 
		setInstance(persistedCommerce);		
		
		// send a mail
		SendAccountCreateSuccessMail(instance);	
		System.out.println("->>  newly created commerce Id : " + persistedCommerce.getId());
		
		flushIfNecessary();
		createdMessage();
		raiseAfterTransactionSuccessEvent();
		
		System.out.println("<-- EditerSocieteAction.persistCommerceContract() ");
		return "create_OK";
	}

	private boolean checkAccIdAvailability(String pUserId) {
		return !(getCommerceManager().userIdExists(pUserId));
	}
	
	public String updateComplInfo() {
		System.out.println("--> EditerCommerceAction.updateComplInfo()");
		String returnStr = null;
		
		if (shopAddress != null) {
			getShopInstance().setShopAddress(shopAddress);
		}
		System.out.println("IBAN : " + getInstance().getIban());
		System.out.println("status : " + getInstance().getAccStatus());

		System.out.println("address : " + getShopAddress());
		System.out.println(getShopAddress().getAddress1());
		System.out.println(getShopAddress().getCity().getNom());

		
		if (getIdFromObject(getInstance()) == null) {
			unmanagedMessage();
			
		} else if ((getInstance().getAccStatus() >= Constants.ACC_STATUS_VALID)) {
			
			getInstance().setAccStatus(Constants.ACC_STATUS_VALID_WITH_COMPL_INFO); 
			//update();
			
			returnStr = Constants.UPDATE_OK;
			
		} else {
			accNotValidMessage();
		}
		
		System.out.println("return status " + returnStr);
		System.out.println("<-- EditerCommerceAction.updateComplInfo()");
		return returnStr;
	}
	
	/** Persistance method for the complementory information about company account.
	 * @return success - if persistance is OK, null - otherwise
	 */
	public String updateShopInfo() {
		System.out.println("--> EditerCommerceAction.updateShopInfo()");
		
		if (getIdFromObject(getInstance()) == null) {
			unmanagedMessage();
			return null;
		}
					
		getInstance().getShopInfo().setShopAddress(shopAddress);
		joinTransaction();
		Commerce lEntity = getCommerceManager().updateCommerce(getInstance()); 
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
		
		System.out.println("<-- EditerVilleAction.updateShopInfo()");
		return "update_shop_info_OK";
	}
	
	/* Mise a jour */
	public String update(){
		System.out.println("--> EditerCommerceAction.update()");
		
		if (getIdFromObject(getInstance()) == null) {
			unmanagedMessage();
			return null;
		}
		
		joinTransaction();
		
		Commerce lEntity = getCommerceManager().updateCommerce(getInstance()); 
		
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
		
		System.out.println("<-- EditerCommerceAction.update()");
		return "update";
	}

	
	// listener pour upload de la photo du vendeur
	public void photoUploadEventListener(UploadEvent event) throws Exception {
    	System.out.println("--> EditerCommerceAction.photoUploadEventListener() ");
    	
    	UploadItem item = event.getUploadItem();
        Image img = null;
        List<Image> imageSet = null;
        
        if (getInstance() == null) {
        	System.out.println("	- 'Commerce' Instance does not exist.");
        }
        if (getInstance().getShopInfo() == null) {
        	System.out.println("	- 'ShopInfo' instance is null.");
        }
        if (getInstance().getShopInfo().getShopPics() == null) {
        	System.out.println("	- 'shopPics' is null.");
        }
        
        if (getShopInstance().getShopPics() == null) {
        	imageSet = new ArrayList<Image>();
        	getShopInstance().setShopPics(imageSet);
        } else {
        	imageSet = getShopInstance().getShopPics();
        }
        
        img = new Image();
    	img.setLength(((Integer)item.getData().length).longValue());
    	img.setName(item.getFileName());
        img.setContent(item.getData());
        
        imageSet.add(img);
        
        System.out.println("<-- EditerCommerceAction.photoUploadEventListener() ");
    }

	/* Methode en frontal du Backing Bean */
	// clear the upload image area
	public String clearUploadData() {
		System.out.println("--> EditerCommerceAction.clearUploadData() ");
		getInstance().getShopInfo().setShopPics(null);
		System.out.println("<-- EditerCommerceAction.clearUploadData() ");
        return null;
    }

	public String validateCompanyAcc() {
		System.out.println("--> EditerCommerceAction.validateCompanyAcc()");
		if (getInstance().getAccStatus() == Constants.ACC_STATUS_CREATED) {
			getInstance().setAccStatus(Constants.ACC_STATUS_VALID);
		}
		
		update();
		
		accValidatedMessage();
		SendAccountValidatedMail(instance);
		
		System.out.println("<-- EditerCommerceAction.validateCompanyAcc()");
		return "VALIDATE_ACC_OK";
	}
	
	public String rejectCompanyAcc() {
		System.out.println("--> EditerCommerceAction.rejectCompanyAcc()");
		
		SendAccountRejectMail(instance);
		//accValidatedMessage();
		
		System.out.println("<-- EditerCommerceAction.rejectCompanyAcc()");
		return "VALIDATE_ACC_OK";
	}
	/** Data persistance method for <Commerce> entity.
	 * @return "persist" if entity is stored successfully in DB. 
	 */
	public String validateCommerceContract() {
		
		System.out.println("--> EditerCommerceAction.validateCommerceContract() ");
		
		System.out.println("->	Commerce to validate : " + instance.getNomCommerce() 
				+ "(" + instance.getId() + ", " + instance.getContract().getIdContract() + ")" );
		
		this.getInstance().getContract().setStatus(2);
		Commerce commerce = getCommerceManager().saveCommerce(instance);
		System.out.println("-> status (after save) : " + commerce.getContract().getStatus());

		flushIfNecessary();
		//generateContract();
		raiseAfterTransactionSuccessEvent();
		
		FacesMessages.instance().add("Contract is Validated.");
		System.out.println("<-- EditerCommerceAction.validateCommerceContract() ");
		return "validated";
	}

	public String cancel(){
		System.out.println("--> EditerCommerceAction.cancel()");
		/*if (getDefinedInstance() == null) {
			return "cancel";
		}*/
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
		
		System.out.println("<-- EditerCommerceAction.cancel()");
		return "cancel";
	}
	
	@SuppressWarnings("deprecation")
	private ContractToCommerce makeContractToCommerce(Contract pContract, Commerce pCommerce) {
		System.out.println("--> EditerSocieteAction.makeContractToCommerce() ");
		ContractToCommerce commerceContract;

		//if (contractAccepted) {
			commerceContract = new ContractToCommerce();

			//TODO : commerce must be present for the contract to be created. else raise an error.
			if (pCommerce != null) {
				commerceContract.setCommerce(pCommerce);
			} else {
				return null;
			}
			commerceContract.setStatus(1);
			commerceContract.setComissionType(pContract.getComissionType());
			commerceContract.setComissionAmount(pContract.getComissionAmount());
			commerceContract.setContractDescription(pContract.getContractDescription());
			
			//TODO replace with real selected date (or calculated date)
			commerceContract.setStartDate(new Date("01/12/2011"));
			commerceContract.setEndDate(new Date("01/12/2012"));
		/*} else {
			commerceContract = null;
		}*/
		
		System.out.println("<-- EditerSocieteAction.makeContractToCommerce() ");
		return commerceContract;
		
	}
	
	public String generateContract() {
		System.out.println("--> EditerSocieteAction.generateContract() ");
		
		if (listSelectedTypeCommerce == null || listSelectedTypeCommerce.get(0) == null) {
			return null;
		}

		this.setListContract(this.getContractManager().findContractListByCommerceType(listSelectedTypeCommerce.get(0), 0, 20));
		if (this.getListContract().size() > 0) {
			this.setSelectedContract(getListContract().get(0));
		} else {
			this.setSelectedContract(getNullContract());
		}
		this.setContractInstance(this.makeContractToCommerce(getSelectedContract(), instance));
		
		System.out.println("Contract List >>>> " + getListContract().size());
		
		System.out.println("<-- EditerSocieteAction.generateContract() ");
		return "contract";
	}
	
	
	public Commerce getInstance() {
		System.out.println("--> getInstance()");
		joinTransaction();
		
		if (instance == null) {
			initInstanceForEdit();
		}
		System.out.println("<-- getInstance()");
		return instance;
		
	}

	public void setInstance(Commerce pInstance) {
		
		// Set dirty flag
		setDirty(this.instance, pInstance);
		
		// If pInstance is not null, set the id.
		if (pInstance != null) {
			assignId(getIdFromObject(pInstance));
		}
		
		this.instance = pInstance;
	}

	private Object getIdFromObject(Commerce pEntity) {
		
		if (pEntity == null) {
			NoSuchMethodError lError = new NoSuchMethodError("Méthode getId() inexistante pour l'entité null");
			throw lError;
		}
		
		return pEntity.getId();
	}
	
	protected void initInstanceForEdit() {
		System.out.println("--> EditerCommerceAction.initInstanceForEdit()");
		
		initCityList();
		this.companyAddress = new Address();
		this.setListTypeCommerce(getTypeCommerceManager().findAll());
		
		initInstance();
		
		System.out.println("<-- EditerCommerceAction.initInstanceForEdit()");
	}
	
	protected void initInstance() {
		
		System.out.println("--> EditerCommerceAction.initInstance()");
		
		if (isIdDefined()) {
			Commerce commerce = getCommerceManager().findCommerceById(getCommerceId());
			if (commerce == null) {
				handleNotFound();
			} else {
				setInstance(commerce);
			}
		}
		
		if (instance == null) {
			setInstance(new Commerce());
		}
		
		System.out.println("<-- EditerCommerceAction.initInstance()");
	}
	
	private void initCityList() {

		//TODO : replace default country (pays) by country from user profile
		Pays pay = new Pays();
		pay.setId(new Long(1));
		
		this.setListVille(getVilleManager().listAllVillesPays(pay));
		System.out.println("Total ville : " + this.getListVille().size());
		
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
	
	
	public boolean isIdDefined() {
		if (getCommerceId() == null) {
			return false;
		}
		if (idDefined == null) {
			idDefined = isNotNullKey(getCommerceId());
		}
		return idDefined;
	}

	// Retourne vrai si le devise existe en base de données.
	public boolean isManaged() {
		return (getInstance() != null && isIdDefined());
	}

	@SuppressWarnings("unchecked")
	public static boolean isNotNullKey(Object pKey){
		
    	if (pKey == null) {
            return false;
        }
        if (org.springframework.beans.BeanUtils.isSimpleProperty(pKey.getClass())) {
            return !"".equals(pKey);
        }
        
        Map lMap = null;
        try {
            lMap = BeanUtils.describe(pKey);
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

	protected Pays handleNotFound() throws EntityNotFoundException {
		throw new EntityNotFoundException(getCommerceId(), this.instance.getClass());
	}

	/******************************************************************************************************/
	public void setShopInstance(ShopInfo shopInstance) {
		this.shopInstance = shopInstance;
		
		
	}

	public ShopInfo getShopInstance() {
		System.out.println("--> getShopInstance()");
		
		joinTransaction();
		if (shopInstance == null) {
			System.out.println("- create new instance.");
			initShopInstance();
		}
		
		System.out.println("<-- getShopInstance()");
		return shopInstance;
		

	}

	private void initShopInstance() {
		System.out.println("--> EditerCommerceAction.initShopInstance()");
		this.shopAddress = new Address();
		if (isIdDefined()) {
			//Commerce commerce = getCommerceManager().findCommerceById(getCommerceId());
			/*if (commerce == null) {
				handleNotFound();
			} else {
				setInstance(commerce);
			}*/
			
			// initiate list of cities
			initCityList();
			
			if (instance.getShopInfo() == null) {
				shopInstance = new ShopInfo();
				shopInstance.setCompany(instance);
				setShopInstance(shopInstance);
				instance.setShopInfo(shopInstance);
				//this.shopAddress = new Address();
			} else {
				shopInstance = instance.getShopInfo();
				//this.shopAddress = shopInstance.getShopAddress() == null ? new Address():shopInstance.getShopAddress();
			}
			
		} else {
			System.out.println("id is not defined.");
		}
		
		System.out.println("<-- EditerCommerceAction.initShopInstance()");
	}

	
	public String downloadContract() {

		HttpServletResponse response = (HttpServletResponse)extCtx.getResponse();
		response.setContentType("application/pdf");
                response.addHeader("Content-disposition", "attachment; filename=\"test.pdf\"");
		try {
			ServletOutputStream os = response.getOutputStream();
			os.write(null);
			os.flush();
			os.close();
			facesContext.responseComplete();
		} catch(Exception e) {
			log.error("\nFailure : " + e.toString() + "\n");
		}

		return null;
	}
	/*********************************************************************************************************************/
	/****************   Gestion de l'etat instance-entite-Id du bean action de la conversation   *****************

	/** Set the dirty flag if the value has changed. Call whenever a subclass attribute is updated */
	protected <U> boolean setDirty(U aOldValue, U aNewValue) {
		boolean lAttributeDirty = aOldValue != aNewValue && (aOldValue == null || !aOldValue.equals(aNewValue));
		dirty = dirty || lAttributeDirty;
		return lAttributeDirty;
	}
	
	/** Met à jour l'identifiant de l'entité gérée par cette action. Mais ne modifie pas l'entite(c'est en fait son instance) de cette action */
	void assignId(Object pId) {
		setDirty(commerceId, pId);
		commerceId = pId;
		idDefined = null;
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
	
	private IVilleManager getVilleManager() {
		return super.evaluateValueExpression("#{villeManager}", IVilleManager.class); 
	}
	
	private ITypeCommerceManager getTypeCommerceManager() {
		return super.evaluateValueExpression("#{typeCommerceManager}", ITypeCommerceManager.class);
	}

	private IContractManager getContractManager() {
		return super.evaluateValueExpression("#{contractManager}", IContractManager.class);
	}
	/** 
	 * The service ICommerceManager is managed by Spring in visu-service.xml 
	 */
	private ICommerceManager getCommerceManager() {
		return super.evaluateValueExpression("#{commerceManager}", ICommerceManager.class); 		
	}
	
/*	private IAddressManager getAddressManager() {
		return super.evaluateValueExpression("#{addressManager}", IAddressManager.class);
	}*/
	private void flushIfNecessary() {
		EntityManager lEntityManager = getEntityManager();
		if (lEntityManager != null) {
			lEntityManager.flush();
		}
	}

	private void SendAccountCreateSuccessMail(Commerce pAccInstance) {
		System.out.println("--> EditerCommerceAction.sendAccCreateSuccessMail()");
		
		String[] to = new String[] {pAccInstance.getEmail()};
		String from = Constants.VISUC_MAIL;
		String subject = "Your visucommerce account is created successfully.";
		String message = "Your visucommerce account is created successfully.* <br>"
			+ "account id : " + pAccInstance.getId() + "<br>"
			+ "user id : " + pAccInstance.getUserId() + "<br>"
			+ "Company : " + pAccInstance.getNomCommerce() + "<br>"
			+ "Commerce type : " + pAccInstance.getType().getTypeCommerceName() + "<br>"
			+ "Owner : " + pAccInstance.getOwnerName() + " " + pAccInstance.getOwnerLastName() + "<br><br>"
			+ "<b>* The account will be validated within 48 hours. Meanwhile, no action is possible on the account."
			+ "<br><b>Please Fill and sign the contract and send back to visucommerce on the following adress.</b> ";
		try {
			MailUtility.postMail(to, subject, message, from);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (javax.mail.MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("<-- EditerCommerceAction.sendAccCreateSuccessMail()");
	}

	
	private void SendAccountRejectMail(Commerce pAccInstance) {
		System.out.println("--> EditerCommerceAction.sendAccountRejectMail()");
		
		String[] to = new String[] {pAccInstance.getEmail()};
		String from = Constants.VISUC_MAIL;
		String subject = "Your visucommerce account is not validated.";
		String message = "Your visucommerce account is not validated.* <br>"
			+ "account id : " + pAccInstance.getId() + "<br>"
			+ "user id : " + pAccInstance.getUserId() + "<br>"
			+ "Company : " + pAccInstance.getNomCommerce() + "<br>"
			+ "Commerce type : " + pAccInstance.getType().getTypeCommerceName() + "<br>";
		try {
			MailUtility.postMail(to, subject, message, from);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (javax.mail.MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("<-- EditerCommerceAction.sendAccountRejectMail()");
	}

	private void SendAccountValidatedMail(Commerce pAccInstance) {
		System.out.println("--> EditerCommerceAction.sendAccountRejectMail()");
		
		String[] to = new String[] {pAccInstance.getEmail()};
		String from = Constants.VISUC_MAIL;
		String subject = "Your visucommerce account is validated.";
		String message = "Your visucommerce account is validated.* <br>"
			+ "account id : " + pAccInstance.getId() + "<br>"
			+ "user id : " + pAccInstance.getUserId() + "<br>"
			+ "Company : " + pAccInstance.getNomCommerce() + "<br>"
			+ "Commerce type : " + pAccInstance.getType().getTypeCommerceName() + "<br>";
		try {
			MailUtility.postMail(to, subject, message, from);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (javax.mail.MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("<-- EditerCommerceAction.sendAccountRejectMail()");
	}

	/*****************************************************/

	public DataModel getSpDataModel() {
		DataModel dataModel = wrap(instance.getListVendeurs());
		return dataModel;
	}

	/**Cette méthode permet d'encapsuler la liste résultat dans un objet {@link DataModel} englobant la liste elle-même
	 * mais également les compteurs connexes (nombre total d'enregistrements, nombre d'enregistrements par page...).
	 */
	protected DataModel wrap(List<Vendeur> aList) {
		if (aList == null) {
			return null;
		}
		return new ListDataModel(aList);
	}

	public DataModel getShopPicsDataModel() {
		DataModel dataModel = wrapShopPics(shopInstance.getShopPics());
		return dataModel;
	}

	/**Cette méthode permet d'encapsuler la liste résultat dans un objet {@link DataModel} englobant la liste elle-même
	 * mais également les compteurs connexes (nombre total d'enregistrements, nombre d'enregistrements par page...).
	 */
	protected DataModel wrapShopPics(List<Image> aList) {
		if (aList == null) {
			return null;
		}
		return new ListDataModel(aList);
	}
	
	/**
	 * @param listVille the listVille to set
	 */
	public void setListVille(List<Ville> listVille) {
		this.listVille = listVille;
	}

	/**
	 * @return the listVille
	 */
	public List<Ville> getListVille() {
		if(listVille == null) {
			System.out.println("'listeVille' is null");
			initCityList();
		}
		return listVille;
	}
	
	public Ville getSelectedVille() {
		return selectedVille;
	}

	public void setSelectedVille(Ville selectedVille) {
		this.selectedVille = selectedVille;
		if (instance != null) {
			instance.getAddress().setCity(this.selectedVille);
		}
		System.out.println("selected ville : " + this.selectedVille.getNom());
	}

	public List<TypeCommerce> getListTypeCommerce() {
		return listTypeCommerce;
	}

	public void setListTypeCommerce(List<TypeCommerce> listTypeCommerce) {
		this.listTypeCommerce = listTypeCommerce;
	}

	public TypeCommerce getSelectedTypeCommerce() {
		return selectedTypeCommerce;
	}

	public void setSelectedTypeCommerce(TypeCommerce selectedTypeCommerce) {
		this.selectedTypeCommerce = selectedTypeCommerce;
		if (instance != null) {
			instance.setType(this.selectedTypeCommerce);
		}
		System.out.println("selected commerce type : " + this.selectedTypeCommerce.getTypeCommerceName());
	}

	public List<Contract> getListContract() {
		return listContract;
	}

	public void setListContract(List<Contract> listContract) {
		this.listContract = listContract;
	}

	public Contract getSelectedContract() {
		return selectedContract;
	}

	public void setSelectedContract(Contract selectedContract) {
		System.out.println("-->	EditerCommerceAction.setSelectedContract() " + selectedContract.getComissionType());
		this.selectedContract = selectedContract;
	}

	public Long getCommerceId() {
		return (Long) commerceId;
	}

	public void setCommerceId(Long pCommerceId) {
		
    	if (setDirty(this.commerceId, pCommerceId)) {
    		
    		boolean lIsDebugEnabled = getLog().isDebugEnabled();
    		if (lIsDebugEnabled) {
    			getLog().debug("Dirty - setCommerceId -> #0, #1", this.commerceId, pCommerceId);
    		}
			setInstance(null);
		}

		this.commerceId = pCommerceId;
	}

	public void setToValidate(boolean toValidate) {
		this.toValidate = toValidate;
	}

	public boolean isToValidate() {

		if (instance.getContract() != null && instance.getContract().getStatus() == 1)
			toValidate = true;
		else 
			toValidate = false;
		
		System.out.println("--> to validate ? " + toValidate);

		return toValidate;
	}
	
	public boolean getToValidate() {
		boolean status;
		if (instance.getContract().getStatus() == 1)
			status = true;
		else 
			status = false;
		
		System.out.println("--> to validate ? " + status);
		return status;
	}
	
	private Contract getNullContract() {
		Contract nullContract = new Contract();
		nullContract.setIdContract(new Long(0));
		nullContract.setComissionType(0);
		nullContract.setComissionAmount(0);
		nullContract.setContractDescription("New type of commerce type. Please Contact us on 12345 to negotiate the price.");
		return nullContract;
	}

	/********************************************************************************************************************/
	/****************** Gestion des evenements declenches par manipulation de l'entite Commerce ******/
	protected void raiseAfterTransactionSuccessEvent() {
		
		raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess");
		
		String lSimpleEntityName = getSimpleEntityName();
		if (lSimpleEntityName != null) {
			System.out.println("--> DEBUG :EditerCommerceAction: " 
					+ "raiseAfterTransactionSuccessEvent : " + "org.jboss.seam.afterTransactionSuccess. > " 
					+ lSimpleEntityName);
			raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess. > " + lSimpleEntityName);
		}
	}
	
	/**Retourne le nom de la classe de l'entité gérée par cette Action sans
	 * prendre en compte le nom de paquetage (i.e. Employe pour l'entité
	 * <code>com.natixis.aws.employe.entite.Employe</code>).
	 */
	protected String getSimpleEntityName() {
		String lName = Commerce.class.getName();
		return lName.lastIndexOf(".") > 0 && lName.lastIndexOf(".") < lName.length() ? 
				lName.substring(lName.lastIndexOf(".") + 1, lName.length()) : lName;
	}
	
	/**********************************************************************/
	/**************** messaging methods ***********************************/
	
	/** Add a successful creation message to the status messages. */
	private void createdMessage() {
		getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.INFO, 
				getCreatedMessageKey(), getCreatedMessage());
	}

	/** Add a successful update message to the status messages. */
	private void updatedMessage() {
		getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.INFO, 
				getUpdatedMessageKey(), getUpdatedMessage());
	}	

	private void accValidatedMessage() {
		getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.INFO, 
				getAccValidatedMessageKey(), getAccValidatedMessage());
	}
	
	private void accNotValidMessage() {
		getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.INFO, 
				getAccNotValidMessageKey(), getAccNotValidMessage());
	}
	/** Add a successful update message to the status messages. */
	@SuppressWarnings("unused")
	private void deletedMessage() {
		getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.INFO, 
				getDeletedMessageKey(), getDeletedMessage());
	}	

	/** Cette méthode permet d'indiquer que l'instance n'est pas managé en Base de donnée */
	protected void unmanagedMessage() {
		getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.WARN, 
				getUnmanagedMessageKey(), getUnmanagedMessage());
	}

	private String getCreatedMessage() {
		return createdMessage;
	}

	public String getUpdatedMessage() {
		return updatedMessage;
	}

	public String getAccValidatedMessage() {
		return accValidatedMessage;
	}
	
	public String getAccNotValidMessage() {
		return accNotValidMessage;
	}
	private String getDeletedMessage() {
		return deletedMessage;
	}

	public String getUnmanagedMessage() {
		return unmanagedMessage;
	}

	private String getCreatedMessageKey() {
		return getMessageKeyPrefix() + "created";
	}

	private String getUpdatedMessageKey() {
		return getMessageKeyPrefix() + "updated";
	}
	
	private String getAccValidatedMessageKey() {
		return getMessageKeyPrefix() + "validated";
	}
	
	private String getAccNotValidMessageKey() {
		return getMessageKeyPrefix() + "not valid";
	}
	
	private String getDeletedMessageKey() {
		return getMessageKeyPrefix() + "deleted";
	}
	
	protected String getUnmanagedMessageKey() {
		return getMessageKeyPrefix() + "unmanaged";
	}
	

	public void setUnmanagedMessage(String unmanagedMessage) {
		this.unmanagedMessage = unmanagedMessage;
	}
		
	// ----- Prefix a tout message provenant de l'entite Devise
	private String getMessageKeyPrefix() {
		String lClassName = this.getClass().getName();
		return lClassName.substring(lClassName.lastIndexOf('.') + 1) + '_';
	}

	/**
	 * @param contractAccepted the contractAccepted to set
	 */
	public void setContractAccepted(boolean contractAccepted) {
		this.contractAccepted = contractAccepted;
	}

	/**
	 * @return the contractAccepted
	 */
	public boolean isContractAccepted() {
		return contractAccepted;
	}

	/**
	 * @param contractInstance the contractInstance to set
	 */
	public void setContractInstance(ContractToCommerce contractInstance) {
		this.contractInstance = contractInstance;
	}

	/**
	 * @return the contractInstance
	 */
	public ContractToCommerce getContractInstance() {
		return contractInstance;
	}

	public void setListSelectedTypeCommerce(List<TypeCommerce> pListSelectedTypeCommerce) {
		this.listSelectedTypeCommerce = pListSelectedTypeCommerce;
		if (instance != null && pListSelectedTypeCommerce != null && pListSelectedTypeCommerce.size() > 0) {
			instance.setType(this.listSelectedTypeCommerce.get(0));
		}
		System.out.println("selected commerce type : " + this.instance.getType().getTypeCommerceName());

	}

	public List<TypeCommerce> getListSelectedTypeCommerce() {
		return listSelectedTypeCommerce;
	}

	public void setPasswordReType(String passwordReType) {
		this.passwordReType = passwordReType;
	}

	public String getPasswordReType() {
		return passwordReType;
	}

	public void setCompanyAddress(Address companyAddress) {
		this.companyAddress = companyAddress;
		if (instance != null) {
			instance.setAddress(companyAddress);
		}
	}

	public Address getCompanyAddress() {
		return companyAddress;
	}

	public Address getShopAddress() {
		return shopAddress;
	}

	public void setShopAddress(Address shopAddress) {
		this.shopAddress = shopAddress;
		if (shopInstance != null) {
			shopInstance.setShopAddress(shopAddress);
		}
	}


	public Object getNullObject() {
		return null;
	}
}
