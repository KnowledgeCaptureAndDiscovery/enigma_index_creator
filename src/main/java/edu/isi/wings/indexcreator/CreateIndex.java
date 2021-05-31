/**
 * Copyright 2016 Daniel Garijo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package edu.isi.wings.indexcreator;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class loads one or multiple models into a single OntModel and produces
 * an html that serializes everything alphabetically.
 * @author dgarijo
 */
public class CreateIndex {
    
    /**
     * @param model
     * @param ontoPath
     * @param ontoURL 
     */
    private static void readModel(OntModel model, String path){
        String[] serializations = {"RDF/XML", "TTL", "N3"};
        String ontoPath = path;
        String ext = "";
        for(String s:serializations){
            InputStream in;
            try{
                in = FileManager.get().open(ontoPath);
                if (in == null) {
                    System.err.println("Error: Ontology file not found: "+path);
                    return;
                }
                model.read(in, null, s);
                System.out.println("Vocab loaded in "+s);
                if(s.equals("RDF/XML")){
                    ext="xml";
                }else if(s.equals("TTL")){
                    ext="ttl";
                }else if(s.equals("N3")){
                    ext="n3";
                }
                break;
            }catch(Exception e){
                System.err.println("Could not open the ontology in "+s);
            }
        }
        
    }
    
    public static void processModel(OntModel m, String docLink, String docTitle, HashMap<String, Entry> h){
        Iterator<Statement> it = m.listStatements();
        while(it.hasNext()){
            Statement current = it.next();
            String s = current.getSubject().getURI();
            if(s!=null && s.contains(Constants.namespace)){
                String entryName = current.getSubject().getURI().replace(Constants.namespace, "");
                if(!entryName.equals("")){
                    Entry e = new Entry(entryName, null,docLink, docTitle );
//                    System.out.print(current.getSubject().getLocalName()+" --- ");
                    Statement desc = m.getResource(s).getProperty(m.getAnnotationProperty("http://www.w3.org/2000/01/rdf-schema#comment"));
                    if(desc!=null){
//                        System.out.print(desc.getString());
                        e.setDescription(desc.getString());
                    }
//                    System.out.println();
                    if(!h.containsKey(entryName.toLowerCase())){
                        h.put(entryName.toLowerCase(), e);
                    }
                }
            }
        }
    }
    
    /**
     * Prints the merged model
     * @param args 
     */
//    public static void main(String[] args){
//        OntModel all = ModelFactory.createOntologyModel();
////    LINKED EARTH ONTOS
////        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\Ontology\\release\\core\\1.2.0\\ontology.ttl");
////        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\Ontology\\release\\sensor\\1.0.0\\ontology.ttl");
////        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\Ontology\\release\\observation\\1.0.0\\ontology.ttl");
////        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\Ontology\\release\\archive\\1.0.0\\ontology.ttl");
////        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\Ontology\\release\\instrument\\1.0.0\\ontology.ttl");
////        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\Ontology\\release\\inferredVariable\\1.0.0\\ontology.ttl");
////    ENIGMA ONTOS
////        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\release\\cohort\\1.0.0\\ontology.ttl");
////        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\release\\organization\\1.0.0\\ontology.ttl");
////        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\release\\person\\1.0.0\\ontology.ttl");
////        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\release\\project\\1.0.0\\ontology.ttl");
////        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\release\\upper\\1.0.0\\ontology.ttl");
////        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\release\\scanner\\1.0.0\\ontology.ttl");
////        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\release\\workingGroup\\1.0.0\\ontology.ttl");
////        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\release\\roles\\1.0.0\\ontology.ttl");
//// development version
//        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\development\\CohortOntology.owl");
//        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\development\\OrganizationOntology.owl");
//        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\development\\PersonOntology.owl");
//        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\development\\ProjectOntology.owl");
//        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\development\\ProtocolOntology.owl");
//        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\development\\RolesOntology.owl");
//        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\development\\ScannerOntology.owl");
//        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\development\\WorkingGroupOntology.owl");
//           
//    
//        all.write(System.out, "RDF/XML");
////        all.write(System.out, "TTL");
//    }
    public static void main(String[] args){
        
//Linked Earth Ontos
       /*        
        OntModel core = ModelFactory.createOntologyModel();
        OntModel sensor = ModelFactory.createOntologyModel();
        OntModel obs = ModelFactory.createOntologyModel();
        OntModel archive = ModelFactory.createOntologyModel();
        OntModel instrument = ModelFactory.createOntologyModel();
        OntModel inferredVariable = ModelFactory.createOntologyModel();
        //The ontology is an aggregation of ontologies separated in files.
         readModel(core, "C:\\Users\\dgarijo\\Documents\\GitHub\\Ontology\\release\\core\\1.2.0\\ontology.ttl");
        readModel(sensor, "C:\\Users\\dgarijo\\Documents\\GitHub\\Ontology\\release\\sensor\\1.0.0\\ontology.ttl");
        readModel(obs, "C:\\Users\\dgarijo\\Documents\\GitHub\\Ontology\\release\\observation\\1.0.0\\ontology.ttl");
        readModel(archive, "C:\\Users\\dgarijo\\Documents\\GitHub\\Ontology\\release\\archive\\1.0.0\\ontology.ttl");
        readModel(instrument, "C:\\Users\\dgarijo\\Documents\\GitHub\\Ontology\\release\\instrument\\1.0.0\\ontology.ttl");
        readModel(inferredVariable, "C:\\Users\\dgarijo\\Documents\\GitHub\\Ontology\\release\\inferredVariable\\1.0.0\\ontology.ttl");

        HashMap<String, Entry> entries = new HashMap();
        processModel(core, "core/1.2.0/index-en.html", "The Linked Earth LiPD Ontology", entries);
        processModel(sensor, "sensor/1.0.0/index-en.html", "The Proxy Sensor Ontology", entries);
        processModel(obs, "observation/1.0.0/index-en.html", "The Proxy Observation Type Ontology", entries);
        processModel(archive, "archive/1.0.0/index-en.html", "The Proxy Archive Ontology", entries);
        processModel(instrument, "instrument/1.0.0/index-en.html", "The Instrument Ontology", entries);
        processModel(inferredVariable, "inferredVariable/1.0.0/index-en.html", "The Inferred Variable Ontology", entries);*/
        
        //ENIGMA ONTOS (remember change the namespace constant!)
        
        OntModel cohort = ModelFactory.createOntologyModel();
        OntModel org = ModelFactory.createOntologyModel();
        OntModel person = ModelFactory.createOntologyModel();
        OntModel project = ModelFactory.createOntologyModel();
        OntModel core = ModelFactory.createOntologyModel();
        OntModel scanner = ModelFactory.createOntologyModel();
        OntModel wg = ModelFactory.createOntologyModel();
        OntModel roles = ModelFactory.createOntologyModel();
        readModel(cohort, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\release\\cohort\\1.1.0\\ontology.ttl");
        readModel(org, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\release\\organization\\1.0.0\\ontology.ttl");
        readModel(person, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\release\\person\\1.1.0\\ontology.ttl");
        readModel(project, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\release\\project\\1.1.0\\ontology.ttl");
        readModel(core, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\release\\core\\1.1.0\\ontology.ttl");
        readModel(scanner, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\release\\scanner\\1.0.0\\ontology.ttl");
        readModel(wg, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\release\\workingGroup\\1.1.0\\ontology.ttl");
        readModel(roles, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\release\\roles\\1.1.0\\ontology.ttl");
        
        HashMap<String, Entry> entries = new HashMap();
        processModel(cohort, "cohort/1.1.0/index-en.html", "The Cohort Ontology", entries);
        processModel(org, "organization/1.1.0/index-en.html", "The Organization Ontology", entries);
        processModel(person, "person/1.1.0/index-en.html", "The Person Ontology", entries);
        processModel(project, "project/1.1.0/index-en.html", "The Project Ontology", entries);
        processModel(core, "core/1.1.0/index-en.html", "The Core Ontology", entries);
        //processModel(scanner, "scanner/1.0.0/index-en.html", "The Scanner Ontology", entries);
        processModel(wg, "workingGroup/1.1.0/index-en.html", "The Working Group Ontology", entries);
        processModel(roles, "roles/1.1.0/index-en.html", "The Roles Ontology", entries);
        
        SortedSet<String> keys = new TreeSet<>(entries.keySet());
        String currentLetter=(""+keys.first().charAt(0)).toUpperCase();
        String nextLetter = "";
        String entryHTML="\n<h2 id=\""+currentLetter+"\">"+currentLetter+"</h2>\n";
        String overviewHTML="<p><a href=\"#"+currentLetter+"\">"+currentLetter+"</a> | ";
        for(String i:keys){
            nextLetter = (""+i.charAt(0)).toUpperCase();
            if(!nextLetter.equals(currentLetter)){
                currentLetter = nextLetter;
                entryHTML +="\n<h2 id=\""+currentLetter+"\">"+currentLetter+"</h2>\n";
                overviewHTML += "<a href=\"#"+currentLetter+"\">"+currentLetter+"</a> | ";
            }
//            System.out.println(entries.get(i).toHTML());
            entryHTML+=entries.get(i).toHTML()+"\n";
        }
        overviewHTML+="</p>";
        
        System.out.println(overviewHTML);
//        System.out.println("\n\n\n");
        System.out.println(entryHTML);
        
    }
    
}
