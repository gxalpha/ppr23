let changing = false

async function startProtocols() {
    changing = true
    document.getElementById("start").style.display = "none"

    await fetch("/admin/sessions/start")
    document.getElementById("stop").style.display = "block"
    document.getElementById("detailed_progress_container").style.display = "block"
    changing = false
}

async function stopProtocols() {
    changing = true
    document.getElementById("stop").style.display = "none"

    await fetch("/admin/sessions/stop")
    document.getElementById("start").style.display = "block"
    document.getElementById("detailed_progress_container").style.display = "none"
    changing = false
}

document.getElementById("controls_container").style.display = "none"
document.getElementById("progress_bar_container").style.display = "none"
document.getElementById("detailed_progress_container").style.display = "none"
document.getElementById("stop").style.display = "none"


let analyzerReady = false

async function updateProgress() {
    let responseData = await fetch("/admin/sessions/status", {
        headers: {
            "Content-Type": "application/json"
        }
    })
    let response = await responseData.json()

    if (!analyzerReady) {
        if (response.status === "running" || response.status === "notRunning") {
            analyzerReady = true
            document.getElementById("controls_container").style.display = "block"
            document.getElementById("progress_bar_container").style.display = "block"
            document.getElementById("startup_progress_bar_container").style.display = "none"
        } else {
            return
        }
    }

    if (response.status === "running" && !changing) {
        document.getElementById("start").style.display = "none"
        document.getElementById("detailed_progress_container").style.display = "block"
        document.getElementById("stop").style.display = "block"
    } else if (response.status === "notRunning" && !changing) {
        document.getElementById("start").style.display = "block"
        document.getElementById("detailed_progress_container").style.display = "none"
        document.getElementById("stop").style.display = "none"
    } else {
        // Unknown response. Possibly not authorized?
    }

    let total_progress = response.total_progress * 100
    document.getElementById("progress_bar_text").innerHTML = Math.round(total_progress * 100) / 100 + "%"
    document.getElementById("progress_bar").value = total_progress
    if (response.status === "running") {
        document.getElementById("detailed_progress").innerHTML = response.detailed_progress
    }
}

window.setInterval(updateProgress, 1000)
updateProgress()
