var $grid = $("#jsGrid");
var REST_URL = '/rest/' + ROOT_URL;

$grid.jsGrid({
    width: "100%",
    height: "auto",

    data: parseUsersPermission(USERS),

    controller: {
        loadData: function (){
            var step1, step2;

            step1 = $.ajax({
                type: "GET",
                url: REST_URL,
                dataType: "json"
            });

            step2 = step1.then(
                function (data) {
                    var def = new $.Deferred();
                    var users = parseUsersPermission(data);
                    def.resolve(users);

                    return def.promise(data);
                },
                function (err) {
                    toastr["error"]("Server error #8. Please refresh the page.");
                }
            );
            return step2;

        },
        insertItem: function(inputData){
            inputData = parseInputPermissions(inputData);
            var step1, step2;

            step1 = $.ajax({
                type: "POST",
                url: REST_URL,
                contentType: "application/json",
                data: JSON.stringify(inputData)
            });
            step2 = step1.then(
                function (data) {
                    data = parseUsersPermission([data])[0];
                    var def = new $.Deferred();
                    def.resolve(data);
                    return def.promise(data);
                },
                function (err) {
                    toastr["error"]("Server error #9. Please refresh the page.");
                }
            );
            return step2;
        },
        updateItem: function(inputData){
            inputData = parseInputPermissions(inputData);
            var step1, step2;

            step1 = $.ajax({
                type: "PUT",
                url: REST_URL,
                contentType: "application/json",
                data: JSON.stringify(inputData)
            });
            step2 = step1.then(
                function (data) {
                    data = parseUsersPermission([data])[0];
                    var def = new $.Deferred();
                    def.resolve(data);
                    return def.promise(data);
                },
                function (err) {
                    toastr["error"]("Server error #10. Please refresh the page.");
                }
            );
            return step2;
        },
        deleteItem: function(inputData){
            $.ajax({
                type: "DELETE",
                url: REST_URL,
                contentType: "application/json",
                data:JSON.stringify(inputData)
            }).fail(function(e){
                toastr["error"]("Server error #11. Please refresh the page.");
            });
        }
    },
    inserting: IS_EDITABLE,
    editing: IS_EDITABLE,
    sorting: false,
    fields: [
        { name: "username", type: "text", width: 40, validate: "required" },
        { name: "password", type: "text", width: 30, validate: "required" },
        { name: "email", type: "text", width: 30 },
        { name: "color", type: "select", width: 50, items: COLORS, valueField: "hex", textField: "name", validate: "required" },
        { name: "event edit", type: "checkbox", width: 20},
        { name: "user edit", type: "checkbox", width: 20},
        { name: "equipment edit", type: "checkbox", width: 30},
        { name: "location edit", type: "checkbox", width: 25},
        IS_EDITABLE ? { type: "control", width: 15 } : ''
    ]
});

$("#jsGrid").on("editFormRendered", function(e, id){
    var $password_input = $('.jsgrid-edit-row').find('input[type="text"]').eq(1);
    $.ajax({
        type: "GET",
        url: "/rest/users/" + id
    }).success(function(data){
        $password_input.val(data.password);
    }).fail(function(e){
        toastr["error"]("Server error #12. Please refresh the page.");
    });
});

function parseUsersPermission(users){
    for(i in users){
        var user = users[i];
        for(j in user.authorities){
            var authority = user.authorities[j].name;
            if(authority.contains("event")){
                user["event edit"] = true;
            }
            if(authority.contains("user")){
                user["user edit"] = true;
            }
            if(authority.contains("equipment")){
                user["equipment edit"] = true;
            }
            if(authority.contains("location")){
                user["location edit"] = true;
            }
        }
    }
    return users;
}

function parseInputPermissions(inputData){
    inputData.authorities = [];
    if(inputData["event edit"]){
        inputData.authorities.push({name:"event_edit"})
    }
    if(inputData["user edit"]){
        inputData.authorities.push({name:"user_edit"})
    }
    if(inputData["equipment edit"]){
        inputData.authorities.push({name:"equipment_edit"})
    }
    if(inputData["location edit"]){
        inputData.authorities.push({name:"location_edit"})
    }
    return inputData;
}