package com.visuc.referentiel.pays.entite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.visuc.referentiel.devise.entite.Devise;
import com.visuc.referentiel.langue.entite.Langue;
import com.visuc.referentiel.ville.entite.Ville;

@Entity
@SuppressWarnings("serial")
@Table(name="REF_PAYS")
public class Pays implements Serializable {
	
	@Id
	@Column(name = "ID_PAYS")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "NOM_PAYS")
	private String nom;
	
	
	@OneToMany(mappedBy="pays")
	List<Ville> listVilles;
	
	@ManyToOne
	@JoinColumn(name = "FK_DEVISE")
	private Devise devise;
	
	@ManyToMany(cascade={CascadeType.PERSIST},fetch= FetchType.LAZY)
	@JoinTable(name="link_langue_pays",joinColumns = @JoinColumn(name = "FK_PAYS"), 
									   inverseJoinColumns = @JoinColumn(name = "FK_LANGUE"))
	private Set<Langue> setLangues;
	
	
	public Pays(){};
	
	
	
	public Pays(Long id, String nom, List<Ville> listVilles, Devise devise,Set<Langue> setLangues) {
		super();
		this.id = id;
		this.nom = nom;
		this.listVilles = listVilles;
		this.devise = devise;
		this.setLangues = setLangues;
	}



	// ******** Getter & Setter **********
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public List<Ville> getListVilles(){
		return listVilles;
	}
	public void setListVilles(List<Ville> aList){
		this.listVilles = aList;
	}
	
	public Devise getDevise() {
		return devise;
	}
	public void setDevise(Devise aDevise) {
		this.devise = aDevise;
	}

	public Set<Langue> getSetLangues() {
		return setLangues;
	}
	public void setSetLangues(Set<Langue> langues) {
		this.setLangues = langues;
	}
	@Transient
    public List<Langue> getLanguesList() {
        return new ArrayList<Langue>(setLangues);
    }
	
	//----------------------------------------------------------------------
	// Redéfinition des méthodes "techniques"
	// ---------------------------------------------------------------------
	
	/**
     * <p>On redéfinit la méthode toString() pour avoir un affichage plus 
     * parlant (en logs, dans le débuggeur...).</p>
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append("Object :: [ ");
    	sb.append(id+", ");
    	sb.append(nom);
    	sb.append(" ]");
    	
        return new ToStringBuilder(this).append(sb.toString()).toString();
    	//return nom;
    }
    
    /**
	 * <p>
	 * Deux object sont identiques s'ils ont le même id.
	 * </p>
	 * <p>
	 * Il faut systématiquement redéfinir la méthode hashCode() avec les mêmes critères fonctionnels!
	 * </p>
	 * 
	 * @param aObject l'objet de comparaison
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Pays other = (Pays) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

}
