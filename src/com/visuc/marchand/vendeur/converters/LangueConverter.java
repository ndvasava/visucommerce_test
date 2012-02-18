package com.visuc.marchand.vendeur.converters;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.visuc.referentiel.langue.entite.Langue;

public class LangueConverter implements Converter {

	private Map<String, Object> languesMap;
	
	@Override
	public Object getAsObject(FacesContext pFacesContext, UIComponent pUIComp, String label) {
		return languesMap.get(label);
	}

	@Override
	public String getAsString(FacesContext pFacesContext, UIComponent pUIComp, Object langueObj) {
		
		Langue langue = (Langue) langueObj;
		String langueLabel = langue.getNom();
		return langueLabel;
	}

	public void setLangueMap(Map<String, Object> pLangueMap) {
		this.languesMap = pLangueMap;
	}
}
