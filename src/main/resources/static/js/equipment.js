var $grid = $("#jsGrid");
var REST_URL = '/rest/' + ROOT_URL;
EQUIPMENT = mapEquipmentType(EQUIPMENT);

TYPES = [
    {name: 'camera'},
    {name: 'lens'},
    {name: 'sound'},
    {name: 'light'},
    {name: 'accessory'}
]

$grid.jsGrid({
    width: "100%",
    height: "auto",
    data:EQUIPMENT,
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
                toastr["error"]("Server error #12. Please refresh the page.");
            });
        },
        deleteItem: function(inputData){
            $.ajax({
                type: "DELETE",
                url: REST_URL,
                contentType: "application/json",
                data: JSON.stringify(inputData)
            }).fail(function(e){
                toastr["error"]("Server error #15. Please refresh the page.");
            });
        }
    },
    inserting: IS_EDITABLE,
    editing: IS_EDITABLE,
    sorting: false,

    fields: [
        { name: "name", type: "text", width: 150, validate: "required" },
        //{ name: "color", type: "select", width: 50, items: COLORS, valueField: "hex", textField: "name", validate: "required" },
        { name: "type", type: "select", width: 50, items: TYPES, valueField: "name", textField: "name", validate: "required" },
        IS_EDITABLE ? { type: "control", width: 15, deleteButton: false }:''
    ]
});
function mapEquipmentType(equipment) {
    return equipment.map(function(item){
        item.type = item.type.$name.toLowerCase();
        return item;
    })
}
//	bootbox.confirm({
//		size: 'small',
//		className: 'confirmation',
//		message: 'Are you sure?',
//		callback: function(result) {
//			console.log(result);
//		}
//	});