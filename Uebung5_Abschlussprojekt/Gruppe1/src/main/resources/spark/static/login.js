function login() {
    let username = document.getElementById("input_username").value
    if (username === "") {
        alert("Der Benutzername darf nicht leer sein.")
        return
    }
    let password = document.getElementById("input_password").value
    if (password === "") {
        alert("Das Passwort darf nicht leer sein.")
        return
    }

    let hashed_password = prehashPassword(username, password)

    let body = {
        username: username,
        password: hashed_password
    }
    fetch("/login/authenticate", {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            "Content-Type": "application/json"
        }
    }).then(async function (data) {
        let response = await data.json()
        if (response.status === "success") {
            window.location.replace(redirectPage)
        } else if (response.status === "userNotFound") {
            alert("The user couldn't be found.")
        } else if (response.status === "userAuthenticationFailed") {
            alert("Authentication failed - are you sure the password is correct?")
        } else {
            alert(`Unknown response: ${response.status}`)
        }
    })
}
