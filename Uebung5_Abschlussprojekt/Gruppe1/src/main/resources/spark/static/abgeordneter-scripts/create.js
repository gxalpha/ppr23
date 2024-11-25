function createAbgeordneter() {
    let id = document.getElementById("input_id").value
    if (id === "") {
        alert("ID darf nicht leer sein!")
        return
    }

    let body = {
        id: id
    }
    fetch("/abgeordneter/create", {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            "Content-Type": "application/json"
        }
    }).then(async function (data) {
        let response = await data.json()
        if (response.status === "success") {
            window.location.replace("/abgeordneter/edit?id=" + id)
        } else {
            alert(`Unknown response: ${response.status}`)
        }
    })
}
