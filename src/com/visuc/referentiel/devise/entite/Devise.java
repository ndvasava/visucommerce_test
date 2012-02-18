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
	
	/** Pour l'exemple, on specifie ci-dessous le mode de d�claration explicite d'un type simple.
	 *  => Nom est une propri�t� persistante et � charger imm�diatement. 2 strat�gies de fetch existe (qui influe sur la performance du syst�me et la charge de requete) : 
	 * 		-> EAGER : la valeur est toujours charg�e (valeur par d�faut si association en ToOne)
	 *  	-> LAZY  : la valeur est charg�e uniquement lors de son utilisation (Valeur par d�faut si association en ToMany)
	 */
	@Column(name = "NOM_DEVISE")
	//@Basic(fetch= FetchType.LAZY) // LAZY non pris en compte par d�faut sur types simples, attention, Basic se refere a un type simple, sinon feth doit etre porte par @OneToOne etc ...
	private String nom;
	
	@Column(name = "SYMBOLE_DEVISE")
	private String symbole;
	
	@OneToMany(mappedBy="devise")
	private List<Pays> listPays;
	
	/* Permet de g�rer les acces concurrents � une meme don�es par 2 utilisateurs dans une appli web */
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
	// Red�finition des m�thodes "techniques"
	// ---------------------------------------------------------------------

	/**
     * <p>On red�finit la m�thode toString() pour avoir un affichage plus 
     * parlant (en logs, dans le d�buggeur...).</p>
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
	 * Deux object sont identiques s'ils ont le m�me id.
	 * </p>
	 * <p>
	 * Il faut syst�matiquement red�finir la m�thode hashCode() avec les m�mes crit�res fonctionnels!
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
