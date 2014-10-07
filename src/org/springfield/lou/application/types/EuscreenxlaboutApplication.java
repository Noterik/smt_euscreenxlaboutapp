package org.springfield.lou.application.types;

import org.springfield.lou.application.Html5Application;
import org.springfield.lou.screen.Screen;

public class EuscreenxlaboutApplication extends Html5Application{
		
 	public EuscreenxlaboutApplication(String id) {
		super(id); 
		
		this.addReferid("mobilenav", "/euscreenxlelements/mobilenav");
		this.addReferid("header", "/euscreenxlelements/header");
		this.addReferid("footer", "/euscreenxlelements/footer");
		this.addReferid("terms", "/euscreenxlelements/terms");
		this.addReferid("linkinterceptor", "/euscreenxlelements/linkinterceptor");
		
		this.addReferidCSS("daniel", "/euscreenxlelements/generic");
		this.addReferidCSS("bootstrap", "/euscreenxlelements/bootstrap");
		
	}
 	
 	public void initializeScreen(Screen s){
 		s.putMsg("header", "", "setActivePage(about)");
 	}
 	
 	public String getFavicon() {
        return "/eddie/apps/euscreenxlelements/img/favicon.png";
    }

}
