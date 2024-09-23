package br.com.agilizeware.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Hex;

import br.com.agilizeware.util.Util;

/**
 * 
 * Nome: TokenUtils.java Propósito:
 * <p>
 * Utilitário para gerar o token que armazena os dados da sessão do usuário
 * autenticado na plataforma.
 * </p>
 * 
 * @author Gestum / LMS <BR/>
 *         Equipe: Gestum - Software -São Paulo <BR>
 * @version: 1.7
 * 
 *           Registro de Manutenção: 31/03/2014 16:17:08 - Autor: Tiago de
 *           Almeida Lopes - Responsável: Regis Machado - Criação.
 */
public class TokenUtils {

	/**
	 * Atributo MAGIC_KEY.
	 */
	public static final String MAGIC_KEY = "obfuscate";

	/**
	 * 
	 * Cria o token a partir das informações do usuário que está autenticado na plataforma. Armazena ainda o tempo de
	 * inatividade da sessão do usuário, esgotando o tempo a sessão expira.
	 * 
	 * @param userDetails
	 *            Objeto que encapsula os atributos de autenticação do usuário.
	 * @return Token gerado com os dados do usuário.
	 */
	public static String createToken(UserDetails userDetails) {

		/* Expires in one hour */
		long expires = System.currentTimeMillis() + 1000L * 60 * 60 * 24;

		StringBuilder tokenBuilder = new StringBuilder();
		tokenBuilder.append(userDetails.getUsername());
		tokenBuilder.append(":");
		tokenBuilder.append(expires);
		tokenBuilder.append(":");
		tokenBuilder.append(TokenUtils.computeSignature(userDetails, expires));

		return tokenBuilder.toString();
	}

	/**
	 * 
	 * Criptografa o token gerado.
	 * 
	 * @param userDetails
	 *            Encapsula os atributos do usuário logado.
	 * @param expires
	 *            Tempo que a sessão expira.
	 * @return Token criptografado.
	 */
	public static String computeSignature(UserDetails userDetails, long expires) {

		StringBuilder signatureBuilder = new StringBuilder();
		signatureBuilder.append(userDetails.getUsername());
		signatureBuilder.append(":");
		signatureBuilder.append(expires);
		signatureBuilder.append(":");
		signatureBuilder.append(userDetails.getPassword());
		signatureBuilder.append(":");
		signatureBuilder.append(TokenUtils.MAGIC_KEY);

		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No MD5 algorithm available!");
		}

		return new String(Hex.encode(digest.digest(signatureBuilder.toString()
				.getBytes())));
	}

	/**
	 * 
	 * Retorna o userme ou login do usuário armazenado no token.
	 * 
	 * @param authToken
	 *            Token.
	 * @return Username ou login do usuário.
	 */
	public static String getUserNameFromToken(String authToken) {

		if (Util.isNotNull(authToken)) {
			String[] parts = authToken.split(":");
			return parts[0];
		}
		return null;
	}

	/**
	 * 
	 * Valida se o token ainda é valido, se o tempo de expiração do token já
	 * excedeu.
	 * 
	 * @param authToken
	 *            Token.
	 * @param userDetails
	 *            Usuário logado.
	 * @return Verdadeiro caso o token ainda seja valido, falso do contrário.
	 */
	public static boolean validateToken(String authToken,
			UserDetails userDetails) {

		String[] parts = authToken.split(":");
		long expires = Long.parseLong(parts[1]);
		String signature = parts[2];

		if (expires < System.currentTimeMillis()) {
			return false;
		}

		return signature.equals(TokenUtils.computeSignature(userDetails,
				expires));
	}
}
