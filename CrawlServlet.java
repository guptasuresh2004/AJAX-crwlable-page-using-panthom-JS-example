package com.citrixosd.utils;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingFilterScope;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
* Simple servlet filter component that serves incoming requests from google crawlers.
*/
@SlingFilter(generateComponent = false, generateService = true, order = -700, scope = SlingFilterScope.REQUEST)
@Component(immediate = true, metatype = false)
public class CrawlServlet  implements Filter {
    
    private Logger logger = LoggerFactory.getLogger(CrawlServlet .class);

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
    	 SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
    	 String queryString= slingRequest.getQueryString();
    	if ((queryString != null) && (queryString.contains("_escaped_fragment_"))) {
    		logger.info("Request from Crawler-->"+queryString);
    	      
    		   String url_with_hash_fragment=slingRequest.getRequestURI();
    	       // rewrite the URL back to the original #! version
    	       // remember to unescape any %XX characters
    		   url_with_hash_fragment= URLDecoder.decode(url_with_hash_fragment, "UTF-8");
    		   String url_with_escaped_fragment= HTMLSnippetHelper.getURL(slingRequest);
    	       url_with_hash_fragment = HTMLSnippetHelper.rewriteQueryString(url_with_escaped_fragment);

    	       HTMLSnippetHelper HTMLSnippetHelper = new HTMLSnippetHelper();
    	       String html =HTMLSnippetHelper.getHTMLSnippet(url_with_hash_fragment);
    	      
    	       PrintWriter out =response.getWriter();

    	       // return the snapshot
    	       out.println(html);
    	     } 
    	

        chain.doFilter(request, response);
    }

    public void destroy() {
    }

}
