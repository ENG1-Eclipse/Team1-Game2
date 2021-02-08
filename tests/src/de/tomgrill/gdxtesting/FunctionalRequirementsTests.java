/*******************************************************************************
 * Copyright 2015 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.tomgrill.gdxtesting;
import org.json.JSONArray;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.badlogic.gdx.Gdx;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

@RunWith(GdxTestRunner.class)
public class FunctionalRequirementsTests {

	JSONObject gameData;
	JSONArray testArray;

	@Test
	//Check if there are eight operatives
	public void operativeEqualsEight() {
		gameData = new JSONObject(Gdx.files.internal("../core/assets/mapdata.json").readString());
		testArray = gameData.getJSONArray("operativeData");
		int num_operatives = testArray.length();
		assertEquals(8, num_operatives);
	}

	@Test
	//Check if there are at least fifteen systems
	public void systemsEqualFifteen() {
		gameData = new JSONObject(Gdx.files.internal("../core/assets/mapdata.json").readString());
		testArray = gameData.getJSONArray("rooms");
		int num_rooms = testArray.length();
		assertTrue(15 <= num_rooms);
	}

	//Check if at least four unique rooms exist
	@Test
	public void fourUniqueRoomsExist() {
		gameData = new JSONObject(Gdx.files.internal("../core/assets/mapdata.json").readString());
		testArray = gameData.getJSONArray("rooms");
		String[] rooms = new String[testArray.length()];
		for (int i = 0; i < testArray.length(); i++) {
			JSONObject room = testArray.getJSONObject(i);
			rooms[i] = room.getString("name");
		}
		assertTrue(verifyUniqueRooms(rooms, 4));
	}

	//Helper function to verify the number of unique strings in an array. If they are greater of equal to the number required, return true
	public boolean verifyUniqueRooms(String array[], int reqRooms) {
		int unique = 0;
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < i; j++) {
				if (array[i].equals(array[j])) {
					unique -=1;
				}
			}
			unique += 1;
		}
		if (unique >= reqRooms){
			return true;
		}
		else {
			return false;
		}
	}

	//Checks whether an infirmary exists in the game data
	@Test
	public void oneInfirmaryRoomExists(){
		gameData = new JSONObject(Gdx.files.internal("../core/assets/mapdata.json").readString());
		testArray = gameData.getJSONArray("rooms");
		boolean medbayExists = false;
		for (int i = 0; i < testArray.length(); i++) {
			JSONObject room = testArray.getJSONObject(i);
			if (room.getString("name").equals(("Medbay"))){
				medbayExists = true;
			}
		}
		assertTrue(medbayExists);
	}

	//Tests whether, given the medbay exists, some array exists for the teleporter coordinates (and is not null)
	@Test
	public void medbayTeleportExists(){
		gameData = new JSONObject(Gdx.files.internal("../core/assets/mapdata.json").readString());
		testArray = gameData.getJSONArray("rooms");
		boolean tp_exists = false;
		Object tp_coords;
		for (int i = 0; i < testArray.length(); i++) {
			JSONObject room = testArray.getJSONObject(i);
			if (room.getString("name").equals(("Medbay"))){
				tp_coords = room.get("teleporterCoords");
				if (tp_coords instanceof JSONArray){
					tp_exists = true;
				}
			}
		}
		assertTrue(tp_exists);
	}
}
