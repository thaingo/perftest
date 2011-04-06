package org.membase.perf.config;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.membase.perf.lib.MachineList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class HostsParser {
	private static final Logger LOG = LoggerFactory.getLogger(HostsParser.class);
	private String path;

	public HostsParser(String path) {
		this.path = path;
	}

	public MachineList parse() {
		MachineList mlist = new MachineList();
		try {
			File file = new File(path);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();

			NodeList nodeLst = doc.getElementsByTagName("ssh");
			for (int s = 0; s < nodeLst.getLength(); s++) {
				Node sshNode = nodeLst.item(s);
				if (sshNode.getNodeType() == Node.ELEMENT_NODE) {
					NodeList khNode = ((Element) sshNode)
							.getElementsByTagName("known_hosts");
					Element khElem = (Element) khNode.item(0);

					NodeList kfNode = ((Element) sshNode)
							.getElementsByTagName("key_file");
					Element kfElem = (Element) kfNode.item(0);

					NodeList khList = khElem.getChildNodes();
					NodeList kfList = kfElem.getChildNodes();

					mlist.setKnownHostFile(((Node) khList.item(0)).getNodeValue());
					mlist.setKeyFile(((Node) kfList.item(0)).getNodeValue());
					LOG.info("Added Known Hosts File: " + ((Node) khList.item(0)).getNodeValue());
					LOG.info("Added Key File: " + ((Node) kfList.item(0)).getNodeValue());
				}
			}

			nodeLst = doc.getElementsByTagName("servers");
			for (int i = 0; i < nodeLst.getLength(); i++) {
				Node fstNode = nodeLst.item(i);
				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					NodeList serverList = ((Element) fstNode).getElementsByTagName("server");
					for (int j = 0; j < serverList.getLength(); j++) {
						Element serverElem = (Element) serverList.item(j);
						String host = serverElem.getAttribute("host");
						String user = serverElem.getAttribute("user");
						String password = serverElem.getAttribute("password");
						mlist.addServer(host, user, password);
						LOG.info("Added Server: " + host + " " + user + " " + password);
					}
				}
			}
			
			nodeLst = doc.getElementsByTagName("clients");
			for (int i = 0; i < nodeLst.getLength(); i++) {
				Node fstNode = nodeLst.item(i);
				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					NodeList serverList = ((Element) fstNode).getElementsByTagName("client");
					for (int j = 0; j < serverList.getLength(); j++) {
						Element serverElem = (Element) serverList.item(j);
						String host = serverElem.getAttribute("host");
						String user = serverElem.getAttribute("user");
						String password = serverElem.getAttribute("password");
						mlist.addClient(host, user, password);
						LOG.info("Added Client: " + host + " " + user + " " + password);
					}
				}
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return mlist;
	}
}
