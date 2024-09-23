package br.com.agilizeware.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.omg.CORBA.portable.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Functions {

	static Logger log = LoggerFactory.getLogger(Functions.class);
	
	/**
	 * Transforma inputStream em byte[]
	 * 
	 * @param inputStream
	 * @return
	 * @throws ApplicationException 
	 */
	public static byte[] inputStream2Byte(InputStream inputStream) {
		byte[] buffer = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int next = inputStream.read();
			while (next > -1) {
				bos.write(next);
				next = inputStream.read();
			}
			bos.flush();
			buffer = bos.toByteArray();
		} catch (Exception ee) { // nao deveria ocorrer erro ...
			log.error("exception em inputStream2Byte", ee);
		}
		return buffer;
	}
	
	/**
	 * retorna string contendo exception com causas
	 */
	public static String getExceptionMessage(Throwable t) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(out);
		t.printStackTrace(ps);
		String message = out.toString();
		return message;
	}
	
	/**
	 * Retorna o objeto Enum a partir do ordinal
	 * @param ordinal
	 * @param classEnum
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Enum getEnumByOrdinal(int ordinal, Class classEnum) {
		Enum objEnum = null;
		Enum[] objEnums = (Enum[]) classEnum.getEnumConstants();
		for (Enum enumObj : objEnums) {
			if (enumObj.ordinal() == ordinal) {
				objEnum = enumObj;
				return objEnum;
			}
		}
		return null;
	}

	/**
	 * Transforma array byte[] em Byte[]
	 * 
	 * @param buffer
	 * @return
	 */
	public static Byte[] buffer2Object(byte[] buffer) {
		Byte[] bufferB = new Byte[buffer.length];
		for (int i = 0; i < buffer.length; i++) {
			bufferB[i] = Byte.valueOf(buffer[i]);
		}
		return bufferB;
	}

	/**
	 * Transforma array Byte[] em byte[]
	 * 
	 * @param bufferB
	 * @return
	 */
	public static byte[] buffer2Primitive(Byte[] bufferB) {
		byte[] buffer = new byte[bufferB.length];
		for (int i = 0; i < bufferB.length; i++) {
			buffer[i] = bufferB[i];
		}
		return buffer;
	}
}
