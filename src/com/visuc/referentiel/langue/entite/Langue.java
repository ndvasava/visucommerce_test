package com.visuc.referentiel.langue.entite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.visuc.common.entity.Image;
import com.visuc.referentiel.pays.entite.Pays;


@Entity
@Table(name="REF_LANGUE")
@SuppressWarnings("serial")
public class Langue implements Serializable{

	/*TOPO sur ManyToMany en JPA
	 * 
	 * Exemple d'une relation manytoMany Personne <=> Activite, avec deux entités déclarées
	 * les @Entity Personne et Activite sont reliées par une relation ManyToMany. Comme dans la relation
	 *	OneToOne, il y a symétrie des entités dans cette relation. On peut choisir librement l'@Entity qui détiendra la
	 *	relation principale et celle qui aura la relation inverse. Ici, nous décidons que l'@Entity Personne aura la relation
	 *	principale
	 *
	 * 		// relation Personne (many) -> Activite (many) via une table de jointure personne_activite
	 *		// personne_activite(PERSONNE_ID) est clé étangère sur Personne(id)
	 *		// personne_activite(ACTIVITE_ID) est clé étangère sur Activite(id)
	 *		// cascade=CascadeType.PERSIST : persistance d'1 personne entraîne celle de ses activités
	 *		@ManyToMany(cascade={CascadeType.PERSIST})
	 *	 	@JoinTable(name="jpa08_hb_personne_activite",joinColumns = @JoinColumn(name = "PERSONNE_ID"), 
	 *													 inverseJoinColumns = @JoinColumn(name = "ACTIVITE_ID"))
	 *		private Set<Activite> activites = new HashSet<Activite>();
	 *
	 *		
	 *		// relation inverse Activite -> Personne
	 *		@ManyToMany(mappedBy = "activites")
	 * 		private Set<Personne> personnes = new HashSet<Personne>();
	 * 
	 */
	
	@Id
	@Column(name = "ID_LANGUE")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "NOM_LANGUE")
	private String nom;
	
	@Column(name = "CODE_ISO")
	private String codeIso;
	
	@OneToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="FK_IMAGE")
	private Image drapeau;

	// La relation sera porté par PAYS, et la relation inverse se trouvera définie dans LANGUE
	@ManyToMany(mappedBy = "setLangues",fetch= FetchType.LAZY)
	private Set<Pays> setPays;

	
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
	public void setNom(String aNom) {
		this.nom = aNom;
	}
	
	public String getCodeIso() {
		return codeIso;
	}
	public void setCodeIso(String aCodeIso) {
		this.codeIso = aCodeIso;
	}

	public Set<Pays> getSetPays() {
		return setPays;
	}
	public void setSetPays(Set<Pays> setPays) {
		this.setPays = setPays;
	}
	@Transient
    public List<Pays> getListPays() {
        return new ArrayList<Pays>(setPays);
    }
	
	public Image getDrapeau(){
		return this.drapeau;
	}
	public void setDrapeau(Image aImage){
		this.drapeau = aImage;
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
    	sb.append(nom+", ");
    	sb.append(codeIso);
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
		final Langue other = (Langue) obj;
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
