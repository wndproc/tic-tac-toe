let stompClient = null;
const sessionId = uuidv4();
let user;

function connect() {
    const socket = new SockJS('/tic-tac-toe', {}, {
        sessionId: function () {
            return sessionId;
        }
    });

    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
    });
}

function joinGame() {
    let name = $("#player-name").val();
    $('#modal').modal('toggle');

    stompClient.subscribe(`/user/queue/user`, function (userJson) {
        user = JSON.parse(userJson.body);
        stompClient.subscribe('/app/fields', function (fields) {
            JSON.parse(fields.body).forEach(function (field) {
                addOrUpdateField(field)
            })
        });
        stompClient.subscribe('/topic/fields', function (field) {
            addOrUpdateField(JSON.parse(field.body));
        });
    });
    stompClient.send("/app/users/create", {}, JSON.stringify({'name': name}));
}

function addOrUpdateField(field) {
    if (field.ownerId == user.id) {
        window.location.href = `field.html?fieldId=${field.id}&sessionId=${sessionId}`;
    }

    if ($(`#table-fields-row-${field.id}`).length) {
        if (field.playersNumber) {
            $(`#table-fields-row-${field.id} .players`).html(field.playersNumber);
        }
        if (field.lastMoveTime) {
            $(`#table-fields-row-${field.id} .last-move-time`).html(field.lastMoveTime);
        }
    } else {
        $("#table-fields-body").append(
            `<tr id="table-fields-row-${field.id} fieldId="${field.id}" class="table-fields-row">` +
            `<td>${field.name}</td>` +
            `<td class="players">${field.playersNumber}</td>` +
            `<td class="last-move-time">${field.lastMoveTime ? field.lastMoveTime : ""}</td>` +
            `</tr>`
        );
        $(`#table-fields-row-${field.id}`).click(function () {
            let fieldId = $(this).attr("fieldId");
            joinField(fieldId);
            window.location.href = `field.html?fieldId=${fieldId}&sessionId=${sessionId}`;
        });
    }
}

function createField() {
    stompClient.send("/app/fields/create", {}, JSON.stringify({'name': $("#input-field-name").val()}));
}

function joinField(fieldId) {
    stompClient.send(`/app/fields/${fieldId}/join`);
}

function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        let r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

connect();
$(document).ready(function () {
    $('#modal').modal({
        backdrop: 'static',
        keyboard: false
    });
});