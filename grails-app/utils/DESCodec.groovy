/**
 * Created by sthaiya on 2/1/15.
 */

import grails.util.Holders
import org.apache.commons.codec.binary.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom

class DESCodec {

    static ConfigObject config = Holders.config

    def static CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"
    def static KEY_ALGORITHM = "AES"
    def static PASSWORD_HASH_ALGORITHM = "PBKDF2WithHmacSHA1"

    def static SALT_LENGTH = 8
    def static random = new SecureRandom()
    static String encoded = ""
    static String base64EncodedSalt = ""
    static String base64EncodedIv = ""

    static decode = { String target ->
        def locations =  config.grails.config.locations
        String configFileName = locations[0].split("file:")[1]

        def props = loadProperties(configFileName)
        base64EncodedIv = props.getProperty('options.password.hashing.iv', null)
        base64EncodedSalt = props.getProperty('options.password.hashing.salt', null)
        encoded = props.getProperty('options.password.encrypted', "false")

        if (encoded.equals("false")) {
            // passphrase is not encoded so we encode it
            base64EncodedSalt = toBase64(generateSalt())
            def cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            base64EncodedIv = toBase64(generateIv(cipher.getBlockSize()))
            target = encode(target)

            //now password is encoded so we modify the file and save it
            modifyProperties(configFileName, target)
        }
        def cipher = getCipher(Cipher.DECRYPT_MODE)
        def decodedPassword = new String(cipher.doFinal(target.decodeBase64()))

        return decodedPassword
    }

    private static encode(String target) {
        def cipher = getCipher(Cipher.ENCRYPT_MODE)
        return cipher.doFinal(target.bytes).encodeBase64()
    }

    private static OrderedProperties loadProperties(String configFileName) {
        def builder = new OrderedProperties.OrderedPropertiesBuilder();
        builder.withOrdering(String.CASE_INSENSITIVE_ORDER);
        builder.withSuppressDateInComment(true);
        OrderedProperties props = builder.build();

        File propsFile = new File(configFileName)

        props.load(new FileInputStream(propsFile))

        return props
    }

    private static modifyProperties(String configFileName, String encodedPassword) {
        File propsFile = new File(configFileName)
        def props = loadProperties(configFileName)
        props.setProperty('options.password.hashing.iv', base64EncodedIv)
        props.setProperty('options.password.hashing.salt', base64EncodedSalt)
        props.setProperty('options.password.encrypted', "true")
        props.setProperty('dataSource.password', encodedPassword)
        props.store(propsFile.newWriter(), null)
    }

    private static SecretKey deriveKey(String passphrase, byte[] salt) throws Exception {
        // minimum values recommended by PKCS#5
        def ITERATION_COUNT = 1000
        def KEY_LENGTH = 256

        /* Use this to securely derive a key from the passphrase: */
        def keySpec = new PBEKeySpec(passphrase.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
        def keyFactory = SecretKeyFactory.getInstance(PASSWORD_HASH_ALGORITHM)
        def keyBytes = keyFactory.generateSecret(keySpec).getEncoded()

        return new SecretKeySpec(keyBytes, KEY_ALGORITHM)
    }

    private static getCipher(mode) {
        def cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        def ivParams = new IvParameterSpec(fromBase64(base64EncodedIv))
        def key = deriveKey(getPassPhrase(), fromBase64(base64EncodedSalt))
        cipher.init(mode, key, ivParams)
        return cipher
    }

    /**
     * The passphrase used to create the cipher.  This is not the same as the password
     * that we want to encrypt/decrypt.  That's specified in the call argument(s).
     *
     * @return
     */
    private static getPassPhrase() {
        return "MuzimaS1r1yaEncrypt1ngP@ssw0rd"
    }

    private static generateIv(length) {
        def ivBytes = new byte[length]
        random.nextBytes(ivBytes)

        return ivBytes
    }

    private static generateSalt() {
        def saltBytes = new byte[SALT_LENGTH]
        random.nextBytes(saltBytes)

        return saltBytes
    }

    private static String toBase64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    private static byte[] fromBase64(String base64) {
        return Base64.decodeBase64(base64);
    }

    static void main(args) {
        if(args) {
            println encode(args[0])
        }
    }
}