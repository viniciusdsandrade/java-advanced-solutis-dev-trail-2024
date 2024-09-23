package br.com.agilizeware.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.agilizeware.enums.ErrorCodeEnum;
import br.com.agilizeware.exception.AgilizeException;

/**
 * Simple password encoder that uses a salt value and encodes the password with
 * SHA-256.
 * 
 */

/**
 * 
 * Nome: SaltedSHA256PasswordEncoder.java Propósito:
 * <p>
 * Codificador simples para senha que usa um valor forte e codifica a senha com
 * SHA-256. O algoritmo para criptografia deverá ser alterado para uma chave
 * maior, visto que com essa chave 256 a criptografia é de facil quebra.
 * </p>
 * 
 * @author Gestum / LMS <BR/>
 *         Equipe: Gestum - Software -São Paulo <BR>
 * 
 *         Registro de Manutenção: 31/03/2014 16:01:51 - Autor: Tiago de Almeida
 *         Lopes - Responsável: Regis Machado - Criação.
 */
public class SaltedSHA256PasswordEncoder implements PasswordEncoder {

	private static final String SALT = "sometimesbloodoverbindwithus";
	
	/**
	 * Atributo salt, torna a senha mais complexa de quebrar por ataques com
	 * dicionários.
	 */
	private final String salt;

	/**
	 * Atributo digest usado para acionar a função one-way para criptografar a
	 * senha do usuário.
	 */
	private final MessageDigest digest;

	/**
	 * 
	 * Construtor padrão da classe.
	 * 
	 * @param salt
	 *            Salt definido para criptografia da senha.
	 * @throws NoSuchAlgorithmException
	 *             Exceção possível de ser lançada caso o algoritmo selecionado
	 *             para criptografia não seja encontrado dentro do pacote
	 *             Security.
	 */
	public SaltedSHA256PasswordEncoder(String salt)
			throws NoSuchAlgorithmException {

		this.salt = salt;
		this.digest = MessageDigest.getInstance("SHA-256");
	}
	
	/**
	 * 
	 * Construtor padrão da classe.
	 * 
	 * @param salt
	 *            Salt definido para criptografia da senha.
	 * @throws NoSuchAlgorithmException
	 *             Exceção possível de ser lançada caso o algoritmo selecionado
	 *             para criptografia não seja encontrado dentro do pacote
	 *             Security.
	 */
	public SaltedSHA256PasswordEncoder() {
		this.salt = SALT;
		try {
			this.digest = MessageDigest.getInstance("SHA-256");
		}
		catch(NoSuchAlgorithmException nsae) {
			throw new AgilizeException(HttpStatus.INTERNAL_SERVER_ERROR.ordinal(), ErrorCodeEnum.PASSWORD_ENCODER_EXCEPTION, nsae);
		}
	}

	public String encodeWithoutSalt(CharSequence rawPassword) {

		try {
			return new String(Hex.encode(this.digest.digest(rawPassword.toString()
					.getBytes("UTF-8"))));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 not supported");
		}
	}

	public String encode(CharSequence rawPassword) {
		// TODO Auto-generated method stub
		String saltedPassword = rawPassword + this.salt;
		try {
			return new String(Hex.encode(this.digest.digest(saltedPassword
					.getBytes("UTF-8"))));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 not supported");
		}
	}

	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		// TODO Auto-generated method stub
		return this.encode(rawPassword).equals(encodedPassword);
	}

}
