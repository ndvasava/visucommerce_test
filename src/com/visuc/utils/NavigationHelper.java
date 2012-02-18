package com.visuc.utils;

//Imports
//---------

//Standard
import java.util.HashMap;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.springframework.util.StringUtils;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
* <p>
* Ensemble de m�thodes utilitaires li�es � la navigation dans une application Jsf.
* </p>
* 
* <p>
* Cette classe a pour vocation d'�tre utilis�e dans les fichier pages.xml (ou xxx.pages.xml) de Seam.
* </p>
* 
* <p>
* Synth�se des fonctionnalit�s de cette classe :
* </p>
* <ol>
* <li> gestion retour sur une page jsf dynamique (navigation <a href="#inter-fonctionnalites">inter-fonctionnalit�s</a>).</li>
* </ol>
* 
* <p>
* <a name="inter-fonctionnalites">Cette classe permet de stocker dans le cas d'une navigation interfonctionnalit�s la
* page d'o� l'on vient</a>, afin de retourner sur cette derni�re via un bouton "Retour".
* </p>
* Elle est appel�e dans le "pages.xml" comme suit : <br/>
* 
* <pre>
* &lt;page view-id=&quot;/views/contrat/recherche_contrat.xhtml&quot;&gt;
*     &lt;action execute=&quot;#{navigationHelper.add('clientFrom','/views/contrat/recherche_contrat.xhtml')}&quot;/&gt;
*     &lt;restrict&gt;#{s:hasRole('EDUWSPH CONINT')}&lt;/restrict&gt;  
*     &lt;navigation from-action=&quot;#{rechercherContratAction.reset}&quot;&gt;
*         &lt;render view-id=&quot;/views/contrat/recherche_contrat.xhtml&quot;/&gt;
*     &lt;/navigation&gt;
*     &lt;navigation from-action=&quot;#{rechercherContratAction.view}&quot;&gt;
*         &lt;begin-conversation nested=&quot;true&quot;/&gt;
*         &lt;render view-id=&quot;/views/contrat/detail_contrat.xhtml&quot;/&gt;
*     &lt;/navigation&gt;          
*     &lt;navigation from-action=&quot;#{rechercherContratAction.viewClient}&quot;&gt;
*         &lt;begin-conversation nested=&quot;true&quot;/&gt;
*         &lt;render view-id=&quot;/views/client/detail_client.xhtml&quot;/&gt;
*     &lt;/navigation&gt;       
* &lt;/page&gt;
* </pre>
* 
* Une remarque : dans l'exemple ci-dessus, cette action
* 
* <pre>
*  
* &lt;action execute=&quot;#{navigationHelper.add('clientFrom','/views/contrat/recherche_contrat.xhtml')}&quot;/&gt;
* </pre>
* 
* n'a de sens que pour la derni�re navigation :
* 
* <pre>
*  &lt;navigation from-action=&quot;#{rechercherContratAction.viewClient}&quot;&gt;
* </pre>
* 
* Cependant, Seam ne nous permet pas de positionner l'action sous la balise navigation !
* 
* <p>
* Au niveau de la page de la description associ�e � cette navigation cible, on trouve :
* </p>
* 
* <pre>
*  &lt;navigation from-action=&quot;goListe&quot;&gt;
*    &lt;end-conversation/&gt;
*    &lt;render view-id=&quot;#{empty navigationHelper['clientFrom'] ? '/views/client/recherche_client.xhtml' : navigationHelper['clientFrom']}&quot;/&gt;
*  &lt;/navigation&gt;
* </pre>
* 
*/
@Name("navigationHelper")
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
@Install(precedence = Install.FRAMEWORK)
@SuppressWarnings("serial")
public class NavigationHelper extends HashMap<String, String> {

	/**
	 * <p>
	 * Cette m�thode est n�cessaire car la m�thode {@link #put(String, String)} de HashMap retourne la valeur, donc ici
	 * <code>aViewId</code>
	 * <p>
	 * Et alors SEAM s'emploie � recharger la page <code>aViewId</code> dont l'appel dans le pages.xml refait un ...
	 * {@link #put(String, String)}, ce qui provoque une belle boucle infinie.
	 * </p>
	 * @param aCode String
	 * @param aViewId String
	 */
	public void add(String aCode, String aViewId) {
		super.put(aCode, aViewId);
	}

	/**
	 * <p>
	 * M�thode identique � {@link #add(String, String)}. Elle stocke la page courante. Elle est donc �quivalente �
	 * l'appel � :
	 * </p>
	 * 
	 * <pre>
	 * add(aCode, FacesContext.getCurrentInstance().getViewRoot().getViewId());
	 * </pre>
	 * 
	 * <p>
	 * Elle �vite principalement la duplication de valeur dans pages.xml :
	 * 
	 * <pre>
	 * 	 &lt;page view-id=&quot;/views/client/detail_client.xhtml&quot;&gt;
	 * 	 &lt;action execute=&quot;#{navigationHelper.add('chargeFrom','/views/client/detail_client.xhtml')}&quot;/&gt;
	 * 	
	 * </pre>
	 * 
	 * <p>
	 * Devient :
	 * </p>
	 * 
	 * <pre>
	 * 	 &lt;page view-id=&quot;/views/client/detail_client.xhtml&quot;&gt;
	 * 	 &lt;action execute=&quot;#{navigationHelper.addCurrentViewId('chargeFrom')}&quot;/&gt;
	 * 	
	 * </pre>
	 * 
	 * @param aCode code logique de la page de retour
	 * @exception IllegalStateException si la page courante est null ou vide (ce qui est impossible si la classe est
	 * ex�cut�e en contexte Jsf).
	 */
	public void addCurrentViewId(String aCode) {
		String lViewId = getViewId();
		if (!StringUtils.hasLength(lViewId)) {
			throw new IllegalStateException("addCurrentViewId : Erreur interne, viewId courant est null.");
		}
		add(aCode, lViewId);
	}

	/**
	 * <p>
	 * Retourne le viewId courant.
	 * </p>
	 */
	private String getViewId() {
		UIViewRoot lViewRoot = getViewRoot();
		return (lViewRoot == null) ? null : lViewRoot.getViewId();
	}

	/**
	 * <p>
	 * Retourne le viewRoot courant.
	 * </p>
	 */
	private UIViewRoot getViewRoot() {
		return FacesContext.getCurrentInstance().getViewRoot();
	}
}
