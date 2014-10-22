package org.springfield.lou.application.types;

import org.springfield.lou.application.Html5Application;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.lou.screen.Screen;

public class EuscreenxlaboutApplication extends Html5Application{
		
 	public EuscreenxlaboutApplication(String id) {
		super(id); 
		
		this.addReferid("mobilenav", "/euscreenxlelements/mobilenav");
		this.addReferid("header", "/euscreenxlelements/header");
		this.addReferid("footer", "/euscreenxlelements/footer");
		this.addReferid("terms", "/euscreenxlelements/terms");
		this.addReferid("linkinterceptor", "/euscreenxlelements/linkinterceptor");
		this.addReferid("analytics", "/euscreenxlelements/analytics");
		
		this.addReferidCSS("fontawesome", "/euscreenxlelements/fontawesome");
		this.addReferidCSS("bootstrap", "/euscreenxlelements/bootstrap");
		this.addReferidCSS("theme", "/euscreenxlelements/theme");
		this.addReferidCSS("genericadditions", "/euscreenxlelements/generic");
		this.addReferidCSS("all", "/euscreenxlelements/all");
		this.addReferidCSS("terms", "/euscreenxlelements/terms");
		
	}
 	
 	private boolean inDevelMode() {
    	return LazyHomer.inDeveloperMode();
    }
 	
 	public void initializeScreen(Screen s){
 		s.putMsg("header", "", "setActivePage(about)");
 		if(!this.inDevelMode()){
			s.putMsg("linkinterceptor", "", "interceptLinks()");
		} else {
			s.removeContent("linkinterceptor");
		}
 	}
 	
 	public String getFavicon() {
        return "/eddie/apps/euscreenxlelements/img/favicon.png";
    }

}
