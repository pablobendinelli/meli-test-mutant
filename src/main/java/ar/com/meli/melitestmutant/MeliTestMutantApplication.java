package ar.com.meli.melitestmutant;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.common.collect.Iterators;

@SpringBootApplication
@RestController
public class MeliTestMutantApplication {

	private static JSONObject stats = null;

	private static String kind = "DNA";

	public static void main(String[] args) {
		SpringApplication.run(MeliTestMutantApplication.class, args);
	}

	private static JSONObject createNewJSON() {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		Query<Entity> query = Query.newEntityQueryBuilder().setKind(kind).setFilter(PropertyFilter.eq("isMutant", true))
				.build();

		QueryResults<Entity> mutants = datastore.run(query);

		Integer mutantNumber = 0;
		if (mutants != null) {
			mutantNumber = Iterators.size(mutants);
		}

		query = Query.newEntityQueryBuilder().setKind(kind).setFilter(PropertyFilter.eq("isMutant", false)).build();

		QueryResults<Entity> humans = datastore.run(query);

		Integer humanNumber = 0;
		if (humans != null) {
			humanNumber = Iterators.size(humans);
		}

		double ratio = 0;
		if (humanNumber > 0) {
			ratio = ((double) mutantNumber) / humanNumber;
		}

		JSONObject json;
		try {
			json = new JSONObject();
			json.put("count_mutant_dna", mutantNumber);
			json.put("count_human_dna", humanNumber);
			json.put("ratio", ratio);
		} catch (JSONException e) {
			json = new JSONObject();
		}

		return json;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String hello() {
		return "Prueba MELI Mutantes";
	}

	@RequestMapping(value = "/mutant", method = RequestMethod.POST)
	@Consumes("application/json")
	public Response mutant(@RequestBody String dna) throws JSONException {

		JSONObject json = new JSONObject(dna);

		JSONArray array = json.optJSONArray("dna");

		String[] dnaArray = new String[array.length()];

		String entityName = "";

		for (int i = 0; i < array.length(); i++) {
			dnaArray[i] = array.optString(i);
			entityName = entityName + array.optString(i) + "-";
		}

		entityName = entityName.substring(0, entityName.length() - 1);

		Entity retrieved = findEntity(entityName);

		Integer status = 0;
		String message = "";

		if (retrieved != null) {

			if (retrieved.getBoolean("isMutant")) {
				status = 200;
				message = "Es Mutante";
			} else {
				status = 403;
				message = "NO es Mutante";
			}
		} else {

			Boolean isMutant = MutantUtils.isMutant(dnaArray);
			if (isMutant) {
				status = 200;
				message = "Es Mutante";
			} else {
				status = 403;
				message = "NO es Mutante";
			}

			saveEntity(entityName, isMutant);

			stats = createNewJSON();
		}

		return Response.status(status).entity(message).build();
	}

	private void saveEntity(String entityName, Boolean isMutant) {
		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		Key taskKey = datastore.newKeyFactory().setKind(kind).newKey(entityName);

		Entity entity = Entity.newBuilder(taskKey).set("isMutant", isMutant).build();

		datastore.put(entity);

	}

	@RequestMapping(value = "/stats", method = RequestMethod.GET)
	@Produces("application/json")
	public Response stats() throws JSONException {

		if (stats == null) {
			stats = createNewJSON();
		}

		String result = "JSON: " + stats;
		return Response.status(200).entity(result).build();
	}

	private Entity findEntity(String entityName) {
		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		Key taskKey = datastore.newKeyFactory().setKind(kind).newKey(entityName);
		return datastore.get(taskKey);
	}

}
