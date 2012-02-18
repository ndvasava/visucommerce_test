/**
 * Gestionnaires de fermeture et d'ouverture des bandeaux Haut et Gauche.
 * Remplace les <rich:effect> dans les pages.
 * @return {Object}
 * @constructor
 * 
 * @author Lionel VITSE - Micropole Univers - 02/2010 
 */
function Bandeaux(){

	// +-------------------------+
	// | Gestion du bandeau Haut |
	// +-------------------------+
	
	// Bloc visible ou non
	var B_CLI_HIDE_OR_NOT_ID='bCliHideOrNot';
	
	// ID HTML de la barre de fermeture du bandeau Haut
	var BANDEAU_HAUT_FERMER_ID = 'bandeauHautFermer';
	
	// ID HTML de la barre d'ouverture du bandeau Haut
	var BANDEAU_HAUT_OUVRIR_ID = 'bandeauHautOuvrir';	
	
	// Durée de l'ouverture et fermeture du bandeau	
	var defaultDuration = {duration:0.25};

	// +---------------------------+
	// | Gestion du bandeau Gauche |
	// +---------------------------+
	
	// ID HTML du bloc menu	
	var MENU_GAUCHE_ID = 'menuGauche';
	
	// ID HTML du bloc corps de page	
	var CORPS_DE_PAGE_ID = 'corpsDePage';	
	
	// Classe CSS par défaut du bloc corps de page lorsque le bandeau gauche est ouvert
	var CORPS_DE_PAGE_CSS_CLASS = 'corpsDePage';	
	
	// Classe CSS du bloc corps de page lorsque le bandeau gauche est fermé	
	var CORPS_DE_PAGE_WITHOUT_BANDEAU_GAUCHE_CSS_CLASS = 'corpsDePageWithoutBandeauGauche';		

	// ID HTML de la barre de fermeture du bandeau gauche
	var BANDEAU_GAUCHE_FERMER_ID = 'bandeauGaucheFermer'

	// ID HTML de la barre d'ouverture du bandeau gauche
	var BANDEAU_GAUCHE_OUVRIR_ID = 'bandeauGaucheOuvrir'
	
	// +-------------------------------------------------------------------------------------------+
	// | Actions sur les bandeaux et dialogue Ajax avec com.natixis.sphinx.jsf.util.BandeauxAction |
	// +-------------------------------------------------------------------------------------------+

	var bandeaux = {
		gauche_ouvert_id:'bandeauxForm:gaucheOuvert',
		haut_ouvert_id  :'bandeauxForm:hautOuvert',
		changerAction   :'bandeauxForm:changer', 

		changerGauche : 
			/**
			 * Envoi au serveur du changement d'état du bandeau gauche.
			 * @param {boolean} aOuvert état du bandeau : true pour ouvert, false pour fermé 
			 */		
			function (aOuvert)
			{
				this.changer(this.gauche_ouvert_id,aOuvert);
			},
			
		changerHaut : 
			/**
			 * Envoi au serveur du changement d'état du bandeau haut.
			 * @param {boolean} aOuvert état du bandeau : true pour ouvert, false pour fermé
			 */			
			function (aOuvert)
			{
				this.changer(this.haut_ouvert_id,aOuvert);
			},
			
		changer :
			/**
			 * Envoi au serveur du changement d'état d'un bandeau.
			 * @param {String}  aBandeauEtatId id du champ HTML contenant l'état du bandeau
			 * @param {boolean} aOuvert état du bandeau : true pour ouvert, false pour fermé
			 */
			function (aBandeauEtatId, aOuvert){
				$(aBandeauEtatId).value=aOuvert;
				$(this.changerAction).click();		
			}
	};
	

	/**
	 * Fermer le bandeau du haut.
	 * @return {void}
	 */
	this.fermerHaut = function(){
		//alert('bandeauHautFermer ...');
		Effect.BlindUp(B_CLI_HIDE_OR_NOT_ID,defaultDuration);
		Effect.Fade(BANDEAU_HAUT_FERMER_ID,defaultDuration);
		Effect.Appear(BANDEAU_HAUT_OUVRIR_ID,defaultDuration);
		
		bandeaux.changerHaut(false);
	};

	/**
	 * Ouvrir le bandeau du haut.
	 * @return {void}
	 */
	this.ouvrirHaut = function(){
		//alert('bandeauHautFermer ...');
		Effect.BlindDown(B_CLI_HIDE_OR_NOT_ID,defaultDuration);
		Effect.Fade(BANDEAU_HAUT_OUVRIR_ID,defaultDuration);
		Effect.Appear(BANDEAU_HAUT_FERMER_ID,defaultDuration);
		
		bandeaux.changerHaut(true);
	};

	/**
	 * Fermer le bandeau gauche.
	 * @return {void}
	 */
	this.fermerGauche=function(){	
		Effect.BlindUp(MENU_GAUCHE_ID,{scaleX:true,scaleY:false,duration:0.25});
		$(CORPS_DE_PAGE_ID).morph(CORPS_DE_PAGE_WITHOUT_BANDEAU_GAUCHE_CSS_CLASS,{ duration: 0.40 });
		Effect.Fade(BANDEAU_GAUCHE_FERMER_ID,{duration:0.00});
		Effect.Appear(BANDEAU_GAUCHE_OUVRIR_ID,{duration:0.00});
		
		bandeaux.changerGauche(false);
	};

	/**
	 * Ouvrir le bandeau gauche.
	 * @return {void}
	 */

	this.ouvrirGauche=function(){
		//alert('bandeauGaucheOuvrir ...');
		Effect.BlindDown(MENU_GAUCHE_ID,{scaleX:true,scaleY:false,duration:0.00});
		$(CORPS_DE_PAGE_ID).className=CORPS_DE_PAGE_CSS_CLASS;
		Effect.Fade(BANDEAU_GAUCHE_OUVRIR_ID,{duration:0.00});
		Effect.Appear(BANDEAU_GAUCHE_FERMER_ID,{duration:0.00});
		
		bandeaux.changerGauche(true);
	};
}

var bandeauxCaisseEpargne = new Bandeaux();


