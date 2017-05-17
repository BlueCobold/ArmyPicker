package de.game_coding.armypicker.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import de.game_coding.armypicker.model.Army;
import de.game_coding.armypicker.util.FileUtil;

public class ArmyStreamConverter extends AbstractHttpMessageConverter<Army> {

	@Override
	protected boolean canRead(final MediaType mediaType) {
		return true;
	}

	@Override
	protected boolean supports(final Class<?> clazz) {
		return clazz.equals(Army.class);
	}

	@Override
	protected Army readInternal(final Class<? extends Army> clazz, final HttpInputMessage inputMessage)
		throws IOException, HttpMessageNotReadableException {

		if (inputMessage instanceof ClientHttpResponse) {
			if (((ClientHttpResponse) inputMessage).getStatusCode() != HttpStatus.OK) {
				return null;
			}
		}

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final byte[] buffer = new byte[1024];
		int n;

		final InputStream reader = inputMessage.getBody();
		while ((n = reader.read(buffer)) > 0) {
			bos.write(buffer, 0, n);
		}

		if (bos.size() < 8) {
			return null;
		}

		final byte[] bytes = bos.toByteArray();
		return FileUtil.readData(bytes, Army.CREATOR);
	}

	@Override
	protected void writeInternal(final Army t, final HttpOutputMessage outputMessage)
		throws IOException, HttpMessageNotWritableException {
		// TODO Auto-generated method stub

	}

}