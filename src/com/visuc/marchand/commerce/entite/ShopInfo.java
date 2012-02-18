package com.visuc.marchand.commerce.entite;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.Pattern;

import com.visuc.common.entity.Address;
import com.visuc.common.entity.Image;


@Entity
@SuppressWarnings("serial")
@Table(name="SHOP_INFO") 
public class ShopInfo implements Serializable {

	@Id
	@Column(name = "SHOP_INFO_ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	//------ SHOP CONTACT --------------
	@Column(name = "SHOP_NAME")
	private String shopName;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="ID_ADDRESS")
	private Address shopAddress;
	
	@Pattern(regex="[0-9()-+ ]*{0,20}", message="Invalid phone number")
	@Column(name = "PHONE")
	private String phoneWork;

	@Pattern(regex="[A-Za-z0-9._%+-]+@[A-Za-z0-9]+[.][A-Za-z.]{2,6}", message="Invalid email adress")
	@Column(name = "EMAIL")
	private String email;

	//---- VARIOUS STATUSES -------------
	@Column(name = "visibility")
	private boolean visibilityStatus;
	
	@Column(name = "available")
	private boolean availabile;
	
	//----- SHOP INFO --------------------
	@Column(name = "OPEN_TIME")
	private String openTime;
	
	@Column(name = "MORE_INFO")
	private String additionalInfo;
	
	@Column(name = "DELIVERY_FLAG")
	private boolean deliveryStatus;
	
	@Column(name = "AWARDS")
	private String awards;
	
	//---- REF ------------------------
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="ID_COMMERCE")
	private Commerce company;

	//for joing the tables (many-to-many)
	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name = "SHOP_IMAGE",
			joinColumns = {@JoinColumn(name="SHOP_ID")},
			inverseJoinColumns = {@JoinColumn(name="IMAGE_ID")}	)
	private List<Image> shopPics;


	//-------------------------------------------------------
	public ShopInfo() {
		shopAddress = new Address();
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public List<Image> getShopPics() {
		return shopPics;
	}

	public void setShopPics(List<Image> shopPics) {
		this.shopPics = shopPics;
	}

	public boolean isDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(boolean deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public void setCompany(Commerce company) {
		this.company = company;
	}

	public Commerce getCompany() {
		return company;
	}

	public void setShopAddress(Address shopAddress) {
		this.shopAddress = shopAddress;
	}

	public Address getShopAddress() {
		return shopAddress;
	}
	/**
	 * @param shopName the shopName to set
	 */
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	/**
	 * @return the shopName
	 */
	public String getShopName() {
		return shopName;
	}
	/**
	 * @param visibilityStatus the visibilityStatus to set
	 */
	public void setVisibilityStatus(boolean visibilityStatus) {
		this.visibilityStatus = visibilityStatus;
	}
	/**
	 * @return the visibilityStatus
	 */
	public boolean isVisibilityStatus() {
		return visibilityStatus;
	}
	/**
	 * @param phoneWork the phoneWork to set
	 */
	public void setPhoneWork(String phoneWork) {
		this.phoneWork = phoneWork;
	}
	/**
	 * @return the phoneWork
	 */
	public String getPhoneWork() {
		return phoneWork;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param availabile the availabile to set
	 */
	public void setAvailabile(boolean availabile) {
		this.availabile = availabile;
	}
	/**
	 * @return the availabile
	 */
	public boolean isAvailabile() {
		return availabile;
	}
	/**
	 * @param awards the awards to set
	 */
	public void setAwards(String awards) {
		this.awards = awards;
	}
	/**
	 * @return the awards
	 */
	public String getAwards() {
		return awards;
	}

}
