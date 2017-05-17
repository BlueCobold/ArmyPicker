package de.game_coding.armypicker.web;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.RestClientErrorHandling;

import de.game_coding.armypicker.model.Army;

@Rest(rootUrl = "https://www.game-coding.de/downloads/armypicker", converters = { ArmyStreamConverter.class })
public interface Downloader extends RestClientErrorHandling {

	@Get("/template.php?name={templateName}")
	Army getTemplate(String templateName);
}