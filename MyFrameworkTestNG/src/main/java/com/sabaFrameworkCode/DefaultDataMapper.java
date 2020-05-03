/* ================================================================= *
 *  (c) Copyright 1997-2003, Saba Software
 *  All Rights Reserved
 *  Company Confidential
 *
 *  Created on Dec 24, 2009
 * ================================================================= *
 */
package com.sabaFrameworkCode;


import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * This class is responsible for reading the default data files and storing the values into the <br>
 * defaultDataMap which is later used by XmlDataMapper to replace the referenced values
 *
 */
public class DefaultDataMapper {
	

	
	/**
	 * The default data entity and default data component files are read at the first point of referencing <br>
	 * for an entity or component key. This method does the following
	 * #Reads the entity and component file
	 * #Fills data into defaultDataMap
	 * @param defaultDataEntityFile - location of defaultDataEntityFile read from apitest.properties
	 * @param defaultDateComponentFile - location of defaultDateComponentFile read from apitest.properties
	 * @return
	 */
	public Map fillDefaultDataEntityMap(String defaultDataEntityFile, String defaultDateComponentFile) {

		Map defaultDataMap = new HashMap();

		readDefaultEntityFile(defaultDataMap,defaultDataEntityFile);
		readDefaultComponentFile(defaultDataMap,defaultDateComponentFile);

		return defaultDataMap;
	}
	
/**
 * Reads the component file given in fileName and fills the defaultDataMap
 * @param defaultDataMap - Map storing data provided from entity and component file
 * @param fileName
 */
	private void readDefaultComponentFile(Map defaultDataMap, String fileName){
		
		Map defMap = new HashMap();
		Node fileRoot=null;
		try {
			System.out.println(fileName);
			fileRoot = XmlDataMapper.getRootNode(new File(fileName));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        if(!(fileRoot.getNodeName().equals("DefaultDataEntity") || fileRoot.getNodeName().equals("DefaultDataFunctionality"))){
        	
      	  // error
  //      }
        NodeList defaultEntity = fileRoot.getChildNodes();
        
        for(int i=0;i<defaultEntity.getLength();i++){
      	  
      	  Node  currnode = defaultEntity.item(i);
      	  
      	  if(currnode.getNodeType()==1){
      	  
            List datasetNodes = XMLUtil.getStrictChildNodes(currnode, "dataset");
            Map dataSetNameMap = new HashMap();
            for(int j=0;j<datasetNodes.size();j++)
            {
            	Node dataSetNode = (Node) datasetNodes.get(j);

                            
                   List dataNodes =XMLUtil.getStrictChildNodes(dataSetNode, "data");

                        boolean hasDataNode = !dataNodes.isEmpty()?true:false;
                  
                    if(hasDataNode)
                    {
                        Iterator dataIter = dataNodes.iterator();
                        Map dataMap = new HashMap();
                       // System.out.println("Putting ==== "+currnode.getNodeName()+"."+XmlDataMapper.getAttributeValue(dataSetNode.getAttributes(),"name")+" ==> ");
                        while (dataIter.hasNext()) {
                      	  
                          Node dataNode = (Node) dataIter.next();
                          
                          fillDefaultDataTag(dataMap, dataNode);

                        }

                      //  dataSetMap.put(XmlDataMapper.getAttributeValue(dataSetNode.getAttributes(),"name"), dataMap);
                        
                        
                        dataSetNameMap.put(XmlDataMapper.getAttributeValue(dataSetNode.getAttributes(),"name"), dataMap);
                    }
                           
             }
           
            defMap.put(currnode.getNodeName(), dataSetNameMap);
           }

        }
        defaultDataMap.put(fileRoot.getNodeName(), defMap);
      
	}

	/**
	 * Reads the component file given in fileName and fills the defaultDataMap
	 * @param defaultDataMap - Map storing data provided from entity and component file
	 * @param fileName
	 */
	private void readDefaultEntityFile(Map defaultDataMap, String fileName){
		
		Node fileRoot=null;
		try {
			System.out.println(fileName);
			fileRoot = XmlDataMapper.getRootNode(new File(fileName));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        if(!(fileRoot.getNodeName().equals("DefaultDataEntity") || fileRoot.getNodeName().equals("DefaultDataFunctionality"))){
        	
      	  // error
  //      }
   //     NodeList defaultEntity = fileRoot.getChildNodes();
        
        //for(int i=0;i<defaultEntity.getLength();i++){
      	  
     // 	  Node  currnode = XMLUtil.getStrictChildNodes(fileRoot, "dataset"); 
      	  
      //	  if(currnode.getNodeType()==1){
      	  
            List datasetNodes = XMLUtil.getStrictChildNodes(fileRoot, "dataset");
            Map dataSetNameMap = new HashMap();
            for(int j=0;j<datasetNodes.size();j++)
            {
            	Node dataSetNode = (Node) datasetNodes.get(j);

                            
                   List dataNodes =XMLUtil.getStrictChildNodes(dataSetNode, "data");

                        boolean hasDataNode = !dataNodes.isEmpty()?true:false;
                  
                    if(hasDataNode)
                    {
                        Iterator dataIter = dataNodes.iterator();
                        Map dataMap = new HashMap();
                       // System.out.println("Putting ==== "+currnode.getNodeName()+"."+XmlDataMapper.getAttributeValue(dataSetNode.getAttributes(),"name")+" ==> ");
                        while (dataIter.hasNext()) {
                      	  
                          Node dataNode = (Node) dataIter.next();
                          
                          fillDefaultDataTag(dataMap, dataNode);

                        }
                      //  dataSetMap.put(XmlDataMapper.getAttributeValue(dataSetNode.getAttributes(),"name"), dataMap);
                        dataSetNameMap.put(XmlDataMapper.getAttributeValue(dataSetNode.getAttributes(),"name"), dataMap);
                    }
                           
             }
           
            defaultDataMap.put(fileRoot.getNodeName(), dataSetNameMap);
           
/*
        }
        defaultDataMap.put(fileRoot.getNodeName(), defMap); */
    
	}

	
	
	/**
	 * This method fills default data into the map, after reading the key value pairs and replacing any refs
	 * @param dataMap - data for current map
	 * @param dataNode - the data node for which key value pairs have to be extracted
	 * @return
	 */
	private void fillDefaultDataTag(Map dataMap, Node dataNode) {

		
        NamedNodeMap dataAttrs = dataNode.getAttributes();

        if(dataAttrs.getNamedItem("value")!=null){

        	
        	String value = XmlDataMapper.getAttributeValue(dataAttrs, "value");
        	dataMap.put(XmlDataMapper.getAttributeValue(dataAttrs, "key"), XmlDataMapper.handleSpecialValues(value));
        	
        	// dataMap.put(XmlDataMapper.getAttributeValue(dataAttrs, "key"), handleSpecialValues(value) );
        	// in case special values like current_date are needed to be handled.
        	
        	
         }else if(dataAttrs.getNamedItem("ref")!=null){
         		
         	String ref = XmlDataMapper.getAttributeValue(dataAttrs, "ref");
        	dataMap.put(XmlDataMapper.getAttributeValue(dataAttrs, "key"), "$"+ref+"$");

         }else{
         
	        	Map innerDataMap = new HashMap();
	        	List innerValues =XMLUtil.getStrictChildNodes(dataNode, "data");
	  
	        	if(innerValues.size()>0){
	        		//List innerDataNodes =XMLUtil.getStrictChildNodes((Node)values.get(0), "data");
		        	for(int i=0;i<innerValues.size();i++){
		        		
		        		Node curr = (Node)innerValues.get(i);
		        		NamedNodeMap currattrs = curr.getAttributes();
		        		fillDefaultDataTag(innerDataMap, curr);
		        	}
	        	}
	
	        	dataMap.put(XmlDataMapper.getAttributeValue(dataAttrs, "key"), innerDataMap );
         }
		
	
		
		
	}


}
