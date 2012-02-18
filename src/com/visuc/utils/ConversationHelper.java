package com.visuc.utils;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.persistence.ManagedPersistenceContext;

/**
 * <p>
 * Ensemble de m�thodes utilitaires li�es � la gestion de la conversation dans
 * une application Jsf.
 * </p>
 * 
 * <p>
 * Cette classe a pour vocation d'�tre utilis�e dans les fichier pages.xml (ou
 * xxx.pages.xml) de Seam.
 * </p>
 * 
 * <p>
 * Synth�se des fonctionnalit�s de cette classe :
 * </p>
 * <ol>
 * <li> terminer la conversation racine et de ses conversations filles -
 * {@link #endRootConversation()}.</li>
 * <li> modification du flushMode Jpa - {@link #changeFlushMode(String)}.</li>
 * </ol>
 */
@Name("conversationHelper")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
public class ConversationHelper {

	/** Commons-logging Logger. */
	private static final Log logger = LogFactory.getLog(ConversationHelper.class);

	/**
	 * <p>
	 * Terminer la conversation racine et ses conversations filles
	 * </p>
	 * 
	 * <p>
	 * Cette fonctionnalit� nous para�t fondamentale pour toute application Seam
	 * se basant sur l'utilisation des conversations.<br/> Cette fonctionnalit�
	 * sera utilis� pour 'vidanger' toutes les conversations de l'application �
	 * chaque d�but de processus.<br/> Qu'est ce que nous appelons ici
	 * processus ?<br/> Un processus est une interaction compl�te avec un
	 * utilisateur. Lorsque le processus d�bute, il est important qu'il
	 * n'attende aucune information (i.e. pas d'id d'un objet ou autre).<br/>
	 * Bref, lorsqu'on d�but un processus, on peut vidanger tout le contenu des
	 * conversations utilis�es par les processus pr�c�dents.<br/> Quand est ce
	 * qu'on d�marre un processus ?<br/> Typiquement, on le d�marrera lorsque
	 * l'utilisateur cliquera sur le menu principal de l'application. Il suffira
	 * de lier chaque lien du menu principal � une URL virtuelle commencant par
	 * un mot cl� quelconque (en g�n�ral on utilisera le pr�fixe '/global'), et
	 * d'indiquer dans le fichier de configuration pages.xml la configuration
	 * suivante :
	 * </p>
	 * 
	 * <pre>
	 *  &lt;page view-id=&quot;/global/*&quot;&gt;
	 *    &lt;action execute=&quot;#{conversationHelper.endRootConversation}&quot;/&gt;
	 *  &lt;/page&gt;
	 * </pre>
	 */
	public void endRootConversation() {
		boolean lDebugEnabled = logger.isDebugEnabled();
		if (lDebugEnabled) {
			logger.debug("endRootConversation -> conversationId : " + Conversation.instance().getId());
		}
		Conversation lConversation = Conversation.instance();
		if (lConversation.isNested()) {
			lConversation.root();
		}
		lConversation.endBeforeRedirect();
		if (lDebugEnabled) {
			logger.debug("endRootConversation <- conversationId : " + Conversation.instance().getId());
		}
	}

	/**
	 * <p>
	 * Permet de changer le flushMode de la conversation courante.
	 * </p>
	 * 
	 * <p>
	 * Similaire � l'appel de la m�thode
	 * <code>Conversation.instance().changeFlushMode(lFlushModeType)</code>.
	 * </p>
	 * 
	 * <p>
	 * Il est conseill� d'utiliser syst�matiquement le flushMode � MANUAL.
	 * </p>
	 * 
	 * <p>
	 * Exemple d'utilisation dans une application seam (fragment pages.xml) :
	 * </p>
	 * 
	 * <pre>
	 * 	 &lt;page view-id=&quot;/global/*&quot;&gt;
	 * 	 &lt;action execute=&quot;#{conversationHelper.changeFlushMode('manual')}&quot;/&gt;
	 * 	 &lt;/page&gt;
	 * </pre>
	 * 
	 * @param aFlushMode
	 *            [obligatoire] une valeur parmi AUTO, MANUAL, COMMIT (la case
	 *            n'est pas importante).
	 * @exception IllegalArgumentException
	 *                si la valeur de aFlushMode est invalide.
	 */
	public void changeFlushMode(String aFlushMode) {
		String lFlushModeAsString = (aFlushMode == null ? null : aFlushMode.toUpperCase());
		FlushModeType lFlushModeType = FlushModeType.valueOf(lFlushModeAsString);
		if (lFlushModeType == null) {
			throw new IllegalArgumentException("argument flushMode invalide (" + aFlushMode 
					+ ") - seules les valeurs AUTO, MANUAL, COMMIT sont accept�es.");
		}
		Conversation.instance().changeFlushMode(lFlushModeType);
	}

	/**
	 * <p>
	 * Vide la conversation en cours de tous ses attributs (sauf des attributs
	 * de type EntityManager et Session pour lesquels la m�thode fait clear
	 * dessus).
	 * </p>
	 */
	public void clearMyConversation() {
		boolean lDebugEnabled = logger.isDebugEnabled();
		if (lDebugEnabled) {
			logger.debug("clearConversation -> conversationId : " + Conversation.instance().getId());
		}
		Context lContext = Contexts.getConversationContext();
		for (String lName : lContext.getNames()) {
			clearConversationAttribute(lName, lContext);
		}
		if (lDebugEnabled) {
			logger.debug("clearConversation <- conversationId : " + Conversation.instance().getId());
		}
	}

	/**
	 * <p>
	 * Identique � {@link #clearConversation()}, mais on revient d'abord sur la
	 * conversation racine et on nettoie la conversation racine (les
	 * conversations filles tomberont en timeout).
	 * </p>
	 */
	public void clearRootConversation() {
		boolean lDebugEnabled = logger.isDebugEnabled();
		if (lDebugEnabled) {
			logger.debug("clearRootConversation -> conversationId : " + Conversation.instance().getId());
		}
		Conversation lConversation = Conversation.instance();
		if (lConversation.isNested()) {
			lConversation.root();
		}
		clearMyConversation();
		if (lDebugEnabled) {
			logger.debug("clearRootConversation <- conversationId : " + Conversation.instance().getId());
		}
	}

	/**
	 * <p>
	 * Appel� par {@link #clearConversation()} pour chaque attribut de la
	 * conversation � nettoyer.
	 * </p>
	 * 
	 * <p>
	 * Si l'attribut est :
	 * <ul>
	 * <li> une Session Hibernate : appelle clear sur la session.</li>
	 * <li> un entityManager Jpa : appelle clear sur l'entityManager.</li>
	 * <li> un autre type d'objet : supprime l'objet de la conversation.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param aName
	 *            [obligatoire]
	 * @param aContext
	 *            [obligatoire] contexte de conversation.
	 */
	protected void clearConversationAttribute(String aName, Context aContext) {
		Object lValue = aContext.get(aName);
		if (lValue instanceof EntityManager) {
			((EntityManager) lValue).clear();
		} else if (lValue instanceof Session) {
			((Session) lValue).clear();
		} else if (lValue instanceof ManagedPersistenceContext) {
			try {
				((ManagedPersistenceContext) lValue).getEntityManager().clear();
			} catch (NamingException err) {
				logger.error ("Erreur lors de la purge de l'attribut de conversation {"
						+aName+","+lValue+"}, erreur ignor�e : "+err.toString(), err);
			} catch (SystemException err) {
				logger.error ("Erreur lors de la purge de l'attribut de conversation {"
						+aName+","+lValue+"}, erreur ignor�e : "+err.toString(), err);
			}
		} else if (shouldRemove(lValue)) {
			aContext.remove(aName);
		}
	}
	
	/**
	 * <p>M�thode appel�e par clearConversation pour d�terminer si la valeur
	 * aValue doit �tre supprim�e des attributs de conversations.</p>
	 *  
	 * @param aValue [obligatoire] un des attributs stock�s dans la conversation courante
	 * @return vrai si cet attribut doit �tre supprim�
	 */
	private boolean shouldRemove(Object aValue) {
		if (aValue instanceof FacesMessages) {
			return false;
		} else if (aValue instanceof Conversation) {
			return false;
		}
		return true;
	}
}

