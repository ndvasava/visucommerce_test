package com.visuc.marchand.commerce.entite;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@SuppressWarnings("serial")
@Entity
@Table(name="CONTRACT")
public class ContractToCommerce implements Serializable {

	@Id
	@Column(name="ID_CONTRACT")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long idContract; 
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="ID_COMMERCE")
	private Commerce commerce;
	
	@Column(name="START_DATE")
	private Date startDate;
	
	@Column(name="END_DATE")
	private Date endDate;
	
	@Column(name="STATUS")
	private int status;
	
	@Column(name="COMISSION_TYPE")
	private int comissionType;
	
	@Column(name="COMISSION_AMOUNT")
	private float comissionAmount;
	
	@Column(name="DESCRIPTION")
	private String contractDescription;
	
	
	public Long getIdContract() {
		return idContract;
	}
	public void setIdContract(Long idContract) {
		this.idContract = idContract;
	}
	public Commerce getCommerce() {
		return commerce;
	}
	public void setCommerce(Commerce commerce) {
		this.commerce = commerce;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
	
}
