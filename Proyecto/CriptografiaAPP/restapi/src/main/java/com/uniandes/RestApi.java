package com.uniandes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.glassfish.jersey.media.multipart.FormDataParam;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.spec.KEMExtractSpec;
import org.bouncycastle.jcajce.spec.KEMGenerateSpec;
import org.bouncycastle.pqc.jcajce.spec.BIKEParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.FrodoParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.KyberParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.McElieceKeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.NTRUParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.RainbowParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.SABERParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.SPHINCSPlusParameterSpec;

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

    
    @POST
    @Path("/PQCryptoAsim")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response cifradoAsim(@FormDataParam("cantidad") String cantidad, @FormDataParam("algorithm") String algorithm) {

        Provider bcpqcProvider = Security.getProvider("BCPQC");
        if (bcpqcProvider == null) {
            System.out.println("BCPQC provider not found! Ensure it's added to the classpath.");
        }

        long initTime = System.currentTimeMillis();
        long memoryUsed = 0;
        
        try {
            for(int i=0; i<Integer.parseInt(cantidad); i++){
                Runtime runtime = Runtime.getRuntime();
                runtime.gc();
                long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
                KeyPairGenerator keyGen = null;
                if(algorithm.equals("Kyber1024")){
                    keyGen = KeyPairGenerator.getInstance("Kyber", "BCPQC");
                    keyGen.initialize(KyberParameterSpec.kyber1024, new SecureRandom());
                }
                else if(algorithm.equals("Kyber768")){
                    keyGen = KeyPairGenerator.getInstance("Kyber", "BCPQC");
                    keyGen.initialize(KyberParameterSpec.kyber768, new SecureRandom());
                }
                else if(algorithm.equals("Kyber512")){
                    keyGen = KeyPairGenerator.getInstance("Kyber", "BCPQC");
                    keyGen.initialize(KyberParameterSpec.kyber512, new SecureRandom());
                }
                else if(algorithm.equals("BIKE")){
                    keyGen = KeyPairGenerator.getInstance("BIKE", "BCPQC");
                    keyGen.initialize(BIKEParameterSpec.bike192, new SecureRandom());
                }
                else if(algorithm.equals("RSA")){
                    keyGen = KeyPairGenerator.getInstance("RSA");
                }
                else if(algorithm.equals("Frodo")){
                    keyGen = KeyPairGenerator.getInstance("Frodo", "BCPQC");
                    keyGen.initialize(FrodoParameterSpec.frodokem976aes, new SecureRandom());
                }
                else if(algorithm.equals("Saber")){
                    keyGen = KeyPairGenerator.getInstance("Saber", "BCPQC");
                    keyGen.initialize(SABERParameterSpec.lightsaberkem256r3, new SecureRandom());
                }
                else if(algorithm.equals("NTRU")){
                    keyGen = KeyPairGenerator.getInstance("NTRU", "BCPQC");
                    keyGen.initialize(NTRUParameterSpec.ntruhps2048509, new SecureRandom());
                }

                KeyPair keyPair = keyGen.generateKeyPair();
                PublicKey publicKey = keyPair.getPublic();
                PrivateKey privateKey = keyPair.getPrivate();

                /* 
                byte[] publicKeyEncoded = keyPair.getPublic().getEncoded();
                byte[] privateKeyEncoded = keyPair.getPrivate().getEncoded();

                System.out.println("Tamaño de la clave pública: " + publicKeyEncoded.length + " bytes");
                System.out.println("Tamaño de la clave privada: " + privateKeyEncoded.length + " bytes");
                */

                if(!algorithm.equals("RSA")){
                    SecretKeyWithEncapsulation skwe = generateSecretKeySender(publicKey, algorithm);
                    // System.out.println("Tamaño de la llave cifrada: " + skwe.getEncoded().length + " bytes");
                    byte[] encapsulation = skwe.getEncapsulation();
                    generateSecretKeyReciever(privateKey, encapsulation, algorithm);
                }
                else{
                    KeyGenerator aesKeyGen = KeyGenerator.getInstance("AES");
                    aesKeyGen.init(256);
                    SecretKey aesKey = aesKeyGen.generateKey();
                    //System.out.println("Tamaño de la llave: " + 256/8 + " bytes");
                    //System.out.println("Tamaño de la llave cifrada: " + encryptAESKeyWithRSA(aesKey, publicKey).length + " bytes");
                }
                long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
                memoryUsed += memoryAfter - memoryBefore;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok("Something failed", "text/plain").build();
        }
        long endTime = System.currentTimeMillis();
        int time = (int) (endTime - initTime);
        memoryUsed = memoryUsed / 1024;
        return Response.ok(time + "," + memoryUsed, "text/plain").build();
    }

    // Algoritmos

    private static SecretKeyWithEncapsulation generateSecretKeySender(PublicKey publicKey, String algoritmo) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algoritmo, "BCPQC");
        KEMGenerateSpec kemGenerateSpec = new KEMGenerateSpec(publicKey, "AES");
        keyGenerator.init(kemGenerateSpec);
        // System.out.println("Tamaño de la llave: " + keyGenerator.generateKey().getEncoded().length + " bytes");
        return  (SecretKeyWithEncapsulation)keyGenerator.generateKey();
    }

    private static SecretKey generateSecretKeyReciever(PrivateKey privateKey, byte[] encapsulation, String algoritmo) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algoritmo, "BCPQC");
        KEMExtractSpec kemExtractSpec = new KEMExtractSpec(privateKey, encapsulation, "AES");
        keyGenerator.init(kemExtractSpec);
        return (SecretKeyWithEncapsulation)keyGenerator.generateKey();
    }

    private static byte[] encryptAESKeyWithRSA(SecretKey aesKey, PublicKey publicKey) throws Exception {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC");
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return rsaCipher.doFinal(aesKey.getEncoded());
    }

    // Firmado

    public static void Firmado() throws Exception{
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("Rainbow", "BCPQC");
        keyPairGen.initialize(RainbowParameterSpec.rainbowIIIclassic);
        KeyPair keyPairRainbow = keyPairGen.generateKeyPair();

        String mensaje = "Mensaje para firmar con Rainbow";
        byte[] firmaRainbow = firmarRainbow(keyPairRainbow.getPrivate(), mensaje.getBytes());

        boolean verificadoRainbow = verificarRainbow(keyPairRainbow.getPublic(), mensaje.getBytes(), firmaRainbow);

        keyPairGen = KeyPairGenerator.getInstance("SPHINCSPlus", "BCPQC");
        keyPairGen.initialize(SPHINCSPlusParameterSpec.sha2_256f);
        KeyPair keyPairSPHINCS = keyPairGen.generateKeyPair();

        byte[] firmaSPHINCS = firmarSPHINCS(keyPairSPHINCS.getPrivate(), mensaje.getBytes());

        boolean verificadoSPHINCS = verificarSPHINCS(keyPairSPHINCS.getPublic(), mensaje.getBytes(), firmaSPHINCS);
    }

    // Rainbow

    public static byte[] firmarRainbow(PrivateKey privateKey, byte[] data) throws Exception {
        Signature signature = Signature.getInstance("Rainbow", "BCPQC");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }
    
    public static boolean verificarRainbow(PublicKey publicKey, byte[] data, byte[] firma) throws Exception {
        Signature signature = Signature.getInstance("Rainbow", "BCPQC");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(firma);
    }

    // Sphincs+

    public static byte[] firmarSPHINCS(PrivateKey privateKey, byte[] data) throws Exception {
        Signature signature = Signature.getInstance("SPHINCSPlus", "BCPQC");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }
    
    public static boolean verificarSPHINCS(PublicKey publicKey, byte[] data, byte[] firma) throws Exception {
        Signature signature = Signature.getInstance("SPHINCSPlus", "BCPQC");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(firma);
    }
    
    /*
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
    */
}
