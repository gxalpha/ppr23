const height = 600
const width = 800
const margin = 50

let chart = d3.select("#sentiment_boxplot")
    .append("svg")
    .attr("width", width + margin)
    .attr("height", height + margin)
    .append("g")
    .attr("transform", `translate(${margin}, ${margin})`)


let aggregate_sentiments = d3.nest()
    .key(function (param) {
        return param.fraktion
    })
    .rollup(function (param) {
        let sorted_data = param[0].sentiments.sort(d3.ascending)
        let lower_quantile = d3.quantile(sorted_data, 0.25)
        let median = d3.quantile(sorted_data, 0.5)
        let upper_quantile = d3.quantile(sorted_data, 0.75)
        let min = sorted_data[0]
        let max = sorted_data[sorted_data.length - 1]

        let color = param[0].color
        return ({
            lower_quantile: lower_quantile,
            median: median,
            upper_quantile: upper_quantile,
            min: min,
            max: max,
            color: color
        })
    })
    .entries(reden_sentiment)

let x_scale = d3.scaleBand()
    .domain(d3.values(aggregate_sentiments).map(function (d) {
        return d.key
    }))
    .range([0, width - margin])
    .paddingInner(1)
    .paddingOuter(.5)
chart.append("g")
    .attr("transform", `translate(0,${height - margin})`)
    .call(d3.axisBottom(x_scale))


let y_scale = d3.scaleLinear()
    .domain([1, -1])
    .range([0, height - margin])
chart.append("g").call(d3.axisLeft(y_scale))


chart.selectAll("center_lines")
    .data(aggregate_sentiments)
    .enter()
    .append("line")
    .attr("x1", function (param) {
        return x_scale(param.key)
    })
    .attr("x2", function (param) {
        return x_scale(param.key)
    })
    .attr("y1", function (param) {
        return y_scale(param.value.min)
    })
    .attr("y2", function (param) {
        return y_scale(param.value.max)
    })
    .attr("stroke", "black")

let half_box_width = 20

chart.selectAll("boxes")
    .data(aggregate_sentiments)
    .enter()
    .append("rect")
    .attr("x", function (param) {
        return x_scale(param.key) - half_box_width
    })
    .attr("y", function (param) {
        return y_scale(param.value.upper_quantile)
    })
    .attr("width", function (_) {
        return 2 * half_box_width
    })
    .attr("height", function (param) {
        return y_scale(param.value.lower_quantile) - y_scale(param.value.upper_quantile)
    })
    .attr("stroke", "black")
    .attr("fill", function (param) {
        return param.value.color
    })

chart.selectAll("upper_lines")
    .data(aggregate_sentiments)
    .enter()
    .append("line")
    .attr("x1", function (param) {
        return x_scale(param.key) - half_box_width
    })
    .attr("x2", function (param) {
        return x_scale(param.key) + half_box_width
    })
    .attr("y1", function (param) {
        return y_scale(param.value.max)
    })
    .attr("y2", function (param) {
        return y_scale(param.value.max)
    })
    .attr("stroke", "black")

chart.selectAll("median_lines")
    .data(aggregate_sentiments)
    .enter()
    .append("line")
    .attr("x1", function (param) {
        return x_scale(param.key) - half_box_width
    })
    .attr("x2", function (param) {
        return x_scale(param.key) + half_box_width
    })
    .attr("y1", function (param) {
        return y_scale(param.value.median)
    })
    .attr("y2", function (param) {
        return y_scale(param.value.median)
    })
    .attr("stroke", "black")

chart.selectAll("lower_lines")
    .data(aggregate_sentiments)
    .enter()
    .append("line")
    .attr("x1", function (param) {
        return x_scale(param.key) - half_box_width
    })
    .attr("x2", function (param) {
        return x_scale(param.key) + half_box_width
    })
    .attr("y1", function (param) {
        return y_scale(param.value.min)
    })
    .attr("y2", function (param) {
        return y_scale(param.value.min)
    })
    .attr("stroke", "black")
