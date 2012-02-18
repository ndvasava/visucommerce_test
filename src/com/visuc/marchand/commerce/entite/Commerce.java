package com.visuc.marchand.commerce.entite;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.Pattern;

import com.visuc.common.entity.Address;
import com.visuc.marchand.vendeur.entite.Vendeur;
import com.visuc.referentiel.typeCommerce.entite.TypeCommerce;
import com.visuc.referentiel.ville.entite.Ville;


@Entity
@SuppressWarnings("serial")
@Table(name="COMPANY") 
public class Commerce implements Serializable {

	@Id
	@Column(name = "ID_COMMERCE")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	/**
	 * This field is used to decide the access and actions can be done by this account.
	 * 0:	Account is created, yet to validate.
	 * 1:	Account is validated
	 * 2:	Account complementary info fulfilled, contract is not present.
	 * 3:	Account contract is valid.
	 */
	@Column(name = "ACC_STATUS")
	private int accStatus;
	
	@Column(name = "VISIBLE")
	private boolean visible;
	
	@Column(name = "USERNAME")
	private String userId;
	
	@Column(name = "PASSWORD")
	private String password;


	//---------- Company Info ---------------
	@Column(name = "COMPANY_NAME")
	private String nomCommerce;
	
	@ManyToOne
	@JoinColumn(name="ID_TYPE_COMMERCE")
	private TypeCommerce type;

	@Column(name = "REG_TYPE")
	private String regType;
	
	@Column(name = "REG_ID")
	private String regId;

	@Column(name = "OWNER_NAME")
	private String ownerName;
	
	@Column(name = "OWNER_LASTNAME")
	private String ownerLastName;
	
/*	@Column(name="ADRESS1")
	private String adress1;

	@Column(name="ADRESS2")
	private String adress2;

	@ManyToOne
	@JoinColumn(name="ID_CITY")
	private Ville city;
*/	
	@OneToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="ADDRESS")
	private Address address;
	
	@Pattern(regex="[A-Za-z0-9._%+-]+@[A-Za-z0-9]+[.][A-Za-z.]{2,6}", message="Invalid email adress")
	@Column(name = "EMAIL")
	private String email;

	@Pattern(regex="[0-9()-+ ]*{0,20}", message="Invalid phone number")
	@Column(name = "PHONE_WORK")
	private String phoneWork;
		
	@OneToOne(mappedBy = "commerce", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private ContractToCommerce contract;
	
	@OneToMany(mappedBy="commerce")
	private List<Vendeur> listVendeurs;

	//-------- more info ---------
	@OneToOne(mappedBy="company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private ShopInfo shopInfo;
	
	@Pattern(regex="[0-9A-Z]*{0,32}")
	@Column(name = "IBAN")
	private String iban;
	
	//-------- GETTERS -- SETTERS --------------
	
	public Commerce(){
		address = new Address();
	};
	
	// ******** Getter & Setter **********
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public void setUserId(String pUserId) {
		this.userId = pUserId;
	}

	public String getUserId() {
		return userId;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setRegType(String regType) {
		this.regType = regType;
	}

	public String getRegType() {
		return regType;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getRegId() {
		return regId;
	}

	public TypeCommerce getType() {
		return type;
	}
	public void setType(TypeCommerce type) {
		this.type = type;
	}

	public void setAccStatus(int accStatus) {
		this.accStatus = accStatus;
	}

	public int getAccStatus() {
		return accStatus;
	}

	public String getNomCommerce() {
		return nomCommerce;
	}
	public void setNomCommerce(String nomCommerce) {
		this.nomCommerce = nomCommerce;
	}

/*	public String getAdress1() {
		return adress1;
	}
	public void setAdress1(String pAdress) {
		this.adress1 = pAdress;
	}
	
	public String getAdress2() {
		return adress2;
	}

	public void setAdress2(String adress2) {
		this.adress2 = adress2;
	}

	public Ville getCity() {
		return city;
	}
	public void setCity(Ville ville) {
		this.city = ville;
	}
*/
	public void setContract(ContractToCommerce contract) {
		this.contract = contract;
	}
	public ContractToCommerce getContract() {
		return contract;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerLastName(String ownerLastName) {
		this.ownerLastName = ownerLastName;
	}

	public String getOwnerLastName() {
		return ownerLastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneWork() {
		return phoneWork;
	}

	public void setPhoneWork(String phoneWork) {
		this.phoneWork = phoneWork;
	}

	public void setListVendeurs(List<Vendeur> listVendeurs) {
		this.listVendeurs = listVendeurs;
	}

	public List<Vendeur> getListVendeurs() {
		return listVendeurs;
	}

	public void setShopInfo(ShopInfo shopInfo) {
		this.shopInfo = shopInfo;
	}

	public ShopInfo getShopInfo() {
		return shopInfo;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param iban the iban to set
	 */
	public void setIban(String iban) {
		this.iban = iban;
	}

	/**
	 * @return the iban
	 */
	public String getIban() {
		return iban;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(Address address) {
		System.out.println("--> Commerce.setAddress()");
		this.address = address;
		//this.adress1 = address.getAdress1();
		//this.adress2 = address.getAdress2();
		//this.city = address.getCity();
		System.out.println("<-- Commerce.setAddress()");
	}

	/**
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}

}
