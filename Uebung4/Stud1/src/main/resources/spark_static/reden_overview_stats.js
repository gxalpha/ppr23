let positive = reden_sentiment.map(element => element.sentiments.filter(val => val > 0).length).reduce((previousValue, currentValue) => previousValue + currentValue, 0)
let neutral = reden_sentiment.map(element => element.sentiments.filter(val => val === 0).length).reduce((previousValue, currentValue) => previousValue + currentValue, 0)
let negative = reden_sentiment.map(element => element.sentiments.filter(val => val < 0).length).reduce((previousValue, currentValue) => previousValue + currentValue, 0)

let total = positive + neutral + negative

document.getElementById("sentiment_pos").innerHTML = positive + " (" + Math.round((positive * 10_000) / total) / 100 + "%)"
document.getElementById("sentiment_neu").innerHTML = neutral + " (" + Math.round((neutral * 10_000) / total) / 100 + "%)"
document.getElementById("sentiment_neg").innerHTML = negative + " (" + Math.round((negative * 10_000) / total) / 100 + "%)"
document.getElementById("total_speeches").innerHTML = total.toString()
