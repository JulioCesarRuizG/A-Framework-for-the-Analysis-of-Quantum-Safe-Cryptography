

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.openquantumsafe.Common;
import org.openquantumsafe.KEMs;
import org.openquantumsafe.KeyEncapsulation;

@Path("/CryptographyTest")
public class RestApi {

    static{
        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new BouncyCastlePQCProvider());
    }

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";

    public static String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHA_NUMERIC_STRING.length());
            sb.append(ALPHA_NUMERIC_STRING.charAt(index));
        }
        return sb.toString();
    }

	@Context
	private ServletContext context;

	public static File createDummyFile(String fileName, int sizeInMB) throws IOException {
        byte[] buffer = new byte[1024];
        Random random = new Random();

        File file = new File(fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            for (int i = 0; i < sizeInMB * 1024; i++) {
                random.nextBytes(buffer);
                fos.write(buffer);
            }
        }

        return file;
    }

    // AES  

	@POST
	@Path("/AES")
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	public Response AES(@FormDataParam("size") String size){
        String inName = generateRandomString(10);
        String outName = generateRandomString(10);
		long initTime = System.currentTimeMillis();
        long memoryUsed;
        File in;
        File out;
		try {
			in = createDummyFile(inName, Integer.parseInt(size));
			out = new File(outName);

			String algorithm = "AES";
            KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();

            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
			CifradoAES(secretKey, in, out);
            long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
            memoryUsed = memoryAfter - memoryBefore;
		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok("Something failed", "text/plain").build();
		}
		long endTime = System.currentTimeMillis();
		int time = (int) (endTime-initTime);
		System.out.println(time);
        if (out.exists()) {
            if (out.delete()) {
                System.out.println("File deleted successfully.");
            } else {
                System.out.println("Failed to delete the file.");
            }
        } else {
            System.out.println("The file does not exist.");
        }
        if (in.exists()) {
            if (in.delete()) {
                System.out.println("File deleted successfully.");
            } else {
                System.out.println("Failed to delete the file.");
            }
        } else {
            System.out.println("The file does not exist.");
        }
		return Response.ok(time+","+memoryUsed, "text/plain").build();
	}

	public static void CifradoAES(SecretKey key, File inputFile, File outputFile) throws Exception {
        if (inputFile == null || !inputFile.exists() || !inputFile.isFile()) {
            throw new IllegalArgumentException("Archivo de entrada no válido.");
        }
    
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, key);
    
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
    
            byte[] buffer = new byte[1024];
            byte[] outputBytes;
            int bytesRead;
    
            while ((bytesRead = fis.read(buffer)) != -1) {
                outputBytes = cipher.update(buffer, 0, bytesRead);
                if (outputBytes != null) {
                    fos.write(outputBytes);
                }
            }
    
            outputBytes = cipher.doFinal();
            if (outputBytes != null) {
                fos.write(outputBytes);
            }
        }
    }

    // RSA

	@POST
	@Path("/RSA")
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response RSA(@FormDataParam("size") String size) {
        String inName = generateRandomString(10);
        String outName = generateRandomString(10);
        long initTime = System.currentTimeMillis();
        long memoryUsed;
        File in;
        File out;
        try {
            in = createDummyFile(inName, Integer.parseInt(size));
            out = new File(outName);

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
            CifradoRSA(publicKey, in, out);
            long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
            memoryUsed = memoryAfter - memoryBefore;
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok("Something failed", "text/plain").build();
        }
        long endTime = System.currentTimeMillis();
        int time = (int) (endTime - initTime);
        System.out.println(time);

        if (out.exists()) {
            if (out.delete()) {
                System.out.println("File deleted successfully.");
            } else {
                System.out.println("Failed to delete the file.");
            }
        } else {
            System.out.println("The file does not exist.");
        }
        if (in.exists()) {
            if (in.delete()) {
                System.out.println("File deleted successfully.");
            } else {
                System.out.println("Failed to delete the file.");
            }
        } else {
            System.out.println("The file does not exist.");
        }
        return Response.ok(time+","+memoryUsed, "text/plain").build();
    }

    public static void CifradoRSA(PublicKey publicKey, File inputFile, File outputFile) throws Exception {
        if (inputFile == null || !inputFile.exists() || !inputFile.isFile()) {
            throw new IllegalArgumentException("Archivo de entrada no válido.");
        }

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        try (FileInputStream fis = new FileInputStream(inputFile);
                FileOutputStream fos = new FileOutputStream(outputFile)) {

                byte[] buffer = new byte[245]; // Tamaño máximo para RSA de 2048 bits con relleno
                byte[] encryptedBytes;
                int bytesRead;

                // Leer el archivo y cifrar en bloques de 245 bytes
                while ((bytesRead = fis.read(buffer)) != -1) {
                    encryptedBytes = cipher.doFinal(buffer, 0, bytesRead);
                    fos.write(encryptedBytes);
                }
            }
    }

    

    // Kyber

    // Sike

    // Bike

    // Rainbow

    // Sphincs+
    
    @POST
    @Path("/PQCrypto")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response PQCrypto(@FormDataParam("size") String size, @FormDataParam("algorithm") String algorithm) {
    String inName = generateRandomString(10);
    String outName = generateRandomString(10);
    long initTime = System.currentTimeMillis();
    long memoryUsed;
    File in;
    File out;

    try {
        in = createDummyFile(inName, Integer.parseInt(size));
        out = new File(outName);

        Common.print_list(KEMs.get_supported_KEMs());
        // Configura el algoritmo post-cuántico
        System.out.println("pasa1");
        KeyEncapsulation keyEncapsulation = new KeyEncapsulation(algorithm);
        System.out.println("pasa2");
        byte[] publicKey = keyEncapsulation.generate_keypair();
        byte[] ciphertext = new byte[0];
        byte[] sharedSecret = new byte[0];

        // Uso de memoria antes de la operación
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        // Cifra usando encapsulación de llave

        ciphertext = keyEncapsulation.encap_secret(publicKey).getLeft();
        sharedSecret = keyEncapsulation.encap_secret(ciphertext).getLeft();
        System.out.println(keyEncapsulation.encap_secret(publicKey).getLeft().toString());
        System.out.println(keyEncapsulation.encap_secret(ciphertext).getLeft().toString());
        System.out.println(keyEncapsulation.encap_secret(publicKey).getRight().toString());
        System.out.println(keyEncapsulation.encap_secret(ciphertext).getRight().toString());

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        memoryUsed = memoryAfter - memoryBefore;
    } catch (Exception e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
        return Response.ok("Something failed", "text/plain").build();
    }

    long endTime = System.currentTimeMillis();
    int time = (int) (endTime - initTime);
    System.out.println(time);

    // Limpia archivos temporales
    if (out.exists() && out.delete()) {
        System.out.println("File deleted successfully.");
    }
    if (in.exists() && in.delete()) {
        System.out.println("File deleted successfully.");
    }

        return Response.ok(time + "," + memoryUsed, "text/plain").build();
    }

    // Método para cifrar el contenido del archivo con el algoritmo especificado
    public static void PQCipher(File inputFile, File outputFile, String algorithm, byte[] publicKey) throws IOException {
        try (FileInputStream fis = new FileInputStream(inputFile);
            FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            byte[] ciphertext;

            KeyEncapsulation keyEncapsulation = new KeyEncapsulation(algorithm);
            
            while ((bytesRead = fis.read(buffer)) != -1) {
                ciphertext = keyEncapsulation.encap_secret(publicKey).getLeft(); // Usa la clave pública para cifrar
                fos.write(ciphertext);
            }
        }
    }

    @GET
    @Path("/PQCrypto")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response PQCrypto() {
    String inName = generateRandomString(10);
    String outName = generateRandomString(10);
    long initTime = System.currentTimeMillis();
    long memoryUsed;
    File in;
    File out;

    try {
        in = createDummyFile(inName, Integer.parseInt("10"));
        out = new File(outName);

        Common.print_list(KEMs.get_supported_KEMs());
        // Configura el algoritmo post-cuántico
        System.out.println("pasa1");
        KeyEncapsulation keyEncapsulation = new KeyEncapsulation("Kyber512");
        System.out.println("pasa2");
        byte[] publicKey = keyEncapsulation.generate_keypair();
        byte[] ciphertext = new byte[0];
        byte[] sharedSecret = new byte[0];

        // Uso de memoria antes de la operación
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        // Cifra usando encapsulación de llave

        ciphertext = keyEncapsulation.encap_secret(publicKey).getLeft();
        sharedSecret = keyEncapsulation.encap_secret(ciphertext).getLeft();
        System.out.println(keyEncapsulation.encap_secret(publicKey).getLeft().toString());
        System.out.println(keyEncapsulation.encap_secret(ciphertext).getLeft().toString());
        System.out.println(keyEncapsulation.encap_secret(publicKey).getRight().toString());
        System.out.println(keyEncapsulation.encap_secret(ciphertext).getRight().toString());

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        memoryUsed = memoryAfter - memoryBefore;
    } catch (Exception e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
        return Response.ok("Something failed", "text/plain").build();
    }

    long endTime = System.currentTimeMillis();
    int time = (int) (endTime - initTime);
    System.out.println(time);

    // Limpia archivos temporales
    if (out.exists() && out.delete()) {
        System.out.println("File deleted successfully.");
    }
    if (in.exists() && in.delete()) {
        System.out.println("File deleted successfully.");
    }

        return Response.ok(time + "," + memoryUsed, "text/plain").build();
    }
}
