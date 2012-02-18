package com.visuc.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * <p>Cette classe permet d'ajaxifier un peu plus simplement les modalPanel.</p>
 * 
 * <p>L'ajaxification d'un modalPanel permet principalement de minimiser la taille de la
 * page HTML g�n�r�e (au minimum 5ko par modalPanel).</p>
 *   
 * <p>Cette classe est ni l'�quivalent d'une hashMap 
 * qui contient la liste des modalPanel ajax devant �tre affich�es.
 * La port�e de cette classe est conversation (elle fonctionne donc en conversation longue par d�faut).</p>
 * <p>Exemple d'utilisation :
 *
 * </p>
 */
@SuppressWarnings("serial")
@Name("modalPanelAction")
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class ModalPanelAction implements Serializable {
	
	private Map<String,Boolean> modalPanelRendered = new HashMap<String,Boolean>();

	public Map<String, Boolean> getModalPanelRendered() {
		return modalPanelRendered;
	}

	public void setModalPanelRendered(Map<String, Boolean> aModalPanelRendered) {
		modalPanelRendered = aModalPanelRendered;
	}
	
	/**
	 * <p>Demande l'affichage de la ou des modalPanel dont les id sont aModelPanel.</p>
	 * 
	 * <p>Dans le cas o� on veut afficher plusieurs modalPanel, il suffit de s�parer
	 * les id par des ','.</p>
	 * 
	 * <p>Cela a pour effet de bord de 
	 * cacher les autres modalPanels g�r�s par cet action.</p>
	 * 
	 * @param aModalPanel [obligatoire]
	 */
	public void showModalPanel(String aModalPanel) {
		if (aModalPanel != null) {
			String[] lPanels = aModalPanel.split(",");
			modalPanelRendered.clear();
			for (String lPanel : lPanels) {
				modalPanelRendered.put(lPanel.trim(), true);
			}
		}
	}
	
	/**
	 * <p>Demande de cacher la ou des modalPanel dont les id sont aModelPanel.</p>
	 * 
	 * <p>Dans le cas o� on veut afficher plusieurs modalPanel, il suffit de s�parer
	 * les id par des ','.</p>
	 * 
	 * @param aModalPanel [obligatoire]
	 */
	public void hideModalPanel(String aModalPanel) {
		if (aModalPanel != null) {
			String[] lPanels = aModalPanel.split(",");
			for (String lPanel : lPanels) {
				modalPanelRendered.remove(lPanel.trim());
			}
		}
	}
}


