import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.DatasetImpl;

public class Query {

	public static void main(String[] args) {
		String query = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" + 
				"PREFIX geopos: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n" + 
				"select ?s ?lat ?long where {\n" + 
				"SERVICE <http://fuseki.mooo.com/lgd-dbpedia/sparql>  {\n" + 
				"  select * where {\n" + 
				"    ?s owl:sameAs ?geo .\n" + 
				"    FILTER regex(str(?geo),\"dbpedia\")\n" + 
				"  } \n" + 
				"}\n" + 
				"service <http://fuseki.mooo.com/dbpedia-geo/sparql> {\n" + 
				"  select * where{\n" + 
				"	?geo geopos:lat ?lat ;\n" + 
				"       geopos:long ?long .\n" + 
				"  } 	\n"; 
		
		long start = System.currentTimeMillis();
		QueryExecution exec = QueryExecutionFactory.create(QueryFactory.create(query), 
				new DatasetImpl(ModelFactory.createDefaultModel()));
		ResultSet rs = exec.execSelect();
		int n = 0;
		while (rs.hasNext()) {
			rs.next();
			n++;
		}
		long time = System.currentTimeMillis() - start;
		System.out.println(n + " results found in " + time + "ms.");
	}
}
