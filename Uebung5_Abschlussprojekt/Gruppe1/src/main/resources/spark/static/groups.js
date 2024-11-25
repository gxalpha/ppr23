function createGroup() {
    let group_name = document.getElementById("input_create").value
    if (group_name === "") {
        alert("Der Gruppenname darf nicht leer sein.")
        return
    }

    let body = {
        group_name: group_name
    }
    fetch("/admin/groups/create", {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            "Content-Type": "application/json"
        }
    }).then(async function (data) {
        let response = await data.json()
        if (response.status === "success") {
            location.reload()
        } else if (response.status === "groupAlreadyExists") {
            alert("There already is a group with that name.")
        } else {
            alert(`Unknown response: ${response.status}`)
        }
    })
}

function deleteGroup() {
    let group_name = document.getElementById("input_delete").value
    if (group_name === "") {
        alert("Der Gruppenname darf nicht leer sein.")
        return
    }

    let body = {
        group_name: group_name
    }
    fetch("/admin/groups/delete", {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            "Content-Type": "application/json"
        }
    }).then(async function (data) {
        let response = await data.json()
        if (response.status === "success") {
            location.reload()
        } else if (response.status === "groupNotFound") {
            alert("There is no group with that name.")
        } else {
            alert(`Unknown response: ${response.status}`)
        }
    })
}

function grantPermission() {
    let group_name = document.getElementById("input_grantperm_name").value
    if (group_name === "") {
        alert("Der Gruppenname darf nicht leer sein.")
        return
    }
    let permission = document.getElementById("input_grantperm_permission").value
    if (permission === "") {
        alert("Die Berechtigung darf nicht leer sein.")
        return
    }

    let body = {
        group_name: group_name,
        permission: permission
    }
    fetch("/admin/groups/grant", {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            "Content-Type": "application/json"
        }
    }).then(async function (data) {
        let response = await data.json()
        if (response.status === "success") {
            location.reload()
        } else if (response.status === "groupNotFound") {
            alert("There is no group with that name.")
        } else if (response.status === "groupAlreadyHasPermission") {
            alert("The given group already has the selected permission.")
        } else {
            alert(`Unknown response: ${response.status}`)
        }
    })
}

function revokePermission() {
    let group_name = document.getElementById("input_revokeperm_name").value
    if (group_name === "") {
        alert("Der Gruppenname darf nicht leer sein.")
        return
    }
    let permission = document.getElementById("input_revokeperm_permission").value
    if (permission === "") {
        alert("Die Berechtigung darf nicht leer sein.")
        return
    }

    let body = {
        group_name: group_name,
        permission: permission
    }
    fetch("/admin/groups/revoke", {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            "Content-Type": "application/json"
        }
    }).then(async function (data) {
        let response = await data.json()
        if (response.status === "success") {
            location.reload()
        } else if (response.status === "groupNotFound") {
            alert("There is no group with that name.")
        } else if (response.status === "groupDoesntHavePermission") {
            alert("The given group didn't even have the selected permission.")
        } else {
            alert(`Unknown response: ${response.status}`)
        }
    })
}
