$(document).ready(function() {

    var $dashboard = $('.dashboard');
    var $chartContainer = $('<div>').addClass('chart-container');
    var $chart = $('<canvas>').addClass('chart');
    var projectColors = {};
    var locationColors = {};
    var userNames = {};
    var locationNames = {};


    (function init(){
        PROJECTS.forEach(function(project) {
            projectColors[project.name] = project.color;
        });
        USERS.forEach(function(user) {
            userNames[user.id] = user.name;
        });
        LOCATIONS.forEach(function(location) {
            locationNames[location.id] = location.name;
            locationColors[location.id] = location.color;
        });
    })();

    (function drawLocationGraph(){
        new Chart(generateChartDiv(), {
            type: 'horizontalBar',
            data: {
                labels: mapLocationNames(DASHBOARD.locationWeekDataset.keys),
                datasets: [{
                    data: DASHBOARD.locationWeekDataset.values,
                    backgroundColor: mapColors(DASHBOARD.locationWeekDataset.keys, "0.2"),
                    borderColor: mapColors(DASHBOARD.locationWeekDataset.keys, "1"),
                    borderWidth: 1
                }]
            },
            options : {
                animation : false,
                title: {
                    display: true,
                    text: "Locations this week"
                },
                legend: {
                    display: false
                },
                tooltips: {
                    enabled: false
                },
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero:true
                        }
                    }],
                    xAxes: [{
                        ticks: {
                            beginAtZero:true
                        }
                    }]
                }
            }
        });
    })();

    (function drawEventGraph(){
        new Chart(generateChartDiv(), {
            type: 'line',
            data: {
                labels: DASHBOARD.eventWeekDataset.keys,
                datasets: [{
                    data: DASHBOARD.eventWeekDataset.values,
                    backgroundColor: provideColors(DASHBOARD.eventWeekDataset.keys.length, "0.2"),
                    borderColor: provideColors(DASHBOARD.eventWeekDataset.keys.length, "1"),
                    borderWidth: 1
                }]
            },
            options : {
                animation : false,
                title: {
                    display: true,
                    text: "Events this week"
                },
                legend: {
                    display: false
                },
                tooltips: {
                    enabled: false
                },
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero:true
                        }
                    }]
                }
            }
        });
    })();

    (function drawEquipmentGraph(){
        var datasets = [];
        for(var i=0;i<DASHBOARD.projectEventsDatasets.length;i++) {
            var projectName = DASHBOARD.projectEventsDatasets[i].title;
            var projectColor = projectColors[projectName]
            datasets.push({
                label: projectName,
                data: DASHBOARD.projectEventsDatasets[i].values,
                backgroundColor: hexToRgbA(projectColor, 0.1),
                borderColor: hexToRgbA(projectColor, 1),
                borderWidth: 1
            })
        }


        new Chart(generateChartDiv(), {
            type: 'line',
            data: {
                labels: DASHBOARD.projectEventsDatasets[0].keys,
                datasets: datasets
            },
            options : {
                animation : false,
                title: {
                    display: true,
                    text: "Events by projects this week"
                },
                tooltips: {
                    enabled: false
                },
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero:true
                        }
                    }]
                }
            }
        });
    })();

    (function drawOperatorGraph(){
        var datasets = [];
        for(var i=0;i<DASHBOARD.operatorEventsDatasets.length;i++) {
            datasets.push({
                label: mapUserNames([DASHBOARD.operatorEventsDatasets[i].title]),
                data: DASHBOARD.operatorEventsDatasets[i].values,
                backgroundColor: provideColor(i, 0.1),
                borderColor: provideColor(i, 1),
                borderWidth: 1
            })
        }


        new Chart(generateChartDiv(), {
            type: 'line',
            data: {
                labels: DASHBOARD.operatorEventsDatasets[0].keys,
                datasets: datasets
            },
            options : {
                animation : false,
                title: {
                    display: true,
                    text: "Operators this week"
                },
                tooltips: {
                    enabled: false
                },
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero:true
                        }
                    }]
                }
            }
        });
    })();

    (function drawFreeOperatorSlotGraph(){
        var datasets = [];
        for(var i=0;i<DASHBOARD.freeOperatorSlotEventsDatasets.length;i++) {
            datasets.push({
                label: mapUserNames([DASHBOARD.freeOperatorSlotEventsDatasets[i].title]),
                data: DASHBOARD.freeOperatorSlotEventsDatasets[i].values,
                backgroundColor: provideColor(i, 0.1),
                borderColor: provideColor(i, 1),
                borderWidth: 1
            })
        }


        new Chart(generateChartDiv(), {
            type: 'line',
            data: {
                labels: DASHBOARD.freeOperatorSlotEventsDatasets[0].keys,
                datasets: datasets
            },
            options : {
                animation : false,
                title: {
                    display: true,
                    text: "Free hours of operators this week"
                },
                tooltips: {
                    enabled: false
                },
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero:true
                        }
                    }]
                }
            }
        });
    })();

    function mapColors(keys, alpha){
        return keys.map(function (key) {
            return hexToRgbA(locationColors[key], alpha);
        });
    }

    function mapLocationNames(keys){
        return keys.map(function (key) {
            return locationNames[key];
        });
    }

    function mapUserNames(keys){
        return keys.map(function (key) {
            return userNames[key];
        });
    }

    function generateChartDiv() {
        var $currentChart = $chart.clone();
        var $currentChartContainer = $chartContainer.clone().append($currentChart);
        $dashboard.append($currentChartContainer);
        return $currentChart;
    }

    function hexToRgbA(hex, alpha){
        var c;
        if(/^#([A-Fa-f0-9]{3}){1,2}$/.test(hex)){
            c= hex.substring(1).split('');
            if(c.length== 3){
                c= [c[0], c[0], c[1], c[1], c[2], c[2]];
            }
            c= '0x'+c.join('');
            return 'rgba('+[(c>>16)&255, (c>>8)&255, c&255].join(',')+',' + alpha + ')';
        }
        return "#555";
    }

    function provideColors(amount, opacity) {
        return [
           'rgba(255, 99, 132, ' + opacity + ')',
           'rgba(54, 162, 235, ' + opacity + ')',
           'rgba(255, 206, 86, ' + opacity + ')',
           'rgba(75, 192, 192, ' + opacity + ')',
           'rgba(153, 102, 255, ' + opacity + ')',
           'rgba(255, 159, 64, ' + opacity + ')'
        ].slice(0, amount)
    }

    function provideColor(index, opacity) {
        return [
           'rgba(255, 99, 132, ' + opacity + ')',
           'rgba(54, 162, 235, ' + opacity + ')',
           'rgba(255, 206, 86, ' + opacity + ')',
           'rgba(75, 192, 192, ' + opacity + ')',
           'rgba(153, 102, 255, ' + opacity + ')',
           'rgba(255, 159, 64, ' + opacity + ')'
        ].slice(index, index + 1)
    }
});