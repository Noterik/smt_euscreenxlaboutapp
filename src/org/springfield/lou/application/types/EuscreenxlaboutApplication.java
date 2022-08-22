package org.springfield.lou.application.types;

import org.json.simple.JSONObject;
import org.springfield.lou.application.Html5Application;
import org.springfield.lou.euscreen.config.Config;
import org.springfield.lou.euscreen.config.ConfigEnvironment;
import org.springfield.lou.euscreen.config.SettingNotExistException;
import org.springfield.lou.homer.LazyHomer;
import org.springfield.lou.screen.Screen;

public class EuscreenxlaboutApplication extends Html5Application{
	
	private Config config;
		
 	public EuscreenxlaboutApplication(String id) {
		super(id); 
		
		this.addReferid("mobilenav", "/euscreenxlelements/mobilenav");
		this.addReferid("header", "/euscreenxlelements/header");
		this.addReferid("footer", "/euscreenxlelements/footer");
		this.addReferid("terms", "/euscreenxlelements/terms");
		this.addReferid("linkinterceptor", "/euscreenxlelements/linkinterceptor");
		this.addReferid("analytics", "/euscreenxlelements/analytics");
		this.addReferid("config", "/euscreenxlelements/config");
		this.addReferid("urltransformer", "/euscreenxlelements/urltransformer");
		
		this.addReferidCSS("fontawesome", "/euscreenxlelements/fontawesome");
		this.addReferidCSS("bootstrap", "/euscreenxlelements/bootstrap");
		this.addReferidCSS("theme", "/euscreenxlelements/theme");
		this.addReferidCSS("genericadditions", "/euscreenxlelements/generic");
		this.addReferidCSS("all", "/euscreenxlelements/all");
		this.addReferidCSS("terms", "/euscreenxlelements/terms");
		this.addReferidCSS("jqvmap", "/euscreenxlelements/jqvmap");
		
		try{
		
			if(this.inDevelMode()){
				config = new Config(ConfigEnvironment.PROD);
			}else{
				config = new Config();
			}
		}catch(SettingNotExistException snee){
			snee.printStackTrace();
		}
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
 		
 		try {
 			this.loadContent(s, "config", "config");
 	 		this.loadContent(s, "urltransformer", "urltransformer");
			s.putMsg("config", "", "update(" + config.getSettingsJSON() + ")");
		} catch (SettingNotExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
 		DynamicMapContent.getLastChange();
 	}
 	
 	public String getFavicon() {
        return "/eddie/apps/euscreenxlelements/img/favicon.png";
    }
 	
 	@SuppressWarnings("unchecked")
	public void initMapTablet(Screen s){
 		System.out.println("initMap()");
 		
 		String regionCode = s.getParameter("code");
 		
 		JSONObject args = new JSONObject();
 		args.put("device", "tablet");
 		 		
 		if(regionCode != null){
 			args.put("region", regionCode);
 		}
 		
 		s.putMsg("template", "", "initMap(" + args + ")");
 	}
 	
 	@SuppressWarnings("unchecked")
	public void initMap(Screen s){
 		System.out.println("initMap()");
 		
 		String regionCode = s.getParameter("code");
 		
 		JSONObject args = new JSONObject();
 		
 		if(regionCode != null){
 			args.put("region", regionCode);		
 		}
 		
 		args.put("mapInfo", DynamicMapContent.getJsonMapData());	
 		s.putMsg("template", "", "initMap(" + args + ")");
 	};

}
