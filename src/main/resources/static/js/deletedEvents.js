var $grid = $("#jsGrid");
var REST_URL = '/rest/' + ROOT_URL;

$grid.jsGrid({
    width: "100%",
    height: "auto",
    data:HISTORY.map(function(item){
        item.author = item.author.name;
        item.title = item.diff.title;
        item.project = item.diff.project ? item.diff.project.name : "";
        item.date = item.date.substring(0, item.date.length - 10).replace("T", " ");
        return item;
    }),
    inserting: false,
    editing: false,
    sorting: false,

    fields: [
        { title: "Title", name: "title", type: "text", width: 150 },
        { title: "Project", name: "project", type: "text", width: 50 },
        { title: "Date", name: "date", type: "text", width: 150 },
        { title: "Author", name: "author", type: "text", width: 150 },
    ]
});