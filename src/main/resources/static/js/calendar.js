$(document).ready(function() {

	var repo = {
		locations: {},
		equipment: {},
		users: {},
		userIds: {},
		colors:{
            locations: {},
            equipment: {},
            users: {}
		},
        free:{},
        busy:{}
	};

	var currentEvent;
	var currentUser;
	var movingEvent = null;
	var REST_URL = "/rest/events";
	var wasModalSubmitted = false;

	(function initEntities(){
		for(var i=0;i<LOCATIONS.length;i++) {
			repo.locations[LOCATIONS[i].name] = LOCATIONS[i].id;
			repo.colors.locations[LOCATIONS[i].name] = LOCATIONS[i].color;
		}
		for(var i=0;i<EQUIPMENT.length;i++) {
			repo.equipment[EQUIPMENT[i].name] = EQUIPMENT[i].id;
			repo.colors.equipment[EQUIPMENT[i].name] = EQUIPMENT[i].color;
		}
		for(var i=0;i<USERS.length;i++) {
		    repo.users[USERS[i].username] = USERS[i];
			repo.userIds[USERS[i].username] = USERS[i].id;
			repo.colors.users[USERS[i].username] = USERS[i].color;
		}
		currentUser = {
            id:repo.userIds[CURRENT_USER_NAME],
            username:CURRENT_USER_NAME
		};
	})();

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
        $(".modal-edit .modal-body form").submit();
		wasModalSubmitted = true;
    });

    function fetchEventFromForm(){
        var data = {};
        var $container = $('.modal-edit');
        data.title = $container.find('#name').val();
        data.start = $container.find('#start-date').val() + "T" + $('#start-time').val();
        data.end = $container.find('#end-date').val() + "T" + $container.find('#end-time').val();
        var locationName = $container.find('#location').val();
        data.location = {
            id: repo.locations[locationName],
            name: locationName
        };
        data.users = $container.find('#user').val().map(function(username){
            return {
                id:repo.userIds[username],
                username:username,
                email:repo.users[username].email
            };
        });
        if($container.find('#equipment').val()) {
            data.equipment = $container.find('#equipment').val().map(function(name){
                return {
                    id:repo.equipment[name],
                    name:name
                };
            });
        }
        return data;
    }

    $('.date, .time').on('change', function(){
		if($('#start-time').select2('val')!=='0:00' && $('#end-time').select2('val')!=='0:00'
			&& $('#start-time').select2('val') !== $('#end-time').select2('val')){
			fillSelects();
		}
	});

	function hasAnyBusyResource(param){
		var event = param.event;
		var data = {
			start: event.start.format('YYYY-MM-DD') + "T" + padTime(event.start),
			end: event.end.format('YYYY-MM-DD') + "T" + padTime(event.end),
			id: event.id
		};
		var isBusy = false;
		$.when(
			$.ajax({
				type: "GET",
				url: "/rest/locations/free",
				data:data,
				success: function(data){
					if(!mapNames(data).contains(event.location.name)){
						isBusy = true;
						toastr["warning"](event.location.name + " is busy");
					}
				}
			}),
			$.ajax({
				type: "GET",
				url: "/rest/equipment/free",
				data:data,
				success: function(data){
					var busy = filterBusy(mapNames(event.equipment), mapNames(data));
					if(busy.length > 0) {
						isBusy = true;
						toastr["warning"](busy.toString() + " is busy");
					}
				}
			}),
			$.ajax({
				type: "GET",
				url: "/rest/users/free",
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
				param.free(event);
			}
		});
	}

	function fillSelects(){
		var ajaxCounter = 0;
		$.ajax({
			type: "GET",
			url: "/rest/locations/free",
			data:{
				start:$('#start-date').val() + "T" + $('#start-time').val(),
				end:$('#end-date').val() + "T" + $('#end-time').val(),
				id: currentEvent ? currentEvent.id : ''
			},
			success: function(data){
				ajaxCounter++;
				var $select = $('.modal-edit').find('#location');
				var prev = $select.val();
				prepareSelect(data, 'name', Object.keys(repo.locations), 'location');
				if(prev){
					$select.val(prev);
				}
				if(currentEvent){
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
			url: "/rest/equipment/free",
			data:{
				start:$('#start-date').val() + "T" + $('#start-time').val(),
				end:$('#end-date').val() + "T" + $('#end-time').val(),
				id: currentEvent ? currentEvent.id : ''
			},
			success: function(data){
				ajaxCounter++;
				var $select = $('.modal-edit').find('#equipment');
				var prev = $select.val();
				prepareSelect(data, 'name', Object.keys(repo.equipment), 'equipment');
				if(prev){
					$select.val(prev);
				}
				if(currentEvent) {
					$select.select2('val', currentEvent.equipment.map(function (equipment) {
						return equipment.name
					}));
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
			url: "/rest/users/free",
			data:{
                start:$('#start-date').val() + "T" + $('#start-time').val(),
                end:$('#end-date').val() + "T" + $('#end-time').val(),
				id: currentEvent? currentEvent.id : ''
			},
			success: function(data){
				ajaxCounter++;
				var $select = $('.modal-edit').find('#user');
				var prev = $select.val();
				prepareSelect(data, 'username', Object.keys(repo.userIds), 'user');
				if(prev){
					$select.val(prev);
				}
				if(currentEvent) {
					$select.select2('val', currentEvent.users.map(function (user) {
						return user.username
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

		function getFreeArray(data, property){
			var freeArray = [];
			for(var i=0;i<data.length;i++) {
				freeArray.push(data[i][property]);
			}
			return freeArray;
		}

        function fillSelect($input, entities, busyEntities) {
            $input.empty();
            var option = $('<option>');
            for(var i=0;i<entities.length;i++) {
                $input.append(option.clone().val(entities[i]).html(entities[i]))
            }
			if(busyEntities){
				var optgroup = $('<optgroup>').attr('label', 'busy');
				for(i=0;i<busyEntities.length;i++) {
					optgroup.append(option.clone().val(busyEntities[i]).html(busyEntities[i]))
				}
				$input.append(optgroup);
			}
			$input.val('').addClass('multiselect-primary').removeClass('multiselect-default');
        }

		function prepareSelect(data, property, entities, entityName){
			repo.free[entityName] = getFreeArray(data, property);
			repo.busy[entityName] = filterBusy(entities, repo.free[entityName]);
			var $input = $('.modal-edit #' + entityName);
			fillSelect($input, repo.free[entityName], repo.busy[entityName]);
			$input.prop( "disabled", false );
		}
	}

	function updateEvent(event) {
		delete event.source;
		$.ajax({
			type: "PUT",
			url: REST_URL,
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
		events:EVENTS,
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
			if(!isDurationMoreThanOrEqualsOneHour(padTime(event.start), padTime(event.end))){
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
		        var duration = getDurationInMinutes(padTime(movingEvent.startPoint), padTime(date));
		        if(duration>0) {
		            movingEvent.start = movingEvent.startPoint;
                    movingEvent.end = date.add(30, 'minute');
                    $('#calendar').fullCalendar('renderEvent', movingEvent);
		        } else if(duration<-30) {
                    movingEvent.start = date;
                    movingEvent.end = movingEvent.startPoint;
                    $('#calendar').fullCalendar('renderEvent', movingEvent);
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
		                username:currentUser.username
		            }
		        };
		    }
		},
		eventRender: function (event, $target, view) {
			var authorColor = repo.colors.users[event.author.username];
			$target.css('background-color', authorColor);
			$target.css('border-color', authorColor);

			if(event.isStub && !$target.find('.fc-content').hasClass('empty-field')) {
                $target.find('.fc-content').addClass('empty-field');
			}
			if(view.type.contains("agend") && event.location){
				var locationColor = repo.colors.locations[event.location.name];
				$target.find('.fc-title').css('background-color', locationColor);

				var $container = $('<div>').addClass('equipment-container');
				var $element = $('<div>');
				for(var i=0;event.equipment && i<event.equipment.length;i++){
					$container.append($element.clone().text(event.equipment[i].name + "")
						//.css('color', repo.colors.equipment[event.equipment[i].name])
					);
				}
				$target.append($container);

				var gridClass = "fc-event-username col-md-" + 12 / event.users.length;
				var gridHolder = $('<div>').addClass("row");
				var gridElement = $('<div>').addClass(gridClass);
				for(var i=0;i<event.users.length;i++){
					gridHolder.append(gridElement.clone()
						.css('background-color', repo.colors.users[event.users[i].username])
						.text(event.users[i].username.split(' ').map(function(str){return str.trim()[0];}).join(''))
					);
				}
				$target.append(gridHolder);
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
			if(IS_EDITABLE){
				$target.popover({
					title:event.title + '<button type="button" class="close" data-dismiss="popover-container"><span>x</span></button>',
					html: true,
					animation:true,
					placement:'auto left',
					content: function() {
						function clearPopover() {
							$('.popover').popover('hide');
							$container = $('#popover-container');
							$container.find('.date').empty();
							$container.find('.author').empty();
							$container.find('.created').empty();
							$container.find('.location').empty();
							$container.find('.users').empty();
							$container.find('.equipment').empty();
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
							$popover.find('.author').text(event.author.username);
							console.log(event)
                            $popover.find('.created').text(moment(event.created).format('YYYY-MM-DD HH:mm'));
							$popover.find('.location').text(event.location.name);
							var $li = $('<li>');
							var usersContainer = $popover.find('.users');
							event.users.forEach(function(user) {usersContainer.append($li.clone().text(user.username))});
							var equipmentContainer = $popover.find('.equipment');
							if(event.equipment) {
							    event.equipment.forEach(function(equipment) {equipmentContainer.append($li.clone().text(equipment.name))});
							}
							$('body').on('click', '.editing', function() {
								var $modal = $('.modal-edit');
								$modal.find('#name').val(event.title);
								$modal.find('#start-date').val(event.start.format('YYYY-MM-DD'));
								$modal.find('#start-time').select2('val', event.start.hour() + ":" + padZero(event.start.minute()));
								$modal.find('#end-date').val(event.end.format('YYYY-MM-DD'));
								$modal.find('#end-time').select2('val', event.end.hour() + ":" + padZero(event.end.minute()));
								fillSelects(event);
								$modal.modal('show');
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
        if(movingEvent && isDurationMoreThanOrEqualsOneHour(padTime(movingEvent.start), padTime(movingEvent.end))){
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

	function onEventClick(event) {
		currentEvent = event;
	}

	function onSubmit(e){
        var event = fetchEventFromForm();
		if(!currentEvent){
			event.author = currentUser;
			$.ajax({
				type: "POST",
				url: REST_URL,
				contentType: "application/json",
				data: JSON.stringify(event)
			}).success(function(data){
			    $('.modal-edit').modal('hide');
			}).fail(function(e){
				toastr["error"]("Server error #5. Please refresh the page.");
			});
		} else {
			event.author = currentEvent.author;
			event.id = currentEvent.id;
			$.ajax({
				type: "PUT",
				url: REST_URL,
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
		delete currentEvent.source;
		$.ajax({
			type: "DELETE",
			url: REST_URL,
			contentType: "application/json",
			data: JSON.stringify(currentEvent)
		}).done(function(data){
			$('.popover').popover('hide');
			resetForm();
		}).fail(function(e){
			toastr["error"]("Server error #7. Please refresh the page.");
		});
	});

	$('body').on('click', 'button.history', function(){
		$.ajax({
			type: "GET",
			url: "/rest/event/changelog/" + currentEvent.id,
			success: function(data){
				var bubble = $('<div>').addClass('bubble');
				var bubbleTitle = $('<div>').addClass('title');
				var bubbleContainer = $('<div>').addClass('bubble-container');
				for(var i=0;i<data.length;i++){
					var currentBubleContainer = bubbleContainer.clone();
					var dateStr = moment(data[i].date).format('YYYY-MM-DD HH:mm');
					currentBubleContainer.append(bubbleTitle.clone().text(data[i].author.username + " " + dateStr));
					var authorColor = repo.colors.users[data[i].author.username];
					bubble.css('background-color', authorColor);
					var diff = data[i].diff;
					currentBubleContainer
						.append(diff.title === null ? '' : bubble.clone().text(diff.title))
						.append(diff.start === null ? '' : bubble.clone().text(diff.start.replace('T',' ')))
						.append(diff.end === null ? '' : bubble.clone().text(diff.end.replace('T',' ')))
						.append(diff.location === null ? '' : bubble.clone().text(diff.location.name))
						.append(diff.users === null ? '' : bubble.clone().text(mapNames(diff.users).toString().replace(',', ', ')))
						.append(diff.equipment === null ? '' :bubble.clone().text( mapNames(diff.equipment).toString().replace(',', ', ')));

					$('.modal-history').find('.modal-body').append(currentBubleContainer);
				}
				$('.modal-history').modal('show');
			}
		});
	});

	$('.modal-history').on('hidden.bs.modal', function () {
		$(this).find('.modal-body').empty();
	});

	$('.modal-edit').on('hidden.bs.modal', function () {
		clearPopup();
		currentEvent = null;
		wasModalSubmitted = false;
	});

	function clearPopup() {
		var $container = $('.modal-edit');
		$container.find('#name').val('');
		$container.find('#start-date').val('');
		$container.find('#start-time').val('0:00').trigger('change');
		$container.find('#end-date').val('');
		$container.find('#end-time').val('0:00').trigger('change');

		$container.find('#location').select2('val', '');
		$container.find('#location').select2('destroy');
		$container.find('#location').select2();

		$container.find('#user').select2('val', '');
		$container.find('#equipment').select2('val', '');
		$("form").validate().resetForm();

		disableForm();
	}

	function resetForm(){
		disableForm();
		currentEvent = null;
		currentDate = null;
		currentStartDate = null;
		currentFinishDate = null;
	}

	function disableForm(){
		$('#location').prop( "disabled", true );
		$('#user').prop( "disabled", true ).removeClass('multiselect-primary').addClass('multiselect-default');
		$('#equipment').prop( "disabled", true ).removeClass('multiselect-primary').addClass('multiselect-default');
	}
	disableForm();

	function enableForm(){
		$('#location').prop( "disabled", false );
		$('#user').prop( "disabled", false );
		$('#equipment').prop( "disabled", false );
	}

	function padZero(number) {
		return (number < 10) ? ("0" + number) : number;
	}

	function padTime(moment) {
		return padZero(moment.hour()) + ":" + padZero(moment.minute());
	}

	function mapNames(entities){
		return entities.map(function (entity) {
			return entity.name || entity.username;
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

    function getDurationInMinutes(firstTimeStr, secondTimeStr) {
        var duration = new Date('01/01/2011 ' + secondTimeStr).getTime() - new Date('01/01/2011 ' + firstTimeStr).getTime();
        return Math.floor(duration / 60000);
    }

    function getDurationInHours(firstTimeStr, secondTimeStr) {
        var duration = new Date('01/01/2011 ' + secondTimeStr).getTime() - new Date('01/01/2011 ' + firstTimeStr).getTime();
        return Math.floor(duration / 3600000);
    }

	function isDurationMoreThanOrEqualsOneHour(firstTimeStr, secondTimeStr){
		return getDurationInHours(firstTimeStr, secondTimeStr) >= 1;
	}

	(function addCustomValidatorMethods(){

		$.validator.addMethod(
			"isDurationMoreThanOrEqualsOneHour",
			function (secondTimeStr, element, param){
				var firstTimeStr = $('[name="' + param + '"]').val();
                return isDurationMoreThanOrEqualsOneHour(firstTimeStr, secondTimeStr);
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
            function(value, element){
				var $element = $(element);
				var values = $element.val();
				var busy = [];
				if(values) {
					busy = values.filter(function(value){
						return repo.busy.equipment.contains(value);
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
		rules: {
			name: "required",
			'start-date': "required",
			'end-date': {
				required:true,
				isDateBeforeOrEquals:'start-date'
			},
			'end-time': {
                isDurationMoreThanOrEqualsOneHour:'start-time'
            },
			location: {
                required:true,
                isFreeLocation:true
            },
			user: {
                required:true,
                isFreeUser:true
            },
			equipment: {
                isFreeEquipment:true
            }
		},

		messages: {
			name: "Please enter event's name",
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
    });

    $("select").select2({ dropdownCssClass : 'dropdown' });

    (function connect() {
        var socket = new SockJS('/websocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            stompClient.subscribe('/event/create', function (message) {
                var createdEvent = JSON.parse(message.body);
                $('#calendar').fullCalendar('renderEvent', createdEvent);
            });
            stompClient.subscribe('/event/update', function (message) {
                var updatedEvent = JSON.parse(message.body);
                $('#calendar').fullCalendar('removeEvents', updatedEvent.id);
                $('#calendar').fullCalendar('renderEvent', updatedEvent);
            });
            stompClient.subscribe('/event/delete', function (message) {
                var deletedEvent = JSON.parse(message.body);
                $('#calendar').fullCalendar('removeEvents', deletedEvent.id);
            });

        });
        function equals(localEvent, serverEvent) {
            function mapUsername(user){return user.username;}
            function mapEquipmentName(equipment){return equipment.name;}
            return !!localEvent && !!serverEvent
                && localEvent.author.username === serverEvent.author.username
                && localEvent.created === serverEvent.created
                && localEvent.title === serverEvent.title
                && localEvent.start.format('YYYY-MM-DD HH:mm') === serverEvent.start.replace("T", " ")
                && localEvent.end.format('YYYY-MM-DD HH:mm') === serverEvent.end.replace("T", " ")
                && localEvent.location.name === serverEvent.location.name
                && JSON.stringify(localEvent.users.map(mapUsername)) === JSON.stringify(serverEvent.users.map(mapUsername))
                && JSON.stringify(localEvent.equipment.map(mapEquipmentName)) === JSON.stringify(serverEvent.equipment.map(mapEquipmentName));
        }
    })();
});


