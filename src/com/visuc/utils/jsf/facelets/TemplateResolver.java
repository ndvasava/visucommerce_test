package com.visuc.utils.jsf.facelets;

import java.net.URL;

import org.apache.log4j.Logger;

import com.sun.facelets.impl.DefaultResourceResolver;
import com.sun.facelets.impl.ResourceResolver;

/**
 * This facelets resource resolver allows us to put facelet files in jars 
 * on the classpath, as well as the context root of the webapp.
 *
 * Because JSF automatically finds faces-config.xml in your jar files, it provides 
 *	a neat opportunity for building a simple jar-based module system for your webapps. 
 * All you need is a way to bundle your templates and resources right? Well, 
 *  thanks to Facelets, and Furnace it is actually very easy.
 * Facelets lets you implement your own ResourceResolver which is used to find 
 *  templates. 
 * The following simple implementation extends the default resolver to check the 
 *  classpath for the required template.
 *
 * @author roger
 */
public class TemplateResolver extends DefaultResourceResolver implements ResourceResolver {
	
    private Logger log = Logger.getLogger(getClass().getName());
    
    /** first check the context root, then the classpath */
    public URL resolveUrl(String path) {
        log.debug("Resolving URL " + path);
        
        URL url = super.resolveUrl(path);
        if (url == null) {
            
            /* classpath resources don't start with / */
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            url = Thread.currentThread().getContextClassLoader().
                    getResource(path);
        }
        return url;
    }
}
