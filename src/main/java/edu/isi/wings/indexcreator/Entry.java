package edu.isi.wings.indexcreator;

/**
 * An entry consists of a Name, a description and a reference link to the document where it is described.
 * @author dgarijo
 */
public class Entry {
    String name;
    String description;
    String linkToSpec;
    String titleOfDocWhereItsDefined;

    public Entry(String name, String description, String linkToSpec, String title) {
        this.name = name;
        this.description = description;
        this.linkToSpec = linkToSpec;
        this.titleOfDocWhereItsDefined = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
    
    public String toHTML(){
        String html = "<h3 id=\""+this.name+"\">"+this.name+"</h3>\n";
        if(description!=null && !description.equals("")){
            html +="<p>"+this.description+"</p> \n";
        }
        html +="<p>Defined in: <a href=\""+this.linkToSpec+"#"+this.name+"\">"+this.titleOfDocWhereItsDefined+"</a></p>";
        return html;
    }
            
    
}
