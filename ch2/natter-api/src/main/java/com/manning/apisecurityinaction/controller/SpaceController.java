package com.manning.apisecurityinaction;

import org.dalesbred.Database;
import org.json.*;
import spark.*;
import java.sql.SQLException;

/*
	Controllers are objects that simply receive "Request" and prepare "Response".
	In charge of "spaces"
*/
public class SpaceController {
	private final Database database;

	public SpaceController(Database database) {
		this.database = database;
	}

	public JSONObject createSpace(Request request, Response response) throws SQLException {
		var json = new JSONObject(request.body());

		// Validate "name" 
		var spaceName = json.getString("name");
		if (spaceName.length() > 255) {
			throw new IllegalArgumentException("space name too long");
		}

		// Validate "owner"
		var owner = json.getString("owner");
		if (!owner.matches("[a-zA-Z][a-zA-Z0-9]{1,29}")) {
			throw new IllegalArgumentException("invalid username: " + owner);
		}

		return database.withTransaction(tx -> {
			var spaceId = database.findUniqueLong("SELECT NEXT VALUE FOR space_id_seq");

			// SQL Injection warning -- create a new "space" with a space_id, name, and owner
			// database.updateUnique(
			// 	"INSERT INTO spaces(space_id, name, owner) " + 
			// 	"VALUES(" + spaceId + ", '" + spaceName + "', '" + owner + "');");

			// To avoid the SQL injection, we'll use a prepared statement, in which the database API
			// ensures that user input is never treated as statements to executed - sanitized
			database.updateUnique(
				"INSERT INTO spaces(space_id, name, owner) " +
				"VALUES(?, ?, ?);", spaceId, spaceName, owner);

			response.status(201);
			response.header("Location", "/spaces/" + spaceId);

			return new JSONObject()
				.put("name", spaceName)
				.put("uri", "/spaces/" + spaceId);
		});
	}
}