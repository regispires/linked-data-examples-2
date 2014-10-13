import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class SimpleQueryOld {
	final static Logger logger = LoggerFactory.getLogger(SimpleQueryOld.class.getName());
	
    public static long execute(String serviceURI, String sparqlFile) {
    	long time = -1;
        try {
            String sparqlFileName = SimpleQueryOld.class.getResource("/").getPath() + sparqlFile;
            String queryString;
            queryString = Optimizer.readFile(sparqlFileName);
            logger.info("query: {}", queryString);
            
            Query query = QueryFactory.create(queryString);
            logger.info("Binding/Values variables: {}", query.getValuesVariables());
            logger.info("Project vars: {}", query.getProjectVars());

//            List<Var> vars = new ArrayList<Var>();
//            Var var = Var.alloc("s");
//            vars.add(var);
//            
//            List<Binding> values = new ArrayList<Binding>();
//            Node node = Node.createURI("http://example.org/book/book5");
//            Binding b = BindingFactory.binding(var, node);
//            values.add(b);
            
//            query.setBindings(vars, values);

            QueryExecution qe = null;
            if (serviceURI == null) {
                // Local execution
                Model model = ModelFactory.createDefaultModel();
                qe = QueryExecutionFactory.create(query, model);
                //qe.setInitialBinding(null); //-> not supported for remote queries
            } else {
                // Remote Execution
                qe = QueryExecutionFactory.sparqlService(serviceURI, query);
            }

            long startTime = System.currentTimeMillis();
            ResultSet results = qe.execSelect();
            logger.info("Time: " + (System.currentTimeMillis() - startTime));
            //ResultSetFormatter.out(System.out, results, query);
            for (int i=1; results.hasNext(); i++) {
            	QuerySolution qs = results.next();
            	logger.info(i + ": " + qs);
            }
            time = System.currentTimeMillis() - startTime;
            logger.info("Time: " + time);
            qe.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return time;
    }
    
    public static void main(String[] args) {
        //String serviceURI = "http://www4.wiwiss.fu-berlin.de/drugbank/sparql";
        //String serviceURI = "http://dbpedia.org/sparql";
        //String serviceURI = "http://localhost:2020/sparql";
        //String serviceURI = "http://localhost:3030/teste/query";
    	//String serviceURI = "http://localhost:3030/teste/query";
    	//String serviceURI = "http://200.19.179.244/drugbank/query";
    	//String serviceURI = "http://200.19.179.244/researchers/sparql";
    	//String serviceURI = "http://200.19.179.244/dblp/sparql";
    	//String serviceURI = "http://plankton.mooo.com/sparql";
    	//String serviceURI = "http://dblp.mooo.com/dblp/sparql";
    	//String serviceURI = "http://dblp.l3s.de/d2r/sparql";
    	String serviceURI = "http://fuseki.mooo.com/researchers/sparql";
    	
    	
    	String sparqlFile = "q-researchers-01.spql";
    	int n = 10;
    	
    	long minTime = Long.MAX_VALUE;
    	long maxTime = Long.MIN_VALUE;
    	long sumTime = 0;
    	
    	System.out.println("Execution started.");
    	
    	for (int i=0; i <= n; i++) {
    		long time = execute(serviceURI, sparqlFile);
    		System.out.println("Time " + i + ": " + time);
    		
    		// Do not use the first result
    		if (i == 0) {
    			System.out.println("Discard first execution.");
    			continue;
    		}
    		
    		if (time < minTime) {
    			minTime = time;
    		}
    		if (time > maxTime) {
    			maxTime = time;
    		}
    		sumTime += time; 
    	}
    	
    	double averageTime = sumTime / (double)n;
    	System.out.println("serviceURI: " + serviceURI);
    	System.out.println(n + " Executions");
    	System.out.println("Min Time: " + minTime + "ms");
    	System.out.println("Average Time: " + averageTime + "ms");
    	System.out.println("Max Time: " + maxTime + "ms");
    }

}
