package com.lanars.purchase.billing

import android.text.TextUtils
import android.util.Base64
import com.android.billingclient.api.Purchase
import com.android.billingclient.util.BillingHelper
import java.io.IOException
import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.Signature
import java.security.SignatureException
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec

/**
 * Security-related methods. For a secure implementation, all of this code should be implemented on
 * a server that communicates with the application on the device.
 */
object BillingSecurity {
    private const val TAG = "IABUtil/Security"
    private const val KEY_FACTORY_ALGORITHM = "RSA"
    private const val SIGNATURE_ALGORITHM = "SHA1withRSA"
    private const val base64Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkdcTejdL/4KyG0u7SnQdTqAVMXJCmBGDzjiCXMjtrbTbnfNGuJQQUIRzd00dv4OBx3fwlPP76029UE/qqhUfV7D/KRfF/e7KwyDBXLSQbZVmiDUxna57QIouiAJJBoR/aua3Sk858OYzxy6XPIboucdVNw5vVcZLm2JMiH/fE8YXvfm+BzbA8OmM5qSDfgS77MITsziiRHc50GDExfS17llgNm064Rc2VlzzFhU5DHN61QULnVfDOM1i7qP5Jhk/glyMcbwjgZWPyye+m+tthcAKcQCXYD+tjC0/+H80sgPY0DkWEjIiytu7BTtq54kWPV/TLr3oJxlWLA4I+HOcywIDAQAB"

    fun verifyValidSignature(purchase: Purchase): Boolean {
        return try {
            verifyPurchase(base64Key, purchase.originalJson, purchase.signature)
        } catch (e: IOException) {
            BillingHelper.logWarn(TAG, "Got an EXCEPTION trying to validate a purchase: $e")
            false
        }
    }

    /**
     * Verifies that the data was signed with the given signature, and returns the verified
     * purchase.
     * @param base64PublicKey the base64-encoded public key to use for verifying.
     * @param signedData the signed JSON string (signed, not encrypted)
     * @param signature the signature for the data, signed with the private key
     * @throws IOException if encoding algorithm is not supported or key specification
     * is invalid
     */
    @Throws(IOException::class)
    fun verifyPurchase(base64PublicKey: String, signedData: String,
                       signature: String): Boolean {
        if (TextUtils.isEmpty(signedData) || TextUtils.isEmpty(base64PublicKey)
                || TextUtils.isEmpty(signature)) {
            BillingHelper.logWarn(TAG, "Purchase verification failed: missing data.")
            return false
        }

        val key = generatePublicKey(base64PublicKey)
        return verify(key, signedData, signature)
    }

    /**
     * Generates a PublicKey instance from a string containing the Base64-encoded public key.
     *
     * @param encodedPublicKey Base64-encoded public key
     * @throws IOException if encoding algorithm is not supported or key specification
     * is invalid
     */
    @Throws(IOException::class)
    fun generatePublicKey(encodedPublicKey: String): PublicKey {
        try {
            val decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT)
            val keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM)
            return keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))
        } catch (e: NoSuchAlgorithmException) {
            // "RSA" is guaranteed to be available.
            throw RuntimeException(e)
        } catch (e: InvalidKeySpecException) {
            val msg = "Invalid key specification: $e"
            BillingHelper.logWarn(TAG, msg)
            throw IOException(msg)
        }

    }

    /**
     * Verifies that the signature from the server matches the computed signature on the data.
     * Returns true if the data is correctly signed.
     *
     * @param publicKey public key associated with the developer account
     * @param signedData signed data from server
     * @param signature server signature
     * @return true if the data and signature match
     */
    private fun verify(publicKey: PublicKey, signedData: String, signature: String): Boolean {
        val signatureBytes: ByteArray
        try {
            signatureBytes = Base64.decode(signature, Base64.DEFAULT)
        } catch (e: IllegalArgumentException) {
            BillingHelper.logWarn(TAG, "Base64 decoding failed.")
            return false
        }

        try {
            val signatureAlgorithm = Signature.getInstance(SIGNATURE_ALGORITHM)
            signatureAlgorithm.initVerify(publicKey)
            signatureAlgorithm.update(signedData.toByteArray())
            if (!signatureAlgorithm.verify(signatureBytes)) {
                BillingHelper.logWarn(TAG, "Signature verification failed.")
                return false
            }
            return true
        } catch (e: NoSuchAlgorithmException) {
            // "RSA" is guaranteed to be available.
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            BillingHelper.logWarn(TAG, "Invalid key specification.")
        } catch (e: SignatureException) {
            BillingHelper.logWarn(TAG, "Signature exception.")
        }

        return false
    }
}
