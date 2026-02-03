#!/bin/sh
# Generar keystore si no existe
if [ ! -f /app/certificates/keystore.p12 ]; then
    openssl pkcs12 -export \
        -in /app/certificates/certificate.crt \
        -inkey /app/certificates/private.key \
        -out /app/certificates/keystore.p12 \
        -name ${SERVER_SSL_KEY_ALIAS} \
        -password pass:${SSL_KEYSTORE_PASSWORD}
    chmod 644 /app/certificates/keystore.p12
fi

exec java ${JAVA_OPTS} -jar app.jar