var bandeauxCaisseEpargne = null;

// jQuery va exécuter cette fonction lorsque la page sera chargée par le navigateur. Plus précisément, lorsque le DOM sera chargé.
// jQuery(document).ready(function...) pour équivalent à window.onload
// http://www.babylon-design.com/site/index.php/2008/01/21/211-apprendre-et-comprendre-jquery-1-3
// http://www.lesintegristes.net/2007/06/11/inauguration-et-introduction-a-jquery/

jQuery(function (){
	bandeauxCaisseEpargne = new Bandeaux();
	bandeauxCaisseEpargne.initializeRender();
	return;
});

/**
 * Gestionnaires de fermeture et d'ouverture des bandeaux Haut et Gauche via jQuery.
 * Remplace les <rich:effect> dans les pages.
 *
 * Utilisation de la fonction jQuery() au lieu de $() qui pour l'instant est attribué à prototypejs. 
 * 
 * @return {Object}
 * @constructor
 * 
 * @author Lionel VITSE - Micropole Univers - 02/2010 
 */

function Bandeaux(){

	// +-------------------------+
	// | Gestion du bandeau Haut |
	// +-------------------------+
	
	// Zone d'informations client dans le bandeau haut visible ou non
	var B_CLI_HIDE_OR_NOT_ID='#bCliHideOrNot';
	
	// ID HTML de la barre de fermeture du bandeau Haut
	var BANDEAU_HAUT_FERMER_ID = '#bandeauHautFermer';
	
	// ID HTML de la barre d'ouverture du bandeau Haut
	var BANDEAU_HAUT_OUVRIR_ID = '#bandeauHautOuvrir';	
	
	// +---------------------------+
	// | Gestion du bandeau Gauche |
	// +---------------------------+
	
	// ID HTML du bloc menu	
	var MENU_GAUCHE_ID = '#menuGauche';
	
	// ID HTML de la barre de fermeture du bandeau gauche
	var BANDEAU_GAUCHE_FERMER_ID = '#bandeauGaucheFermer';

	// ID HTML de la barre d'ouverture du bandeau gauche
	var BANDEAU_GAUCHE_OUVRIR_ID = '#bandeauGaucheOuvrir';

	// ID HTML du bloc corps de page	
	var CORPS_DE_PAGE_ID = '#corpsDePage';	
	
	// Classe CSS par défaut du bloc corps de page lorsque le bandeau gauche est ouvert
	var CORPS_DE_PAGE_CSS_CLASS = 'corpsDePage';	
	
	// Classe CSS du bloc corps de page lorsque le bandeau gauche est fermé	
	var CORPS_DE_PAGE_WITHOUT_BANDEAU_GAUCHE_CSS_CLASS = 'corpsDePageWithoutBandeauGauche';		

	// ID du formulaire HTML mémorisant l'état des bandeaux haut et gauche
	var BANDEAU_FORM_ID	= "#bandeauxForm";	

	// Corps de page : marge haute lorsque le bandeau gauche est ouvert
	var corpsDePageMarginLeftHigh;
	
	// Corps de page : marge basse lorsque le bandeau gauche est ouvert
	var corpsDePageMarginLeftLow;	
	
	/**
	 * Initialisation des bandeaux.
	 * - Evénements "click"
	 * - Affichage 
	 */

	this.initializeRender = function ()
	{	
		// +-------------------------------+
		// | Initialisation des évènements |	
		// +-------------------------------+
		
		jQuery(BANDEAU_HAUT_FERMER_ID).click(this.fermerHaut);
		jQuery(BANDEAU_HAUT_OUVRIR_ID).click(this.ouvrirHaut);

		jQuery(BANDEAU_GAUCHE_FERMER_ID).click(this.fermerGauche);
		jQuery(BANDEAU_GAUCHE_OUVRIR_ID).click(this.ouvrirGauche);

		// Un click détecté sur la zone du menu ( MENU_GAUCHE_ID) déclenche 
		// la mise à jour de l'affichage de la hauteur du bandeau gauche.
		jQuery(MENU_GAUCHE_ID).click(function (aEvent) {bandeaux.renderGauche();});

		// +-------------------------------+
		// | Initialisation de l'affichage |	
		// +-------------------------------+

		bandeaux.renderGauche();

	};
	
	// +-------------------------------------------------------------------------------------------+
	// | Actions sur les bandeaux et dialogue Ajax avec com.natixis.sphinx.jsf.util.BandeauxAction |
	// +-------------------------------------------------------------------------------------------+

	var bandeaux = {
		// http://api.jquery.com/category/selectors/
		// (#;&,.+*~':"!^$[]()=>|/ ) : you must escape the character with two backslashes: \\
		// Autre facon d'écrire les identifiants
		// GAUCHE_OUVERT_ID	:"input[id='bandeauxForm:gaucheOuvert']",
		// HAUT_OUVERT_ID	:"input[id='bandeauxForm:hautOuvert']",

		// ID du champ HTML hidden mémorisant l'état du bandeau haut
		HAUT_OUVERT_ID		:"#bandeauxForm\\:hautOuvert",
		
		// ID du champ HTML hidden mémorisant l'état du bandeau gauche
		GAUCHE_OUVERT_ID	:"#bandeauxForm\\:gaucheOuvert",
	
		/**
		 * Mise à jour de la hauteur des bandeaux gauches [fermer] et [ouvrir]
		 * avec la hauteur du menu.
		 * @return {void}		 
		 */
		renderGauche : function () {	
			var lMenuGaucheHeight = jQuery(MENU_GAUCHE_ID).height();
			jQuery(BANDEAU_GAUCHE_FERMER_ID).height(lMenuGaucheHeight);
			jQuery(BANDEAU_GAUCHE_OUVRIR_ID).height(lMenuGaucheHeight);
		},

		/**
		 * Envoi au serveur du changement d'état du bandeau gauche.
		 * @param {boolean} aOuvert état du bandeau : true pour ouvert, false pour fermé 
		 * @return {void}
		 */		
		changerGauche : function (aOuvert) {
			this.changer(this.GAUCHE_OUVERT_ID,aOuvert);
		},
			
		/**
		 * Envoi au serveur du changement d'état du bandeau haut.
		 * @param {boolean} aOuvert état du bandeau : true pour ouvert, false pour fermé
		 * @return {void}		 
		 */			
		changerHaut : function (aOuvert) {
			this.changer(this.HAUT_OUVERT_ID,aOuvert);
		},
			
		/**
		 * Envoi au serveur du changement d'état d'un bandeau.
		 * @param {String}  aBandeauEtatId id du champ HTML contenant l'état du bandeau
		 * @param {boolean} aOuvert état du bandeau : true pour ouvert, false pour fermé
		 * @return {void}
		 */
		changer : function (aBandeauEtatId, aOuvert){
			jQuery(aBandeauEtatId).val(aOuvert);				

			var lBandeauxForm = jQuery(BANDEAU_FORM_ID);
			var lParams = lBandeauxForm.serializeArray();
			lParams[lParams.length]={name:'bandeauxForm:changer',value:'changer'};
			
			var lAction = lBandeauxForm[0].action;				
			jQuery.post(lAction, lParams);
		}
	};
		
	/**
	 * Fermer le bandeau du haut.
	 * @return {void}
	 */
	this.fermerHaut = function(){		
		bandeaux.changerHaut(false);
	
		jQuery(B_CLI_HIDE_OR_NOT_ID).slideToggle();
		jQuery(BANDEAU_HAUT_FERMER_ID).hide(0);
		jQuery(BANDEAU_HAUT_OUVRIR_ID).show(0);
	};

	/**
	 * Ouvrir le bandeau du haut.
	 * @return {void}
	 */
	this.ouvrirHaut = function(){
		bandeaux.changerHaut(true);
		
		jQuery(B_CLI_HIDE_OR_NOT_ID).slideToggle();
		jQuery(BANDEAU_HAUT_FERMER_ID).show(0);
		jQuery(BANDEAU_HAUT_OUVRIR_ID).hide(0);
				
	};

	/**
	 * Fermer le bandeau gauche.
	 * @return {void}
	 */
	this.fermerGauche=function(){	
		bandeaux.changerGauche(false);
	
		jQuery(MENU_GAUCHE_ID).hide(500);
		jQuery(BANDEAU_GAUCHE_FERMER_ID).hide(0);
		jQuery(BANDEAU_GAUCHE_OUVRIR_ID).show(0);

		// Mise à jour du formulaire pour récupérer ensuite le marginLeft
		// à appliquer à CORPS_DE_PAGE_ID.
		jQuery(BANDEAU_FORM_ID).addClass(CORPS_DE_PAGE_WITHOUT_BANDEAU_GAUCHE_CSS_CLASS);
		if (!corpsDePageMarginLeftLow){
			corpsDePageMarginLeftLow = jQuery(BANDEAU_FORM_ID).css('marginLeft');
		}
		
		jQuery(CORPS_DE_PAGE_ID).animate({marginLeft: corpsDePageMarginLeftLow},500 );
			
	};

	/**
	 * Ouvrir le bandeau gauche.
	 * @return {void}
	 */

	this.ouvrirGauche=function(){
		bandeaux.changerGauche(true);
		
		jQuery(BANDEAU_GAUCHE_FERMER_ID).show(0);
		jQuery(BANDEAU_GAUCHE_OUVRIR_ID).hide(0);
		
		// On force le marginLeft de CorpsDePage avec celui de CORPS_DE_PAGE_WITHOUT_BANDEAU_GAUCHE_CSS_CLASS
		// avant de retirer la classe CORPS_DE_PAGE_WITHOUT_BANDEAU_GAUCHE_CSS_CLASS.
		var lMarginLeft = jQuery(CORPS_DE_PAGE_ID).css('margin-left');
		jQuery(CORPS_DE_PAGE_ID).css('margin-left',lMarginLeft);
		jQuery(CORPS_DE_PAGE_ID).removeClass(CORPS_DE_PAGE_WITHOUT_BANDEAU_GAUCHE_CSS_CLASS);		
		
		// Mise à jour du formulaire pour récupérer ensuite le marginLeft
		// à appliquer à CORPS_DE_PAGE_ID.
		jQuery(BANDEAU_FORM_ID).removeClass(CORPS_DE_PAGE_WITHOUT_BANDEAU_GAUCHE_CSS_CLASS);
		if (!corpsDePageMarginLeftHigh){
			corpsDePageMarginLeftHigh = jQuery(BANDEAU_FORM_ID).css('marginLeft');
		}
		
		jQuery(CORPS_DE_PAGE_ID).animate({marginLeft: corpsDePageMarginLeftHigh},500 );	
		jQuery(MENU_GAUCHE_ID).show(500);
	};
}
