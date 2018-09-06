package org.springfield.lou.controllers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.util.Random;

import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.lou.screen.Screen;

public class ExhibitionMemberManager {
	
    public static FsNode getMember(Screen s) {
    	checkAvailableNameslist(s);
    	
    	FsNode member = s.getModel().getNode("/shared/exhibition/member/"+s.getModel().getProperty("@exhibitionid")+"/name/"+s.getBrowserId());
    	String name = member.getProperty("name");
    	if (name==null || name.equals("")) {
    		return null;
    	}
    	return member;
    }
    
    public static void freeAllNames(Screen s) {
       	List<FsNode> nodes = s.getModel().getList("/shared/exhibition/availablenames/"+s.getModel().getProperty("@exhibitionid")+"/").getNodes();
    		if (nodes != null) {
    			for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
    				FsNode namenode = iter.next();
    				namenode.setProperty("used","false");
    			}
    		}
    }
    
    public static String getNextFreeName(Screen s) {
    	List<FsNode> nodes = s.getModel().getList("/shared/exhibition/availablenames/"+s.getModel().getProperty("@exhibitionid")+"/").getNodes();
		if (nodes != null) {
			for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
				FsNode namenode = iter.next();
				System.out.println("NODE="+namenode);
				String used = namenode.getProperty("used");
				if (used==null || !used.equals("true")) {
					namenode.setProperty("used","true");
					return namenode.getId();
				}
			}
		}
		return null;	
    }
    
    public static void freeName(Screen s,String username) {
    	FsNode member = s.getModel().getNode("/shared/exhibition/availablenames/"+s.getModel().getProperty("@exhibitionid")+"/name/"+username);
    	member.setProperty("used", "false");
    }
    
    public static FsNode claimMember(Screen s,String username) { 
    	FsNode member = s.getModel().getNode("/shared/exhibition/availablenames/"+s.getModel().getProperty("@exhibitionid")+"/name/"+s.getBrowserId());
    	String name = member.getProperty("name");
    	if (name==null || name.equals("")) {
    		member.setProperty("name",username);
    		member.setProperty("score","0");	
    		member.setProperty("lastseen","");
    		member.setProperty("mastercount","0");
    		member.setProperty("master","waiting");
    		member.setProperty("lastmaster","");
    		return member;
    	} else {
    		return null;
    	}
    }
    
	public static void checkAvailableNameslist(Screen s) {
		int size=0;
		FSList l = s.getModel().getList("/shared/exhibition/availablenames/"+s.getModel().getProperty("@exhibitionid")+"/");
		if (l!=null) size=l.size();
		if (size==0) {
			String[] list = {"Bassie","Adriaan","Theo","Thea","Oma Knots","Swiebertje","Pipo de Clown","Mamalou","Rembo & Rembo","Purno de Purno",
					"Bert","Ernie","Elmo","Grover","Kermit de Kikker","Pino","Mumford","Koekiemonster","Troel","Tommy","Ieniemienie","Purk","Karbonkel",
					"Meneer Aart","Bor de Wolf","Meneer de Uil","Juffrouw Ooievaar","Lowieke de Vos","Ed Bever","Willem Bever","Gerrit de Postduif",
					"Meneer de Raaf","Zaza Zebra","Zuster Klivia","Gerrit de inbreker","De ingenieur","Bertus","Lorre","De Klisjeemannetjes","F. Jacobse",
					"Tedje van Es","Gé Temmes","Arie Temmes","Koos Koets","Doortje Lefèvre","Kootje de Beer","Lukas Blijschap","Lena Braams","Arie Balk",
					"Opoe Withof","Dinky","Fred Onderbuik","Ria Onderbuik","Bob de Rooy","Annie de Rooy","Tita Tovernaar","Willempie","Ome Joop","Jaap Aap"
			};
			for (int i=0;i<list.length;i++) {
				FsNode namenode = new FsNode("name",list[i]);
				s.getModel().putNode("/shared/exhibition/availablenames/"+s.getModel().getProperty("@exhibitionid"),namenode);
			}
		}
	}
	
    public static int getMemberCount(Screen s) {
    	FSList members = getActiveMembers(s,60);
    	if (members==null) return 0;
    	return members.size();
    }
    
    public static FSList getActiveMembers(Screen s,int expiretime) {
    	FSList list = s.getModel().getList("/shared/exhibition/member/"+s.getModel().getProperty("@exhibitionid"));
		List<FsNode> nodes = list.getNodes();
		FSList members = new FSList();
		long now = new Date().getTime();
		if (nodes != null) {
			for (Iterator<FsNode> iter = nodes.iterator(); iter.hasNext();) {
				FsNode node = (FsNode) iter.next();
				FsNode nnode = new FsNode("member",node.getId());
				String name = node.getProperty("name");
				String score = node.getProperty("score");
				String lastseen = node.getProperty("lastseen");

				try {
					long ls = Long.parseLong(lastseen);
					long dl = (now-ls)/1000;
					//System.out.println("LAST SEEN="+name+" TIME="+dl);
					if (dl<expiretime) {
						if (name!=null && !name.equals("")) {
							nnode.setProperty("name",name);
							if (score!=null && !score.equals("")) {
								nnode.setProperty("score",score);
							} else {
								nnode.setProperty("score","0");
							}
							nnode.setProperty("lastseen",lastseen);
							members.addNode(nnode);
						}
					}
				} catch(Exception e) {
					// if a error on parse we don't want this node anyway.
				}
			}
		}			
    	return members;
    }
    
}