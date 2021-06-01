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
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.rdf.model.Model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class loads one or multiple models into a single OntModel and produces
 * an html that serializes everything alphabetically.
 * @author dgarijo
 */
public class CreateIndex {
    private static String getSerializationFromPath (String path) {
        String ext = path.contains(".") ? path.substring(path.lastIndexOf('.')) : "";

        String serialization;
        if (ext.equals(".ttl") || ext.equals(".TTL")) {
            serialization = "TTL";
        } else if (ext.equals(".n3") || ext.equals(".N3")) {
            serialization = "N3";
        } else {
            serialization = "RDF/XML";
        }
        return serialization;
    }

    /**
     * @param model where to store the model
     * @param path path of the ontology file
     */
    private static void readModel(OntModel model, String path) {
        String serialization = getSerializationFromPath(path);
        try {
            InputStream in = FileManager.get().open(path);
            if (in == null) throw new Exception("File not found: " + path);
            model.read(in, null, serialization);
        } catch (Exception e) {
            System.err.println("Could not open the ontology " + path);
        }
    }

    private static void readModel(Model model, String path) {
        String serialization = getSerializationFromPath(path);
        try {
            InputStream in = FileManager.get().open(path);
            if (in == null) throw new Exception("File not found: " + path);
            model.read(in, null, serialization);
        } catch (Exception e) {
            System.err.println("Could not open the ontology " + path);
        }
    }

    public static void exportRDFFile(String outFile, Model model, String mode){
        OutputStream out;
        try {
            out = new FileOutputStream(outFile);
            model.write(out,mode);
            out.close();
        } catch (Exception ex) {
            System.out.println("Error while writing the model to file "+ex.getMessage() + " oufile "+outFile);
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
                    Statement desc = m.getResource(s).getProperty(m.getAnnotationProperty("http://www.w3.org/2000/01/rdf-schema#comment"));
                    if (desc!=null) {
                        e.setDescription(desc.getString());
                    }
                    if (!h.containsKey(entryName.toLowerCase())) {
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
    public static void main(String[] args){
        if (args.length == 0) {
            System.out.println("USAGE: ./createIndex file.conf");
            return;
        }
        String inputConf = args[0];
        System.out.println("Reading " + inputConf);

        Path cpath = Paths.get(inputConf);
        String cname = cpath.getFileName().toString();
        if (cname.contains(".")) {
            cname =  cname.substring(0, cname.lastIndexOf('.'));
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(cpath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        HashMap<String, Entry> entries = new HashMap<String, Entry>();
        Model all = ModelFactory.createDefaultModel();
        for (String line: lines) {
            String[] sp = line.split(",");
            if (sp.length == 3) {
                System.out.println(sp[0] + " = " + sp[1]);
                String name = sp[0];
                String path = sp[1];
                String web = sp[2];

                OntModel model = ModelFactory.createOntologyModel();
                readModel(model, path);
                readModel(all, path);
                processModel(model, web, name, entries);
            }
        }

        exportRDFFile("ontology_" + cname + ".owl", all, "RDF/XML");
        System.out.println("Created: ontology_" + cname + ".owl");
        exportRDFFile("ontology_" + cname + ".ttl", all, "TTL");
        System.out.println("Created: ontology_" + cname + ".ttl");

        writeHtml(entries, cname + ".html");
        System.out.println("Created: " + cname + ".html");
    }

    public static void writeHtml (Map<String, Entry> entries, String outname) {
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
            entryHTML+=entries.get(i).toHTML()+"\n";
        }
        overviewHTML+="</p>";

        try {
            PrintWriter out = new PrintWriter(outname);
            out.println(overviewHTML);
            out.println(entryHTML);
            out.close();
        } catch (Exception e) {
            System.err.println("Could not save ontology " + outname);
            return;
        }
    }
    
}
