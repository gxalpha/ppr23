function createUser() {
    let username = document.getElementById("input_create_username").value
    if (username === "") {
        alert("Der Benutzername darf nicht leer sein.")
        return
    }
    let password = document.getElementById("input_create_password").value
    if (password === "") {
        alert("Das Passwort darf nicht leer sein.")
        return
    }

    let hashed_password = prehashPassword(username, password)

    let body = {
        username: username,
        password: hashed_password
    }
    fetch("/admin/users/create", {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            "Content-Type": "application/json"
        }
    }).then(async function (data) {
        let response = await data.json()
        if (response.status === "success") {
            location.reload()
        } else if (response.status === "userAlreadyExists") {
            alert("There already is a user with that name.")
        } else {
            alert(`Unknown response: ${response.status}`)
        }
    })
}

function deleteUser() {
    let username = document.getElementById("input_delete_username").value
    if (username === "") {
        alert("Der Benutzername darf nicht leer sein.")
        return
    }

    let body = {
        username: username
    }
    fetch("/admin/users/delete", {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            "Content-Type": "application/json"
        }
    }).then(async function (data) {
        let response = await data.json()
        if (response.status === "success") {
            location.reload()
        } else if (response.status === "userNotFound") {
            alert("There is no user with that name.")
        } else {
            alert(`Unknown response: ${response.status}`)
        }
    })
}

function addUserToGroup() {
    let username = document.getElementById("input_addgroup_user").value
    if (username === "") {
        alert("Der Benutzername darf nicht leer sein.")
        return
    }

    let group_name = document.getElementById("input_addgroup_group").value
    if (group_name === "") {
        alert("Der Gruppenname darf nicht leer sein.")
        return
    }

    let body = {
        username: username,
        group_name: group_name
    }
    fetch("/admin/users/addgroup", {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            "Content-Type": "application/json"
        }
    }).then(async function (data) {
        let response = await data.json()
        if (response.status === "success") {
            location.reload()
        } else if (response.status === "userNotFound") {
            alert("There is no user with that name.")
        } else if (response.status === "groupNotFound") {
            alert("There is no group with that name.")
        } else if (response.status === "userAlreadyInGroup") {
            alert("The user already is in that group.")
        } else if (response.status === "userIsWebmaster") {
            alert("The user is a webmaster. Their groups cannot be modified.")
        } else {
            alert(`Unknown response: ${response.status}`)
        }
    })
}

function removeUserFromGroup() {
    let username = document.getElementById("input_removegroup_user").value
    if (username === "") {
        alert("Der Benutzername darf nicht leer sein.")
        return
    }

    let group_name = document.getElementById("input_removegroup_group").value
    if (group_name === "") {
        alert("Der Gruppenname darf nicht leer sein.")
        return
    }

    let body = {
        username: username,
        group_name: group_name
    }
    fetch("/admin/users/removegroup", {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            "Content-Type": "application/json"
        }
    }).then(async function (data) {
        let response = await data.json()
        if (response.status === "success") {
            location.reload()
        } else if (response.status === "userNotFound") {
            alert("There is no user with that name.")
        } else if (response.status === "groupNotFound") {
            alert("There is no group with that name.")
        } else if (response.status === "userNotInGroup") {
            alert("The user wasn't even in that group.")
        } else if (response.status === "userIsWebmaster") {
            alert("The user is a webmaster. Their groups cannot be modified.")
        } else {
            alert(`Unknown response: ${response.status}`)
        }
    })
}
