package org.membase.perf.config;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConfigParser {
	
	public static PerfTestCaseList parse() {
		PerfTestCaseList testcases = new PerfTestCaseList();
		try {
			File file = new File("/Users/mikewied/Desktop/testset.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			
			NodeList nodeLst = doc.getElementsByTagName("setup");
			for (int i = 0; i < nodeLst.getLength(); i++) {
				Node fstNode = nodeLst.item(i);
				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					NodeList serverList = ((Element) fstNode).getElementsByTagName("membase");
					Element serverElem = (Element) serverList.item(0);
					testcases.setMembaseVersion(serverElem.getAttribute("version"));
					testcases.setVersionNumber(serverElem.getAttribute("number"));
					serverList = ((Element) fstNode).getElementsByTagName("moxi");
					serverElem = (Element) serverList.item(0);
					testcases.setMoxiVersion(serverElem.getAttribute("version"));
				}
			}
			
			nodeLst = doc.getElementsByTagName("test");
			for (int i = 0; i < nodeLst.getLength(); i++) {
				Node fstNode = nodeLst.item(i);
				PerfTestCase ptc = new PerfTestCase();
				ptc.clazz = ((Element)fstNode).getAttribute("class");
				ptc.export_path = ((Element)fstNode).getAttribute("export");
				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					NodeList serverList = ((Element) fstNode).getElementsByTagName("sinfo");
					
					for (int j = 0; j < serverList.getLength(); j++) {
						Element serverElem = (Element) serverList.item(j);
						ptc.sc.put(serverElem.getAttribute("name"), serverElem.getAttribute("value"));
					}
					serverList = ((Element) fstNode).getElementsByTagName("param");
					for (int j = 0; j < serverList.getLength(); j++) {
						Element serverElem = (Element) serverList.item(j);
						ptc.tc.put(serverElem.getAttribute("name"), serverElem.getAttribute("value"));
					}
					testcases.add(ptc);
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return testcases;
	}
}
