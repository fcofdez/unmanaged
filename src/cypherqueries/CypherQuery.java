package cypherqueries;

import com.google.gson.Gson;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.*;

import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//START SNIPPET: extensions.HelloWorldResource
@Path( "/cypherquery" )
public class CypherQuery
{

    private final GraphDatabaseService database;
    private final ExecutionEngine executionEngine;

    public CypherQuery( @Context GraphDatabaseService database )
    {
        this.database = database;
        this.executionEngine = new ExecutionEngine(database);
    }

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    @Path( "/{cuisineName}" )
    public Response ingredientType( @PathParam( "cuisineName" ) String cuisineName )
    {
        ArrayList<Object> ingCategories = new ArrayList<Object>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cuisine_name", cuisineName);
        String query = "MATCH (cuisine:CUISINE {name: {cuisine_name}})<-[:OF_CUISINE]-(recipe:RECIPE)\n" +
                "WITH recipe, cuisine \n" +
                "MATCH (recipe)<-[:INGR_PART_OF]-(ingredient:INGREDIENT)-[:BELONGS_TO]->(ingredcat:INGREDIENT_CATEGORY)\n" +
                "RETURN cuisine.name AS Cuisine, ingredcat.name AS IngredientCategory, count(DISTINCT recipe) AS NumberOfRecipes\n" +
                "ORDER BY Cuisine ASC, NumberOfRecipes DESC;";

        Iterator<Map<String, Object>> result = executionEngine.execute(query, params).iterator();
        while (result.hasNext()) {
            Map<String, Object> row = result.next();
            Map<String, Object> ingCategory = new HashMap<String, Object>();

            ingCategory.put("cuisine", row.get("Cuisine"));
            ingCategory.put("ing_cat", row.get("IngredientCategory"));
            ingCategory.put("number", row.get("NumberOfRecipes"));

            ingCategories.add(ingCategory);
        }
        Gson gson = new Gson();
        String json = gson.toJson(ingCategories);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
}
