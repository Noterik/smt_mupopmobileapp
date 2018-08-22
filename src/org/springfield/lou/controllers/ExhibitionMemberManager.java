package org.springfield.lou.controllers;

import java.util.HashMap;

import org.springfield.fs.FsNode;
import org.springfield.lou.screen.Screen;

public class ExhibitionMemberManager {

    public static FsNode getMember(Screen s) {
    	FsNode member = s.getModel().getNode("/shared/exhibition/member/"+s.getModel().getProperty("@exhibitionid")+"/"+s.getBrowserId());
    	String name = member.getProperty("name");
    	if (name==null && name.equals("")) {
    		return null;
    	}
    	return member;
    }
    
    public static FsNode claimMember(Screen s,String username) { 
    	FsNode member = s.getModel().getNode("/shared/exhibition/member/"+s.getModel().getProperty("@exhibitionid")+"/"+s.getBrowserId());
    	String name = member.getProperty("name");
    	if (name==null && name.equals("")) {
    		member.setProperty("name",username);
    		return member;
    	} else {
    		return null;
    	}
    }
    
}
