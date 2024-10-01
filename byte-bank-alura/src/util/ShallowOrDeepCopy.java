package util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/// A classe 'ShallowOrDeepCopy' fornece métodos para verificar e criar cópias profundas de objetos.
///
/// Esta classe trata tanto a cópia rasa quanto a cópia profunda, dependendo das propriedades do objeto.
/// A cópia profunda tenta duplicar completamente o estado de um objeto, incluindo seus campos internos.
public class ShallowOrDeepCopy {

    /// Verifica se o objeto de entrada implementa a interface {@link Cloneable} e, se possível, retorna uma cópia profunda.
    ///
    /// Este metodo examina se o objeto passado como parâmetro pode ser clonado (implementa a interface 'Cloneable').
    /// Se for possível, é retornada uma cópia profunda do objeto. Caso contrário, o próprio objeto original é retornado.
    ///
    /// @param data O objeto a ser verificado e copiado.
    /// @return Se o objeto é 'Cloneable', uma cópia profunda é retornada. Caso contrário, o objeto original é retornado.
    public static Object verifyAndCopy(Object data) {
        if (data instanceof Cloneable)
            return deepCopy(data);
        return data;
    }

    /// Cria uma cópia profunda do objeto de entrada.
    ///
    /// Este metodo tenta realizar a clonagem do objeto, invocando o metodo 'clone' via reflexão.
    /// Caso o objeto não possua o metodo 'clone' ou ocorra algum erro durante a clonagem, o objeto original é retornado.
    ///
    /// @param data O objeto a ser copiado profundamente.
    /// @return Uma cópia profunda do objeto, ou o próprio objeto se a cópia não for possível.
    public static Object deepCopy(Object data) {
        try {
            Class<?> x = data.getClass();
            Method method = x.getMethod("clone");
            return method.invoke(data);
        } catch (InvocationTargetException |
                 NoSuchMethodException |
                 IllegalAccessException ignored) {
            return data;
        }
    }
}
