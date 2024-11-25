function radarChart(chart_selector, data) {

    // Mithilfe von folgender Quelle erstellt: https://irene.hashnode.dev/radar-charts-with-d3js

    // Ränder etc. festlegen
    const height = 400
    const width = 400
    const margin = 100

    // Radar-Chart erstellen
    let svg = d3.select(chart_selector)
        .append("svg")
        .attr("width", width + 2*margin)
        .attr("height", height+ margin)
        .append("g")
        .attr("transform", `translate(${margin}, ${margin})`)

    // Anzahl der Achsen anhand der Kategorien (pos, neu, neg) bestimmen
    var categories = data.map(function(d) {return d.name;});

    var angleSlice = Math.PI * 2 / data.length;

    // Achsen zeichnen
    var axis = svg.selectAll(".axis")
    .data(categories)
    .enter().append("g")
    .attr("class", "axis");

    axis.append("line")
    .attr("x1", width / 2)
    .attr("y1", height / 2)
    .attr("x2", function(d, i) {return width / 2 * (1 - Math.sin(angleSlice * i));})
    .attr("y2", function(d, i) {return height / 2 * (1 - Math.cos(angleSlice * i));});

    // Achsenbeschriftung
    axis.append("text")
    .attr("class", "title")
    .attr("transform", function(d, i) {
    var angle = angleSlice * i;
    var x = width / 2 * (1 - 1.2 * Math.sin(angle)) - 30;
    var y = height / 2 * (1 - 1.2 * Math.cos(angle));
    return "translate(" + x + "," + y + ")";})
    .text(function(d) {return d;});

    var line = d3.line()
    .x(function(d, i) {return width / 2 * (1 - d.value * Math.sin(angleSlice * i));})
    .y(function(d, i) {return height / 2 * (1 - d.value * Math.cos(angleSlice * i));});

    svg.append("path")
    .datum(data)
    .attr("class", "line")
    .attr("d", line);

    // Punkte einfügen
    var point = svg.selectAll("circle")
    .data(data)
    .enter().append("circle")
    .attr("class", "point")
    .attr("cx", function(d, i) {return width / 2 * (1 - d.value * Math.sin(angleSlice * i));})
    .attr("cy", function(d, i) {return height / 2 * (1 - d.value * Math.cos(angleSlice * i));})
    .attr("r", 5)
    .attr("stroke", "gray")
}