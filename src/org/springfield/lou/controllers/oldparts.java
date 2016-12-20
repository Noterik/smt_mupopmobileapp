package org.springfield.lou.controllers;


//get languages from the languages
//FSList languageList = model.getList("@languages");

//model.onNotify("@exhibition/intro/languageselection", "onLanguageSelected", this);

//model.onNotify("@exhibition/entryscreen/requested", "onEntryScreenRequested", this);


/*
//only show languageselection when multiple languages are configured
if (languageList != null && languageList.size() > 1) {
    //only show language selection when the user has not language selected earlier or the selected
    //language is not available in this exhibition
    if (userLanguage == null || languageList.getNodesById(userLanguage).size() == 0) {
    	if (model.getProperty("@exhibitionid").equals("1475504815025")){
    		screen.get("#mobile").append("div", "languageselectionmupopremote", new LanguageSelectionRemoteControllerMupop());
    	}else{
    		screen.get("#mobile").append("div", "languageselectionremote", new LanguageSelectionRemoteController());
    	}
    } else {
	stationSelection();
    }
} else {
    if (languageList != null) {
    	
    		//Set user language to the only language available
    		System.out.println("LANGUAGE LIST SIZE="+languageList.size());
    		System.out.println("LANGUAGE PATH="+model.getProperty("@languages"));
    		model.setProperty("@userlanguage", languageList.getNodes().get(0).getId());
    	
    } else {
	System.out.println("No languages set! Please add at least one language!");
    }
    stationSelection();
}
*/

/*
public void onEntryScreenRequested(ModelEvent e) {
FsNode target = (FsNode) e.target;

if (model.getProperty("@exhibitionid").equals(target.getProperty("exhibition"))) {
    System.out.println("Removing current mobile page due to mainscreen reload");
    screen.get("#languageselectionremote").remove();
    screen.get("#audiotestremote").remove();
    screen.get("#coverflowremote").remove();
    screen.get("#audiotestremote").remove();
    screen.get("#interactivevideoremote").remove();
}
}
*/

/*
public void onStationSelected(ModelEvent e) {
	FsNode target = e.getTargetFsNode();

	//only reach device that triggered this event
	if (!target.getProperty("deviceid").equals(deviceid)) {
		return;
	}

	if (target.getId().equals("selected")) {		
		screen.get("#stationselectionremote").remove();
    
		String exhibitionpath  = model.getProperty("/screen/exhibitionpath");
		FsNode stationnode = model.getNode(exhibitionpath+"/station/"+(String)target.getProperty("stationid"));	
	
		if (stationnode!=null) {
			model.setProperty("@stationid", stationnode.getId());
			//openApp();
	
		} else {
			//TODO: should show some illegal station controller with urls to all the valid ones?
			screen.get("#mobile_content").html("");
		}
	}	
}
*/

/*
public void onLanguageSelected(ModelEvent e) {
	System.out.println("LANGUAGE SELECTION EVENT!!!!");
	FsNode target = e.getTargetFsNode();
	System.out.println("LANGUAGE PAGE REQUESTED");
	//only reach device that triggered this event
	if (!target.getProperty("deviceid").equals(deviceid)) {
	    return;
	}
		
	if (target.getId().equals("selected")) {
	    screen.get("#languageselectionmupopremote").remove();
	    screen.get("#languageselectionremote").remove();
	    stationSelection();
	}
}
*/

/*
public void onLanguagePageRequested(ModelEvent e) {
	FsNode target = e.getTargetFsNode();
    
	//only reach device that triggered this event
	if (!target.getProperty("deviceid").equals(deviceid)) {
		return;
	}
	
if (target.getId().equals("requested")) {
    String originalController = target.getProperty("originalcontroller");

    screen.get("#"+originalController).remove();
    if (model.getProperty("@exhibitionid").equals("1475504815025")){
		screen.get("#mobile").append("div", "languageselectionmupopremote", new LanguageSelectionRemoteControllerMupop());
	}else{
		screen.get("#mobile").append("div", "languageselectionremote", new LanguageSelectionRemoteController());
	}
    model.onNotify("/shared/exhibition/intro/languageselection", "onLanguageSelected", this);
}
}
*/