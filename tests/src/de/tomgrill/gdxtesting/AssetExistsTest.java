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

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.Gdx;

import de.tomgrill.gdxtesting.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class AssetExistsTest {

	@Test
	//Checks that all necessary assets are present in the correct location
	public void mapAssetsExist() {
		assertTrue(Gdx.files.internal("../core/assets/auber_map.tmx").exists());
		assertTrue(Gdx.files.internal("../core/assets/mapdata.json").exists());
		assertTrue(Gdx.files.internal("../core/assets/walkable_map.txt").exists());
	}

	public void skinAssetsExist() {
		assertTrue(Gdx.files.internal("../core/assets/skin/default.fnt").exists());
		assertTrue(Gdx.files.internal("../core/assets/skin/uiskin.atlas").exists());
		assertTrue(Gdx.files.internal("../core/assets/skin/uiskin.json").exists());
		assertTrue(Gdx.files.internal("../core/assets/skin/uiskin.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/").exists());
	}

	public void audioAssetsExist() {
		assertTrue(Gdx.files.internal("../core/assets/audio/ambience.mp3").exists());
		assertTrue(Gdx.files.internal("../core/assets/audio/footstep.mp3").exists());
		assertTrue(Gdx.files.internal("../core/assets/audio/menuMusic.mp3").exists());
		assertTrue(Gdx.files.internal("../core/assets/audio/menuSelect.ogg").exists());
		assertTrue(Gdx.files.internal("../core/assets/audio/metalDeath.mp3").exists());
		assertTrue(Gdx.files.internal("../core/assets/audio/notification.mp3").exists());
		assertTrue(Gdx.files.internal("../core/assets/audio/punch1.mp3").exists());
		assertTrue(Gdx.files.internal("../core/assets/audio/punch2.mp3").exists());
		assertTrue(Gdx.files.internal("../core/assets/audio/swing.mp3").exists());
		assertTrue(Gdx.files.internal("../core/assets/audio/teleporter.mp3").exists());
	}

	public void imgAssetsExist() {
		assertTrue(Gdx.files.internal("../core/assets/img/gameOver.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/gameWin.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/mapScreen.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/operative.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/operative_attack.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/operativeArmoured.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/operativeSpeed.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/operativeStrength.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/player.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/player_attack.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/player_left.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/player_right.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/player_target.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/player_up.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/player_walkleft.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/player_walkright.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/smoke.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/specialAttack.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/specialAttackPowerUp.png").exists());

		//tilesets
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/auber_tileset4").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/dark tileset.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/infirmary tileset.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/Nebula-Aqua-Pink.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/props1.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/props2.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/spaceBackground.jpg").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/teleporter.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/tileset.png").exists());

		//powerUps
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/health.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/health.pspimage").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/null.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/regen.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/specialAttackPowerUp.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/speed.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/strength.png").exists());
	}

	public void imgMenuAssetsExist() {
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/auberLogo.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/backButtonActive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/backButtonInactive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/easyButtonActive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/easyButtonInactive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/hardButtonActive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/hardButtonInactive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/insutructions.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/instructionsButtonActive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/instructionsButtonInactive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/menuButtonActive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/menuButtonInactive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/mutedButtonActive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/mutedButtonInactive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/noActive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/noInactive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/normalButtonActive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/normalButtonInactive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/playButtonActive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/playButtonInactive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/quitButtonActive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/quitButtonInactive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/resume.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/unmutedButtonActive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/unmutedButtonInactive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/yesActive.png").exists());
		assertTrue(Gdx.files.internal("../core/assets/img/tilesets/yesInactive.png").exists());
	}

}
