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

@RunWith(GdxTestRunner.class)
public class UnitTestExample {

	JSONObject gameData;
	JSONArray operArray;

	@Test
	public void oneEqualsOne() {
		assertEquals(1, 1);
	}

	@Test
	public void operativeEqualsEight() {
		gameData = new JSONObject(Gdx.files.internal("../core/assets/mapdata.json").readString());
		operArray = gameData.getJSONArray("operativeData");
		int num_operatives = operArray.length();
		assertEquals(8, num_operatives);
	}


}
