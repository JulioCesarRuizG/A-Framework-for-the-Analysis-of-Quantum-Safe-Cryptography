import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class CriptografiaAPI {
    public static void CifradoAES(SecretKey key, File inputFile, File outputFile) throws Exception {
        // Crear una instancia del cifrador AES
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");

        // Inicializar el cifrador en modo de cifrado
        cipher.init(Cipher.ENCRYPT_MODE, key);

        // Leer el archivo de entrada y cifrar su contenido
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] inputBytes = new byte[(int) inputFile.length()];
            fis.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            fos.write(outputBytes);
        }
    }
}
