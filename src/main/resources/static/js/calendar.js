$(document).ready(function() {

	var repo = {
		locations: {},
		equipment: {},
		camera: {},
		lens: {},
		light: {},
		sound: {},
		accessory: {},
		users: {},
		userIds: {},
		colors:{
            locations: {},
            users: {}
		},
        free:{},
        busy:{}
	};

	var currentEvent;
	var currentUser;
	var currentFilter;
	var movingEvent = null;
	var EVENT_REST_URL = "/rest/events";
	var SERIES_REST_URL = "/rest/series";
	var BIRTHDAY_EVENT_COLOR = '#BDC3C7';
	var wasModalSubmitted = false;

    function mapEquipmentType(equipment) {
        return equipment.map(function(item){
            item.type = item.type.$name.toLowerCase();
            return item;
        })
    }

    function mapDeletedItems(items) {
        return items.map(addDeletedSuffixIfDeleted);
    }

    function addDeletedSuffixIfDeleted(item){
        if(item && item.deleted) {
            if(item.name) {
                item.name += " (deleted)";
            }
        }
        return item;
    }

    EQUIPMENT = mapDeletedItems(EQUIPMENT);
    USERS = mapDeletedItems(USERS);
    LOCATIONS = mapDeletedItems(LOCATIONS);

	(function initEntities(){
		for(var i=0;i<LOCATIONS.length;i++) {
			repo.locations[LOCATIONS[i].name] = LOCATIONS[i].id;
			repo.colors.locations[LOCATIONS[i].name] = LOCATIONS[i].color;
		}

        EQUIPMENT = mapEquipmentType(EQUIPMENT);
		for(var i=0;i<EQUIPMENT.length;i++) {
			repo.equipment[EQUIPMENT[i].name] = EQUIPMENT[i].id;
			if(EQUIPMENT[i].type==='camera'){
			    repo.camera[EQUIPMENT[i].name] = EQUIPMENT[i].id;
			}
            if(EQUIPMENT[i].type==='lens'){
                repo.lens[EQUIPMENT[i].name] = EQUIPMENT[i].id;
            }
            if(EQUIPMENT[i].type==='light'){
                repo.light[EQUIPMENT[i].name] = EQUIPMENT[i].id;
            }
            if(EQUIPMENT[i].type==='sound'){
                repo.sound[EQUIPMENT[i].name] = EQUIPMENT[i].id;
            }
            if(EQUIPMENT[i].type==='accessory'){
                repo.accessory[EQUIPMENT[i].name] = EQUIPMENT[i].id;
            }
		}
		var currentUserName = null;
		for(var i=0;i<USERS.length;i++) {
		    repo.users[USERS[i].name] = USERS[i];
			repo.userIds[USERS[i].name] = USERS[i].id;
			repo.colors.users[USERS[i].name] = USERS[i].color;
			if(CURRENT_USERNAME === USERS[i].username) {
			    currentUserName = USERS[i].name;
			}
		}
		currentUser = {
            id:repo.userIds[currentUserName],
            name:currentUserName
		};
	})();

    function renderBirthdayEvent(){
        var birthdayEvents = [];
        for(var i=0;i<USERS.length;i++) {
            var birthday = USERS[i].birthday;
            if(birthday){
                birthdayEvents.push({
                    title: "Birthday of " + USERS[i].name,
                    start:moment(birthday).add(8, 'hours').year(getNearestYear(birthday)),
                    end:moment(birthday).add(18, 'hours').year(getNearestYear(birthday)),
                    id: 'birthday' + i,
                    isBirthday: true,
                    author: {
                        name:currentUser.name
                    }
                });
            }
        }
        $('#calendar').fullCalendar('addEventSource', birthdayEvents);

        function getNearestYear(birthday){
            var currentDate = new Date();
            var currentYear = moment(currentDate).year();

            var prevYearDate = moment(birthday).year(currentYear-1).valueOf();
            var yearDate = moment(birthday).year(currentYear).valueOf();
            var nextYearDate = moment(birthday).year(currentYear+1).valueOf();

            var prevYearDateDiff = Math.abs(currentDate.valueOf() - prevYearDate.valueOf());
            var yearDateDiff = Math.abs(currentDate.valueOf() - yearDate.valueOf());
            var nextYearDateDiff = Math.abs(currentDate.valueOf() - nextYearDate.valueOf());

            if(prevYearDateDiff < yearDateDiff && prevYearDateDiff < nextYearDateDiff){
                return moment(birthday).year(currentYear-1).year();
            }
            if(nextYearDateDiff < yearDateDiff && nextYearDateDiff < prevYearDateDiff){
                return moment(birthday).year(currentYear+1).year();
            }
            if(yearDateDiff < prevYearDateDiff && yearDateDiff < nextYearDateDiff){
                return moment(birthday).year(currentYear).year();
            }
        }
    }


	(function initTimePickers() {
	    $('.time').each(function(i, v){
	        var option = $('<option>');
	        var $v = $(v);
	        for(var i=0;i<24;i++){
	            var hour = i+ ":" + "00";
	            var halfOfHour = i+ ":" + "30";
	            $v.append(option.clone().val(hour).html(hour));
	            $v.append(option.clone().val(halfOfHour).html(halfOfHour));
	        }
	    });
	})();

    (function initDatePicker(){
        $('#start-date').datepicker({
            showOtherMonths: true,
            selectOtherMonths: true,
            dateFormat: "yy-mm-dd",
            minDate:'today'
        });

        $('#end-date').datepicker({
            showOtherMonths: true,
            selectOtherMonths: true,
            dateFormat: "yy-mm-dd",
            minDate:'today'
        });

        $('#start-date').on('change', function(){
            $('#end-date').val($(this).val());
        });
    })();

    $('#save').on('click', function(){
        toastr.remove();
        $(".modal-edit .modal-body form").submit();
		wasModalSubmitted = true;
    });

    fillSelect($('#filter-location'), filterDeletedByName([' '].concat(Object.keys(repo.locations))));
    fillSelect($('#filter-user'), filterDeletedByName(Object.keys(repo.users)));
    fillSelect($('#filter-camera'), filterDeletedByName(Object.keys(repo.camera)));
    fillSelect($('#filter-lens'), filterDeletedByName(Object.keys(repo.lens)));
    fillSelect($('#filter-light'), filterDeletedByName(Object.keys(repo.light)));
    fillSelect($('#filter-sound'), filterDeletedByName(Object.keys(repo.sound)));
    fillSelect($('#filter-accessory'), filterDeletedByName(Object.keys(repo.accessory)));

    function filterDeletedByName(items){
        return items.filter(function(item){
            return !item.contains("(deleted)");
        })
    }

    $('#filter-location, #filter-user, #filter-camera, #filter-lens, #filter-light, #filter-sound, #filter-accessory')
        .on('change', function(){
            var data = {};
            var $container = $('.filter-sidebar');
            var filterPrefix = "filter-";
            fetchUser($container, data, filterPrefix);
            fetchLocationAndEquipment($container, data, filterPrefix);
            mapIdsAndNames(data);
            currentFilter = data;
            console.log(data);
            var url = isEmptyFilter(data) ? EVENT_REST_URL : EVENT_REST_URL + "/filter";
            performFilerRequest(data, url);
    });

    function isEmptyFilter(filter){
        return !filter || (!filter.location && filter.users.length === 0 && filter.equipment.length === 0)
    }

    function performFilerRequest(data, url){
        $.ajax({
            type: "GET",
            url: url,
            data: data,
            success: function(response){
                console.log(response);
                $('#calendar').fullCalendar('removeEvents');
                $('#calendar').fullCalendar('addEventSource', mapFilteredEvents(response));
                if(isEmptyFilter(data)){
                    renderBirthdayEvent();
                }
            }
        })
    }

    function mapIdsAndNames(data){
        data.users = data.users.map(function(item){return item.name;})
        data.equipment = data.equipment.map(function(item){return item.id;})
        data.location = data.location ? data.location.id : null;
    }

    function mapFilteredEvents(events) {
        return events.map(function(event){
            event.equipment = mapDeletedItems(event.equipment);
            event.users = mapDeletedItems(event.users);
            event.location = addDeletedSuffixIfDeleted(event.location);
            return splitEquipment(event)
        });
    }

    function mapEvents(events){
        return events.map(function(event){
            event.equipment = mapDeletedItems(event.equipment);
            event.users = mapDeletedItems(event.users);
            event.location = addDeletedSuffixIfDeleted(event.location);
            event.equipment = mapEquipmentType(event.equipment);
            return splitEquipment(event)
        });
    }

    function fetchEventFromForm(){
        var data = {};
        var $container = $('.modal-edit');
        data.title = $container.find('#name').val();
        data.description = $container.find('#description').val();
        data.start = $container.find('#start-date').val() + "T" + $('#start-time').val();
        data.end = $container.find('#end-date').val() + "T" + $container.find('#end-time').val();
        data.isSeries = $("#series").prop('checked');
        fetchUser($container, data);
        if($("#recording").prop('checked')) {
            fetchLocationAndEquipment($container, data);
        }
        return data;
    }

    function fetchUser($container, data, prefix){
        prefix = prefix || '';
        var currentId = '#' + prefix + 'user';
        var userFieldValue = $container.find(currentId).val() ? $container.find(currentId).val() : [];
        data.users = userFieldValue.map(function(name){
            return {
                id:repo.userIds[name],
                name:name,
                email:repo.users[name].email
            };
        });
        return data;
    }

    function fetchLocationAndEquipment($container, data, prefix){
        prefix = prefix || '';
        var locationName = $container.find('#' + prefix + 'location').val();
        data.location = null;
        if(locationName) {
            data.location = {
                id: repo.locations[locationName],
                name: locationName
            };
        }

        data.equipment = fetchEquipmentByType('camera', [], $container, prefix);
        data.equipment = fetchEquipmentByType('lens', data.equipment, $container, prefix);
        data.equipment = fetchEquipmentByType('sound', data.equipment, $container, prefix);
        data.equipment = fetchEquipmentByType('light', data.equipment, $container, prefix);
        data.equipment = fetchEquipmentByType('accessory', data.equipment, $container, prefix);
        return data;
    }

    function fetchEquipmentByType(type, equipment, $container, prefix){
        var currentId = '#' + prefix + type;
        if($container.find(currentId).val()) {
            currentTypeEquipment = $container.find(currentId).val().map(function(name){
                return {
                    id:repo.equipment[name],
                    name:name,
                    type: type
                };
            });
            return equipment.concat(currentTypeEquipment);
        }
        return equipment;
    }

    function fillSelectsIfFieldsIsReady(){
        if($('#start-time').select2('val')!=='0:00' && $('#end-time').select2('val')!=='0:00'
            && $('#start-time').select2('val') !== $('#end-time').select2('val')){
            var isSeries = $("#series").prop('checked');
            fillSelects(isSeries);
        }
    }

    $("#series").on('change.radiocheck', function(){
		fillSelectsIfFieldsIsReady();
    });

    $('.date, .time').on('change', function(){
		fillSelectsIfFieldsIsReady();
	});

	function hasAnyBusyResource(param){
		var event = param.event;
		if(event.seriesId){
            seriesOrSingleEventEdit()
                .done(function(result){
                    var urlFreeSuffix = result === "series" ? "/series/free" : "/free";
                    var urlUpdateSuffix = result === "series" ? SERIES_REST_URL : EVENT_REST_URL;
                    sendRequest(urlFreeSuffix, urlUpdateSuffix, "seriesId");
                })
                .fail(function(){
                    param.busy();
                })
		} else {
		    sendRequest("/free", EVENT_REST_URL, "id");
		}


		function sendRequest(urlFreeSuffix, urlUpdateSuffix, idName) {
		    var isBusy = false;

            var data = {
                start: event.start.format('YYYY-MM-DD') + "T" + padTime(event.start),
                end: event.end.format('YYYY-MM-DD') + "T" + padTime(event.end)
            };
            data[idName] = event[idName];
		    $.when(
                $.ajax({
                    type: "GET",
                    url: "/rest/locations" + urlFreeSuffix,
                    data:data,
                    success: function(data){
                        if(event.location && !mapNames(data).contains(event.location.name)){
                            isBusy = true;
                            toastr["warning"](event.location.name + " is busy");
                        }
                    }
                }),
                $.ajax({
                    type: "GET",
                    url: "/rest/equipment" + urlFreeSuffix,
                    data:data,
                    success: function(data){
                        var busy = filterBusy(mapNames(fetchGroupedEquipment(event)), mapNames(data));
                        if(busy.length > 0) {
                            isBusy = true;
                            toastr["warning"](busy.toString() + " is busy");
                        }
                    }
                }),
                $.ajax({
                    type: "GET",
                    url: "/rest/users" + urlFreeSuffix,
                    data:data,
                    success: function(data){
                        var busy = filterBusy(mapNames(event.users), mapNames(data));
                        if(busy.length > 0) {
                            isBusy = true;
                            toastr["warning"](busy.toString() + " is busy");
                        }
                    }
                })
            ).then(function(){
                if(isBusy){
                    param.busy();
                } else {
                    param.free(event, urlUpdateSuffix);
                }
            });
		}
    }

    function toggleRecordingCheckbox(checked) {
        if(checked) {
            $("#recording").prop("checked", true);
        } else {
            $("#recording").prop("checked", false);
        }
    }

	function fillSelects(isSeries){
	    var urlFreeSuffix = isSeries ? "/series/free" : "/free";
	    var idName = isSeries ? "seriesId" : "id";
		var ajaxCounter = 0;
	    toggleRecordingCheckbox(currentEvent && currentEvent.location);
	    var data = {
            start:$('#start-date').val() + "T" + $('#start-time').val(),
            end:$('#end-date').val() + "T" + $('#end-time').val()
        }
	    data[idName] = currentEvent ? currentEvent[idName] : '';
		$.ajax({
			type: "GET",
			url: "/rest/locations" + urlFreeSuffix,
			data: data,
			success: function(data){
				ajaxCounter++;
				var $select = $('.modal-edit').find('#location');
				var prev = $select.val();
				var additionalValues = currentEvent && currentEvent.location ? [currentEvent.location] : [];
				prepareSelect(data, 'name', Object.keys(repo.locations), 'location', additionalValues);
				if(prev){
					$select.select2('val', prev);
				}
				if(currentEvent && currentEvent.location){
					$select.select2('val', currentEvent.location.name);
				}
				if(wasModalSubmitted && ajaxCounter % 3 === 0){
					$(".modal-edit .modal-body form").valid();
					ajaxCounter=0;
				}
			},
			fail:function(){
				toastr["error"]("Server error #1. Please refresh the page.");
			}
		});

		$.ajax({
			type: "GET",
			url: "/rest/equipment" + urlFreeSuffix,
			data: data,
			success: function(data){
				ajaxCounter++;
				prepareEquipment('camera');
				prepareEquipment('lens');
				prepareEquipment('light');
				prepareEquipment('sound');
				prepareEquipment('accessory');
				function prepareEquipment(type) {
                    var $select = $('.modal-edit').find('#' + type);
                    var prev = $select.val();
                    var equipmentContainer = { equipment : data };
                    var additionalValues = currentEvent ? currentEvent[type] : [];
                    prepareSelect(splitEquipment(equipmentContainer)[type], 'name', Object.keys(repo[type]), type, additionalValues);
                    if(prev){
                        $select.select2('val', prev);
                    }
                    if(currentEvent) {
                        $select.select2('val', mapNames(currentEvent[type]));
                    }
				}
                if(wasModalSubmitted && ajaxCounter % 3 === 0){
                    $(".modal-edit .modal-body form").valid();
                    ajaxCounter=0;
                }
			},
			fail:function(){
				toastr["error"]("Server error #2. Please refresh the page.");
			}
		});

		$.ajax({
			type: "GET",
			url: "/rest/users" + urlFreeSuffix,
			data: data,
			success: function(data){
				ajaxCounter++;
				var $select = $('.modal-edit').find('#user');
				var prev = $select.val();
				var additionalValues = currentEvent ? currentEvent.users : [];
				prepareSelect(data, 'name', Object.keys(repo.userIds), 'user', additionalValues);
				if(prev){
					$select.select2('val', prev);
				}
				if(currentEvent) {
					$select.select2('val', currentEvent.users.map(function (user) {
						return user.name
					}));
				}
				if(wasModalSubmitted && ajaxCounter === 3){
					$(".modal-edit .modal-body form").valid();
					ajaxCounter=0;
				}
			},
			fail:function(){
				toastr["error"]("Server error #3. Please refresh the page.");
			}
		});

		function prepareSelect(data, property, entities, entityName, additionalNames){
			repo.free[entityName] = getFreeArray(data, property);
			repo.busy[entityName] = filterBusy(entities, repo.free[entityName]);
			var selectValues = addAdditionalValuesIfAbsent(repo.free[entityName], mapNames(additionalNames));
			var $input = $('.modal-edit #' + entityName);
			fillSelect($input, selectValues);
			$input.prop( "disabled", false );
		}

        function getFreeArray(data, property){
            var freeArray = [];
            for(var i=0;i<data.length;i++) {
                freeArray.push(data[i][property]);
            }
            return freeArray;
        }

        function addAdditionalValuesIfAbsent(values, additional){
            return values.concat(additional.filter(function(value){
                return !values.contains(value);
            }))
        }

	}

    function fillSelect($input, entities) {
        $input.empty();
        var option = $('<option>');
        for(var i=0;i<entities.length;i++) {
            $input.append(option.clone().val(entities[i]).html(entities[i]))
        }
        $input.select2("val", "").addClass('multiselect-primary').removeClass('multiselect-default');
    }

	function updateEvent(event, url) {
		delete event.source;
		event = groupEquipment(event);
		$.ajax({
			type: "PUT",
			url: url,
			contentType: "application/json",
			data: JSON.stringify(event)
		}).success(function (data) {
		}).fail(function (e) {
			toastr["error"]("Server error #4. Please refresh the page.");
		});
	}

	$('#calendar').fullCalendar({
		header: {
			left: 'prev,next today',
			center: 'title',
			right: 'month,agendaWeek,agendaDay'
		},
		events:mapEvents(EVENTS),
		editable:IS_EDITABLE,
		defaultView:'agendaWeek',
	 	timeFormat: 'H:mm',
		displayEventTime: false,
		firstDay:1,
	 	allDaySlot: false,
	 	axisFormat: 'H:mm',
        firstHour:6,
        columnFormat: 'dddd D.M',
        height: 900,
	 	eventClick: IS_EDITABLE? onEventClick : function(){},
	 	eventDrop: function(event, delta, revert){
			hasAnyBusyResource({
				event:event,
				free: updateEvent,
				busy: revert
			});
	 	},
	 	eventResize: function(event, delta, revert){
			if(!isDurationMoreThanOrEqualsOneHourByDates(event.start.toDate(), event.end.toDate())){
				toastr["warning"]("Event's duration should be at least 1 hour");
				revert();
				return;
			}
	 		hasAnyBusyResource({
	 			event:event,
	 			free: updateEvent,
	 			busy: revert
	 		});
	 	},
	 	viewRender: function(){
	 		$('.fc-prev-button').find('span').removeClass().addClass('fa fa-chevron-left');
	 		$('.fc-next-button').find('span').removeClass().addClass('fa fa-chevron-right');

	 		$('.fc-button-group').each(function(){
	 			$(this).removeClass().addClass('btn-group');
	 		});

	 		$('.fc-toolbar').find('button').each(function(){
	 			var buttonClass = 'btn btn-inverse';
	 			if($(this).hasClass('fc-today-button')){
	 				buttonClass += ' fc-today-button';
	 			}
	 			$(this).removeClass().addClass(buttonClass);
	 		});
	 	},
		dayClick: !IS_EDITABLE ? function(){} : function(date) {
			currentEvent = null;
			var $modal = $('.modal-edit');
			$modal.find('#start-date').val(date.format('YYYY-MM-DD'));
			$modal.find('#end-date').val(date.format('YYYY-MM-DD'));
			if(date._isAMomentObject){
				$modal.find('#start-time').select2('val', date.hour() + ":" + padZero(date.minute()));
				var endTime = date.hour() > 20 ? date.hour() : ( date.hour() + 3 ) + ":" + padZero(date.minute());
				$modal.find('#end-time').val(endTime).trigger('change');
			}
			$modal.modal('show');
		},
		dayHit: !IS_EDITABLE ? function(){} : function(date, x) {
		    currentEvent = null;
		    if(movingEvent){
                $('#calendar').fullCalendar('removeEvents', movingEvent.id);
		        var duration = getDurationInMinutesByDates(movingEvent.startPoint.toDate(), date.toDate());
		        if(duration>0) {
		            movingEvent.start = movingEvent.startPoint;
                    movingEvent.end = date.add(30, 'minute');
                    $('#calendar').fullCalendar('renderEvent', movingEvent, true);
		        } else if(duration<-30) {
                    movingEvent.start = date;
                    movingEvent.end = movingEvent.startPoint;
                    $('#calendar').fullCalendar('renderEvent', movingEvent, true);
                } else {
                    movingEvent.start = movingEvent.startPoint;
                    movingEvent.end = movingEvent.startPoint;
                }
		    } else {
		        movingEvent = {
		            startPoint:date,
		            start:date,
                    end:date,
		            id: 'stub id',
		            isStub:true,
		            author: {
		                name:currentUser.name
		            }
		        };
		    }
		},
		eventRender: function (event, $target, view) {
		    if(event.isBirthday) {
		        $target.css('background-color', BIRTHDAY_EVENT_COLOR);
                $target.css('border-color', BIRTHDAY_EVENT_COLOR);
		    } else {
                var authorColor = repo.colors.users[event.author.name];
                $target.css('background-color', authorColor);
                $target.css('border-color', authorColor);
		    }

			if(event.isStub && !$target.find('.fc-content').hasClass('empty-field')) {
                $target.find('.fc-content').addClass('empty-field');
			}
			if(view.type.contains("agend") && !event.isStub){
			    if(event.description) {
			        var descriptionElement = $("<span>")
			            .addClass('fc-description')
			            .text(event.description);
			        $target.find('.fc-title').append(descriptionElement);
			    }
			    if(event.location) {
                    var locationColor = repo.colors.locations[event.location.name];
                    $target.find('.fc-title').css('background-color', locationColor);
			    }
				var $container = $('<div>').addClass('equipment-container');
				var $element = $('<div>');
				appendEquipment('camera');
				appendEquipment('lens');
				appendEquipment('light');
				appendEquipment('sound');
				appendEquipment('accessory');
				function appendEquipment(type) {
                    for(var i=0;event[type] && i<event[type].length;i++){
                        $container.append($element.clone().text(event[type][i].name + "")
                            //.css('color', repo.colors.equipment[event.equipment[i].name])
                        );
                    }
				}
				$target.append($container);
                if(event.users){
                    var gridClass = "fc-event-username col-md-" + 12 / event.users.length;
                    var gridHolder = $('<div>').addClass("row");
                    var gridElement = $('<div>').addClass(gridClass);
                    for(var i=0;i<event.users.length;i++){
                        gridHolder.append(gridElement.clone()
                            .css('background-color', repo.colors.users[event.users[i].name])
                            .text(trimName(event.users[i].name))
                        );
                    }
                }
				$target.append(gridHolder);

				function trimName(name){
				    return name
				        .split(' ')
				        .map(function(str){ return str.trim()[0]; })
				        .filter(function(char){ return char!=='('})
				        .join('')
				}
			}
		},
		eventAfterRender: function (event, $target){
	 		$target.addClass('popover-target');
			var $container = $('.equipment-container').each(function(){
				$container = $(this);
				var containerMaxHeight = $container.parent().height() - $container.parent().find('.fc-content').height() - 19;
				while ($container.children().length > 0 && $container.height() > containerMaxHeight) {
					$container.children().last().remove();
					$container.children().last().text($container.children().last().text() + " ...")
				}
			});
			if(IS_EDITABLE && event.users){
				$target.popover({
					title:event.title + '<button type="button" class="close" data-dismiss="popover-container"><span>x</span></button>',
					html: true,
					animation:true,
					placement:function(popover, event){
                        var leftOffset = this.getPosition().left
                        var popoverWidth = 250 + 50;
					    return $('#calendar').width() - leftOffset > popoverWidth ? 'right' : 'left';
                    },
					content: function() {
						function clearPopover() {
							$('.popover').popover('hide');
							$container = $('#popover-container');
							$container.find('.date').empty();
							$container.find('.description').empty();
							$container.find('.author').empty();
							$container.find('.created').empty();
							$container.find('.location').empty();
							$container.find('.users').empty();
							$container.find('.camera').empty();
							$container.find('.lens').empty();
							$container.find('.sound').empty();
							$container.find('.light').empty();
							$container.find('.accessory').empty();
							$container.find('.description-container').hide();
							$container.find('.location-container').hide();
							$container.find('.camera-container').hide();
							$container.find('.lens-container').hide();
							$container.find('.sound-container').hide();
							$container.find('.light-container').hide();
							$container.find('.accessory-container').hide();
							$('body').off('click', '.editing');
						}

						function fillPopover(event) {
							var $popover = $('#popover-container');
							function formatDuration(event){
								if(event.start.format('YYYY-MM-DD') === event.end.format('YYYY-MM-DD')){
									return event.start.format('YYYY-MM-DD') + " " + event.start.format('HH:mm') + " - " + event.end.format('HH:mm');
								}
								return event.start.format('YYYY-MM-DD HH:mm') + " - " + event.end.format('YYYY-MM-DD HH:mm');
							}
							$popover.find('.date').text(formatDuration(event));
							if(event.description) {
							    $popover.find('.description-container').show();
							    $popover.find('.description').text(event.description);
                            }
							$popover.find('.author').text(event.author.name);
                            $popover.find('.created').text(moment(event.created).format('YYYY-MM-DD HH:mm'));
							if(event.location) {
							    $popover.find('.location-container').show();
							    $popover.find('.location').text(event.location.name);
							    showFormRightPart();
							} else {
							    hideFormRightPart();
							}
							var $li = $('<li>');
							var usersContainer = $popover.find('.users');
							event.users.forEach(function(user) {usersContainer.append($li.clone().text(user.name))});
                            appendEquipment('camera');
                            appendEquipment('lens');
                            appendEquipment('light');
                            appendEquipment('sound');
                            appendEquipment('accessory');
							function appendEquipment(type) {
                                if(event[type].length > 0) {
                                    $popover.find('.' + type + '-container').show();
                                    var equipmentContainer = $popover.find('.' + type);
                                    event[type].forEach(function(equipment) {equipmentContainer.append($li.clone().text(equipment.name))});
                                }
							}
							$('body').on('click', '.editing', function() {
							    function showFilledEditForm(isSeries) {
                                    clearPopover();
                                    var $modal = $('.modal-edit');
                                    $modal.find('#name').val(event.title);
                                    $modal.find('#description').val(event.description);
                                    $modal.find('#start-date').val(event.start.format('YYYY-MM-DD'));
                                    $modal.find('#start-time').select2('val', event.start.hour() + ":" + padZero(event.start.minute()));
                                    $modal.find('#end-date').val(event.end.format('YYYY-MM-DD'));
                                    $modal.find('#end-time').select2('val', event.end.hour() + ":" + padZero(event.end.minute()));
                                    $modal.find("#series").prop('checked', isSeries);
                                    $modal.find("#series").prop( "disabled", true );
                                    fillSelects(isSeries);
                                    $modal.modal('show');
							    }
							    if(!!event.seriesId){
                                    seriesOrSingleEventEdit()
                                        .done(function(result){
                                            showFilledEditForm(result === "series");
                                        })
							    } else {
                                    showFilledEditForm(false);
							    }
							});
						}

						clearPopover();
						fillPopover(event);
						return $('#popover-container').html();
					}
				});
			}
        }
	});

    $('#calendar').on('mouseup', function(){
        if(movingEvent && isDurationMoreThanOrEqualsOneHourByDates(movingEvent.start.toDate(), movingEvent.end.toDate())){
            $('#calendar').fullCalendar('removeEvents', movingEvent.id);
            var $modal = $('.modal-edit');
            $modal.find('#start-date').val(movingEvent.start.format('YYYY-MM-DD'));
            $modal.find('#end-date').val(movingEvent.end.format('YYYY-MM-DD'));
            $modal.find('#start-time').select2('val', movingEvent.start.hour() + ":" + padZero(movingEvent.start.minute()));
            $modal.find('#end-time').select2('val', movingEvent.end.hour() + ":" + padZero(movingEvent.end.minute()));
            $modal.find('#end-time').trigger('change');
            $modal.modal('show');
            movingEvent = null;
        }
    })

    renderBirthdayEvent();

	function onEventClick(event) {
		currentEvent = event;
	}

	function onSubmit(){
        var event = fetchEventFromForm();
        var url = event.isSeries ? SERIES_REST_URL : EVENT_REST_URL;
		if(!currentEvent){
			event.author = currentUser;
			$.ajax({
				type: "POST",
				url: url,
				contentType: "application/json",
				data: JSON.stringify(event)
			}).success(function(data){
			    $('.modal-edit').modal('hide');
			}).fail(function(e){
				toastr["error"]("Server error #5. Please refresh the page.");
			});
		} else {
			event.author = currentEvent.author;
			event.created = currentEvent.created;
			event.id = currentEvent.id;
			event.seriesId = currentEvent.seriesId;
			$.ajax({
				type: "PUT",
				url: url,
				contentType: "application/json",
				data: JSON.stringify(event)
			}).success(function (data) {
				$('.modal-edit').modal('hide')
			}).fail(function (e) {
				toastr["error"]("Server error #6. Please refresh the page.");
			});
		}
	}

	$('body').on('click', 'button.delete', function(){
	    if(currentEvent.seriesId) {
            seriesOrSingleEventEdit()
                .done(function(result){
                    var urlUpdateSuffix = result === "series" ? SERIES_REST_URL : EVENT_REST_URL;
                    deleteCurrentEvent(urlUpdateSuffix);
                })
	    } else {
	        deleteCurrentEvent(EVENT_REST_URL);
	    }
	});

	function deleteCurrentEvent(url) {
		delete currentEvent.source;
		$.ajax({
			type: "DELETE",
			url: url,
			contentType: "application/json",
			data: JSON.stringify(currentEvent)
		}).done(function(data){
			$('.popover').popover('hide');
			resetForm();
		}).fail(function(e){
			toastr["error"]("Server error #7. Please refresh the page.");
		});
	}

	$('body').on('click', 'button.history', function(){
		$.ajax({
			type: "GET",
			url: "/rest/event/changelog/" + currentEvent.id,
			success: showHistory
		});
	});

	function showHistory(data){
        var bubble = $('<div>').addClass('bubble');
        var bubbleContainerTitle = $('<div>').addClass('title');
        var bubbleContainer = $('<div>').addClass('bubble-container');
        for(var i=0;i<data.length;i++){
            var currentBubbleContainer = bubbleContainer.clone();
            var dateStr = moment(data[i].date).format('YYYY-MM-DD HH:mm');
            currentBubbleContainer.append(bubbleContainerTitle.clone().text(data[i].author.name + " " + dateStr));
            var diff = data[i].diff;
            diff = diff.camera ? diff : splitEquipment(diff);
            var prevDiff = data[i-1] ? data[i-1].diff : {equipment:[]};
            prevDiff = prevDiff.camera ? prevDiff : splitEquipment(prevDiff);
            currentBubbleContainer
                .append(generateBubble(prevDiff.title, diff.title))
                .append(generateBubble(prevDiff.description, diff.description))
                .append(generateBubble(printDate(prevDiff.start), printDate(diff.start)))
                .append(generateBubble(printDate(prevDiff.end), printDate(diff.end)))
                .append(generateBubble(printLocation(prevDiff.location), printLocation(diff.location)));

            appendBubblesForArrays(prevDiff.users, diff.users, currentBubbleContainer);
            appendBubblesForArrays(prevDiff.camera, diff.camera, currentBubbleContainer);
            appendBubblesForArrays(prevDiff.lens, diff.lens, currentBubbleContainer);
            appendBubblesForArrays(prevDiff.light, diff.light, currentBubbleContainer);
            appendBubblesForArrays(prevDiff.sound, diff.sound, currentBubbleContainer);
            appendBubblesForArrays(prevDiff.accessory, diff.accessory, currentBubbleContainer);
            $('.modal-history').find('.modal-body').append(currentBubbleContainer);
        }
        $('.modal-history').modal('show');

        function printDate(date) {
            return date ? date.replace('T',' ') : '';
        }

        function printLocation(location) {
            return location ? location.name : '';
        }

        function printArray(array) {
            return array && array.length > 0 ? mapNames(array).toString().replace(',', ', ') : '';
        }

        function filterExclusiveItems(oldArray, newArray) {
            if(!oldArray) {
                return [];
            }
            if(!newArray) {
                return oldArray;
            }
            var result = oldArray.slice();
            for(var i=oldArray.length-1;i>=0;i--){
                for(var j=newArray.length;j>=0;j--){
                    if(oldArray[i] && newArray[j] && oldArray[i].id === newArray[j].id) {
                        result.splice(i, 1);
                    }
                }
            }
            return result;
        }

        function generateBubble(oldValue, newValue) {
            if(!oldValue && newValue) {
                return generateAddedBubble(newValue);
            }
            if(oldValue && !newValue) {
                return generateRemovedBubble(oldValue);
            }
            if(oldValue && newValue && oldValue !== newValue) {
                return bubble.clone()
                   .addClass('modified')
                   .text(newValue);
            }
            return '';
        }

        function appendBubblesForArrays(oldValues, newValues, bubbleContainer) {
            var removedItems = filterExclusiveItems(oldValues, newValues);
            removedItems.forEach(function(item){
                bubbleContainer.append(generateRemovedBubble(item.name));
            })
            var addedItems = filterExclusiveItems(newValues, oldValues);
            addedItems.forEach(function(item){
                bubbleContainer.append(generateAddedBubble(item.name));
            })
        }

        function generateAddedBubble(value) {
            return bubble.clone()
                .addClass('added')
                .text(value);
        }

        function generateRemovedBubble(value) {
            return bubble.clone()
                .addClass('removed')
                .text(value);
        }
	}

	$('.modal-history').on('hidden.bs.modal', function () {
		$(this).find('.modal-body').empty();
	});

	$('.modal-edit').on('hidden.bs.modal', function () {
		clearPopup();
		hideFormRightPart();
		wasModalSubmitted = false;
	});

	function clearPopup() {
		var $container = $('.modal-edit');
		$container.find('#name').val('');
		$container.find('#description').val('');
		$container.find('#start-date').val('');
		$container.find('#start-time').val('0:00').trigger('change');
		$container.find('#end-date').val('');
		$container.find('#end-time').val('0:00').trigger('change');

		$container.find('#location').select2('val', '');
		$container.find('#location').select2('destroy');
		$container.find('#location').select2();

		$container.find('#user').select2('val', '');
		$container.find('#camera').select2('val', '');
		$container.find('#lens').select2('val', '');
		$container.find('#sound').select2('val', '');
		$container.find('#light').select2('val', '');
		$container.find('#accessory').select2('val', '');
		$("form").validate().resetForm();
		resetForm();
	}

	function resetForm(){
		disableForm();
		$("#recording").prop("checked", false).trigger('change.radiocheck');
		$("#series").prop("checked", false);
		$("#series").prop( "disabled", false);
		currentEvent = null;
	}

	function disableForm(){
		$('#location').prop( "disabled", true );
		$('#user').prop( "disabled", true ).removeClass('multiselect-primary').addClass('multiselect-default');
		$('#camera').prop( "disabled", true ).removeClass('multiselect-primary').addClass('multiselect-default');
		$('#lens').prop( "disabled", true ).removeClass('multiselect-primary').addClass('multiselect-default');
		$('#light').prop( "disabled", true ).removeClass('multiselect-primary').addClass('multiselect-default');
		$('#sound').prop( "disabled", true ).removeClass('multiselect-primary').addClass('multiselect-default');
		$('#accessory').prop( "disabled", true ).removeClass('multiselect-primary').addClass('multiselect-default');
	}
	disableForm();

	function enableForm(){
		$('#location').prop( "disabled", false );
		$('#user').prop( "disabled", false );
		$('#camera').prop( "disabled", false );
		$('#lens').prop( "disabled", false );
		$('#light').prop( "disabled", false );
		$('#sound').prop( "disabled", false );
		$('#accessory').prop( "disabled", false );
	}

	function padZero(number) {
		return (number < 10) ? ("0" + number) : number;
	}

	function padTime(moment) {
		return padZero(moment.hour()) + ":" + padZero(moment.minute());
	}

	function mapNames(entities){
		return entities.map(function (entity) {
			return entity.name;
		});
	}

    function mapIds(entities){
        return entities.map(function (entity) {
            return entity.id;
        });
    }

	function filterBusy(all, free){
		return all.filter(function(item) {
		   return free.indexOf(item) === -1;
		});
	}

	$(document).on("keypress", "form", function(event) {
        return event.keyCode != 13;
    });

    function getDurationInMinutesByDates(firstDate, secondDate) {
        var duration = secondDate.getTime() - firstDate.getTime();
        return Math.floor(duration / 60000);
    }

    function isDurationMoreThanOrEqualsOneHourByDates(firstDate, secondDate){
        var duration = getDurationInMinutesByDates(firstDate, secondDate);
        return Math.floor(duration / 60) >= 1;
    }

	(function addCustomValidatorMethods(){

		$.validator.addMethod(
			"isDurationMoreThanOrEqualsOneHourByDates",
			function (secondTimeStr, element, param){
				var startDate = $('[name="' + param[0] + '"]').val() + ' ' + $('[name="' + param[1] + '"]').val();
				var endDate = $('[name="' + param[2] + '"]').val() + ' ' + secondTimeStr;
                return isDurationMoreThanOrEqualsOneHourByDates(new Date(startDate), new Date(endDate));
			},
			"Event's duration should be at least 1 hour"
		);

		$.validator.addMethod(
			"isDateBeforeOrEquals",
			function(endDate, element, param){
				var startDate = $('[name="' + param + '"]').val();
				return new Date(startDate).getTime() <= new Date(endDate).getTime();
			},
			"End date should be equal or greater than start date"
		);

        $.validator.addMethod(
            "isFreeLocation",
            function(value){
				return !repo.busy.location.contains(value);
            }
        );

        $.validator.addMethod(
            "isFreeUser",
            function(value, element){
				var $element = $(element);
				var values = $element.val();
				var busy = [];
				if(values) {
					busy = values.filter(function(value){
						return repo.busy.user.contains(value);
					});
					$element.siblings('.select2-container').find('.select2-search-choice').each(function(){
						var $this = $(this);
						if(busy.contains($this.find('div').text())){
							$this.addClass('busy-select-item');
						} else {
							$this.removeClass('busy-select-item');
						}
					});
				}
				return busy.length === 0;
            },
            "Some users are busy"
        );

        $.validator.addMethod(
            "isFreeEquipment",
            function(value, element, equipmentType){
				var $element = $(element);
				var values = $element.val();
				var busy = [];
				if(values) {
					busy = values.filter(function(value){
						return repo.busy[equipmentType].contains(value);
					});
					$element.siblings('.select2-container').find('.select2-search-choice').each(function(){
						var $this = $(this);
						if(busy.contains($this.find('div').text())){
							$this.addClass('busy-select-item');
						} else {
							$this.removeClass('busy-select-item');
						}
					});
				}
				return busy.length === 0;
            },
            "Some equipment is busy"
        );
	})();

	$('.modal-edit .modal-body form select').not('.time').on('change', function(){
		if(wasModalSubmitted){
			$(".modal-edit .modal-body form").valid();
		}
	});

	$(".modal-edit .modal-body form").validate({
        ignore: [],
        onkeyup: false,
        onfocusout: false,
		rules: {
			'name': "required",
			'start-date': "required",
			'end-date': {
				required:true,
				isDateBeforeOrEquals:'start-date'
			},
			'end-time': {
                isDurationMoreThanOrEqualsOneHourByDates:['start-date', 'start-time', 'end-date']
            },
			location: {
                isFreeLocation:true
            },
			user: {
                required:true,
                isFreeUser:true
            },
			camera: {
                isFreeEquipment:'camera'
            },
			lens: {
                isFreeEquipment:'lens'
            },
			light: {
                isFreeEquipment:'light'
            },
			sound: {
                isFreeEquipment:'sound'
            },
			accessory: {
                isFreeEquipment:'accessory'
            }
		},

		messages: {
			'name': "Please enter event's name",
			'start-date': {
				required:"Please enter event's start date"
			},
			'end-date': {
				required:"Please enter event's end date"
			},
			location: {
				required:"Please enter event's location",
				isFreeLocation:"This location is busy"
			},
			user: {
				required:"Please enter at least one user"
			}
		},
        errorPlacement: function(error, element) {
            toastr["warning"](error);
        },
		submitHandler: function(form) {
			onSubmit();
		}
	});

	$('body').on('click', function (e) {
        if(!$(e.target).is('.popover-target') && !$(e.target).is('.popover') &&
        	$(e.target).parents('.popover-target').length===0 && $(e.target).parents('.popover').length===0){
    		$('.popover').popover('hide');
   		}
    });

    $('body').on('click', '.popover .close', function(){
    	$('.popover').popover('hide');
    	currentEvent = null;
    });

    $("select").select2({ dropdownCssClass : 'dropdown' });
    $("#recording").radiocheck();
    $("#series").radiocheck();

    function seriesOrSingleEventEdit() {
        var dfd = jQuery.Deferred();
        var $confirm = $('.modal-series');
        $confirm.modal('show');
        $('#series-event-edit').off('click').click(function () {
            $confirm.modal('hide');
            dfd.resolve("series");
        });
        $('#single-event-edit').off('click').click(function () {
            $confirm.modal('hide');
            dfd.resolve("single");
        });
        $('#cancel-event-edit').off('click').click(function () {
            $confirm.modal('hide');
            dfd.reject();
        });
        $confirm.on('hidden.bs.modal', function () {
            dfd.reject();
        })
        return dfd.promise();
    }

    $("#recording").on('change.radiocheck', function(){
        if($(this).prop('checked')){
            showFormRightPart();
        } else {
            hideFormRightPart();
        }
    });

    function showFormRightPart() {
        $('.modal-edit .modal-dialog').animate(
            { width:'848px' },
            {
                duration: 200,
                complete:function(){
                    $('.right-part').removeClass('hidden-part');
                }
            }
        );
        $("#location").rules("add", "required");
    }

    function hideFormRightPart() {
        $('.right-part').addClass('hidden-part');
        $('.modal-edit .modal-dialog').animate(
            { width:'426px' },
            {
                duration: 200,
            }
        );
        $("#location").rules("remove", "required");
    }

    function splitEquipment(event) {
        var equipment = event.equipment;
        delete event.equipment;
        event.camera = filterEquipmentByType('camera');
        event.lens = filterEquipmentByType('lens');
        event.light = filterEquipmentByType('light');
        event.sound = filterEquipmentByType('sound');
        event.accessory = filterEquipmentByType('accessory');

        function filterEquipmentByType(type){
            return equipment.filter(function(item){return item.type === type;})
        }

        return event;
    }

    function groupEquipment(event) {
        event.equipment = fetchGroupedEquipment(event);
        delete event.camera;
        delete event.lens;
        delete event.light;
        delete event.sound;
        delete event.accessory;
        return event;
    }

    function fetchGroupedEquipment(event) {
        return event.camera
            .concat(event.lens)
            .concat(event.light)
            .concat(event.sound)
            .concat(event.accessory)
            .map(function(item){
                item.type = item.type.toLowerCase();
                return item;
            });
    }

    (function connect() {
        var socket = new SockJS('/websocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            stompClient.subscribe('/event/create', function (message) {
                var createdEvents = [].concat(JSON.parse(message.body));
                for(var i=0; i < createdEvents.length; i++){
                    var createdEvent = createdEvents[i];
                    if(isEmptyFilter(currentFilter) || doesSatisfyCurrentFilter(createdEvent)){
                        $('#calendar').fullCalendar('renderEvent', splitEquipment(createdEvent), true);
                    }
                }
            });
            stompClient.subscribe('/event/update', function (message) {
                var updatedEvents = [].concat(JSON.parse(message.body));
                for(var i=0; i < updatedEvents.length; i++){
                    var updatedEvent = updatedEvents[i];
                    if(isEmptyFilter(currentFilter) || doesSatisfyCurrentFilter(updatedEvent)){
                        $('#calendar').fullCalendar('removeEvents', updatedEvent.id);
                        $('#calendar').fullCalendar('renderEvent', splitEquipment(updatedEvent), true);
                    }
                }
            });
            stompClient.subscribe('/event/delete', function (message) {
                var deletedEvents = [].concat(JSON.parse(message.body));
                for(var i=0; i < deletedEvents.length; i++){
                    var deletedEvent = deletedEvents[i];
                    $('#calendar').fullCalendar('removeEvents', deletedEvent.id);
                }
            });
        });

        function doesSatisfyCurrentFilter(event){
            return currentFilter
                && ( (event.location && currentFilter.location === event.location.id)
                || currentFilter.users
                    .filter(function(item){return mapNames(event.users).contains(item)})
                    .length != 0
                || currentFilter.equipment
                   .filter(function(item){return mapIds(event.equipment).contains(item)})
                   .length != 0);
        }
    })();
});


