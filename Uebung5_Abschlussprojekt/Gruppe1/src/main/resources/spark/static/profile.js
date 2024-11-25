function changePassword() {
    let old_password = document.getElementById("input_changepassword_old").value
    if (old_password === "") {
        alert("Das neue Passwort darf nicht leer sein.")
        return
    }

    let new_password = document.getElementById("input_changepassword_new").value
    if (new_password === "") {
        alert("Das neue Passwort darf nicht leer sein.")
        return
    }

    old_password = prehashPassword(username, old_password)
    new_password = prehashPassword(username, new_password)

    let body = {
        username: username,
        old_password: old_password,
        new_password: new_password
    }
    fetch("/profile/changepassword", {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            "Content-Type": "application/json"
        }
    }).then(async function (data) {
        let response = await data.json()
        if (response.status === "success") {
            alert("Success! Please log in using your new credentials again.")
            location.reload()
        } else if (response.status === "oldPasswordInvalid") {
            alert("The old password was not accepted. Please make sure you've entered the correct password.")
        } else {
            alert(`Unknown response: ${response.status}`)
        }
    })
}
