var $grid = $("#jsGrid");
var REST_URL = '/rest/' + ROOT_URL;

jsGrid.sortStrategies.priority = function(entity1, entity2) {
    return entity2.priority - entity1.priority;
};

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
    rowClass: function(item, itemIndex) {
        return "row-" + (itemIndex);
    },
    onRefreshed: !IS_EDITABLE ? function(){} : function() {
        var $gridData = $("#jsGrid .jsgrid-grid-body tbody");

        $gridData.sortable({
            update: function(e, ui) {
                // arrays of items
                var items = $.map($gridData.find("tr"), function(row) {
                    return $(row).data("JSGridItem");
                });
                var newPriority = items.indexOf((ui.item).data("JSGridItem"))
                var oldPriority = (ui.item).data("JSGridItem").priority;
                console.log("old: " + oldPriority + " - new: " + newPriority);

                $.ajax({
                    type: "PUT",
                    url: REST_URL + "/priority",
                    contentType: "application/json",
                    data: JSON.stringify({
                        oldPriority: oldPriority,
                        newPriority: newPriority
                    })
                }).success(function(){
                    $grid.jsGrid("loadData");
                }).fail(function(e){
                    toastr["error"]("Server error #13. Please refresh the page.");
                });
            },
            placeholder: "highlight-row"
        });
    },
    inserting: IS_EDITABLE,
    editing: IS_EDITABLE,
    sorting: false,

    fields: [
        { title: "Name", name: "name", type: "text", width: 150, validate: "required", sorter: "priority" },
        { title: "Color", name: "color", type: "select", width: 50, items: COLORS, valueField: "hex", textField: "name", validate: "required" },
        { title: "Parallel", name: "parallel", type: "checkbox", width: 20},
        IS_EDITABLE ? { type: "control", width: 15 }:''
    ]
});