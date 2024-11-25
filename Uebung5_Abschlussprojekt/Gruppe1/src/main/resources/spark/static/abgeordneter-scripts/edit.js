function sendStammdaten() {
    console.log("Sending...")

    let body = {
        abgeordneter: abgeordneterID,
        nachname: document.getElementById("input_nachname").value,
        vorname: document.getElementById("input_vorname").value,
        namenspraefix: document.getElementById("input_namenspraefix").value,
        adelssuffix: document.getElementById("input_adelssuffix").value,
        anrede: document.getElementById("input_anrede").value,
        geburtsdatum: document.getElementById("input_geburtsdatum").value,
        geburtsort: document.getElementById("input_geburtsort").value,
        geschlecht: document.getElementById("input_geschlecht").value,
        religion: document.getElementById("input_religion").value,
        beruf: document.getElementById("input_beruf").value,
        vita: document.getElementById("input_vita").value,
        partei: document.getElementById("input_partei").value,
        fraktion: document.getElementById("input_fraktion").value,
    }

    if (!/^[0-9]{4}-[0-9]{2}-[0-9]{2}$/.test(body.geburtsdatum)) {
        alert("Sehr invalides Datum, irgendwie hat es nichtmal den Regex bestanden...")
        return
    }

    fetch("/abgeordneter/edit/stammdaten", {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            "Content-Type": "application/json"
        }
    }).then(async function (data) {
        let response = await data.json()
        if (response.status === "success") {
            location.reload()
        } else {
            alert(`Unknown response: ${response.status}`)
        }
    })
}
