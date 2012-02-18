package com.visuc.referentiel.contract.entite;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.visuc.referentiel.typeCommerce.entite.TypeCommerce;


@SuppressWarnings("serial")
@Entity
@Table(name="REF_CONTRACT")
public class Contract implements Serializable {

	@Id
	@Column(name="ID_REF_CONTRACT")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long idContract; 
	
	@ManyToOne (fetch= FetchType.LAZY)
	@JoinColumn(name="ID_TYPE_COMMERCE")
	private TypeCommerce commerceType;
	
	@Column(name="COMISSION_TYPE")
	private int comissionType;
	
	@Column(name="COMISSION_AMOUNT")
	private float comissionAmount;
	
	@Column(name="DESCRIPTION")
	private String contractDescription;
	
	/*@ManyToOne (fetch= FetchType.LAZY)
	@JoinColumn(name="ID_COMMERCE")
	private Commerce commerce;*/
	
	
	//-----------------------------------------------------
	public Long getIdContract() {
		return idContract;
	}
	public void setIdContract(Long idContract) {
		this.idContract = idContract;
	}

	public TypeCommerce getCommerceType() {
		return commerceType;
	}
	public void setCommerceType(TypeCommerce commerceType) {
		this.commerceType = commerceType;
	}

	public int getComissionType() {
		return comissionType;
	}
	public void setComissionType(int comissionType) {
		this.comissionType = comissionType;
	}
	public float getComissionAmount() {
		return comissionAmount;
	}
	public void setComissionAmount(float comissionAmount) {
		this.comissionAmount = comissionAmount;
	}
	public String getContractDescription() {
		return contractDescription;
	}
	public void setContractDescription(String contractDescription) {
		this.contractDescription = contractDescription;
	}

/*	public void setCommerce(Commerce commerce) {
		this.commerce = commerce;
	}

	public Commerce getCommerce() {
		return commerce;
	}
*/	
}
