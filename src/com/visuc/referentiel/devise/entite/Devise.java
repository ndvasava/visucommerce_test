package com.visuc.referentiel.devise.entite;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.visuc.referentiel.pays.entite.Pays;

@Entity
@SuppressWarnings("serial")
@Table(name="REF_DEVISE")
public class Devise implements Serializable {
	
	@Id
	@Column(name = "ID_DEVISE")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	/** Pour l'exemple, on specifie ci-dessous le mode de déclaration explicite d'un type simple.
	 *  => Nom est une propriété persistante et à charger immédiatement. 2 stratégies de fetch existe (qui influe sur la performance du système et la charge de requete) : 
	 * 		-> EAGER : la valeur est toujours chargée (valeur par défaut si association en ToOne)
	 *  	-> LAZY  : la valeur est chargée uniquement lors de son utilisation (Valeur par défaut si association en ToMany)
	 */
	@Column(name = "NOM_DEVISE")
	//@Basic(fetch= FetchType.LAZY) // LAZY non pris en compte par défaut sur types simples, attention, Basic se refere a un type simple, sinon feth doit etre porte par @OneToOne etc ...
	private String nom;
	
	@Column(name = "SYMBOLE_DEVISE")
	private String symbole;
	
	@OneToMany(mappedBy="devise")
	private List<Pays> listPays;
	
	/* Permet de gérer les acces concurrents à une meme donées par 2 utilisateurs dans une appli web */
	@Column(name = "VERSION", nullable = false)
	@Version
	private long version;

	
	public Devise(){};
	
	public Devise(Long id, String nom, String symbole, List<Pays> listPays) {
		super();
		this.id = id;
		this.nom = nom;
		this.symbole = symbole;
		this.listPays = listPays;
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
	
	public String getSymbole() {
		return symbole;
	}
	public void setSymbole(String symbole) {
		this.symbole = symbole;
	}
	
	public List<Pays> getListPays(){
		return listPays;
	}
	public void setListPays(List<Pays> aList){
		this.listPays = aList;
	}
	
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long aVersion) {
		this.version = aVersion;
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
		final Devise other = (Devise) obj;
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
