var $grid = $("#jsGrid");
var REST_URL = '/rest/' + ROOT_URL;

$grid.jsGrid({
    width: "100%",
    height: "auto",
    data:LOCATIONS,
    controller: {
        loadData: function (){
            return $.ajax({
                type: "GET",
                url: REST_URL
            });
        },
        insertItem: function(inputData){
            return $.ajax({
                type: "POST",
                url: REST_URL,
                contentType: "application/json",
                data: JSON.stringify(inputData)
            });
        },
        updateItem: function(inputData){
            $.ajax({
                type: "PUT",
                url: REST_URL,
                contentType: "application/json",
                data: JSON.stringify(inputData)
            }).fail(function(e){
                toastr["error"]("Server error #13. Please refresh the page.");
            });
        },
        deleteItem: function(inputData){
            $.ajax({
                type: "DELETE",
                url: REST_URL,
                contentType: "application/json",
                data: JSON.stringify(inputData)
            }).fail(function(e){
                toastr["error"]("Server error #14. Please refresh the page.");
            });
        }
    },
    inserting: IS_EDITABLE,
    editing: IS_EDITABLE,
    sorting: false,

    fields: [
        { name: "name", type: "text", width: 150, validate: "required" },
        { name: "color", type: "select", width: 50, items: COLORS, valueField: "hex", textField: "name", validate: "required" },
        { name: "parallel", type: "checkbox", width: 20},
        IS_EDITABLE ? { type: "control", width: 15, deleteButton: false }:''
    ]
});