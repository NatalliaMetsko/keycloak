package org.keycloak.keys;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.jboss.logging.Logger;
import org.keycloak.common.util.Base64;
import org.keycloak.component.ComponentModel;
import org.keycloak.crypto.KeyWrapper;
import org.keycloak.models.RealmModel;

public class GeneratedEcdsaKeyProvider extends AbstractEcdsaKeyProvider {
    private static final Logger logger = Logger.getLogger(GeneratedEcdsaKeyProvider.class);

    public GeneratedEcdsaKeyProvider(RealmModel realm, ComponentModel model) {
        super(realm, model);
    }

	@Override
	protected KeyWrapper loadKey(RealmModel realm, ComponentModel model) {
        String privateEcdsaKeyBase64Encoded = model.getConfig().getFirst(GeneratedEcdsaKeyProviderFactory.ECDSA_PRIVATE_KEY_KEY);
        String publicEcdsaKeyBase64Encoded = model.getConfig().getFirst(GeneratedEcdsaKeyProviderFactory.ECDSA_PUBLIC_KEY_KEY);
        String ecInNistRep = model.getConfig().getFirst(GeneratedEcdsaKeyProviderFactory.ECDSA_ELLIPTIC_CURVE_KEY);

        try {
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decode(privateEcdsaKeyBase64Encoded));
            KeyFactory kf = KeyFactory.getInstance("EC");
            PrivateKey decodedPrivateKey = kf.generatePrivate(privateKeySpec);

            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decode(publicEcdsaKeyBase64Encoded));
            PublicKey decodedPublicKey = kf.generatePublic(publicKeySpec);

            KeyPair keyPair = new KeyPair(decodedPublicKey, decodedPrivateKey);

            return createKeyWrapper(keyPair, ecInNistRep);
        } catch (Exception e) {
            logger.warnf("Exception at decodeEcdsaPublicKey. %s", e.toString());
            return null;
        }

    }

}
