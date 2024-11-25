function barChart(chart_selector, data) {

    // Daten nach Wert sortieren
    data = data.sort(function (a, b) {
        return d3.ascending(a.value, b.value);
    })

    // Ränder etc. festlegen
    const height = data.length * 50
    const width = 800
    const margin = {top: 20, right: 30, bottom: 30, left: 200}

    // Balkendiagramm erstellen
    let svg = d3.select(chart_selector)
        .append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height+ 2*margin.top)
        .append("g")
        .attr("transform", `translate(${margin}, ${margin})`)

    var x = d3.scaleLinear()
        .range([0, width])
        .domain([0, d3.max(data, function (d) {
            return d.value;
        })]);

    var y = d3.scaleBand()
        .range([height, 0])
        .padding(0.1)
        .domain(data.map(function (d) {
            return d.name;
        }));

    // Achsen sowie deren Skalierung festlegen
    var g = svg.append("g")
        .attr("transform", "translate(" + margin.left + ")");

    g.append("g")
        .attr("class", "axis axis--x")
        .attr("transform", "translate(0," + height +")") // Move x-axis to the bottom
        .call(d3.axisBottom(x));

    g.append("g")
        .attr("class", "axis axis--y")
        .call(d3.axisLeft(y));

    g.selectAll(".bar")
        .data(data)
        .enter().append("rect")
        .attr("class", "bar")
        .attr("x", 0)
        .attr("y", function (d) {
            return y(d.name);
        })
        .attr("width", function (d) {
            return x(d.value);
        })
        .attr("height", y.bandwidth());

    // Beschriftung der x-Achse
    svg.append("text")
        .attr("class", "x label")
        .attr("text-anchor", "end")
        .attr("x", 1.15 * width)
        .attr("y", height + 40)
        .text("Anzahl");
}
