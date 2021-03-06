package net.it4myself.util.tests;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.it4myself.util.RestfulClient;
import android.test.AndroidTestCase;
import android.util.Log;

/*
 * adb shell am instrument -w net.it4myself.util.tests/android.test.InstrumentationTestRunner 
 * adb shell am instrument -w -e class net.it4myself.util.tests.RestfulRailsTest net.it4myself.util.tests/android.test.InstrumentationTestRunner 
 * adb shell am instrument -w -e class net.it4myself.util.tests.RestfulRailsTest#testShouldGetListAndGetDOM net.it4myself.util.tests/android.test.InstrumentationTestRunner 
 */

public class RestfulClientTest extends AndroidTestCase {
	private static String HOST = "http://192.168.10.181:3000"; // TODO: replace to your test server's IP
	private static String TAG = "RestfulTest";
	private DocumentBuilder builder;
	
    protected void setUp() {
    	RestfulClient.basicAuthUsername = "";
    	RestfulClient.basicAuthPassword = "";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//factory.setNamespaceAware(true);
		//factory.setValidating(true);
		//factory.setIgnoringElementContentWhitespace(true);
		try {
			//factory.setFeature("http://xml.org/sax/features/validation", true);
			builder = factory.newDocumentBuilder();
			deleteAll();
		} catch (ParserConfigurationException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		}
		
    }

    public void testShouldPostAndGetString(){
    	HashMap<String,String> params = new HashMap<String,String>();
    	params.put("user[key]", "11");
    	params.put("user[name]", "postTestForString");
    	params.put("from", "unittest"); // this is a marker. not use.
    	try {
			String result = RestfulClient.Post(HOST + "/users.xml", params);
			Log.v(TAG, result);
			assertTrue(null != result);
			return;
		} catch (ClientProtocolException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		}
		assertTrue(false);
    }

    public void testShouldPostAndGetDOM(){
    	HashMap<String,String> params = new HashMap<String,String>();
    	String userKey = "12";
    	params.put("user[key]", userKey);
    	params.put("user[name]", "postTestForDOM");
    	params.put("from", "unittest"); // this is a marker. not use.
    	boolean foundKey = false;
    	try {
    		Document result = RestfulClient.Post(HOST + "/users.xml", params, builder);
			Log.v(TAG, result.toString());
    		NodeList list = result.getDocumentElement().getChildNodes();
    		Node node;
    		for (int i=0; null != (node = list.item(i)); i++) {
    			if(node.getNodeName().equals("key")){
    				assertEquals(userKey, node.getFirstChild().getNodeValue());
    				foundKey = true;
    			}
    		}
			assertTrue(foundKey);
			return;
		} catch (ClientProtocolException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		}
		assertTrue(false);
    }

    public void testShouldGetListAndGetString() {
    	try {
    		String result = RestfulClient.Get(HOST + "/users.xml", null);
    		Log.v(TAG, result);
    		assertTrue(null != result);
			return;
		} catch (ClientProtocolException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		}
		assertTrue(false);
    }
    
    public void testShouldGetListAndGetStringUsingBasicAuth() {
    	RestfulClient.basicAuthUsername = "";
    	RestfulClient.basicAuthPassword = "";
    	try {
    		RestfulClient.Get(HOST + "/userbasics.xml", null);
    		assertTrue(false);
		} catch (ClientProtocolException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		}
		assertTrue(true);
    	RestfulClient.basicAuthUsername = "hoge";
    	RestfulClient.basicAuthPassword = "";
    	try {
    		RestfulClient.Get(HOST + "/userbasics.xml", null);
    		assertTrue(false);
		} catch (ClientProtocolException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		}
		assertTrue(true);
    	RestfulClient.basicAuthUsername = "basic";
    	RestfulClient.basicAuthPassword = "pass";
    	try {
    		String result = RestfulClient.Get(HOST + "/userbasics.xml", null);
    		Log.v(TAG, result);
    		assertTrue(null != result);
			return;
		} catch (ClientProtocolException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		}
		assertTrue(false);
    }

    
    public void testShouldGetListAndGetDOM() {
    	try {
    		deleteAll();
        	HashMap<String,String> params = new HashMap<String,String>();
        	params.put("user[key]", "31");
        	params.put("user[name]", "forList1");
        	params.put("from", "unittest"); // this is a marker. not use.
        	RestfulClient.Post(HOST + "/users.xml", params);
        	params.put("user[key]", "32");
        	params.put("user[name]", "forList2");
        	RestfulClient.Post(HOST + "/users.xml", params);
        	params.put("user[key]", "33");
        	params.put("user[name]", "forList3");
        	RestfulClient.Post(HOST + "/users.xml", params);

        	Document result = RestfulClient.Get(HOST + "/users.xml", null, builder);
    		
        	// for included empty nodes
        	loggingDocument(result.getDocumentElement(), 0);
    		Node removedNode = RestfulClient.RemoveEmptyNodes(result.getDocumentElement());
    		
    		// for removed empty nodes
    		loggingDocument(removedNode, 0);
    		
    		NodeList list = removedNode.getChildNodes();
    		assertEquals(3, list.getLength());
    		Node target = list.item(0);
    		assertEquals("user", target.getNodeName());
    		assertEquals("31", target.getChildNodes().item(2).getFirstChild().getNodeValue());
			return;
		} catch (ClientProtocolException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		}
/*		catch (DOMException e){
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		}
*/		assertTrue(false);
    }

    public void testShouldGetRecoadAndGetString() {
    	try {
        	HashMap<String,String> params = new HashMap<String,String>();
        	params.put("user[key]", "41");
        	params.put("user[name]", "forGetRecord1");
        	params.put("from", "unittest"); // this is a marker. not use.
        	String postedId = getIdString(RestfulClient.Post(HOST + "/users.xml", params, builder), "post");

    		String result = RestfulClient.Get(HOST + "/users/" + postedId + ".xml", null);
			Log.v(TAG, result);
    		assertTrue(null != result);
			return;
		} catch (ClientProtocolException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		}
		assertTrue(false);
    }

    public void testShouldGetRecoadAndGetDOM() {
    	try {
        	HashMap<String,String> params = new HashMap<String,String>();
        	params.put("user[key]", "51");
        	params.put("user[name]", "forGetRecord1");
        	params.put("from", "unittest"); // this is a marker. not use.
        	String postedId = getIdString(RestfulClient.Post(HOST + "/users.xml", params, builder), "post");

        	Document result = RestfulClient.Get(HOST + "/users/" + postedId + ".xml", null, builder);
        	// loggingDocument(result, 0);
    		
    		Node removedNode = RestfulClient.RemoveEmptyNodes(result.getDocumentElement());
    		loggingDocument(removedNode, 0);

    		NodeList list = removedNode.getChildNodes();
    		assertEquals(5, list.getLength());
    		Node target = list.item(1);
    		assertEquals("id", target.getNodeName());
    		assertEquals(postedId, target.getFirstChild().getNodeValue());
			return;
		} catch (ClientProtocolException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		}
		assertTrue(false);
    }
    
    public void testShouldPutAndGetString(){
    	try {
        	HashMap<String,String> params = new HashMap<String,String>();
        	params.put("user[key]", "61");
        	params.put("user[name]", "forPutRecord1");
        	params.put("from", "unittest"); // this is a marker. not use.
        	Document postResult = RestfulClient.Post(HOST + "/users.xml", params, builder);
        	String postedId = getIdString(postResult, "post");

        	params.put("user[id]", postedId);
        	params.put("user[key]", "61");
        	params.put("user[name]", "forPutRecord1Modified");

        	Document putResult = RestfulClient.Put(HOST + "/users/" + postedId + ".xml", params, builder);
        	loggingDocument(putResult, 0);
        	
    		String result = RestfulClient.Get(HOST + "/users/" + postedId + ".xml", params);
			Log.v(TAG, "get agein:" + result);
    		assertTrue(null != result);
			return;
		} catch (ClientProtocolException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		}
		assertTrue(false);
    }

    public void testShouldPutAndGetDOM(){
    	try {
        	HashMap<String,String> params = new HashMap<String,String>();
        	params.put("user[key]", "62");
        	params.put("user[name]", "forPutRecord1");
        	params.put("from", "unittest"); // this is a marker. not use.
        	Document postResult = RestfulClient.Post(HOST + "/users.xml", params, builder);
        	String postedId = getIdString(postResult, "post");

        	params.put("user[id]", postedId);
        	String modifedName = "forPutRecord2Modified";
        	params.put("user[name]", modifedName);

        	Document putResult = RestfulClient.Put(HOST + "/users/" + postedId + ".xml", params, builder);
        	loggingDocument(putResult, 0);
        	
        	Document result = RestfulClient.Get(HOST + "/users/" + postedId + ".xml", null, builder);
        	// loggingDocument(result, 0);
    		
    		Node removedNode = RestfulClient.RemoveEmptyNodes(result.getDocumentElement());
    		loggingDocument(removedNode, 0);
    		
    		NodeList list = removedNode.getChildNodes();
    		assertEquals(5, list.getLength());
    		assertEquals("id", list.item(1).getNodeName());
    		assertEquals(postedId, list.item(1).getFirstChild().getNodeValue());
    		assertEquals("62", list.item(2).getFirstChild().getNodeValue());
    		assertEquals(modifedName, list.item(3).getFirstChild().getNodeValue());
			return;
		} catch (ClientProtocolException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		}
		assertTrue(false);
    }

    public void testShouldDeleteAll(){
    	try {
        	HashMap<String,String> params = new HashMap<String,String>();
        	params.put("user[key]", "1");
        	params.put("user[name]", "fordelete1");
        	params.put("from", "unittest"); // this is a marker. not use.
        	RestfulClient.Post(HOST + "/users.xml", params);
        	params.put("user[key]", "2");
        	params.put("user[name]", "fordelete2");
        	RestfulClient.Post(HOST + "/users.xml", params);
        	params.put("user[key]", "3");
        	params.put("user[name]", "fordelete3");
        	RestfulClient.Post(HOST + "/users.xml", params);
    		
        	deleteAll();

    		Document afterRecord = RestfulClient.Get(HOST + "/users.xml", null, builder);
    		if(afterRecord.getDocumentElement().hasChildNodes()){
    			assertTrue(false);
    		}else{
    			assertTrue(true);
    		}
			return;
		} catch (ClientProtocolException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			Log.v(TAG, e.getMessage());
			e.printStackTrace();
		}
		assertTrue(false);
    }
    
    private void deleteAll() throws ClientProtocolException, IOException, SAXException{
		Document allRecord = RestfulClient.Get(HOST + "/users.xml", null, builder);
		NodeList list = allRecord.getDocumentElement().getChildNodes();
		Node node1;
		for (int i=0; null != (node1 = list.item(i)); i++) {
			if(node1.getNodeName().equals("user")){
	    		NodeList userColumns = node1.getChildNodes();
	    		Node node2;
	    		for (int ii=0; null != (node2 = userColumns.item(ii)); ii++) {
	    			if(node2.getNodeName().equals("id")){
	    				RestfulClient.Delete(HOST + "/users/" + node2.getFirstChild().getNodeValue() + ".xml", null);
	    				break;
	    			}
	    		}
			}
		}
    }
    
    private String getIdString(Document targetDocument, String when){
    	Log.v(TAG, "in getIdString at:" + when);
    	loggingDocument(targetDocument, 0);
		NodeList postedList = targetDocument.getDocumentElement().getChildNodes();
		Node postedNode;
		String postedId = "";
		for (int i=0; null != (postedNode = postedList.item(i)); i++) {
			if(postedNode.getNodeName().equals("id")){
				postedId =  postedNode.getFirstChild().getNodeValue();
				break;
			}
		}
		Log.v(TAG, "Id:" + postedId);
    	return postedId;
    }
    
    public void loggingDocument(Node node, int depth) {
        StringBuilder sb = new StringBuilder();
        String name = node.getNodeName();
        for (int i = 0; i < depth; i++) {
            sb.append(" ");
        }
        short nodeType = node.getNodeType();
        sb.append(name).append('(').append(nodeType).append(')');
        if(Node.TEXT_NODE == nodeType || Node.DOCUMENT_NODE == nodeType){
        	sb.append('[').append(node.getNodeValue()).append(']');
        }
    	Log.v(TAG, sb.toString());
        NodeList list = node.getChildNodes();
        int n = list.getLength();
        for (int i = 0; i < n; i++) {
            Node child = list.item(i);
            loggingDocument(child, depth + 2);
        }
    }

}
