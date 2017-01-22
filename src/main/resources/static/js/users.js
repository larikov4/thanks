var $grid = $("#jsGrid");
var REST_URL = '/rest/' + ROOT_URL;

var PERMISSIONS = [
    {name:'admin'},
    {name:'user'},
    {name:'observer'}
];

USERS = USERS.map(function(user){
    user.birthday = user.birthday ? moment(user.birthday).format('YYYY-MM-DD') : user.birthday;
    return user;
});

var DateField = function(config) {
    jsGrid.Field.call(this, config);
};

DateField.prototype = new jsGrid.Field({

    align: "center",

    dateOptions: {
         showOtherMonths: true,
         selectOtherMonths: true,
         changeYear:true,
         dateFormat: "yy-mm-dd",
         yearRange: "-70:+0"
    },
    input: $("<input>").addClass('form-control').addClass('table-input'),

    sorter: function(date1, date2) {
        return new Date(date1) - new Date(date2);
    },

    itemTemplate: function(value) {
        return value ? moment(new Date(value)).format('YYYY-MM-DD') : '';
    },

    insertTemplate: function(value) {
        return this._insertPicker = this.input.clone().datepicker(this.dateOptions);
    },

    editTemplate: function(value) {
        if(value) {
            return this._editPicker = this.input.clone()
                .datepicker(this.dateOptions)
                .datepicker("setDate", new Date(value));
        }
        return this._editPicker = this.input.clone().datepicker(this.dateOptions);
    },

    insertValue: function() {
        if(!this._insertPicker.datepicker("getDate")){
            return null;
        }
        return moment(this._insertPicker.datepicker("getDate")).format('YYYY-MM-DD');
    },

    editValue: function() {
        if(!this._editPicker.datepicker("getDate")){
            return null;
        }
        return moment(this._editPicker.datepicker("getDate")).format('YYYY-MM-DD');
    }
});

jsGrid.fields.date = DateField;

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
        { title: "Username", name: "username", type: "text", width: 40, validate: "required" },
        { title: "Password", name: "password", type: "text", width: 30, validate: "required" },
        { title: "Name", name: "name", type: "text", width: 30 },
        { title: "Phone", name: "phone", type: "text", width: 30 },
        { title: "Birthday", name: "birthday", type: "date", width: 30 },
        { title: "Email", name: "email", type: "text", width: 30 },
        { title: "Color", name: "color", type: "select", width: 50, items: COLORS, valueField: "hex", textField: "name", validate: "required" },
        { title: "Permission", name: "permission", type: "select", width: 50, items: PERMISSIONS, valueField: "name", textField: "name", validate: "required" },
        { title: "Operator", name: "operator", type: "checkbox", width: 20},
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
        if(user.authorities && user.authorities.length === 5){
            user['permission'] = "admin";
        } else if(user.authorities && user.authorities.length === 1){
            user['permission'] = "user";
        } else {
            user['permission'] = "observer";
        }
    }
    return users;
}

function parseInputPermissions(inputData){
    var permission = inputData['permission'];
    inputData.authorities = [];
    if(permission === "admin"){
        inputData.authorities.push({name:"event_edit"})
        inputData.authorities.push({name:"user_edit"})
        inputData.authorities.push({name:"equipment_edit"})
        inputData.authorities.push({name:"location_edit"})
        inputData.authorities.push({name:"project_edit"})
    }
    if(permission === "user"){
        inputData.authorities.push({name:"self_event_edit"})
    }
    return inputData;
}
