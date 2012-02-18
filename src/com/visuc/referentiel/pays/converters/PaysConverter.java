package com.visuc.referentiel.pays.converters;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.visuc.referentiel.pays.entite.Pays;


public class PaysConverter implements Converter {

	private Map<String, Object> choiceMap;

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String label) {
		return choiceMap.get(label);
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object obj) {
		Pays pays = (Pays) obj;
		String label = pays.getNom();
		return label;
	}

	/**
	 * @param choiceMap, the choiceMap to set
	 */
	public void setChoiceMap(Map<String, Object> choiceMap) {
		this.choiceMap = choiceMap;
	}

}