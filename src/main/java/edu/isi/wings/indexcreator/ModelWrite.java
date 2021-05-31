package edu.isi.wings.indexcreator;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class for aggregating and writing a model
 * @author dgarijo
 */
public class ModelWrite {
    
    private static void readModel(Model model, String path){
        String[] serializations = {"RDF/XML", "TTL", "N3"};
        String ontoPath = path;
        String ext = "";
        for(String s:serializations){
            InputStream in;
            try{
                in = FileManager.get().open(ontoPath);
                if (in == null) {
                    System.err.println("Error: Ontology file not found");
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
    
    
    public static void main(String[] args){
        Model all = ModelFactory.createDefaultModel();
//    ENIGMA ONTOS
        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\development\\CohortOntology.owl");
        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\development\\OrganizationOntology.owl");
        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\development\\PersonOntology.owl");
        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\development\\ProjectOntology.owl");
        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\development\\ProtocolOntology.owl");
        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\development\\RolesOntology.owl");
        //readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\development\\ScannerOntology.owl");
        readModel(all, "C:\\Users\\dgarijo\\Documents\\GitHub\\EnigmaOntology\\development\\WorkingGroupOntology.owl");
           

        exportRDFFile("ontology_all.owl", all, "RDF/XML");
        exportRDFFile("ontology_all.ttl", all, "TTL");
    }
    
    public static void exportRDFFile(String outFile, Model model, String mode){
        OutputStream out;
        try {
            out = new FileOutputStream(outFile);
            model.write(out,mode);
            //model.write(out,"RDF/XML");
            out.close();
        } catch (Exception ex) {
            System.out.println("Error while writing the model to file "+ex.getMessage() + " oufile "+outFile);
        }
    }
}
