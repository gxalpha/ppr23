let changing = false

function startNlp() {
    changing = true
    fetch("/nlp/start")
    document.getElementById("progress_bar_container").style.display = "block"
    document.getElementById("start").style.display = "none"
    document.getElementById("stop").style.display = "block"
    changing = false
}

async function stopNlp() {
    changing = true
    document.getElementById("stop").style.display = "none"

    await fetch("/nlp/stop")
    document.getElementById("progress_bar_container").style.display = "none"
    document.getElementById("start").style.display = "block"
    changing = false
}

document.getElementById("progress_bar_container").style.display = "none"
document.getElementById("controls_container").style.display = "none"
document.getElementById("stop").style.display = "none"


let nlpReady = false

async function updateProgress() {
    if (!nlpReady) {
        let response = await fetch("/nlp/readyStatus")
        response = await response.text()
        if (response === "ready") {
            nlpReady = true
            document.getElementById("controls_container").style.display = "block"
            document.getElementById("startup_progress_bar_container").style.display = "none"
        } else {
            return
        }
    }

    let response = await fetch("/nlp/runningStatus")
    response = await response.text()
    if (response === "running" && !changing) {
        document.getElementById("progress_bar_container").style.display = "block"
        document.getElementById("start").style.display = "none"
        document.getElementById("stop").style.display = "block"
    } else if (!changing) {
        document.getElementById("progress_bar_container").style.display = "none"
        document.getElementById("start").style.display = "block"
        document.getElementById("stop").style.display = "none"
    }

    response = await fetch("/nlp/progress")
    let progress = await response.text() * 100
    document.getElementById("progress_bar_text").innerHTML = Math.round(progress * 100) / 100 + "%"
    document.getElementById("progress_bar").value = progress
}

window.setInterval(updateProgress, 1000)
updateProgress()
