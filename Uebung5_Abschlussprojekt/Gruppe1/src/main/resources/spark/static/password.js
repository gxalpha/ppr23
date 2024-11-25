let bcrypt = dcodeIO.bcrypt;

/**
 * Pre-hashes a password.
 * We also do server-side hashing of whatever password gets sent,
 * but I don't really want the cleartext password to go to the
 * server in the first place. If someone is "malicious", they could
 * of course manually send whatever they want, but again this is
 * just so that we don't *accidentally* receive a real password (and
 * it would be hashed on the server anyways).
 *
 * @param {string} username Username to use as base for the salt
 * @param {string} password Password to hash
 */
function prehashPassword(username, password) {
    let utf8Encode = new TextEncoder();
    /*
     * Hash signs aren't allowed in usernames.
     * This stupid mechanism makes sure no two salts accidentally are the same.
     */
    let salt = username + "#"

    // bcrypt has annoying requirements for salts since it expects to generate it itself.
    function saltToBase64(salt) {
        salt = utf8Encode.encode(salt)
        salt = bcrypt.encodeBase64(salt, salt.length)
        return salt
    }

    while (bcrypt.decodeBase64(saltToBase64(salt), 16).length < 16) {
        salt += username
    }

    return bcrypt.hashSync(password, "$2a$12$" + saltToBase64(salt))
}
