import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class SimpleQuery {

    public static String readFile(String path) throws IOException {
        FileInputStream stream = new FileInputStream(new File(path));
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return Charset.defaultCharset().decode(bb).toString();
        }
        finally {
            stream.close();
        }
    }

	
    public static long[] execute(String serviceURI, String sparqlFile) {
    	long time = -1;
	int i = 0;
        try {
            long startTime = System.currentTimeMillis();
            String queryString;
            queryString = readFile(sparqlFile);
            
            Query query = QueryFactory.create(queryString);

            QueryExecution qe = null;
            if (serviceURI == null) {
                // Local execution
                Model model = ModelFactory.createDefaultModel();
                qe = QueryExecutionFactory.create(query, model);
            } else {
                // Remote Execution
                qe = QueryExecutionFactory.sparqlService(serviceURI, query);
            }

            ResultSet rs = qe.execSelect();
            //ResultSetFormatter.out(System.out, results, query);
	    Writer out = new BufferedWriter(new FileWriter("results.txt"));
            if (! rs.hasNext()) {
			out.write("No results found.");
	    } else {
		for(i=1; rs.hasNext(); i++) {
            		QuerySolution qs = rs.next();
            		out.write(i + ": " + qs + '\n');
		}
            }
	    out.close();
            time = System.currentTimeMillis() - startTime;
            qe.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new long[] {time, i};
    }
    
    public static void main(String[] args) {
        if (args.length == 0) {
	  System.out.println("Usage: SimpleQuery <sparqlQueryFile> [<numberOfExecutions>]");
	  System.exit(0);
	}
	String serviceURI = null;
   	String sparqlFile = args[0];
        int n = 1;
	try {
           n = Integer.parseInt(args[1]);
        } catch (Exception e) { }
    	
    	long minTime = Long.MAX_VALUE;
    	long maxTime = Long.MIN_VALUE;
    	long sumTime = 0;
    	
    	System.out.println("Execution started.");
    	
    	for (int i=0; i <= n; i++) {
    		long[] r = execute(serviceURI, sparqlFile);
		long executionTime = r[0];
		int results = (int)r[1];
    		
    		// Do not use the first result
		if (i == 0) {
			System.out.println("Execution #0 - Time: " + executionTime + " - Results: " + results + " (Not considered)");
			continue;
		} else {
			System.out.println("Execution #" + i + " - Time: " + executionTime + " - Results: " + results);
		}

    		if (executionTime < minTime) {
    			minTime = executionTime;
    		}
    		if (executionTime > maxTime) {
    			maxTime = executionTime;
    		}
    		sumTime += executionTime; 
    	}
    	
    	double averageTime = sumTime / (double)n;
	System.out.println(n + " Execution(s) - " + "Min:" + minTime + 
			" Avg:" + averageTime + " Max:" + maxTime);
    }

}
